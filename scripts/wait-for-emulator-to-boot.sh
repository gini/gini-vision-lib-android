#!/usr/bin/env bash
#
# Waits for the specified emulator to finish booting.
#
# The number of checks is limited to the specified amount. Between each check there is a 5 second delay.
# For ex. with a retry of 5 the script runs for ~25s and executes 6 checks, the first check
# w/o delay and the others with a 5s delay.
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

echo "checking if the emulator has booted"

counter=0
while [ "stopped" != "`adb -s $emulator_id shell getprop init.svc.bootanim | sed 's/\(stopped\).*/\1/'`" ]; do
    let counter=counter+1
    if [ $counter -gt $retry_limit ]; then
        echo "Reached retry limit of $retry_limit retries"
        echo "Failed to verify if emulator has booted"
        exit 1
    fi
    echo "waiting for the emulator to finish booting";
    sleep 5
done;

echo "emulator finished booting"

