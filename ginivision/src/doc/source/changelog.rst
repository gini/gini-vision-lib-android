=========
Changelog
=========

2.4.0 (2017-10-25)
=======================

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
