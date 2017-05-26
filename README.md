![Gini Vision Library for Android](https://www.gini.net/assets/GiniVision_Logo.png)

Changed this file to trigger a build.

Gini Vision Library for Android
===============================

The Gini Vision Library provides Activities and Fragments for capturing, reviewing and analyzing photos of invoices and remittance slips.

By integrating this library in your application you can allow your users to easily take a picture of a document, review it and - by implementing the necessary callbacks - upload the document to the Gini API for analysis .

Communication with the Gini API is not part of this library. You can either use the [Gini API SDK](https://github.com/gini/gini-sdk-android) or implement communication with the Gini API yourself.

The Gini Vision Library can be integrated in two ways, either by using the *Screen API* or the *Component API*. The Screen API provides Activities for easy integration that can be customized in a limited way. The screen and configuration design is based on our long-lasting experience with integration in customer apps. In the Component API we provide Fragments for advanced integration with more freedom for customization. We strongly recommend keeping in mind our UI/UX guidelines, however.

Customization of the Views is provided mostly via overriding of app resources: dimensions, strings, colors, texts, etc. Onboarding can also be customized to show your own pages, each consisting of an image and a short text.

The Gini Vision Library has been designed for portrait orientation. We recommend limiting your concrete Activities which extend the Screen API's abstract Activities to portrait orientation. In case you use the Component API, you should limit the Activities hosting the Component API's Fragments to portrait orientation.

Due to in-memory image handling applications using the Gini Vision Library must enable large heap.

Documentation
-------------

Furhter documentation can be found in our 

* [Integration Guide](http://developer.gini.net/gini-vision-lib-android/html/) and
* [Javadoc](http://developer.gini.net/gini-vision-lib-android/javadoc/index.html)

Architecture
------------

The Gini Vision Library consists of four main screens:

* Onboading: Provides useful hints to the user on how to take a perfect photo of a document.
* Camera: The actual camera screen to capture the image of the document.
* Review: Offers the opportunity to the user to check the sharpness of the image and to rotate it into reading direction, if necessary.
* Analysis: Provides a UI for the analysis process of the document by showing the user a loading indicator and the image of the document.

### Screen API

The Screen API provides a main Activity with which to start the Gini Vision Library and two abstract Activities which you need to override to react to events coming from the Gini Vision Library.

In order to support the widest variety of Android versions while keeping the look and feel consistent, we use the Android Support Library and provide only Activities subclassing the AppCompatActivity.

### Component API

The Component API provides four Fragments which you can include in your own layouts. This allows you more freedom to customize the Gini Vision Library, without being restricted to AppCompatActivities and the Gini Vision Library Theme.

To allow usage even if you aren't using the Android Support Library, we provide a Standard and a Compat version of each Fragment.

Example
-------

We are providing example apps for the Screen API and the Component API. These apps demonstrate how to integrate the Gini Vision Library and how to use it with the Gini API SDK to analyze photos of documents.

Requirements
------------

Screen API: Android 4.0+ (API Level 14+)  
Component API: Android 4.2+ (API Level 17+)

### Hardware

* Back-facing camera with auto-focus and flash.
* Minimum 8MP camera resolution.
* Minimum 512MB RAM.

Installation
------------

To install add our Maven repo to the root build.gradle file and add it as a dependency to your app module's build.gradle.

build.gradle:

```
repositories {
    maven {
        url 'https://repo.gini.net/nexus/content/repositories/open'
    }
}
```

app/build.gradle:

```
dependencies {
    compile 'net.gini:gini-vision-lib:2.2.2'
}
```

## License

Gini Vision Library is available under a commercial license. See the LICENSE file for more info.
