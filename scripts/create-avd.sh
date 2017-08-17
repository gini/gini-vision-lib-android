#!/usr/bin/env bash
#
# Creates an avd in the avd subfolder named as described in get-avd-name.sh with the given prefix:
#   {prefix}_{avd name}
#
# Must be executed from the project root.
#
# Parameters (must be in this order):
#   1. avd name prefix
#   2. system image package path (for ex. "system-images;android-26;google_apis;x86")
#   3. (optional) avdmanager parameters
# 
set -e
#set -x

if [ $# -lt 1 ]; then
    echo "Pass in an avd name prefix, the system image package path and (optional) parameters"
    exit 0
fi

avd_name_prefix=$1
avd_name="$avd_name_prefix"_"$(scripts/get-avd-name.sh)"
avd_path="avd"
sys_image=$2

# Create the avd in the project root avd folder
avdmanager create avd --name "$avd_name" --path "$avd_path/$avd_name" --package "$sys_image" "${@:3}"