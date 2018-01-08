Updating to 2.5.0
====

Google Play Services
----

With version 2.5.0 we added the Google Mobile Vision API dependency which is part of the Google Play Services. We only depend on the ``play-services-vision`` package.

In case you already use the Google Mobile Vision API there might be a conflict related to the ``<meta-data android:name="com.google.android.gms.vision.DEPENDENCIES">`` tag after updating to 2.5.0. If this is the case add the attribute ``tools:replace="android:value"``. Make sure that the value contains ``barcode``:

.. code-block:: xml

    <meta-data
        android:name="com.google.android.gms.vision.DEPENDENCIES"
        tools:replace="android:value"
        android:value="face,barcode"/>

QR Code Scanning
----

By using the Google Mobile Vision API the GVL can read payment data from QR Codes. We support the `BezahlCode <http://www.bezahlcode.de/>`_ and `EPC069-12 <https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation>`_ (`Stuzza (AT) <https://www.stuzza.at/de/zahlungsverkehr/qr-code.html>`_ and `GiroCode (DE) <https://www.girocode.de/rechnungsempfaenger/>`_) formats.

When a supported QR Code is detected and read with valid payment data a popup is shown in the Camera Screen. The user may tap the popup to use the payment data directly without the need to analyse the document.

QR Code Scanning is available on devices running Android 4.2.2 Gingerbread or later.

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

    When your application is installed Google Mobile Services will download libraries to the device in order to do QR Code detection. Under certain circumstances (user not online, slow connection or lack of sufficient storage space) the libraries will not be ready at the time your app starts the Camera Screen and QR Code detection will be silently disabled until the next time the Camera Screen starts.

Handle the Payment Data
^^^^

After the user tapped on the QR Code detected popup the ``CameraFragmentListener#onPaymentDataAvailable(PaymentData)`` method is invoked. In this method you can do additional checks on the payment data and exit the Gini Vision Library to use the payment data in your application.

Using the Screen API extend the ``CameraActivity`` and override the ``onPaymentDataAvailable()`` method.

.. code-block:: java

    @Override
    public void onPaymentDataAvailable(@NonNull final PaymentData paymentData) {
        // Start your activity with the payment data
        final Bundle paymentDataBundle = createPaymentDataBundle(paymentData);
        final Intent intent = new Intent(this, MyTransferActivity.class);
        intent.putExtra(MyTransferActivity.PREFILL_DATA, paymentDataBundle);
        startActivity(intent);
        // Finish the CameraActivity with RESULT_OK
        setResult(Activity.RESULT_OK);
        finish();
    }

With the Component API the only difference is, that you implement the ``onPaymentDataAvailable()`` in your ``CameraFragmentListener`` implementation.

Customizing the UI
^^^^

For costumizing the QR Code popup consult the Javadoc of the ``CameraActivity``.
