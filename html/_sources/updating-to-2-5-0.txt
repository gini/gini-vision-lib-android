Updating to 2.5.0
====

Google Play Services
----

With version 2.5.0 we added the Google Mobile Vision API dependency which is part of the Google Play Services. We only depend on the ``play-services-vision`` package.

In case you already use the Google Mobile Vision API there might be a conflict related to the ``<meta-data android:name="com.google.android.gms.vision.DEPENDENCIES">`` tag after updating to 2.5.0. If this is the case add the attribute ``tools:replace="android:value"``. Make sure that you add ``barcode`` to the ``android:value`` tag.

The following snippet shows how to update the tag, if the app has been using text recognition before updating to 2.5.0: 

.. code-block:: xml

    <meta-data
        android:name="com.google.android.gms.vision.DEPENDENCIES"
        tools:replace="android:value"
        android:value="ocr,barcode"/>

QR Code Scanning
----

By using the Google Mobile Vision API the GVL can read payment data from QR Codes. We support the `BezahlCode <http://www.bezahlcode.de/>`_ and `EPC069-12 <https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation>`_ (`Stuzza (AT) <https://www.stuzza.at/de/zahlungsverkehr/qr-code.html>`_ and `GiroCode (DE) <https://www.girocode.de/rechnungsempfaenger/>`_) formats.

When a supported QR Code is detected and read with valid payment data a popup is shown in the Camera Screen. The user may tap the popup to use the payment data directly without the need to analyse the document.

QR Code Scanning is available on devices running Android 4.2.2 Gingerbread or later with Google Play Services installed.

Enable QR Code Scanning
^^^^

This feature is disabled by default. To enable the QR Code scanning using the Screen API pass a ``GiniVisionFeatureConfiguration`` to the ``CameraActivity`` with QR Code scanning set as enabled:

.. code-block:: java

    // Enable QR Code scanning
    final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
            GiniVisionFeatureConfiguration.buildNewConfiguration()
                    .setQRCodeScanningEnabled(true)
                    .build();
    intent.putExtra(CameraActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
            giniVisionFeatureConfiguration);

For the Component API use the factory method of the ``CameraFragmentCompat`` or ``CameraFragmentStandard``:

.. code-block:: java

    // Enable QR Code scanning
    final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
            GiniVisionFeatureConfiguration.buildNewConfiguration()
                    .setQRCodeScanningEnabled(true)
                    .build();
    CameraFragmentCompat.createInstance(giniVisionFeatureConfiguration);

.. important::

    When your application is installed Google Mobile Services will download libraries to the device in order to do QR Code detection. If another app already uses QR Code detection on the device the library won't be downloaded again. Under certain circumstances (user not online, slow connection or lack of sufficient storage space) the libraries will not be ready at the time your app starts the Camera Screen and QR Code detection will be silently disabled until the next time the Camera Screen starts.

Handle the QR Code
^^^^

After the user tapped on the QR Code detected popup the ``CameraFragmentListener#onQRCodeAvailable(QRCodeDocument)`` method is invoked. In this method you should upload the ``QRCodeDocument`` to the Gini API, retrieve the extractions and exit the Gini Vision Library to use the payment data in your application. You should also send feedback for the QR Codes. Basically you need to execute the same steps as for images, but instead of uploading an image you upload the contents of the QRCodeDocument.

Using the Screen API extend the ``CameraActivity`` and override the ``onQRCodeAvailable()`` method where you can make use of the new methods in the ``CameraActivity`` to show/hide an activity indicator and to show an error snackbar. The new methods are shown on highlighted lines.

.. code-block:: java
    :emphasize-lines: 4,36,39

    @Override
    public void onQRCodeAvailable(@NonNull final QRCodeDocument qrCodeDocument) {
        // Show an activity indicator on the Camera Screen
        showActivityIndicatorAndDisableInteraction();
        // Upload the contents of the QRCodeDocument using 
        // the Gini API SDK (http://developer.gini.net/gini-sdk-android/)
        mDocumentTaskManager
            .createDocument(qrCodeDocument.getData(), null, null)
            .onSuccessTask(
                    new Continuation<net.gini.android.models.Document, Task<net.gini.android.models.Document>>() {
                        @Override
                        public Task<net.gini.android.models.Document> then(
                                Task<net.gini.android.models.Document> task)
                                throws Exception {
                            net.gini.android.models.Document giniDocument = task.getResult();
                            return mDocumentTaskManager.pollDocument(giniDocument);
                        }
                    })
            .onSuccessTask(
                    new Continuation<net.gini.android.models.Document, Task<Map<String, SpecificExtraction>>>() {
                        @Override
                        public Task<Map<String, SpecificExtraction>> then(
                                Task<net.gini.android.models.Document> task)
                                throws Exception {
                            net.gini.android.models.Document giniDocument = task.getResult();
                            return mDocumentTaskManager.getExtractions(giniDocument);
                        }
                    })
            .continueWith(
                    new Continuation<Map<String, SpecificExtraction>, Map<String, SpecificExtraction>>() {
                        @Override
                        public Map<String, SpecificExtraction> then(
                                final Task<Map<String, SpecificExtraction>> task)
                                throws Exception {
                            // Hide the activity indicator
                            hideActivityIndicatorAndEnableInteraction();
                            // Show an error if something went wrong
                            if (task.isFaulted()) {
                                showError("Could not use the QR Code. Try again or take a picture of your document.", 4000);
                                return null;
                            }
                            // Add the extractions to the activity's result and finish with RESULT_OK
                            Map<String, SpecificExtraction> extractions = task.getResult();
                            final Intent result = new Intent();
                            final Bundle extractionsBundle = getExtractionsBundle(extractions);
                            result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
                            setResult(RESULT_OK, result);
                            finish();
                            return null;
                        }
                    });
    }

With the Component API you implement the ``onQRCodeAvailable()`` in your ``CameraFragmentListener`` implementation. The ``CameraFragmentInterface`` contains the same methods as above to show/hide an activity indicator and to show an error snackbar.

Customizing the UI
^^^^

For costumizing the QR Code popup consult the Javadoc of the ``CameraActivity``.
