#!/usr/bin/env bash

#set -x # trace every command executed
#set -u # fail on unset variables
set -e # fail on commands that return non-zero exit codes

usage="$(basename "$0") [-h] [-p -t -g] -- Build Release

where:
    -h  show this help text
    -p  (required) the password
    -t  (required) the release tag
    -g  (required) a Github token
    -a  (required) keystore alias"

while getopts ":p:t:g:a:h" opt; do
    case ${opt} in
        p) rel_password="$OPTARG"
        ;;
        t) rel_tag="$OPTARG"
        ;;
        g) rel_github_token="$OPTARG"
        ;;
        a) rel_alias="$OPTARG"
        ;;
        h) echo "$usage"
           exit
        ;;
        :) printf "missing argument for -%s\n" "$OPTARG" >&2
           echo "$usage" >&2
           exit 1
        ;;
        \?) echo "Invalid option -$OPTARG" >&2
            echo "$usage" >&2
            exit 1
        ;;
    esac
done
shift $((OPTIND - 1))

if [[ -z "$rel_password" ]]
    then
        echo "A password is required"
        echo "$usage" >&2
        exit 1
fi

if [[ -z "$rel_tag" ]]
    then
        echo "A release tag required"
        echo "$usage" >&2
        exit 1
fi

if [[ -z "$rel_github_token" ]]
    then
        echo "A release Github token required"
        echo "$usage" >&2
        exit 1
fi

if [[ -z "$rel_alias" ]]
    then
        echo "A keystore alias is required"
        echo "$usage" >&2
        exit 1
fi

dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
base_dir=$(realpath "${dir}/../..")
apk_file="${base_dir}/app/build/outputs/apk/release/app-release.apk"

echo "dir=$dir"
echo "base_dir=$base_dir"
echo "apk_file=$apk_file"

function cleanup {
    echo "cleaning up"
    unset KEYSTORE_PATH
    unset KEYSTORE_PASS
    unset KEYSTORE_ALIAS
    unset KEYSTORE_ALIAS_PASS
    rm ${base_dir}/fdroid.keystore
}

trap cleanup EXIT

export KEYSTORE_PATH="${base_dir}/fdroid.keystore"
export KEYSTORE_ALIAS="${rel_alias}"
export KEYSTORE_ALIAS_PASS="${rel_password}"
gpg --quiet --batch --yes --decrypt --passphrase="${rel_password}" --output "${KEYSTORE_PATH}" "${KEYSTORE_PATH}.asc"

echo "assembling release apk"
${base_dir}/gradlew assembleRelease

echo "creating Github release"
release_id=$(curl --fail --header "Authorization: token ${rel_github_token}" \
    --header "Content-Type: application/json" \
    --request POST \
    --data "{\"tag_name\": \"${rel_tag}\", \"target_commitish\": \"master\", \"name\": \"${rel_tag}\", \"body\": \"\", \"draft\": false, \"prerelease\": false}" \
    https://api.github.com/repos/manimaul/mxtide-android/releases | jq -r '.id')

echo "uploading APK asset to Github release id ${release_id}"
curl --fail \
    --header "Authorization: token ${rel_github_token}" \
    --header "Content-Type: application/vnd.android.package-archive" \
    --request POST \
    --data-binary "@${apk_file}" \
    "https://uploads.github.com/repos/manimaul/mxtide-android/releases/${release_id}/assets?name=$(basename ${apk_file})"

