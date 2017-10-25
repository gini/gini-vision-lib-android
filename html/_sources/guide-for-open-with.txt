How to Upload PDFs and Images (pre-2.4.0)
=============================

Since version 2.4.0 the :ref:`File Import` feature should be used.

General considerations
----------------------

Enabling your app to open PDFs and images allows your users to open any kind of files which are identified by the OS as PDFs or images. Make sure to check the MIME type and that the file size is below an acceptable threshold (5MB for example). It is also advisable to check the first bytes of the incoming files to determine their type and allow only PDFs and known image types.

Registering PDF and image file types
------------------------------------

Add the following intent filter to the *Activity* in your *AndroidManifest.xml* you wish to receive incoming PDFs and images:

.. code-block:: xml

    <activity android:name=".ui.MyActivity">
        <intent-filter android:label="@string/app_name">
            <action android:name="android.intent.action.VIEW" />
            <action android:name="android.intent.action.SEND" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:mimeType="image/*" />
            <data android:mimeType="application/pdf" />
        </intent-filter>
    </activity>

.. note::

    We recommend adding `ACTION_VIEW <https://developer.android.com/reference/android/content/Intent.html#ACTION_VIEW>`_ to the intent filter to also allow users to send PDFs and images to your app from apps which don’t implement sharing with `ACTION_SEND <https://developer.android.com/reference/android/content/Intent.html#ACTION_SEND>`_ but enable viewing the PDF or file with other apps.

Documentation
^^^^^^^^^^^^^

- https://developer.android.com/training/basics/intents/filters.html

Reading files from external storage
-----------------------------------

Since Android 4.4 (API Level 19) the `READ_EXTERNAL_STORAGE <https://developer.android.com/reference/android/Manifest.permission.html#READ_EXTERNAL_STORAGE>`_ permission has to be declared to access files outside of the app-specific directories. Declare this permission in your *AndroidManifest.xml*:

.. code-block:: xml

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 
Since the introduction of `Run Time Permissions <https://developer.android.com/training/permissions/requesting.html>`_ with Android 6.0 (API Level 23) your app also has to request permission from the user before accessing a PDF or an image file outside of the app-specific directories. 
 
We recommend checking for the permission whenever your app receives a PDF or an image and before accessing it. If an explanation is requested by the system to be shown to the user we highly recommend providing one.
 
It is required to handle the case where the user did not grant permissions. We recommend showing an explanation and providing a link to your app’s *App info* page in the *Settings* app with a short guide on how to grant the permission there. 
 
The Intent for opening the *App info* page can be created as follows:

.. code-block:: java

    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts(Uri."package", BuildConfig.APPLICATION_ID, null);
    intent.setData(uri);
    startActivity(intent);

Documentation
^^^^^^^^^^^^^

- https://developer.android.com/training/permissions/index.html

Handling incoming PDFs and images
---------------------------------

When your app is requested to handle a PDF or an image your *Activity* (declaring the intent filter we mentioned previously) is launched or resumed with an Intent having ``ACTION_VIEW`` or ``ACTION_SEND``.

Checking whether the Intent has the required action:

.. code-block:: java

    String action = intent.getAction();
    if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action)) {
        ...
    }
 
The Intent will contain an ``Uri`` pointing to the PDF or image. The ``Uri`` can be in the ``data`` field or since Android 4.1 (API Level 16) it can also be in the ``clipData`` field. 

.. note:: 

    To also support `ACTION_VIEW <https://developer.android.com/reference/android/content/Intent.html#ACTION_VIEW>`_ we recommend using ``data`` and ``clipData``. The `Androd example <https://developer.android.com/training/sharing/receive.html>`_ uses ``intent.getParcelableExtra(Intent.EXTRA_STREAM)`` to extract the ``Uri``, but this works only with `ACTION_SEND <https://developer.android.com/reference/android/content/Intent.html#ACTION_SEND>`_. 

Getting the ``Uri`` from the Intent:

.. code-block:: java

    Uri uri = intent.getData();
    if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        ClipData clipData = intent.getClipData();
        if (clipData != null && clipData.getItemCount() > 0) {
            uri = clipData.getItemAt(0).getUri();
        }
    }

We recommend checking the MIME type of Intent. The MIME type can be in the ``type`` field or since Android 4.1 (API Level 16) it can also be in the ``clipData`` field.
 
Getting the MIME type from the Intent:

.. code-block:: java

    List<String> mimeTypes = new ArrayList<>();
    String type = context.getContentResolver().getType(data);
    if (type == null) {
        type = intent.getType();
    }
    if (type == null) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
    }
    if (type != null) {
        mimeTypes.add(type);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        ClipData clipData = intent.getClipData();
        if (clipData != null) {
            ClipDescription description = clipData.getDescription();
            for (int i = 0; i < description.getMimeTypeCount(); i++) {
                type = description.getMimeType(i);
                mimeTypes.add(type);
            }
        }
    }

Having the ``Uri``, the contents of the PDF or image files can be read using the ``ContentResolver``.
 
Reading the PDF or image into a byte array:

.. code-block:: java

    byte[] bytes = null;
    ContentResolver contentResolver = activity.getContentResolver()
    InputStream inputStream = null;
    try {
        inputStream = contentResolver.openInputStream(uri);
        if (inputStream != null) {
            // ByteStreams is a utility class from Google’s Guava library
            bytes = ByteStreams.toByteArray(inputStream);
        }
    } finally {
        if (inputStream != null) {
            inputStream.close();
        }
    }

The byte array from the example above can be directly uploaded to the Gini API for information extraction.

Documentation
^^^^^^^^^^^^^

- Android: https://developer.android.com/training/sharing/receive.html
- Gini API: http://developer.gini.net/gini-api/html/documents.html#submitting-files
- Gini API SDK: http://developer.gini.net/gini-sdk-android/guides/common-tasks.html#upload-a-document

Showing a preview of the PDF’s first page
-----------------------------------------

We recommend showing a preview of the PDF’s first page or of the image while the document is being analyzed. Rendering PDFs is possible since Android 5.0 (API Level 21). On older versions we recommend showing a placeholder image.
 
The following code shows how to generate a preview of the PDF’s first page on Android 5.0 and newer versions:

.. code-block:: java

    Bitmap bitmap = null;
    ContentResolver contentResolver = activity.getContentResolver();
    ParcelFileDescriptor fileDescriptor = contentResolver.openFileDescriptor(uri, "r");
    if (fileDescriptor != null) {
        PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
        if (pdfRenderer != null) {
            if (pdfRenderer.getPageCount() > 0) {
                PdfRenderer.Page page = pdfRenderer.openPage(0);
                // Set the width and height based on the desired preview size 
                // and the aspect ratio of the pdf page
                int bitmapWidth = …;
                int bitmapHeight =  …;
                // Create a white bitmap to make sure that PDFs without 
                // a background color are rendered on a white background
                int[] colors = createWhiteColorArray(bitmapWidth, bitmapHeight);
                bitmap = Bitmap.createBitmap(colors, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
            }
    }

Documentation
^^^^^^^^^^^^^

- https://developer.android.com/reference/android/graphics/pdf/PdfRenderer.html


