Component API Example App
=========================

This example app provides you with a sample usage of the Gini Vision Library's Component API.

The Gini Vision Library supports both standard Activities and Fragments and Android Support Library ones. The `GiniVisionActivity` uses standard Activities and Fragments while the `GiniVisionAppCompatActivity` uses the ones from the Android Support Library.

The Gini API SDK is used for analyzing documents and sending feedback.

Before analyzing documents with the Component API example app, you need to set your Gini API Client Id and Secret in the `src/main/res/values/gini_api_credentials.xml`.

Please note, that large heap is enabled for the example app. Your application using the Gini Vision Library should also enable large heap to make sure, that there is enough memory for image handling.

Overview
========

The entry point of the app is `MainActivity`. It starts the `GiniVisionActivity` or the `GiniVisionAppCompatActivity`.

GiniVisionActivity
------------------

A standard multi-fragment Activity which shows the Gini Vision Library's Component API Fragments. To receive events from the Fragments it implements the listener for each Fragment.

GiniVisionAppCompatActivity
---------------------------

The same as the `GiniVisionActivity` but uses the Android Support Library's Activity and Fragment. As both implement the same logic we will use only the `GiniVisionActivity` in the descriptions below.

GiniVisionCoordinator
---------------------

The `GiniVisionCoordinator` is used to show the onboarding on the first run. After the camera was started `GiniVisionCoordinator#onCameraStarted()` is called and, if this is the first run, the `OnboardingFragment` is shown in `GiniVisionActivity#onShowOnboarding()`.

OnboardingFragment
------------------

When the user clicks the onboarding menu item or the app starts the first time the `OnboardingFragment` is shown above the `CameraFragment`. When the user reached the end of the onboarding
the `OnboardingFragment` is removed in `GiniVisionActivity#onCloseOnboarding()`. 

CameraFragment
--------------

The `GiniVisionActivity` starts by showing the `CameraFragment`. When a picture was taken the `GiniVisionActivity#onDocumentAvailable()` is called where the `ReviewFragment` is shown.

ReviewFragment
--------------

When the `ReviewFragment` starts the `GiniVisionActivity#onShouldAnalyzeDocument()` is called where the document analysis is started while the user reviews the picture. If the picture was rotated it cancels the analysis in `GiniVisionActivity#onDocumentWasRotated()`. If the analysis fails it caches an error message which will be given to the `AnalysisFragment` to be shown to the user.

When the document analysis successfully completed it calls `ReviewFragment#onDocumentAnalyzed()`. When the user clicks the next button either `GiniVisionActivity#onDocumentReviewedAndAnalyzed()` or `GiniVisionActivity#onProceedToAnalysisScreen()` is called. In the first method the extractions are shown in the `ExtractionsActivity` and in the second one the `AnalysisFragment` is shown.

The table below shows you when one of those methods is called:

|Document was rotated|Analysis started|Analysis successful|Next button clicked|
|---|---|---|---|
|no|no|-|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|no|yes|no|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|no|yes|yes|`GiniVisionActivity#onDocumentReviewedAndAnalyzed()`|
|yes|no|-|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|yes|yes|no|`GiniVisionActivity#onProceedToAnalysisScreen()`|
|yes|yes|yes|`GiniVisionActivity#onProceedToAnalysisScreen()`|

AnalysisFragment
----------------

When the `AnalysisFragment` starts the `GiniVisionActivity#onAnalyzeDocument()` is called (if an error message was given, this method is called only when the user clicks the retry button) where the document analysis is started or resumed. Notifying the Fragment about successful completion of the analysis is done similarly to the `ReviewFragment` with `AnalysisFragment#onDocumentAnalyzed()`.

An error message is displayed, if the analysis fails with `AnalysisFragment#showError()`. The activity indicator can be started and stopped with `AnalysisFragment#startScanAnimation()` and `AnalysisFragment#stopScanAnimation()`.

ExtractionsActivity
-------------------

Displays the extractions with the possibility to send feedback to the Gini API. It only shows the extractions needed for transactions, we call them the Pay5: payment recipient, IBAN, BIC, amount and payment reference.

Feedback should be sent only for the user visible fields. Other fields should be filtered out.

NoExtractionsActivity
---------------------

Displays tips to the user, if no Pay5 extractions were found. 

We recommend implementing a similar screen to aid the user in the taking better pictures and improve the quality of the extractions.

SingleDocumentAnalyzer
----------------------

Helps with managing the document analysis using our Gini API SDK. 

Analysis can be started in `GiniVisionActivity#onShouldAnalyzeDocument()` and resumed in `GiniVisionActivity#onAnalyzeDocument()` or cancelled while showing the `ReviewFragment` and started anew in the `GiniVisionActivity#onAnalyzeDocument()`. Therefore the `SingleDocumentAnalyzer` allows only one document to be analyzed. Analyzing a new one, requires the old one to be cancelled (even, if analysis was completed).

Gini API SDK
============

The Gini API SDK is created in and accessed using the `ComponentApiApp`. The `SingleDocumentAnalyzer` helps with managing document analysis.

Customization
=============

Customization options are detailed in each Screen API Activity's javadoc: `CameraActivity`, `OnboardingActivity`, `ReviewActivity` and `AnalysisActivity`.

To experiment with customizing the images used in the Gini Vision Library you can copy the contents of the folder `screenapiexample/customized-drawables` to `componentapiexample/src/main/res`.

Text customizations can be tried out by uncommenting and modifying the string resources in the `componentapiexample/src/main/res/values/strings.xml`.

Text styles and fonts can be customized by uncommenting and altering the styles in the `componentapiexample/src/main/res/values/styles.xml`

To customize the colors you can uncomment and modify the color resources in the `componentapiexample/src/main/res/values/colors.xml`.

Customizing the opacity of the onboarding pages' background you can uncomment and modify the string resource in the `componentapiexample/src/main/res/values/config.xml`.