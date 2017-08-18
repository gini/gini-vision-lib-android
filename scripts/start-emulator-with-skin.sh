#!/usr/bin/env bash
#
# Start an emulator with one of the default skins by passing in the avd name, skin folder 
# name and emulator parameters.
#
# Available skin folders are in $ANDROID_HOME/skins. 
#
# Must be executed from the project root.
#
# Parameters (must be in this order):
#   1. avd name (for ex. nexus-5x_gini-vision-lib-android_feature-tablet-support)
#   2. a skin folder name from $ANDROID_HOME/skins (for ex. nexus_5x)
#   3. (optional) emulator parameters
# 
set -e
#set -x

if [ $# -lt 2 ]; then
    echo "Pass in the avd name, skin folder name and (optional) emulator parameters"
    exit 0
fi

avd_name=$1
skin=$2

scripts/start-emulator.sh "$avd_name" -skin "$skin" -skindir "$ANDROID_HOME"/skins "${@:3}"