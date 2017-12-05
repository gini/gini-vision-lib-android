Component API Example App
=========================

This example app provides you with a sample usage of the Gini Vision Library's Component API.

The Gini Vision Library supports both standard Activities and Fragments and Android Support Library ones. Activities without `AppCompat` in their name use standard Activities and Fragments while the other ones use the Android Support Library.

The Gini API SDK is used for analyzing documents and sending feedback.

Before analyzing documents with the Component API example app, you need to set your Gini API Client Id and Secret by creating a `local.properties` file in this folder containing a `clientId` and a `clientSecret` property.

Please note, that large heap is enabled for the example app. Your application using the Gini Vision Library should also enable large heap to make sure, that there is enough memory for image handling.

Overview
========

The entry point of the app is the `MainActivity`. It starts the `CameraExampleActivity` or the `CameraExampleAppCompatActivity`. The `MainActivity` also requests storage and camera permissions when required.

Screen Handlers
---------------

For each screen a base handler implements the screen's logic. This class is abstract and code related to standard or support library logic is implemented by concrete handlers in the `standard` and `compat` sub-packages for each screen.

Activities
----------

For each screen there are two Activities. The one without `AppCompat` in their name use standard Activities and Fragments while the other ones use the Android Support Library.

GiniVisionCoordinator
---------------------

The `GiniVisionCoordinator` is used to show the onboarding on the first run. After the camera was started `GiniVisionCoordinator#onCameraStarted()` is called and, if this is the first run, the `OnboardingFragment` is shown in `GiniVisionActivity#onShowOnboarding()`.

Camera Screen
-------------

The `BaseCameraScreenHandler` contains the main logic. The Activity and concrete handler in the `compat` and `standard` sub-packages provide the implementations dependent on whether the standard or Support Library components are used.

It starts by showing the `CameraFragment`. When a picture was taken or file was imported using the document import button the `CameraFragmentListener#onDocumentAvailable()` is called where either the Review Screen or the Analysis Screen is shown.

For imported files using the document import button the `CameraFragmentListener#onCheckImportedDocument()` is called to allow custom file checks before continuing.

When a file was received from another app the `CameraFragment` is not shown. Instead the Review Screen or the Analysis Screen is started.

In case of an error the `CameraFragmentListener#onError()` will be called.

On first launch or when using the `Tipps` menu item the `OnboardingFragment` is shown. When the Onboarding should be closed the `OnboardingFragmentListener#onCloseOnboarding()` is called.

The Help Screen is shown by clicking the help menu item (question mark).

Help Screen
-----------

This screen is shown using the `HelpActivity`. This Activity is used with both the Component and Screen APIs. Customization options are documented in the `HelpActivity`'s javadoc.

Review Screen
--------------

The `BaseReviewScreenHandler` contains the main logic. The Activity and concrete handler in the `compat` and `standard` sub-packages provide the implementations dependent on whether the standard or Support Library components are used.

When the `ReviewFragment` starts the `ReviewFragmentListener#onShouldAnalyzeDocument()` is called where the document analysis is started while the user reviews the picture. If the picture was rotated the `BaseReviewScreenHandler` cancels the analysis in `ReviewFragmentListener#onDocumentWasRotated()`. If the analysis fails it caches an error message which will be passed to the Analysis Screen to be shown to the user.

When the document analysis successfully completed it calls `ReviewFragmentInterface#onDocumentAnalyzed()`. When the user clicks the next button either `ReviewFragmentListener#onDocumentReviewedAndAnalyzed()` or `ReviewFragmentListener#onProceedToAnalysisScreen()` is called. In the first method the extractions are shown in the `ExtractionsActivity` and in the second one the `AnalysisFragment` is shown.

The table below shows you when one of those methods is called:

|Document was rotated|Analysis started|Analysis successful|Next button clicked|
|---|---|---|---|
|no|no|-|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|no|yes|no|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|no|yes|yes|`GiniVisionActivity#onDocumentReviewedAndAnalyzed()`|
|yes|no|-|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|yes|yes|no|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|yes|yes|yes|`GiniVisionActivity#onProceedToAnalysisScreen()`|

Analysis Screen
----------------

The `BaseAnalysisScreenHandler` contains the main logic. The Activity and concrete handler in the `compat` and `standard` sub-packages provide the implementations dependent on whether the standard or Support Library components are used.

When the `AnalysisFragment` starts the `AnalysisFragmentListener#onAnalyzeDocument()` is called (if an error message was given, this method is called only when the user clicks the retry button) where the document analysis is started or resumed. Notifying the Fragment about successful completion of the analysis is done similarly to the `ReviewFragment` with `AnalysisFragmentInterface#onDocumentAnalyzed()`.

An error message is displayed, if the analysis fails with `AnalysisFragmentInterface#showError()`. The activity indicator can be started and stopped with `AnalysisFragmentInterface#startScanAnimation()` and `AnalysisFragmentInterface#stopScanAnimation()`.

No Results Screen
-----------------

The `BaseNoResultsScreenHandler` contains the main logic. The Activity and concrete handler in the `compat` and `standard` sub-packages provide the implementations dependent on whether the standard or Support Library components are used.

This screen is not shown for PDFs as it shows tips for taking better pictures of documents.

The `NoResultsFragment` is shown with tips for achieving better results. For pictures taken with the camera or files imported from the document import button a back to camera button is shown. When this button is clicked the `NoResultsFragmentListener#onBackToCameraPressed()` will be called.

ExtractionsActivity
-------------------

Displays the extractions with the possibility to send feedback to the Gini API. It only shows the extractions needed for transactions, we call them the Pay5: payment recipient, IBAN, BIC, amount and payment reference.

Feedback should be sent only for the user visible fields. Other fields should be filtered out.

SingleDocumentAnalyzer
----------------------

Helps with managing the document analysis using our Gini API SDK.

Gini API SDK
============

The Gini API SDK is created in and accessed using the `ComponentApiApp`. The `SingleDocumentAnalyzer` helps with managing document analysis.

Customization
=============

Customization options are detailed in each Screen API Activity's javadoc: `CameraActivity`, `HelpActivity`, `OnboardingActivity`, `ReviewActivity` and `AnalysisActivity`.

To experiment with customizing the images used in the Gini Vision Library you can copy the contents of the folder `screenapiexample/customized-drawables` to `componentapiexample/src/main/res`.

Text customizations can be tried out by uncommenting and modifying the string resources in the `componentapiexample/src/main/res/values/strings.xml`.

Text styles and fonts can be customized by uncommenting and altering the styles in the `componentapiexample/src/main/res/values/styles.xml`

To customize the colors you can uncomment and modify the color resources in the `componentapiexample/src/main/res/values/colors.xml`.

Customizing the opacity of the onboarding pages' background you can uncomment and modify the string resource in the `componentapiexample/src/main/res/values/config.xml`.

ProGuard
========

A sample ProGuard configuration file is included in the Component API example app's directory called `proguard-rules.pro`.

The release build is configured to run ProGuard. You need a keystore with a key to sign it. Create a keystore with a key and provide them in the `gradle.properties` or as arguments for the build command:
```
$ ./gradlew componentapiexample::assembleRelease \
    -PreleaseKeystoreFile=<path to keystore> \
    -PreleaseKeystorePassword=<keystore password> \
    -PreleaseKeyAlias=<key alias> \
    -PreleaseKeyPassword=<key password> \
    -PclientId=<Gini API client id> \
    -PclientSecret=<Gini API client secret>
```

