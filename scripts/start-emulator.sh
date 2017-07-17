#!/usr/bin/env bash
#
# Start an emulator by passing in the avd name and emulator parameters.
#
# Must be executed from the project root.
#
# Parameters (must be in this order):
#   1. avd name (for ex. mobilecd_android-25_google_apis-x86_512M)
#   2. (optional) emulator parameters
# 
set -e
#set -x

if [ $# -eq 0 ]; then
    echo "Pass in the avd name and (optional) emulator parameters"
    exit 0
fi

avd_name=$1

# Get the port numbers for running emulators
used_ports=$(adb devices | tail -n +2 | cut -sf 1 | grep emulator | sed s/emulator-//)

# If an emulator is already running with the requested avd name
# return the emulator's port number and stop
for used_port in $used_ports; do
    running_avd_name=$(scripts/emulator-avd-name.sh "$used_port")
    if [ "$running_avd_name" = "$avd_name" ]; then
        echo "$used_port"
        exit 0
    fi
done

# Find the first free port number from the available port number interval 
# of [5554, 5584] of even numbers and launch the emulator using that port
port=5554
while [ $port -le 5584 ]; do
    is_unused=true
    for used_port in $used_ports; do
        if [ $port -eq $used_port ]; then
            is_unused=false
            break
        fi
    done
    if [ $is_unused = true ]; then
        echo "$port"
        # Pipe the output to STDERR to return only the port number on STDOUT
        # while allowing output from the background emulator process to be
        # shown
        "$ANDROID_HOME"/emulator/emulator -port "$port" -avd "$@" 1>&2 &
        break
    fi
    let port=port+2
done
