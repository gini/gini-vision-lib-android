Updating to 2.4.0
====

Breaking Changes
----

Camera Screen
^^^^

The UI of the Camera Screen was updated for a better user experience. The document corner guides in the camera screen are now drawn programmatically. The color of the corner lines can be set by overriding the color resource named ``gv_camera_preview_corners``.

The corner guide png image resources called ``gv_camera_preview_corners.png`` can be deleted as they are not used anymore.

The `CameraFragmentListener` received an additional method for checking imported documents. Even if you don't use the `Document Import`_ feature you need to implement ``CameraFragmentListener#onCheckImportedDocument(Document, DocumentCheckResultCallback)``. If you don't use the `Document Import`_ feature an empty implementation is sufficient. In case you enabled document import you must call one of the callback methods.

Analysis Screen
^^^^

The Analysis Screen UI was updated and in the Screen API the title was removed from the ActionBar. You should instead override the ``gv_analysis_activity_indicator_message`` string resource which is shown below the activity indicator.

Android Support Library
^^^^

We updated to the Android Support Library 26 which requires the Google Maven Repository. Add the following to the ``repositories`` in your ``build.gradle``:

.. code-block:: groovy

    repositories {
        maven {
            url 'https://maven.google.com'
        }
        ...
    }

Deprecation
----

Deprecated methods:

- ``Document#getJpeg()`` - use ``Document#getData()`` instead. This method might return a byte array containing other types, like PDFs.
- ``Document#getRotationForDisplay()`` - use ``ImageDocument#getRotationForDisplay()`` instead, if ``Document#getType()`` is equal to ``Document.Type#IMAGE``.
- ``CameraFragmentInterface#showDocumentCornerGuides()`` - Use ``CameraFragmentInterface#showInterface()`` instead.
- ``CameraFragmentInterface#hideDocumentCornerGuides()`` - Use ``CameraFragmentInterface#hideInterface()`` instead.
- ``CameraFragmentInterface#showCameraTriggerButton()`` - Use ``CameraFragmentInterface#showInterface()`` instead.
- ``CameraFragmentInterface#hideCameraTriggerButton()`` - Use ``CameraFragmentInterface#hideInterface()`` instead.

Deprecated CameraActivity Intent extra:

- ``CameraActivity#EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY`` - The option to close the library with the back button from any screen will be removed in a future version.

New Features
----

Document Import
^^^^

The Document Import feature allows users to select images (jpeg, png and gif) and PDFs from their device or from their cloud storage. The selected document will be made available to the client and may be optionally verified before accepting it for upload and analysis.

.. note::

    You can use ``IntentHelper`` and ``UriHelper`` for retrieving information about the imported document.

Enable Document Import
~~~~

This feature is disabled by default. When it's enabled users will see a new button next to the camera trigger in the Camera Screen.

After the feature is enabled a hint is displayed informing the user about the new button and its function.

To enable the Document Import using the Screen API add the following extra to the ``CameraActivity`` intent and specify the file types you wish to allow:

.. code-block:: java

    // Enable for PDFs and images
    intent.putExtra(CameraActivity.EXTRA_IN_ENABLE_DOCUMENT_IMPORT_FOR_FILE_TYPES,
                DocumentImportEnabledFileTypes.PDF_AND_IMAGES);
    // Or only for PDFs
    intent.putExtra(CameraActivity.EXTRA_IN_ENABLE_DOCUMENT_IMPORT_FOR_FILE_TYPES,
                DocumentImportEnabledFileTypes.PDF);

For the Component API use the factory method of the ``CameraFragmentCompat`` or ``CameraFragmentStandard``:

.. code-block:: java

    // Enable for PDFs and images
    CameraFragmentCompat.createInstance(DocumentImportEnabledFileTypes.PDF_AND_IMAGES);
    // Or only for PDFs
    CameraFragmentCompat.createInstance(DocumentImportEnabledFileTypes.PDF);

Read Storage Permission
~~~~

To access files on the user's device the ``READ_EXTERNAL_STORAGE`` permission is required and if the permission is not granted the Gini Vision Library will prompt the user to grant the permission. The rationale alert dialog and permission denied alert dialog texts can be customized.

Checking Imported Documents
~~~~

The Gini Vision Library verifies the file's mime-type, size (up to 10MB) and in case of PDFs the nr of pages (at most 10 pages). To run custom checks you can extend the ``CameraActivity`` and override the ``onCheckImportedDocument()`` method:

.. code-block:: java

    // As an example we show how to allow only jpegs and pdfs smaller than 5MB
    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        // We can apply custom checks here to an imported document and notify the Gini Vision
        // Library about the result
        // IMPORTANT: do not call super as it will lead to unexpected behaviors

        if (DO_CUSTOM_DOCUMENT_CHECK) {
            // Use the Intent with which the document was imported to access its contents
            // (document.getData() may be null)
            final Intent intent = document.getIntent();
            if (intent == null) {
                callback.documentRejected(getString(R.string.gv_document_import_error));
                return;
            }
            final Uri uri = IntentHelper.getUri(intent);
            if (uri == null) {
                callback.documentRejected(getString(R.string.gv_document_import_error));
                return;
            }
            if (hasMoreThan5MB(uri)) {
                callback.documentRejected(getString(R.string.document_size_too_large));
                return;
            }
            // IMPORTANT: always call one of the callback methods
            if (isJpegOrPdf(uri)) {
                callback.documentAccepted();
            } else {
                callback.documentRejected(getString(R.string.unsupported_document_type));
            }
        } else {
            // IMPORTANT: always call one of the callback methods
            callback.documentAccepted();
        }
    }

Rejecting a document displays the provided message to the user in an alert dialog. The user may select another document or cancel the document import.

.. _Document Import - Analyzing Imported Documents:

Analyzing Imported Documents
~~~~

After the document was accepted the Gini Vision Library shows the Review Screen for images and for PDFs it goes directly to the Analysis Screen. Analyzing imported documents requires no changes. The document's content will be loaded into memory and can be uploaded like the pictures taken by the camera.

Additional methods were added to the ``Document`` to identify whether the document was imported, to get the ``Intent`` with which it was imported, to find out if it's reviewable (PDFs are not reviewable for ex.) and to get it's type (image or pdf).

Use the additional methods in the Review and Analysis screens if you wish to handle imported documents separately from pictures taken by the camera.

.. _Document Import - Analysis Screen for PDFs:

Analysis Screen for PDFs
~~~~

The Analysis Screen will render the first page of the PDF on Android Lollipop and newer. On older versions no preview is shown. In addition above the PDF preview area the PDF filename is displayed and the nr of pages (on Android Lollipop and newer).

.. _Document Import - Customizing the UI:

Customizing the UI
~~~~

Camera Screen:

- Document import button icon
- Document import hint text, text style, background color and close icon color
- Storage permission rationale AlertDialog text and button color
- Storage permission denied AlertDialog text and button color

Analysis Screen:

- Activity indicator message for images
- PDF info panel background and text style

For detailed customization options consult the Javadoc of the ``CameraActivity`` and ``AnalysisActivity``.

.. _File Import:

File Import ("Open With")
^^^^

The File Import feature allows users to send images (jpeg, png and gif) to the Gini Vision Library from other apps through your app.

Registering PDF and image file types
~~~~

Your app should register an Activity for receiving PDFs and images. 

Add the following intent filter to the Activity in your ``AndroidManifest.xml`` you wish to receive incoming PDFs and images:

.. code-block:: xml

    <activity android:name=".ui.MyActivity">
        <intent-filter android:label="@string/label_for_open_with">
            <action android:name="android.intent.action.VIEW" />
            <action android:name="android.intent.action.SEND" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:mimeType="image/*" />
            <data android:mimeType="application/pdf" />
        </intent-filter>
    </activity>

.. note::

    We recommend adding `ACTION_VIEW <https://developer.android.com/reference/android/content/Intent.html#ACTION_VIEW>`_ to the intent filter to also allow users to send PDFs and images to your app from apps which don’t implement sharing with `ACTION_SEND <https://developer.android.com/reference/android/content/Intent.html#ACTION_SEND>`_ but enable viewing the PDF or file with other apps.

Handling Imported File
~~~~

When your app is requested to handle a PDF or an image your Activity (declaring the intent filter shown above) is launched or resumed (``onNewIntent(Intent)``) with an Intent having ``ACTION_VIEW`` or ``ACTION_SEND``.

Checking whether the Intent has the required action:

.. code-block:: java

    String action = intent.getAction();
    if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action)) {
        ...
    }

Using the Screen API create an Intent for launching the Gini Vision Library with ``GiniVisionFileImport.createIntentForImportedFile()``. This method will throw an ``ImportedFileValidationException``, if the file is too large, has the wrong mime-type or has more than 10 pages (only for PDFs).

.. code-block:: java

    void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        try {
            final Intent giniVisionIntent = GiniVisionFileImport.createIntentForImportedFile(
                    importedFileIntent,
                    this,
                    ReviewActivity.class,
                    AnalysisActivity.class);
            startActivityForResult(giniVisionIntent, REQUEST_SCAN);
        } catch (ImportedFileValidationException e) {
            e.printStackTrace();
            String message = "File cannot be analyzed";
            if (e.getValidationError() != null) {
                switch (e.getValidationError()) {
                    case TYPE_NOT_SUPPORTED:
                        message = "File type not supported.";
                        break;
                    case SIZE_TOO_LARGE:
                        message = "File too large, must be less than 10 MB.";
                        break;
                    case TOO_MANY_PDF_PAGES:
                        message = "Pdf must have less than 10 pages.";
                        break;
                }
            }
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            finish();
                        }
                    })
                    .show();
        }
    }

The returned Intent will launch either the ReviewActivity or the AnalysisActivity implementation you provided. PDFs cannot be reviewed in which case the AnalysisActivity will be launched. You should not expect the ReviewActivity to be launched every time.

For the Component API create a ``Document`` with ``GiniVisionFileImport.createDocumentForImportedFile()``. This method will throw an ``ImportedFileValidationException``, if the file is too large, has the wrong mime-type or has more than 10 pages (only for PDFs). The ReviewFragment may only be used with reviewable documents therefore it is important to check whether the document is reviewable or not:

.. code-block:: java

    void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
            try {
                final Document document = GiniVisionFileImport.createDocumentForImportedFile(
                        importedFileIntent,
                        this);
                if (document.isReviewable()) {
                    pushFragment(getReviewFragment(document), R.string.title_review);
                } else {
                    pushFragment(getAnalysisFragment(document), R.string.title_review);
                }
            } catch (ImportedFileValidationException e) {
                e.printStackTrace();
                String message = getString(R.string.gv_document_import_invalid_document);
                if (e.getValidationError() != null) {
                    switch (e.getValidationError()) {
                        case TYPE_NOT_SUPPORTED:
                            message = getString(R.string.gv_document_import_error_type_not_supported);
                            break;
                        case SIZE_TOO_LARGE:
                            message = getString(R.string.gv_document_import_error_size_too_large);
                            break;
                        case TOO_MANY_PDF_PAGES:
                            message = getString(R.string.gv_document_import_error_too_many_pdf_pages);
                            break;
                    }
                }
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, final int i) {
                                finish();
                            }
                        })
                        .show();
            }
        }

Analyzing Imported Documents
~~~~

Same as `Document Import - Analyzing Imported Documents`_.

Analysis Screen for PDFs
~~~~

Same as `Document Import - Analysis Screen for PDFs`_.

Customizing the UI
~~~~

Same as `Document Import - Customizing the UI`_.

Tips in the Analysis Screen
^^^^

When analysis takes more than 5 seconds the Gini Vision Library cycles through tips showing each one for 4 seconds. The tips are shown on the bottom of the Analysis Screen. The tips should help our users achieve better results by offering them advice on how to take good pictures.

No Results Screen
^^^^

The Gini Vision Library contains a new screen providing tips for users in order to achieve better results. The No Results Screen is displayed only for pictures taken by the camera and imported images.

The No Results Screen should be requested only when none of the required extractions were received.

When using the Screen API call the ``onNoExtractionsFound()`` either in your ReviewActivity or AnalysisActivity implementation depending on where analysis completes:

.. code-block:: java

    // In your ReviewActivity and AnalysisActivity subclasses:
    void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
        mExtractions = extractions;
        if (mExtractions == null || hasNoPay5Extractions(mExtractions.keySet())) {
            onNoExtractionsFound();
        } else {
            // Calling onDocumentAnalyzed() is important to notify the
            // ReviewActivity base class that the analysis has completed successfully
            onDocumentAnalyzed();
        }
    }

By invoking ``onNoExtractionsFound()`` the Gini Vision Library will display the NoResultsActivity, if the document was an image. From this Activity users can go back to the Camera Screen, provided the Camera Screen was shown, otherwise users can go back to your Activity that launched the Gini Vision Library. 

For the Component API you should invoke ``GiniVisionCoordinator#shouldShowGiniVisionNoResultsScreen(Document)`` which returns true only if the Document .

.. code-block:: java

    void showExtractions(net.gini.android.models.Document giniApiDocument,
            Map<String, SpecificExtraction> extractions, Document document) {
        // If we have no Pay 5 extractions we query the Gini Vision Library
        // whether we should show the the Gini Vision No Results Screen
        if (hasNoPay5Extractions(extractions.keySet())
                && GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(document)) {
            // Create the No Results Screen's fragment (you may also use NoResultsFragmentStandard if you don't use the Support Library)
            final NoResultsFragmentCompat fragment = NoResultsFragmentCompat.createInstance(document);
            // Show the No Results Screen's fragment
        } else {
            // Show the extractions
        }
    }

In case the document was not an image, the Gini Vision Library will simply return with empty results to the Activity that launched it.

Customizing the UI
~~~~

- Header text style
- Headline text style
- Tip text style
- Tip images
- Button color and text color
- Background color

For detailed customization options consult the Javadoc of the ``NoResultsActivity``.

Tablet Support
^^^^

The Gini Vision Library can be used on tablets, too. Some UI elements have been adapted to offer the best user experience for tablet users. Requirements and resources were also adapted.

You may skip to the `Quick Checklist`_ to get an overview of the steps required for supporting tablets.

Extraction Quality Considerations
~~~~

We recommend implementing checks on tablet hardware to ensure that devices meet the Gini Vision Libraries minimum recommended hardware specifications.

Many tablets with at least 8MP cameras don't have an LED flash (like the popular Samsung Galaxy Tab S2). Therefore we don't require flash for tablets. For this reason the extraction quality on those tablets might be lower compared to smartphones.

Hardware Requirements
~~~~

We disabled the camera flash requirement for tablets. Camera flash is not a standard feature for tablets and even some popular models like the Samsung Galaxy Tab S2 don't have an LED flash.

You can view the Gini Vision Library's hardware requirements `here <http://developer.gini.net/gini-vision-lib-android/javadoc/net/gini/android/vision/requirements/RequirementId.html>`_.

Supporting All Screen Orientations
~~~~

On tablets landscape orientations are also supported (smartphones are portrait only). 

Previously we recommended limiting the orientation to portrait for Activities extending the Screen API's abstract Activities and Activities hosting the Component API's Fragment. If you are updating from a previous version you should remove the portrait limitation. The Gini Vision Library limited the orientation to portrait by adding ``android:screenOrientation="portrait"`` to the Activities in earlier versions. This has been removed and you should also remove it from your Activities, too.

Please note that on orientation change Activites will be restarted and the listener methods will be invoked again on restart. You should make sure your Activity implementations handle additional listener method invocations gracefully on orientation change.

The Gini Vision Library Screen API Activities and Component API Fragments keep their internal state between orientation changes. We recommend you to check that your Activity implementations also maintain their state.

UI Considerations
++++

On tablets in landscape the Camera Screen's UI displays the camera trigger button on the right side of the screen. Users can reach the camera trigger more easily this way. The camera preview along with the document corner guides are shown in landscape to match the device's orientation.

Other UI elements on all the screens maintain their relative position and the screen layouts are scaled automatically to fit the current orientation.

Customizing Tablet Screens
~~~~

Tablet specific images are required only for the Camera Screen for tablets. The following images should be customized and added to your drawable resource folder with the ``sw600dp`` qualifier for mdpi, hdpi, xhdpi, xxhdpi and xxxhdpi (for ex. ``drawable-sw600dp-mdpi``):

* ``gv_onboarding_lighting.png`` - First onboarding page image
* ``gv_onboarding_flat.png`` - Second onboarding page image
* ``gv_onboarding_parallel.png`` - Third onboarding page image
* ``gv_onboarding_align.png`` - Fourth onboarding page image

These images are higher resolution versions of the same images that are used for phones.

Tablet Support Quick Checklist
~~~~

#. Remove portrait orientation limitation from your Activities like ``android:screenOrientation="portrait"``.
#. Remove manual LED flash requirement check, if used. For tablets we don't require an LED flash.
#. Handle multiple listener method invocations on Activity restarts due to orientation change.
#. Preserve state between orientation change related Activity restarts.
#. Customize the tablet specific images and add them to ``drawable-sw600dp-*`` resource folders for mdpi, hdpi, xhdpi, xxhdpi and xxxhdpi:

    * ``gv_onboarding_lighting.png`` - First onboarding page image
    * ``gv_onboarding_flat.png`` - Second onboarding page image
    * ``gv_onboarding_parallel.png`` - Third onboarding page image
    * ``gv_onboarding_align.png`` - Fourth onboarding page image