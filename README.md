Gini Vision Library 2.0
=======================

The Gini Vision Library for Android provides Activities and Fragments for capturing, reviewing and analyzing photos of invoices and remittance slips.

By integrating this library in your application you can allow your users to easily take a picture of a document, review it and - by implementing the necessary callbacks - upload the document to the Gini API for analysis .

Communication with the Gini API is not part of this library. You can either use the Gini API SDK for Android or implement communication with the Gini API yourself.

The Gini Vision Library for Android can be integrated in two ways, either by using the Screen API or the Component API. The Screen API provides Activities for easy integration that can be customized in a limited way. The screen and configuration design is based on our long-lasting experience with integration in customer apps. In the Component API we provide Fragments for advanced integration with more freedom for customization. We strongly recommend keeping in mind our UI/UX guidelines, however.

Customization of the Views is provided mostly via overriding of app resources: dimensions, strings, colors, texts, etc. Onboarding can also be customized to show your own pages, each consisting of an image and a short text.

Requirements
------------

Screen API: Android 4.0+ (API Level 14+)  
Component API: Android 4.2+ (API Level 17+)

### Hardware

* Back-facing camera with auto-focus and flash.
* Minimum 8MP camera resolution.
* Minimum 512MB RAM.

Screen API
----------

The Screen API provides a main Activity with which to start the Gini Vision Library and two abstract Activities which you need to override to react to events coming from the Gini Vision Library.

In order to support the widest variety of Android versions while keeping the look and feel consistent, we use the Android Support Library and provide only Activities subclassing the AppCompatActivity.

Component API
-------------

The Component API provides four Fragments which you can include in your own layouts. This allows you more freedom to customize the Gini Vision Library, without being restricted to AppCompatActivities and the Gini Vision Library Theme.

To allow usage even if you aren't using the Android Support Library, we provide a Standard and a Compat version of each Fragment.

Download
--------

To download add our Maven repo to the root build.gradle file and add it as a dependency to your app module's build.gradle.

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
    compile 'net.gini:gini-vision-lib:2.0.0-alpha.1'
}
```
