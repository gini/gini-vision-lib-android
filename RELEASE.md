# Release Process

This document describes the release process for a new version of the Gini Vision Library for Android.

1. Add new features only in separate `feature` branches and merge them into `develop`
2. Create a `release` branch from `develop`
  * Update the version in `gradle.properties`
  * Update the version in the `README.md`, in the `ginivision-network/README.md` 
    and in the `ginivision-accounting-network/README.md`
3. Push the `release` branch and wait for the Jenkins build to finish
4. If everything is fine create a PR to merge the `release` branch into `master`
5. After merging tag the version on `master` and push the tag
6. Start a new build on the `master` branch (if not automatically started) and confirm the release
7. Create a new release on GitHub with a changelog for the release tag
8. Merge `release` branch into `develop`
9. Delete the `release` branch