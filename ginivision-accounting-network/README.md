![Gini Vision Library for Android](../GiniVision_Logo.png)

Gini Vision Accounting Network Library for Android
==================================================

Implementation using the Gini Accounting API of the network related tasks required by the Gini Vision Library.

Adding this library along with the Gini Vision Library to your application is the quickest way to integrate document
scanning with Gini's Accounting API.

In order for the Gini Vision Library to use this networking library, pass the instances to the `GiniVision.Builder`
when creating a new `GiniVision`.

**Important:** You *must not* enable multi-page in the Gini Vision Library when using it with this networking library.
The multi-page feature is currently only supported by the [default networking
library](https://github.com/gini/gini-vision-lib-android/tree/master/ginivision-network).

Example
-------

The example apps in the Gini Vision Library demonstrate how to integrate the Gini Vision Accounting Network Library to
easily add document analysis to your app.

We also provide a separate standalone [example app](https://github.com/gini/gini-vision-lib-android-example). This is
more like a real world app and serves as an additional help for you to discover how the Gini Vision Accounting Network
Library along with the Gini Vision Library should be used.

Requirements
------------

Android 4.4+ (API Level 19+)

Installation
------------

To install add our Maven repo to the root build.gradle file and add it as a dependency to your app module's build.gradle
along with the Gini Vision Library.

build.gradle:

```
repositories {
    maven {
        url 'https://repo.gini.net/nexus/content/repositories/open
    }
}
```

app/build.gradle:

```
dependencies {
    implementation 'net.gini:gini-vision-lib:3.16.1'
    implementation 'net.gini:gini-vision-accounting-network-lib:3.16.1'
}
```

## License

Gini Vision Library and the Gini Vision Network Library are available under a commercial license. See the LICENSE file
for more info.
