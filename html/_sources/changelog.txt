=========
Changelog
=========

3.11.3 (2019-10-29)
===================

- Fixed a bug where a crash occured on the Camera Screen if it was started without camera
  permissions and the user tapped on the grant access button to open the application details in the
  Android Settings app.

3.11.2 (2019-08-05)
===================

- Fixed a bug occuring on some Samsung devices where a ``ClassNotFoundException`` was thrown when
  unparcelling the ``ImageDocument``.

3.11.1 (2019-07-04)
===================

- Fixed a bug where host app's files were deleted after analysis.

3.11.0 (2019-05-06)
===================

- Flash can be turned off by default by calling ``.setFlashOnByDefault(false)`` when creating a
  new ``GiniVision`` instance. Flash is turned on by default.

3.10.0 (2019-04-17)
===================

- Added support for extracting eps e-payment urls from QR Codes. When using the Screen API the url
  is returned as a ``GiniVisionSpecificExtraction`` in ``CameraActivity's`` result ``Bundle`` with
  the ``"epsPaymentQRCodeUrl"`` key. When using the Component API the url is returned in the
  ``CameraFragmentListener#onExtractionsAvailable()`` method also as a
  ``GiniVisionSpecificExtraction`` in a map with the same ``"epsPaymentQRCodeUrl"`` key.
- The generated anonymous user credentials can be deleted when using the networking plugins by
  invoking the ``GiniVisionNetworkApi#deleteGiniUserCredentials()`` method on your instance of the
  ``GiniVisionNetworkApi``. Consult the
  `Gini Vision Network Library <updating-to-3-0-0.html#gini-vision-network-library>`_ section for
  more information about the networking plugin.

3.9.0 (2019-04-03)
==================

- When using the ``gini-vision-accounting-network-lib`` you can get the last successfully analyzed
  camera picture with ``GiniVisionAccountingNetworkService#getAnalyzedCameraPictureAsJpeg()``.

3.8.0 (2019-03-06)
==================

- Added back buttons to every Activity when using the Screen API. You can revert to having back
  buttons only in the Review and Analysis Screens by calling ``.setBackButtonsEnabled(false)`` when
  creating a new ``GiniVision`` instance.

3.7.1 (2019-03-04)
==================

- Fixed an issue where the pre-upload error from the Review Screen was not shown in the Analysis
  Screen. Occurred only when not enabling the multi-page feature since version 3.5.0.

3.7.0 (2019-02-28)
==================

- A flash on/off toggle button can be shown on the Camera Screen. It is not enabled by default. To
  enable it call ``setFlashButtonEnabled(true)`` when creating a new ``GiniVision`` instance.

3.6.0 (2019-02-13)
==================

- The Supported Formats help screen can be disabled using
  ``setSupportedFormatsHelpScreenEnabled(false)`` when creating a new ``GiniVision`` instance.

3.5.0 (2019-02-11)
==================

- A dialog is presented to the user when the host app is set as default for opening PDFs or images.
  The user is informed on how to revert the app defaults. The dialog is only shown
  when a PDF or image has been opened from another app. See the
  `customization guide <customization-guide.html#clear-defaults-dialog>`_ for adapting the texts.
- The help screen for opening documents from another app has been extended with information about
  app defaults. See the `customization guide <customization-guide.html#file-import-6-3>`_ for
  adapting the texts.

3.4.0 (2019-01-25)
==================

- Added English localization.

3.3.0 (2019-01-17)
==================

- Created a new networking plugin named ``gini-vision-accounting-network-lib`` for analyzing
  documents using the Gini Accounting API.

3.2.2 (2018-12-11)
==================

- Updated Android dependencies:
 - Compile and target SDK versions to 28
 - Support library version to 28.0.0
 - Google Play Services Vision to 17.0.2

3.2.1 (2018-12-10)
==================

- Fix for the multi-page image stack on the Camera Screen. It is now not clickable when it doesn't contain images.

3.2.0 (2018-11-05)
==================

- When using the Component API with multi-page the ``MultiPageReviewFragment`` does not finish its
  hosting Activity anymore when the user presses the add page button or when the user deletes every
  page. Instead the ``MultiPageReviewFragmentListener`` is used to notify the host Activity about
  these events.

3.1.0 (2018-10-31)
==================

- When using the ``gini-vision-network-lib`` default networking implementation you may set document
  metadata to be uploaded with every document. This allows additional information to be associated
  with the created documents. For example in order to know from which branch's app the document was
  uploaded from you may add the "Bankleitzahl" in the metadata as the ``branchId``.

3.0.5 (2018-10-25)
==================

- Fixed view id references in layout XMLs which caused issues when using the library with Xamarin.

3.0.4 (2018-10-12)
==================

- The default networking implementation ``gini-vision-network-lib`` uses the latest Gini API SDK
  release to store the generated anonymous user credentials in encrypted form.
- We raised the minimum Android SDK level to 19.

3.0.3 (2018-09-12)
==================

- Fixed a memory leak that was caused by not clearing cached data when resuming fragments.

3.0.2 (2018-08-10)
==================

- Password protected PDFs are detected and rejected during file validation and users are informed
  about the inability to analyze PDFs with passwords.

3.0.1 (2018-08-01)
==================

- Certificate pinning is now possible when using the default networking implementation
  ``gini-vision-network-lib``.

3.0.0 (2018-07-06)
==================

- Consult the `Updating to 3.0.0 <updating-to-3-0-0.html>`_ page in the guide for detailed information.

Multi-Page
----------

- Users can scan documents with multiple pages by taking a picture of each page. The pages'
  orientation and order can be checked and corrected. This feature is disabled by default.

Configuration
-------------

- Easier configuration with the new ``GiniVision`` class. It has a builder to create and configure a
  new instance. The instance is optional and is required only for using the multi-page scanning
  feature. 
- Previous configuration options are now deprecated, but you may continue using them for
  existing features as we are not planning to remove them anytime soon.

Networking
----------

- Improved networking integration by introducing the ``GiniVisionNetworkService`` and
  ``GiniVisionNetworkApi`` interfaces. Desired implementations are set using the ``GiniVision``
  builder. These are optional and are required only for using the multi-page scanning feature.
- Easier integration with the new default networking implementation ``gini-vision-network-lib``. It
  offers implementation of the networking interfaces and by wiring it up with the ``GiniVision``
  builder you can start extracting invoice data without the need to implement your own networking
  layer to communicate with the Gini API.
- Previous methods used to request networking calls are now deprecated and are only invoked when
  there is no ``GiniVisionNetworkService`` implementation available. You may continue to use the
  deprecated methods if you don't configure a ``GiniVisionNetworkService``. We are not planning to
  remove the deprecated methods anytime soon.

2.5.3 (2018-05-24)
==================

- Fixed a bug caused by Indian IFSC QRCodes and improved our QRCode parsers.

2.5.2 (2018-05-03)
==================

- Updated Android Support Library to 27.1.1 and Google Play Services Vision to 15.0.0.

2.5.1 (2018-02-22)
==================

- Listeners for Component API fragments may be set explicitly in order to avoid making the hosting Activities implement the listener interfaces.

2.5.0 (2018-01-22)
==================

- QRCodes on invoices and remittance slips can be detected and read. Supported formats are the BezahlCode and EPC069-12 (Stuzza (AT) and GiroCode (DE)).
- Consult the `Updating to 2.5.0 <updating-to-2-5-0.html>`_ page in the guide for detailed information.

2.4.3 (2017-11-29)
==================

- Fixed an issue where an imported file's size and name could not be retrieved when using "open with".

2.4.2 (2017-11-14)
==================

- Fixed an issue related to PDF rendering affecting some Android Lollipop devices like the Huawei MediaPad T2 10" Pro.

2.4.1 (2017-11-10)
==================

- Fixed document corner guides not being drawn correctly for camera preview sizes with a 16:9 ratio.

2.4.0 (2017-10-25)
==================

- Consult the `Updating to 2.4.0 <updating-to-2-4-0.html>`_ page in the guide for detailed information.

Features
--------

- Document Import: From the Camera Screen users can select images and PDFs from other apps which are imported into the Gini Vision Library for analysis. This feature is disabled by default.
- Open With: If your app registers itself to handle files of type JPEG, GIF, PNG or PDF you can pass them to the Gini Vision Library for analysis.
- Tips in the Analysis Screen: If analysis takes longer than 5 seconds the tips from the Onboarding Screen are shown one at a time.
- No Results Screen: If none of the required extractions were received the No Results Screen can be shown offering tips to the user for improving the extraction results.
- Help Screens: Screens for users to be able to get information about how to best use the Gini Vision Library.

UI Updates
----------

- Camera Screen UI design was updated and the preview corners are now drawn programmatically. The color of the corners can be customised with the gv_camera_preview_corners color resource. If you customised the corners by overriding the gv_camera_preview_corners.png you can remove these images and instead override the color resource.
- Analysis Screen UI design was updated and in the Screen API the title was removed from the ActionBar. You should instead override the gv_analysis_activity_indicator_message string resource which is shown below the activity indicator.

2.3.0 (2017-08-28)
==================

- Added support for tablets. For details you may consult our guide for `supporting tablets <updating-to-2-4-0.html#tablet-support>`_. Please note that allowing tablets that do not meet our minimum hardware recommendations to use the GVL could lead to lower extraction quality. We recommend implementing hardware checks to avoid this. Many tablets with at least 8MP cameras don't have an LED flash (like the popular Samsung Galaxy Tab S2) therefore we don't require flash for tablets. For this reason the extraction quality on those tablets might be lower compared to smartphones.
- Fixed image meta information handling bug related to ascii tags containing values with null bytes.

2.2.2 (2017-07-03)
==================

- Fixed image rotation bug.

2.2.1 (2017-06-30)
==================

- Fixed image meta information handling bug impacting Android 4.4 and later.

2.2.0 (2017-03-22)
==================

- Added meta information to images to be able to differentiate between Review Screen uploads and Analysis Screen uploads.
- Updated to Android Support Library version 25.3.0.

2.1.0 (2017-01-30)
==================

- Removed the 4:3 aspect ratio requirement for photos. The default camera aspect ratio will be used from now on. An 8MP minimum resolution is still required.
- Removed the continuous-focus mode requirement. Only auto-focus is required.
- If no continuous-focus mode is available then an auto-focus run is triggered when the user activates the capture button.
- Trigger button is aligned to the bottom of the preview area.
- The back button in the ReviewActivity and AnalysisActivity (in the navigation bar and in the ActionBar) leads back to the previous Activity instead of closing the library. The previous behavior can be requested by setting the `CameraActivity#EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY` to `true`.
- Fixed an issue regarding ReviewActivity and AnalysisActivity restart in the Screen API after the app had been killed while in the background.

2.0.1 (2016-10-18)
==================

- Updated Sanselan to Commons Imaging.

2.0.0 (2016-08-25)
==================

- Finalized documentation and example apps.
- Reorganized internal (non-public API) packages and classes.
- Finalized release process.

2.0.0-alpha.1 (2016-08-18)
==========================

Features
--------

- Feature complete version.
- Using the Screen API a picture can be taken with the `CameraActivity`. It can be reviewed with the `ReviewActvitiy` with the possibility to start document analysis. If the document analysis didn't complete or the document was rotated the document analysis can be continued or started again in the `AnalysisActivity`.
- Using the Component API a picture can be taken with one of the Camera Fragments. Showing the picture with one of the Review Fragments allows review and rotation of the picture. You could also start the document analysis when showing one of the Review Fragments. If the document analysis didn't complete or the document was rotated you should show one of the Analysis Fragments and continue or restart the document analysis.
- Consult the example apps for details on how to use the Gini Vision Library.
- Logging with SLF4J.
- Checking if the device meets the Gini Vision Library requirements with GiniVisionRequirements.

2.0.0-stub.1 (2016-07-15)
=========================

Features
--------

- Stub version of the completely rewritten Gini Vision Library.
- Provides two integration options: 1) A Screen API that can be easily implemented using Activities. 2) A more complex but at the same time more flexible Component API using Fragments. 
- For the communication between your app and the Library use the `CameraActivity`, `ReviewActivity` and `AnalysisActivity` for the Screen API or implement the listener methods for the Fragments when using the Component API.
- This stub release implements all calls for the future 2.0.0 release. It allows the user to capture a simulated document and review it. Also screens for onboarding and further analysis are provided. For the final release the UI will be further improved and minor changes are made in the implementation if really necessary.
