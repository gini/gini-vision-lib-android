#!/usr/bin/env bash
#
# Commits the javadoc to gini-vision-library-android's gh-pages branch.
#
# Must be executed from the project root.
#
set -e
#set -x

git_user=$1
git_password=$2

rm -rf gh-pages
git clone -b gh-pages https://"$git_user":"$git_password"@github.com/gini/gini-vision-lib-android.git gh-pages

rm -rf gh-pages/javadoc
cp -a ginivision/build/docs/javadoc gh-pages/
cd gh-pages
touch .nojekyll
git add -u
git add .
git diff --quiet --exit-code --cached || git commit -a -m 'Gini Vision Library Javadoc'
# Will be enabled when finished
#git push