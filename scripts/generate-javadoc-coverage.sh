#!/usr/bin/env bash
#
# Usage: generate-javadoc-coverage ABS_PATH_TO_PROJECT_ROOT DESTINATION
#
set -e
#set -x

export CLASSPATH=$CLASSPATH:$1/tools/doccheck-doclet/tools.jar

javadoc \
    -doclet com.sun.tools.doclets.doccheck.DocCheck \
    -docletpath $1/tools/doccheck-doclet/doccheck.jar \
    -sourcepath $1/ginivision/src/main/java \
    -subpackages net.gini.android.vision \
    -exclude net.gini.android.vision.internal \
    -d $2
