#!/bin/bash

print_usage() # Args: msg
{
  echo "Error: $1"
  echo "Use as argument: mod-pack"
}

check_exit() # Args: exitcode msg
{
  if [ "$1" != "0" ]; then
    echo "Error while command: $2"
    exit $1
  fi
}

if [ -z "$1" ]; then
  print_usage "Missing argument."
  exit 1
elif [ ! -d "$1" ]; then
  print_usage "Argument is no directory."
  exit 1
fi

cd "$1"
VERS="1.0"
MOD_NAME=$(basename "$1")
JAVA_LIST=$(find src/ -name "*.java")
mkdir -p ../target
mkdir -p bin

javac --module-path="../target/:../target-lib/$MOD_NAME/" -d bin $JAVA_LIST
check_exit "$?" "javac"
jar --create --file="../target/$MOD_NAME"_$VERS.jar --module-version=$VERS -C bin .
check_exit "$?" "javac"
jar --describe-module --file="../target/$MOD_NAME"_$VERS.jar
check_exit "$?" "javac"
echo "Sucessfully created: target/$MOD_NAME"_$VERS.jar
