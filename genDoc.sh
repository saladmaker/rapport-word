#!/usr/bin/env bash
set -e

if [[ $# -lt 2 ]]; then
  echo "Usage: ./genDoc.sh <MainClassName> <themeName> [--clean]"
  exit 1
fi

MAIN_CLASS=$1
shift
ARGS="$@"

(
  cd poi-playground
  mvn -q -e exec:java \
    -Dexec.mainClass=rpp.poi.playground.Launcher \
    -Dexec.args="$MAIN_CLASS $ARGS"
)
