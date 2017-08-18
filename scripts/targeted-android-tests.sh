#!/usr/bin/env bash
#
# Executes instrumented android tests only on the targeted device.
#
# Must be executed from the project root.
#
# Requires the gradle module to have the 'getAutomatorLogConverter' task. Make sure your build.gradle
# applies the 'gradle/automator_log_converter.gradle' file.
#
# Parameters (must be in this order):
#   1. module name (for ex. geonosis)
#   2. variant name (for ex. debug)
#   3. package name (for ex. net.gini.android.vision)
#   4. device/emulator id (for ex. emulator-5554) - from the 'adb devices' list
#   5. (optional) additional arguments for 'am instrument'
#
# Test results xml will be written to:
#   {module name}/build/outputs/androidTest-results/targeted/{device/emulator id}-${module name}-${variant name}.txt
#
# Code coverage execution data (JaCoCo) will be written to:
#   {module name}/build/outputs/code-coverage/targeted/{device/emulator id}-${module name}-${variant name}-coverage.ec
#
set -e
#set -x

if [ $# -lt 4 ]; then
    echo "Pass in the module name, variant name, package name and target device/emulator id"
    exit 0
fi

module=$1
variant=$2
package=$3
target=$4
if [ $# -eq 4 ]; then
    instrument_args="-e package ${package}"
else
    instrument_args="${*:5}"
fi


results_dir=${module}/build/outputs/androidTest-results/targeted
results_file=${target}-${module}-${variant}.txt
test_results=${results_dir}/${results_file}

on_device_coverage_file=/data/data/${package}.test/coverage.ec
coverage_dir=${module}/build/outputs/code-coverage/targeted
coverage_file=${target}-${module}-${variant}-coverage.ec
coverage_results=${coverage_dir}/${coverage_file}

adb -s ${target} shell pm uninstall ${package}.test || true

# Download the jar for converting automator log output into a JUnit xml report
./gradlew ${module}:getAutomatorLogConverter

# Upload the test apk and install it
adb -s ${target} push ${module}/build/outputs/apk/${module}-${variant}-androidTest.apk /data/local/tmp/${package}.test
adb -s ${target} shell pm install -r "/data/local/tmp/${package}.test"

# Run the instrumented tests with code coverage reporting
mkdir -p ${results_dir}
adb -s ${target} shell am instrument -w -r --no-window-animation -e debug false -e coverage true -e coverageFile ${on_device_coverage_file} ${instrument_args} ${package}.test/android.support.test.runner.AndroidJUnitRunner > ${test_results}

# Copy the coverage report to an unrestricted location
on_device_temp_coverage_file=/data/local/tmp/${package}.test.coverage.ec
adb -s ${target} shell "run-as ${package}.test cat ${on_device_coverage_file} | cat > ${on_device_temp_coverage_file}"

# Copy the coverage report from the emulator
mkdir -p ${coverage_dir}
adb -s ${target} pull ${on_device_temp_coverage_file} ${coverage_results}

adb -s ${target} shell "rm ${on_device_temp_coverage_file}"

adb -s ${target} shell pm uninstall ${package}.test || true

# Convert the automator log output into a JUnit xml report
java -jar ${module}/build/tmp/automatorLogConverter/automator-log-converter-1.5.0.jar ${test_results} && rm ${test_results}
