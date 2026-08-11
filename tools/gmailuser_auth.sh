#!/bin/bash

USERNAME=$1
PASSWORD=$2
GMAIL=$3

APP_PASS="trainingapp"

PASSWORD=$(./mkpwhash.sh "$PASSWORD")

TOKEN=$(curl -v -X POST \
"http://localhost:8080/inter/auth?appPass=${APP_PASS}&username=${USERNAME}&password=${PASSWORD}&gmail=${GMAIL}")

echo -e "ハッシュ化されたパスワード：${PASSWORD}\nトークンが発行されました。招待メールのURL転送先に入力してください。${TOKEN}"
