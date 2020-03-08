#!/usr/bin/env bash

#set -x # trace every command executed
#set -u # fail on unset variables
set -e # fail on commands that return non-zero exit codes

usage="$(basename "$0") [-h] [-g -t] -- Push Release APK Artifact

where:
    -h  show this help text
    -g  (required) a Github token
    -t  (required) the release tag"

while getopts ":g:t:h" opt; do
    case ${opt} in
        g) rel_github_token="$OPTARG"
        ;;
        t) rel_tag="$OPTARG"
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

if [[ -z "$rel_github_token" ]]
    then
        echo "A release Github token required"
        echo "$usage" >&2
        exit 1
fi


if [[ -z "$rel_tag" ]]
    then
        echo "A release tag required"
        echo "$usage" >&2
        exit 1
fi

rel_tag="$(echo ${rel_tag} | sed 's/refs\/tags\/s*//')"
dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
base_dir=$(realpath "${dir}/../..")
apk_file="${base_dir}/app/build/outputs/apk/release/app-release.apk"

echo "creating Github release from tag: ${rel_tag}"
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
