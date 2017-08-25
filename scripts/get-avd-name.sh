#!/usr/bin/env bash
#
# Creates an avd name as follows:
#   {working dir name}_{branch name} 
#   ex: gini-vision-lib-android_feature-tablet-support
#
# Must be executed from the project root.
# 
set -e
#set -x

branch_name=$(git describe --all --exact-match HEAD | sed -E "s:(heads\/|remotes\/|tags\/|origin\/)::g" | tr "/" "-")
dir_name=$(basename "$(pwd)")
avd_name="$dir_name"_"$branch_name"

echo "$avd_name"