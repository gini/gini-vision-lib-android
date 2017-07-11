#!/usr/bin/env bash
set -e
#set -x

cd ginivision/src/doc
virtualenv ./virtualenv
source virtualenv/bin/activate
pip install -r requirements.txt

make clean
make html singlehtml