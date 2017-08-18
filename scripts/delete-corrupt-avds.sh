#!/usr/bin/env bash
#
# Deletes corrupted avds: avd folder was deleted or something causes avdmanager to report an error for the avd.
# 
set -e
#set -x

# Get the avd names for which avdmanager reports an error
avds=$( avdmanager list avd | grep -B 2 Error | grep Name | tr -d " " | cut -d ":" -sf 2)

for avd in $avds; do
    avdmanager delete avd --name "$avd"
done