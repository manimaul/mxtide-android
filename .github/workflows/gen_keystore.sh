#!/usr/bin/env bash

dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
base_dir=$(realpath "${dir}/../..")

read -p "Enter keystore name: " name
read -p "Enter keystore alias: " alias
echo "Using alias ${alias}"
name="${name}.keystore"
echo "Generating and gpg symmetric encrypting $name"
read -p "Enter password: " -s password

keytool -genkey -v -keystore ${base_dir}/${name} -keyalg RSA -keysize 2048 \
        -validity 36500 -alias "${alias}" -dname "CN=William Kamp, OU=Android, O=Madrona Software, L=Milton, ST=WA, C=US" \
        -storepass "${password}" -keypass "${password}_${password}"

gpg --yes --batch --passphrase="${password}" --armor --symmetric --cipher-algo AES256 ${base_dir}/${name}
rm ${base_dir}/${name}
#gpg --quiet --batch --yes --decrypt --passphrase="${password}" --output ${base_dir}/${name}.keystore
