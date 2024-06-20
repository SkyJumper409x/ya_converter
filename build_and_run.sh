#!/bin/bash

die() { yell "$*"; exit 111; }
# enter script's directory (probably not necessary in most cases)
cd "$(dirname "$0")"

./build.sh
if [[ $? -eq 0 ]]; then
    java -jar ./build/ya_converter.jar
else
    die "something went wrong while building"
fi
