#!/usr/bin/env bash
#
# Creates an avd with the given device definition. The avd is named as described in create-avd.sh
#
# Use the avdmanager to list available device definitions:
#   $ avdmanager list device
#
# Must be executed from the project root.
#
# Parameters (must be in this order):
#   1. avd name prefix
#   2. system image package path (for ex. "system-images;android-26;google_apis;x86")
#   3. device definition name or id
#   4. (optional) avdmanager parameters
# 
set -e
#set -x

if [ $# -lt 3 ]; then
    echo "Pass in the avd name prefix, system image package path and device definition name or id"
    exit 0
fi

avd_name_prefix=$1
sys_image=$2
device=$3

scripts/create-avd.sh "$avd_name_prefix" "$sys_image" --device "$device" "${@:4}"