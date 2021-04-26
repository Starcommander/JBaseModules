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

check_var() # Args: var msg
{
  if [ -z "$1" ]; then
    echo "Error empty var: $2"
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

VERS="1.0"
MOD_NAME=$(basename "$1")
GRP_ID=com.starcom
# $(cat $1/src/module-info.java | grep "^module" | cut -d' ' -s -f 2)
ART_ID=$(echo "$MOD_NAME" | sed -e 's#^mod-#starcom-#g')
# $(echo "$GRP_ID" | rev | cut -d'.' -s -f 1 | rev)
# GRP_ID=$(echo "$GRP_ID" | rev | cut -d'.' -s -f 2-99 | rev)
check_var "$ART_ID" "Cannot obtain artifact id"
check_var "$GRP_ID" "Cannot obtain group id"
cd target
mvn install:install-file \
   -Dfile="$MOD_NAME"_$VERS.jar \
   -DgroupId=$GRP_ID \
   -DartifactId=$ART_ID \
   -Dversion=$VERS \
   -Dpackaging=jar \
   -DgeneratePom=true
check_exit "$?" "On mvn install"
echo "Sucessfully installed: MOD_NAME=$MOD_NAME GRP_ID=$GRP_ID ART_ID=$ART_ID VERS=$VERS"
