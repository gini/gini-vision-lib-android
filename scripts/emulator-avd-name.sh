#!/usr/bin/env bash
#
# Get the avd name of a running emulator by passing in the emulator's port number.
#
# Parameter:
#   1. emulator port number (for ex. 5554)
#
set -e
#set -x

if [ $# -ne 1 ]; then
    echo "Pass in the emulator port number"
    exit 0
fi

emulator_port=$1

# Connect to the emulator console with telnet and get the name of the avd
output=$(expect << EOF
set timeout 1
spawn telnet localhost "$emulator_port"
expect "OK"
send "avd name\r"
expect "OK"
send "exit\r"
EOF)

# Extract the avd name from the output
avd_name=$(echo "$output" | grep -A 1 "avd name" | tail -n 1 | tr -d '[:cntrl:]')

# Print the avd's name if available
if [ "$avd_name" != "avd name" ]; then
    echo "$avd_name"
fi