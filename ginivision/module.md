# Module ginivision

## Gini Vision Library for Android

The Gini Vision Library for Android provides Activities and Fragments for capturing, reviewing and analyzing photos, images and pdfs of
invoices and remittance slips.

By integrating this library in your application you can allow your users to easily take a picture of a document, review it and - by
implementing the necessary callbacks - upload the document to the Gini API for analysis.

Communication with the Gini API is not part of the Gini Vision Library to allow clients the freedom of using a networking implementation of
chossing. The quickest way to add networking is to use the Gini Vision Network Library. You may also use the Gini API SDK for Android
or implement communication with the Gini API yourself.

The Gini Vision Library for Android can be integrated in two ways, either by using the Screen API or the Component API. The Screen API
provides Activities for easy integration that can be customized in a limited way. The screen and configuration design is based on our
long-lasting experience with integration in customer apps. In the Component API we provide Fragments for advanced integration with more
freedom for customization. We strongly recommend keeping in mind our UI/UX guidelines, however.

Customization of the Views is provided mostly via overriding of app resources: dimensions, strings, colors, texts, etc. Onboarding can also
be customized to show your own pages, each consisting of an image and a short text.

Due to in-memory image handling applications using the Gini Vision Library must enable large heap.

### Tablet Support

The Gini Vision Library can be used on tablets too. We have adapted some UI elements to offer a better experience to tablet users and
removed the camera flash requirement for tablets since flash is not present on all tablets. For more information please consult our guide
[Supporting Tablets](http://developer.gini.net/gini-vision-lib-android/html/updating-to-2-4-0.html#tablet-support).

### Requirements

* Screen API: Android 4.4+ (API Level 19+)
* Component API: Android 4.4+ (API Level 19+)

#### Phone Hardware

* Back-facing camera with auto-focus and flash
* Minimum 8MP camera resolution.
* Minimum 512MB RAM.

#### Tablet Hardware

* Back-facing camera with auto-focus.
* Minimum 8MP camera resolution.
* Minimum 512MB RAM.

### Screen API

#### GVL 2.5.0 and older

The Screen API provides a main Activity with which to start the Gini Vision Library and two abstract Activities which you need to override
to react to events coming from the Gini Vision Library.

In order to support the widest variety of Android versions while keeping the look and feel consistent, we use the Android Support Library
and provide only Activities subclassing the AppCompatActivity.

#### GVL 3.0.0 and newer

The Screen API provides a configuration singleton and a main Activity with which to start the Gini Vision Library. 

In order to support the widest variety of Android versions while keeping the look and feel consistent, we use the Android Support Library
and provide only Activities subclassing the AppCompatActivity.

### Component API

The Component API provides Fragments which you can include in your own layouts. This allows you more freedom to customize the Gini
Vision Library, without being restricted to AppCompatActivities and the Gini Vision Library Theme.

To allow usage even if you aren't using the Android Support Library, we provide a Standard and a Compat version of each Fragment.
