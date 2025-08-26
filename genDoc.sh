#!/usr/bin/env bash
set -e

if [[ $# -lt 2 ]]; then
  echo "Usage: ./genDoc.sh <MainClassName> <themeName> [--clean]"
  exit 1
fi

MAIN_CLASS=$1
shift
ARGS="$@"

PROJECT_DIR="poi-playground"
JAR_PATH="target/poi-playground-1.0-SNAPSHOT.jar"

(
  cd "$PROJECT_DIR"

  # Rebuild jar
  mvn -q -e clean package

  # Execute jar with arguments
  java -jar "$JAR_PATH" "$MAIN_CLASS" $ARGS
)
