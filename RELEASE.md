# Release Process

This document describes the release process for a new version of the Gini Vision Library for Android.

1. Add new features only in separate `feature` branches and merge them into `develop`
2. Create a `release` branch from `develop`
  * Update the version in `gradle.properties` 
  * Add entry to the changelog with version and date
3. Push the `release` branch and build it with the `gini-vision-lib-android` Jenkins job
4. If everything is fine release it with the `gini-vision-lib-android-release` Jenkins job
5. Tag `release` branch with the same version used in 2.
6. Merge `release` branch into `master` and `develop`
7. Delete the `release` branch
