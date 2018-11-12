#!/usr/bin/env bash

echo "Info for runtime config of tests in $(pwd)"
echo "Bash at $(which bash)"
echo "Maven at $(which mvn) with config $(mvn -version)"

echo "Preparing test coverage reporting"
curl -L https://codeclimate.com/downloads/test-reporter/test-reporter-latest-linux-amd64 > ./cc-test-reporter
chmod +x ./cc-test-reporter
echo "Will use CodeClimate's test reporter at $(pwd)/cc-test-reporter"
echo "Set before-build notice"
./cc-test-reporter before-build

PREFLIGHT="mvn -q dependency:go-offline"
CMD="mvn clean install"

echo "Test command = ${CMD}"

set -x
${PREFLIGHT}
bash -c "./docker-util/xvfb-run.sh -a ${CMD}"
set +x

echo "Finished running tests!"

echo "Notifying CodeClimate of test build's end"
JACOCO_SOURCE_PATH=src/main/java ./cc-test-reporter format-coverage target/site/jacoco/jacoco.xml --input-type jacoco
./cc-test-reporter upload-coverage -r 9791cde00c987e47a9082b96f73a2b4eb3590f308c501a3c61d34e0276c93ec1
