![Gini Vision Library for Android](../GiniVision_Logo.png)

Gini Vision Network Library for Android
===============================

 The Gini Vision Network Library for Android provides a default implementation of the network related
 tasks required by the Gini Vision Library.
    
Adding this library along with the Gini Vision Library to your application is the quickest way to
integrate invoice scanning.

In order for the Gini Vision Library to use the default implementations, pass the instances to the
`GiniVision.Builder` when creating a new `GiniVision`.

Example
-------

The example apps in the Gini Vision Library demonstrate how to integrate the Gini Vision Network
Library to easily add document analysis to your app.

We also provide a separate standalone [example
app](https://github.com/gini/gini-vision-lib-android-example). This is more like a real world app
and serves as an additional help for you to discover how the Gini Vision Network Library along with
the Gini Vision Library should be used.

Requirements
------------

Android 4.4+ (API Level 19+)

Installation
------------

To install add our Maven repo to the root build.gradle file and add it as a dependency to your app
module's build.gradle along with the Gini Vision Library.

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
    implementation 'net.gini:gini-vision-lib:4.0.1'
    implementation 'net.gini:gini-vision-network-lib:4.0.1'
}
```

## License

Gini Vision Library and the Gini Vision Network Library are available under a commercial license.
See the LICENSE file for more info.
