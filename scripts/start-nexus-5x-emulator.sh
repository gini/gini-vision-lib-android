#!/usr/bin/env bash
#
# Start a Nexus 5X emulator by passing in the avd name and emulator parameters.
#
# Must be executed from the project root.
#
# Parameters (must be in this order):
#   1. avd name (for ex. nexus-5x_gini-vision-lib-android_feature-tablet-support)
#   2. (optional) emulator parameters
# 
set -e
#set -x

if [ $# -eq 0 ]; then
    echo "Pass in the avd name and (optional) emulator parameters"
    exit 0
fi

avd_name=$1

scripts/start-emulator.sh "$avd_name" -skin nexus_5x -skindir "$ANDROID_HOME"/skins "${@:2}"