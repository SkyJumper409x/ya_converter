#!/bin/bash

# the three-fingered claw
yell() { echo "$0: $*" >&2; }
die() { yell "$*"; exit 111; }
try() { "$@" || die "cannot $*"; }

# enter script's directory (probably not necessary in most cases)
cd "$(dirname "$0")"


echo "checking files..."

if [ ! -e "./src" ]; then die "could not find ./src directory"; fi

cd ./src

# convenience variable
sourceFiles="./skySky/Utils.java ./skySky/Yargconvert.java ./skySky/CHKeys.java ./skySky/PlasticInstrument.java"

if [ ! -e "./sf.txt" ]; then yell "sf.txt is missing, converted files will not include sixfret key-values"; fi

IFS=' ' read -ra sourceFileArray <<< ""$sourceFiles""
for i in "${sourceFileArray[@]}"; do
    if [ -e $i ]
    then
        echo "found $i"
    else
        die "file $i not found. Stopping..."
    fi
done
if [ -e "../build/ya_converter.jar" ]; then rm ../build/ya_converter.jar; fi
if [ ! -e "../build" ]; then mkdir ../build; fi

if [ -e "../bin/skySky/Yargconvert.class" ]; then rm -r ../bin/*; fi
if [ ! -e "../bin" ]; then mkdir ../bin; fi

javac -d ../bin $sourceFiles
cd ../bin
jar -cvfe ../build/ya_converter.jar Yargconvert -C ../bin/ *
echo "finished building in "$(times)""
