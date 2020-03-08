#!/usr/bin/env bash

#set -x # trace every command executed
#set -u # fail on unset variables
set -e # fail on commands that return non-zero exit codes

usage="$(basename "$0") [-h] [-p -a] -- Build Release

where:
    -h  show this help text
    -p  (required) the password
    -a  (required) keystore alias"

while getopts ":p:a:h" opt; do
    case ${opt} in
        p) rel_password="$OPTARG"
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

if [[ -z "$rel_alias" ]]
    then
        echo "A keystore alias is required"
        echo "$usage" >&2
        exit 1
fi

dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
base_dir=$(realpath "${dir}/../..")

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
export KEYSTORE_PASS="${rel_password}"
export KEYSTORE_ALIAS="${rel_alias}"
export KEYSTORE_ALIAS_PASS="${rel_password}_${rel_password}"
gpg --quiet --batch --yes --decrypt --passphrase="${rel_password}" --output "${KEYSTORE_PATH}" "${KEYSTORE_PATH}.asc"

echo "assembling release apk"
${base_dir}/gradlew --no-daemon assembleRelease
