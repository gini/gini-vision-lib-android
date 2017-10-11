# Release Process

This document describes the release process for a new version of the Gini Vision Library for Android.

1. Add new features only in separate `feature` branches and merge them into `develop`
2. Create a `release` branch from `develop`
  * Update the version in gradle.properties
  * Add entry to the changelog with version and date
  * Update the version in the README.md
3. Push the `release` branch and wait for the Jenkins build to finish
4. If everything is fine create a PR to merge the `release` branch into `master`
5. After merging tag the version on `master` and push the tag
6. Start a new build on the `master` branch (if not automatically started) and confirm the release
7. Merge `release` branch into `develop`
8. Delete the `release` branch