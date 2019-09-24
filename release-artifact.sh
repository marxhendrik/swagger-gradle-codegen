#!/usr/bin/env bash

REPO_INT=$1
REPO_SNP=$2
USER=$3
PW=$4

echo "repo: $REPO_INT, snapshotrepo: $REPO_SNP, USER: $USER ,PW: $PW"

./gradlew --stacktrace -Pmaven.publish.repo.release=$REPO_INT -Pmaven.publish.repo.snapshot=$REPO_SNP -Pmaven.publish.repo.user=$USER -Pmaven.publish.repo.pw=$PW releaseArtifact ||exit 1
