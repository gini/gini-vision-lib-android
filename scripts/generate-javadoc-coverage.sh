#!/usr/bin/env bash
#
# Usage: generate-javadoc-coverage doclet_path doclet_tools_path source_path subpackages exclude_packages destination_dir
#
set -e
set -x

docletpath=$1
doclet_tools_path=$2
sourcepath=$3
subpackages=$4
exclude=$5
destination=$6

export CLASSPATH=$CLASSPATH:${doclet_tools_path}

javadoc \
    -doclet com.sun.tools.doclets.doccheck.DocCheck \
    -docletpath ${docletpath} \
    -sourcepath ${sourcepath} \
    -subpackages ${subpackages} \
    -exclude ${exclude} \
    -d ${destination}
