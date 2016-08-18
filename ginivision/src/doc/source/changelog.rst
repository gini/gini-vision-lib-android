=========
Changelog
=========

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
