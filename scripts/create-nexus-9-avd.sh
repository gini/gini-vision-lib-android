#!/usr/bin/env bash
#
# Creates a Nexus 9 avd with a name prefixed with the given string.
#
# Must be executed from the project root.
#
# Parameter:
#   1. avd name prefix
#   2. system image package path (for ex. "system-images;android-26;google_apis;x86")
# 
set -e
#set -x

if [ $# -ne 2 ]; then
    echo "Pass in the avd name prefix and system image package path"
    exit 0
fi

avd_name_prefix=$1
final_prefix="$avd_name_prefix"_"nexus-9"
sys_image=$2

scripts/create-avd.sh "$final_prefix" "$sys_image" --device "Nexus 9"