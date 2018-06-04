Updating to 2.6.0
====

API Changes
----

We introduced some significant changes to the library API without breaking backwards compatibility.
These changes were required as the old API was not extensible and it limited us in what features we
could build without making the library integration too difficult. The new API allows us more
flexibility when adding new features while making the integration even easier as before.

We are keeping network code out of the Gini Vision Library in order to allow clients freedom in
choosing their desired network implementation. The multi-page feature required us to add more
network related logic to the library which would have made integration more difficult.

We decided to implement a plugin architecture and introduced a network layer with a single interface
called ``GiniVisionNetworkService``. This declares all the required network tasks and is used in all
the screens where documents have to be sent to the Gini API. The ``GiniVisionNetworkService``
implementation is outside of the scope of the Gini Vision Library and has to be specified before
launching the library.

We provide a default networking implementation in the Gini Vision Network Library. Adding this to
your app along with the Gini Vision Library will allow you to quickly integrate Gini invoice
scanning.

The multi-page feature requires the new API. Existing features will continue to work without any
changes, but new features we add in the future will require the new API.

Configuration
^^^^

We introduced the ``GiniVision`` class as the single entry-point for configuring the Gini Vision
Library. Previously configuration was done by setting Activity extras and using the
``GiniVisionFeatureConfiguration``.

Set Custom Onboarding Pages
~~~~~

2.6.0
____

.. code-block:: java

    GiniVision.newInstance()
        .setCustomOnboardingPages(getOnboardingPages())
        .build();

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);
    intent.putParcelableArrayListExtra(CameraActivity.EXTRA_IN_ONBOARDING_PAGES, myOnboardingPages());

Disable Showing Onboarding on the First Run
~~~~~

2.6.0
____

.. code-block:: java

    GiniVision.newInstance()
        .setShouldShowOnboardingAtFirstRun(false)
        .build();

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);
    intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);

Force Showing of the Onboarding
~~~~~

2.6.0
____

.. code-block:: java

    GiniVision.newInstance()
        .setShouldShowOnboarding(true)
        .build();

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);
    intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING, true);

Close Library on Pressing the Back Button From Any Activity in the Library
~~~~~

2.6.0
____

This option has been removed.

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);
    intent.putExtra(CameraActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, true);

Enable Document Import from the Camera Screen
~~~~

2.6.0
____

.. code-block:: java

    GiniVision.newInstance()
        .setDocumentImportEnabledFileTypes(DocumentImportEnabledFileTypes.PDF_AND_IMAGES)
        .build();

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);

    final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
            GiniVisionFeatureConfiguration.buildNewConfiguration()
                    .setDocumentImportEnabledFileTypes(DocumentImportEnabledFileTypes.PDF_AND_IMAGES)
                    .build();

    intent.putExtra(CameraActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
                giniVisionFeatureConfiguration);

Enable File Import ("open with")
~~~~

2.6.0
____

.. code-block:: java

    GiniVision.newInstance()
        .setFileImportEnabled(true)
        .build();

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);

    final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
            GiniVisionFeatureConfiguration.buildNewConfiguration()
                    .setFileImportEnabled(true)
                    .build();

    intent.putExtra(CameraActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
                giniVisionFeatureConfiguration);

Enable QRCode Scanning
~~~~

2.6.0
____

.. code-block:: java

    GiniVision.newInstance()
        .setQRCodeScanningEnabled(true)
        .build()

2.5.0 and older
____

.. code-block:: java

    Intent intent = new Intent(this, CameraActivity.class);

    final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
            GiniVisionFeatureConfiguration.buildNewConfiguration()
                    .setQRCodeScanningEnabled(true)
                    .build();

    intent.putExtra(CameraActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
                giniVisionFeatureConfiguration);

File Import
^^^^

We moved methods in ``GiniVisionFileImport`` to ``GiniVision`` in order to simplify interaction.
Starting from this version ``GiniVision`` is the go-to class for interaction with the Gini Vision
Library.

Create a Document from the Imported File
~~~~

When using the Component API you need to create a ``Document`` from the imported file and pass the
``Document`` either to the ``ReviewActivity`` (``ReviewFragment``) or the ``AnalysisActivity``
(``AnalysisFragment``).

2.6.0
____

.. code-block:: java

    GiniVision.createDocumentForImportedFile(...);

2.5.0 and older
____

.. code-block:: java

   GiniVisionFileImport.createDocumentForImportedFile(..);

Create an Intent from the Imported File
~~~~

When using the Screen API you only need to create an Intent from the imported file and start it.

2.6.0
____

.. code-block:: java

    GiniVision.createIntentForImportedFile(...);

2.5.0 and older
____

.. code-block:: java

   GiniVisionFileImport.createIntentForImportedFile(...);

Networking Integration
^^^^

We don't provide network code with the Gini Vision Library in order to allow clients to use their desired
networking implementation. In previous versions integration of network code was achieved either by
subclassing Activities and overriding methods or by implementing Fragment listeners.

Starting from this version we unified all required network tasks into the
``GiniVisionNetworkService`` interface. The Gini Vision Library uses this interface to request
document upload and analysis. We also added the ``GiniVisionNetworkApi`` to declare network tasks
which may be called by the client outside of the Gini Vision Library (e.g. for sending feedback).

When using the Screen API the extractions are returned to your app in the ``EXTRA_OUT_EXTRACTIONS``
Bundle in the ``CameraActivity``'s result Intent. This Bundle contains extraction label Strings as
keys and ``GiniVisionSpecificExtraction`` as values.

For the Component API the extractions are returned in the ``onExtractionsAvailable(Map<String,
GiniVisionSpecificExtraction>)`` method of the ``CameraFragmentListener``,
``ReviewFragmentListener`` or ``AnalysisFragmentListener``.

Gini Vision Network Library
~~~~

The Gini Vision Network Library provides a default implementation of the networking interfaces. By using
this library you can quickly integrate invoice scanning in your application.

To use it add the ``gini-vision-network-lib`` dependency to your app's ``build.gradle`` along with
the Gini Vision Library:

.. code-block:: groovy
    :emphasize-lines: 11

    repositories {
        ...
        maven {
            url 'https://repo.gini.net/nexus/content/repositories/open'
        }
    }

    dependencies {
        ...
        implementation 'net.gini:gini-vision-lib:2.6.0'
        implementation 'net.gini:gini-vision-network-lib:2.6.0'
    }

For the Gini Vision Library to be aware of the default implementations create the instances and pass
them to the builder of ``GiniVision``:

.. code-block:: java

    GiniVisionDefaultNetworkService networkService = 
        GiniVisionDefaultNetworkService.builder((Context) this)
            .setClientCredentials(myClientId, myClientSecret, myEmailDomain)
            .build();

    GiniVisionDefaultNetworkApi networkApi = 
        GiniVisionDefaultNetworkApi.builder()
            .withGiniVisionDefaultNetworkService(networkService)
            .build();

    GiniVision.newInstance()
        .setGiniVisionNetworkService(networkService)
        .setGiniVisionNetworkApi(networkApi)
        .build();

Subclassing the ``CameraActivity``, ``ReviewActivity`` and ``AnalysisActivity`` is not required
anymore. Likewise adding network code to your implementations of the ``CameraFragmentListener``,
``ReviewFragmentListener`` and ``AnalysisFragmentListener`` is not required. All related methods
have been deprecated, but will still be used, if there is no ``GiniVision`` instance.

.. warning::

    A ``GiniVision`` instance is required to use the new network integration API. Without a
    ``GiniVision`` instance the Gini Vision Library will fall back to the previous API and requires
    Activity subclasses or Fragment listeners for adding network calls.

Custom Networking Implementation
~~~~

You can also provide your own networking by implementing the ``GiniVisionNetworkService`` and the
``GiniVisionNetworkApi`` interfaces. Pass your instances to the builder of ``GiniVision`` as shown
above.

UI Changes
----

Camera Screen
^^^^

We added a subtitle to the document import button to make the purpose of the button more clear. You
can customize the text via the string resource named ``gv_camera_document_import_subtitle`` and the
text style via overriding the style named
``GiniVisionTheme.Camera.DocumentImportSubtitle.TextStyle`` (make sure to use the root parent style
``Root.GiniVisionTheme.Camera.DocumentImportSubtitle.TextStyle``).

Onboarding Screen
^^^^

We made the background opaque. Usability testing showed that the semi-transparent
background lead users to believe they should already perform the tips while in the onboarding.

Multi-Page Document Scanning
----

The API changes detailed above allowed us to introduce multi-page document scanning. With this
feature users can take or import pictures of invoice pages. The Gini API then analyzes
the pages as a single invoice and extracts the required payment information.

Requirements
^^^^

To use this feature you need to use ``GiniVision`` along with the new networking integration.

Enable Multi-Page
^^^^

Multi-page scanning is disabled by default. Enable it when building a new ``GiniVision`` instance:

.. code-block:: java

     GiniVision.newInstance()
                .setMultiPageEnabled(true)
                .build();

Camera Screen
^^^^

When multi-page is enabled, then taken or imported pictures are shown in an image stack in the bottom
right corner of the Camera Screen. When users tap the stack the Multi-Page Review Screen is
launched.

Customizing the UI
~~~~

- Images stack badge background and text style
- Images stack subtitle

For detailed customization options consult the Javadoc of the ``CameraActivity``.

Multi-Page Review Screen
^^^^

In this screen users can review their images. They can also reordered, rotate and delete them. Users
may add more pages by tapping on the "Add pages" button or going back to the Camera Screen.

The first time users take a picture the Multi-Page Review Screen is launched. Subsequent pictures
are added to the image stack in the Camera Screen and users can tap it to go to the Multi-Page
Review Screen.

Image uploads start as soon as users enter this screen. Upload activity indicators are shown on each
image thumbnail. If the uploads are successfull green checkmarks are displayed on the image
thumbnails. When all uploads were successfull the user can proceed to the Analysis Screen. If
uploads failed, then red crosses are shown on the image thumbnails and users may retry or delete the
failed images.

Customizing the UI
~~~~

- Page indicator
- Next button
- Background colors
- Thumbnail cards
- Thumbnail activity indicator
- Thumbnail upload success and failure icons
- Add page icon and subtitle
- Reorder pages tip
- Error message background color and text styles
- Delete last page confirmation dialog

For detailed customization options consult the Javadoc of the ``MultiPageReviewActivity``.

Importing Multiple Images
^^^^

To allow users to send multiple images (jpeg, png and gif) to the Gini Vision Library from other apps through
your app you need to register one of your Activities to receive multiple images. 

Registering to Receive Multiple Image Files
~~~~

Add the following intent filter to the Activity in your ``AndroidManifest.xml`` you wish to receive multiple incoming images:

.. code-block:: xml

    <activity android:name=".ui.MyActivity">
        <intent-filter android:label="@string/label_for_open_with">
            <action android:name="android.intent.action.VIEW" />
            <action android:name="android.intent.action.SEND" />
            <action android:name="android.intent.action.SEND_MULTIPLE" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:mimeType="image/*" />
        </intent-filter>
    </activity>

Importing multiple PDFs is not supported so the intent filter for PDFs must not contain the
``SEND_MULTIPLE`` action. Simply add a separate intent filter to be able to receive single PDF
files:

.. code-block:: xml

    <activity android:name=".ui.MyActivity">
        <intent-filter android:label="@string/label_for_open_with">
            <action android:name="android.intent.action.VIEW" />
            <action android:name="android.intent.action.SEND" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:mimeType="application/pdf" />
        </intent-filter>
    </activity>

Handling Imported Files
~~~~

When you use the multi-page feature you always have to call the new
``GiniVision.createIntentForImportedFiles()`` method or if you use the Component API the
``GiniVision.createDocumentForImportedFiles()`` method.

.. Note::

    You don't have to check whether the user imported one or multiple files. Simply use
    ``GiniVision.createIntentForImportedFiles()`` or ``GiniVision.createDocumentForImportedFiles()``
    to handle one or more incoming files. 

When your app is requested to handle one or multiple images or a PDF your Activity (declaring the intent filter
shown before) is launched or resumed (``onNewIntent(Intent)``) with an Intent having ``ACTION_VIEW``,
``ACTION_SEND`` or ``ACTION_SEND_MULTIPLE``.

.. important::

    To make sure your application can read the shared file declare and request the
    ``READ_EXTERNAL_STORAGE`` permission before accessing the ``Uri`` or before starting the Gini
    Vision Library.

Checking whether the Intent has the required action:

.. code-block:: java

    String action = intent.getAction();
    if (Intent.ACTION_VIEW.equals(action) 
        || Intent.ACTION_SEND.equals(action)
        || Intent.ACTION_SEND_MULTIPLE.equals(action)) {
        ...
    }

Using the Screen API, create an Intent for launching the Gini Vision Library with
``GiniVisionFileImport.createIntentForImportedFiles()``. This method requires a callback with which
it will notify your app about the outcome of the import process. Since importing multiple files can
take some seconds (images are processed and compressed) you should show an activity indicator until
one of the callback methods is invoked.

.. code-block:: java

    // Token to request cancellation of the file import
    private CancellationToken mFileImportCancellationToken;

    void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        showActivityIndicator();
        mFileImportCancellationToken = GiniVision.getInstance().createIntentForImportedFiles(
                    importedFileIntent, this,
                    new AsyncCallback<Intent, ImportedFileValidationException>() {
                        @Override
                        public void onSuccess(final Intent result) {
                            mFileImportCancellationToken = null;
                            hideActivityIndicator();
                            startActivityForResult(result, REQUEST_SCAN);
                        }

                        @Override
                        public void onError(final ImportedFileValidationException exception) {
                            mFileImportCancellationToken = null;
                            hideActivityIndicator();
                            String message = "File cannot be analyzed";
                            if (exception.getValidationError() != null) {
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

                        @Override
                        public void onCancelled() {
                            mFileImportCancellationToken = null;
                            hideActivityIndicator();
                        }
                    });
    }

The returned Intent will launch either the MultiPageReviewActivity or the AnalysisActivity (the one
from the Gini Vision Library - subclassing the AnalysisActivity is not required anymore). For
example PDFs cannot be reviewed by the user and for those the AnalysisActivity is launched.

For the Component API, create a ``Document`` with
``GiniVisionFileImport.createDocumentForImportedFiles()``. Like the previous method this one
requires a callback, too. You should show an activity indicator until one of the callback methods is
invoked.

The MultiPageReviewFragment may only be used with reviewable documents. Therefore, it is important
to check whether the document is reviewable or not:

.. code-block:: java

    // Token to request cancellation of the file import
    private CancellationToken mFileImportCancellationToken;

    void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        showActivityIndicator();
        mFileImportCancellationToken = GiniVision.getInstance().createDocumentForImportedFiles(
                    importedFileIntent, mActivity,
                    new AsyncCallback<Document, ImportedFileValidationException>() {
                        @Override
                        public void onSuccess(@NonNull final Document result) {
                            if (result.isReviewable()) {
                                launchMultiPageReviewScreen();
                            } else {
                                launchAnalysisScreen(result);
                            }
                            mActivity.finish();
                        }

                        @Override
                        public void onError(
                                @NonNull final ImportedFileValidationException exception) {
                            mFileImportCancellationToken = null;
                            hideActivityIndicator();
                            String message = "File cannot be analyzed";
                            if (exception.getValidationError() != null) {
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

                        @Override
                        public void onCancelled() {

                        }
                    });
    }

