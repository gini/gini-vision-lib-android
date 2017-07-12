#!/usr/bin/env bash
#
# Commits the documentation to gini-vision-library-android's gh-pages branch.
#
# Must be executed from the project root.
#
set -e
#set -x

rm -rf gh-pages
git clone -b gh-pages git@github.com:gini/gini-vision-lib-android.git gh-pages

rm -rf gh-pages/html gh-pages/singlehtml
cp -a ginivision/src/doc/build/html ginivision/src/doc/build/singlehtml gh-pages/
cd gh-pages
touch .nojekyll
git add -u
git add .
git diff --quiet --exit-code --cached || git commit -a -m 'Gini Vision Library Integration Guide'
# Will be enabled when finished
#git push