![Gini Vision Library for Android](GiniVision_Logo.png)

Gini Vision Library for Android
===============================

The Gini Vision Library provides Activities and Fragments for capturing, reviewing and analyzing
photos of invoices and remittance slips.

By integrating this library into your application you can allow your users to easily take pictures
of documents, review them and - by implementing the necessary networking interfaces - upload the
document to the Gini API for analysis.

Communication with the Gini API is not part of the Gini Vision Library in order to allow clients the
freedom to use a networking implementation of their own choosing. The quickest way to add networking
is to use the [Gini Vision Network
Library](https://github.com/gini/gini-vision-lib-android/tree/master/ginivision-network). You may
also use the [Gini API SDK](https://github.com/gini/gini-sdk-android) for Android or implement
communication with the Gini API yourself.

The Gini Vision Library can be integrated in two ways, either by using the *Screen API* or the
*Component API*. The Screen API provides Activities for easy integration that can be customized in a
limited way. The screen and configuration design is based on our long-lasting experience with
integration in customer apps. In the Component API we provide Fragments for advanced integration
with more freedom for customization. We strongly recommend keeping in mind our UI/UX guidelines,
however.

Customization of the Views is provided mostly via overriding of app resources: dimensions, strings,
colors, texts, etc. Onboarding can also be customized to show your own pages, each consisting of an
image and a short text.

The Gini Vision Library can be used on smartphones and tablets, too. On smartphones it has been
designed for portrait orientation only and will always switch to portrait orientation in both Screen
API and Component API usage. On tablets both portrait and landscape orientations are supported.

It is not required to limit your Activities extending the Screen API's abstract Activities or your
Activities hosting the Component API's Fragments to portrait orientation. The Gini Vision Library
takes care of limiting to portrait on smartphones.

Due to in-memory image handling applications using the Gini Vision Library must enable large heap.

Tablet Support
--------------

We enabled landscape orientation and adapted some UI elements to offer a better experience to tablet
users. We also removed the camera flash requirement for tablets since many tablets with at least 8MP
cameras don't have an LED flash (like the popular Samsung Galaxy Tab S2). 

For more information please consult our guide for [supporting
tablets](http://developer.gini.net/gini-vision-lib-android/html/updating-to-2-4-0.html#tablet-support).

> **Note:** Please see our minimum hardware recommendations for tablets below. We recommend
> implementing corresponding hardware checks for the Gini Vision Library to deliver optimal results
> to users. As mentioned many tablets with at least 8MP cameras don't have an LED flash (like the
> popular Samsung Galaxy Tab S2) and we don't require flash for tablets. For this reason the
> extraction quality on those tablets might be lower compared to smartphones.

Documentation
-------------

Furhter documentation can be found in our 

* [Integration Guide](http://developer.gini.net/gini-vision-lib-android/html/) and
* [Gini Vision Javadoc](http://developer.gini.net/gini-vision-lib-android/javadoc/index.html)
* [Network Library Javadoc](http://developer.gini.net/gini-vision-lib-android/javadoc/index.html)

Architecture
------------

The Gini Vision Library consists of four main screens:

* Onboarding: Provides useful hints to the user on how to take a perfect photo of a document.
* Camera: The actual camera screen to capture the image of the document.
* Review: Offers the opportunity to the user to check the sharpness of the image and to rotate it
  into reading direction, if necessary.
* Analysis: Provides a UI for the analysis process of the document by showing the user a loading
  indicator and the image of the document.

### Screen API

The Screen API provides a main Activity with which to start the Gini Vision Library. In previous
versions two abstract Activities which had to be overriden to react to events coming from the Gini
Vision Library. This has been changed and now only the implementations of the
`GiniVisionNetworkService` and `GiniVisionNetworkApi` have to be provided with the help of the
`GiniVision` class.

In order to support the widest variety of Android versions while keeping the look and feel
consistent, we use the Android Support Library and provide only Activities subclassing the
AppCompatActivity.

### Component API

The Component API provides Fragments which you can include into your own layouts. This allows you
more freedom to customize the Gini Vision Library, without being restricted to AppCompatActivities
and the Gini Vision Library Theme.

To allow usage even if you aren't using the Android Support Library, we have been providing a
Standard and a Compat version of Fragments in previous versions. We still provide them for backwards
compatibility, but for new Fragments we will only provide versions using the Android Support
Library. 

Example
-------

We are providing example apps for the Screen API and the Component API. These apps demonstrate how
to integrate the Gini Vision Library and how to use it with the Gini Vision Network Library to
analyze photos of documents.

We also provide a separate standalone [example
app](https://github.com/gini/gini-vision-lib-android-example). This is more like a real world app
and serves as an additional help for you to discover how the Gini Vision Library (and the Gini
Vision Network Library) should be used.

Requirements
------------

Screen API: Android 4.4+ (API Level 19+)
Component API: Android 4.4+ (API Level 19+)

### Phone Hardware

* Back-facing camera with auto-focus and flash.
* Minimum 8MP camera resolution.
* Minimum 512MB RAM.

### Tablet Hardware

* Back-facing camera with auto-focus.
* Minimum 8MP camera resolution.
* Minimum 512MB RAM.

Installation
------------

To install add our Maven repo to the root build.gradle file and add it as a dependency to your app
module's build.gradle.

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
    implementation 'net.gini:gini-vision-lib:3.4.0'
}
```

## License

Gini Vision Library and the Gini Vision Network Library are available under a commercial license.
See the LICENSE file for more info.
