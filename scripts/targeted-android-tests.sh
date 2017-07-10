#!/usr/bin/env bash
set -e
set -x

module=$1
variant=$2
package=$3
target=$4

results_dir=${module}/build/outputs/androidTest-results/targeted
results_file=${target}-${module}-${variant}.txt
test_results=${results_dir}/${results_file}

on_device_coverage_file=/data/data/${package}.test/coverage.ec
coverage_dir=${module}/build/outputs/code-coverage/targeted
coverage_file=${target}-${module}-${variant}-coverage.ec
coverage_results=${coverage_dir}/${coverage_file}

adb -s ${target} shell pm uninstall ${package}.test || true

./gradlew ${module}:getAutomatorLogConverter

adb -s ${target} push ${module}/build/outputs/apk/${module}-${variant}-androidTest.apk /data/local/tmp/${package}.test
adb -s ${target} shell pm install -r "/data/local/tmp/${package}.test"

mkdir -p ${results_dir}
adb -s ${target} shell am instrument -w -r --no-window-animation -e package ${package} -e debug false -e coverage true -e coverageFile ${on_device_coverage_file} ${package}.test/android.support.test.runner.AndroidJUnitRunner > ${test_results}

on_device_temp_coverage_file=/data/local/tmp/${package}.test.coverage.ec

adb -s ${target} shell "run-as ${package}.test cat ${on_device_coverage_file} | cat > ${on_device_temp_coverage_file}"

mkdir -p ${coverage_dir}
adb -s ${target} pull ${on_device_temp_coverage_file} ${coverage_results}

adb -s ${target} shell "rm ${on_device_temp_coverage_file}"

adb -s ${target} shell pm uninstall ${package}.test || true

java -jar ${module}/build/tmp/automatorLogConverter/automator-log-converter-1.5.0.jar ${test_results} && rm ${test_results}
