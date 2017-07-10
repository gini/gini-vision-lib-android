#!/usr/bin/env bash
set -e
# set -x

module=$1
variant=$2
package=$3
target=$4

report_dir=${module}/build/reports/androidTests/targeted
report_file=${module}${variant}TestResults.txt
test_results=${report_dir}/${report_file}

adb -s ${target} shell pm uninstall ${package}.test || true

./gradlew ${module}:getAutomatorLogConverter

adb -s ${target} push ${module}/build/outputs/apk/${module}-${variant}-androidTest.apk /data/local/tmp/${package}.test
adb -s ${target} shell pm install -r "/data/local/tmp/${package}.test"

mkdir -p ${report_dir}
adb -s ${target} shell am instrument -w -r -e package ${package} -e debug false ${package}.test/android.support.test.runner.AndroidJUnitRunner > ${test_results}

adb -s ${target} shell pm uninstall ${package}.test || true

java -jar ${module}/build/tmp/automatorLogConverter/automator-log-converter-1.5.0.jar ${test_results} && rm ${test_results}
