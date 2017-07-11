#!/usr/bin/env bash
set -e
#set -x

avd_name=$1

used_ports=$(adb devices | tail -n +2 | cut -sf 1 | grep emulator | sed s/emulator-//)

for used_port in $used_ports; do
    running_avd_name=$(./emulator-avd-name.sh "$used_port")
    if [ "$running_avd_name" = "$avd_name" ]; then
        echo "$used_port"
        exit 0
    fi
done

port=5554
while [ $port -le 5584 ]; do
    is_unused=true
    for used_port in $used_ports; do
        if [ $port -eq $used_port ]; then
            is_unused=false
            break
        fi
    done
    if [ $is_unused = true ]; then
        echo "$port"
        "$ANDROID_HOME"/emulator/emulator -port "$port" -avd "$@" 1>&2 &
        break
    fi
    let port=port+2
done
