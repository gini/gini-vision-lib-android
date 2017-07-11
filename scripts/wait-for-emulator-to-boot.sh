#!/usr/bin/env bash
set -e
#set -x

if [ $# -ne 2 ]; then
    echo "Pass in emulator qualifier and number of retries"
    exit 0
fi

emulator_qualifier=$1
retry_limit=$2

echo "checking if the emulator has booted"

counter=0
while [ "stopped" != "`adb -s $emulator_qualifier shell getprop init.svc.bootanim | sed 's/\(stopped\).*/\1/'`" ]; do
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

