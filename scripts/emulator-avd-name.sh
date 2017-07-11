#!/usr/bin/env bash
set -e
#set -x

emulator_port=$1

output=$(expect << EOF
set timeout 1
spawn telnet localhost "$emulator_port"
expect "OK"
send "avd name\r"
expect "OK"
send "exit\r"
EOF)

avd_name=$(echo "$output" | grep -A 1 "avd name" | tail -n 1 | tr -d '[:cntrl:]')

if [ "$avd_name" != "avd name" ]; then
    echo "$avd_name"
fi