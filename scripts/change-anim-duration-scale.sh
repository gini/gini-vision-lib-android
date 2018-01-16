#!/usr/bin/env bash
#
# Changes animation duration scaling on the targeted device/emulator.
#
# Parameters (must be in this order):
#   1. device/emulator id (for ex. emulator-5554) - from the 'adb devices' list
#   2. animation scale (float value between 0 and 1)
# 
set -e
#set -x

if [ $# -eq 0 ]; then
    echo "Pass in the target device/emulator id and animation scale (float value between 0 and 1)"
    exit 0
fi

target=$1
scale=$2

adb -s "$target" shell settings put global window_animation_scale "$scale"
adb -s "$target" shell settings put global transition_animation_scale "$scale"
adb -s "$target" shell settings put global animator_duration_scale "$scale"
