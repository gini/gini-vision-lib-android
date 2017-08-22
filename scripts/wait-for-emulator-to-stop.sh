#!/usr/bin/env bash
#
# Waits for the specified emulator to stop.
#
# The number of checks is limited to the specified amount. Between each check there is a 1 second delay.
# For ex. with a retry of 5 the script runs for ~5s and executes 6 checks, the first check
# w/o delay and the others with a 1s delay.
#
# Parameters (must be in this order):
#   1. emulator id (for ex. emulator-5554)
#   2. number of retries
#
set -e
#set -x

if [ $# -ne 2 ]; then
    echo "Pass in the emulator id and number of retries"
    exit 0
fi

emulator_id=$1
retry_limit=$2

echo "checking if the emulator has stopped"

counter=0
while adb -s $emulator_id get-state > /dev/null 2>&1; do
    let counter=counter+1
    if [ $counter -gt $retry_limit ]; then
        echo "Reached retry limit of $retry_limit retries"
        echo "Failed to verify if emulator has stopped"
        exit 1
    fi
    echo "waiting for the emulator to stop";
    sleep 1
done;

echo "emulator stopped"

