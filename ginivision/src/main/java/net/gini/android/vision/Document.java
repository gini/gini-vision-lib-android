package net.gini.android.vision;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * This class is the container for transferring documents between the client application and the
 * Gini Vision Library and between the Fragments of the Gini Vision Library.
 * <p>
 * Due to the size limitations of the {@link android.os.Bundle}, the document data byte array has to
 * be stored in a memory cache when parceling and read from the cache when unparceling.
 * <p>
 * <b>Warning:</b> Always retrieve the {@link Document} extras from a Bundle to force unparceling
 * and removing of the reference to the byte array from the memory cache. Failing to do so will lead
 * to memory leaks.
 */
public interface Document extends Parcelable {

    /**
     * Retrieve the document's unique id.
     *
     * @return a unique id
     */
    @NonNull
    String getId();

    /**
     * The image of a document as a JPEG.
     *
     * @return a byte array containg a JPEG
     * @Deprecated Use {@link Document#getData()} instead. This method might return a byte array
     * containing other types, like PDFs.
     * <p>
     * To check if the byte array contains an image query the type with {@link Document#getType()}
     * and check if it equals {@link Document.Type#IMAGE}.
     */
    @Deprecated
    @NonNull
    byte[] getJpeg();

    /**
     * <p>
     * The amount of clockwise rotation needed to display the image in the correct orientation.
     * </p>
     * <p>
     * Degrees are positive and multiples of 90.
     * </p>
     *
     * @return degrees by which the image should be rotated clockwise before displaying
     * @Deprecated Use
     * {@link net.gini.android.vision.document.ImageDocument#getRotationForDisplay()}
     * instead, if {@link Document#getType()} equals {@link Document.Type#IMAGE}.
     */
    @Deprecated
    int getRotationForDisplay();

    /**
     * Get the concrete document type.
     *
     * @return the document type
     */
    Type getType();

    /**
     * Retrieve the document's mime type (media type).
     *
     * @return mime type string
     */
    String getMimeType();

    /**
     * <p>
     * The contents of a document, if the document was loaded into memory.
     * </p>
     * <p>
     *     For photos captured with the camera or for QR Codes this is never null.
     * </p>
     * <p>
     *     If {@link Document#isImported()} is {@code true} then this might be null. If it's null you can use {@link Document#getIntent()} and access the contents using the Intent.
     * </p>
     *
     * @return a byte array containing one of the supported document types
     */
    @Nullable
    byte[] getData();

    /**
     * <p>
     * The {@link Intent} with which the imported document was received.
     * </p>
     *
     * @return the {@link Intent} of the imported document
     */
    @Nullable
    Intent getIntent();

    /**
     * <p>
     *     The {@link Uri} of the imported document.
     * </p>
     *
     * @return {@link Uri}
     */
    @Nullable
    Uri getUri();

    /**
     * <p> Document is imported if it was picked from another app from the Camera Screen's document
     * upload button or if a file was passed to the Gini Vision Library through the client
     * application from another app. </p>
     *
     * @return {@code true} if the document was imported
     */
    boolean isImported();

    /**
     * Retrieve with which method the document has been imported.
     *
     * @return the document's {@link ImportMethod}
     */
    @NonNull
    ImportMethod getImportMethod();

    /**
     * Retrieve from which source the document originates from.
     *
     * @return the document's {@link Source}
     */
    @NonNull
    Source getSource();

    /**
     * <p>
     * Documents like PDFs are not reviewable and can be passed directly to the Analysis Screen.
     * Reviewable documents have to be shown in the Review Screen first before passing it on to the
     * Analysis Screen.
     * </p>
     *
     * @return {@code true} if the document can be reviewed in the Review Screen otherwise the
     * document has to be passed directly to the Analysis Screen
     */
    boolean isReviewable();

    /**
     * Supported document types.
     */
    enum Type {
        /**
         * The document is an image of type jpeg, png or gif.
         */
        IMAGE,
        /**
         * The document is a PDF.
         */
        PDF,
        /**
         * The document is a payment QR Code.
         */
        QRCode,
        /**
         * The document contains multiple images.
         */
        IMAGE_MULTI_PAGE,
        /**
         * The document contains multiple QR Codes.
         */
        QR_CODE_MULTI_PAGE,
        /**
         * The document contains multiple PDFs.
         */
        PDF_MULTI_PAGE
    }

    /**
     * Enum of supported methods for importing documents.
     */
    enum ImportMethod {
        /**
         * Document was sent from another app. It was "opened with" the app containing the Gini Vision Library.
         */
        OPEN_WITH("openwith"),
        /**
         * Document was picked by using the document import button in the Camera Screen.
         */
        PICKER("picker"),
        /**
         * Document was not imported.
         */
        NONE("");

        private static final Map<String, ImportMethod> sLookup = new HashMap<>();

        static {
            for (final ImportMethod importMethod : ImportMethod.values()) {
                sLookup.put(importMethod.asString(), importMethod);
            }
        }

        public static ImportMethod forName(@NonNull final String name) {
            if (sLookup.containsKey(name)) {
                return sLookup.get(name);
            }
            return ImportMethod.NONE;
        }

        private final String mName;

        ImportMethod(final String name) {
            mName = name;
        }

        public String asString() {
            return mName;
        }
    }

    /**
     * Source of the document (e.g. camera or external).
     */
    class Source implements Parcelable {

        private final String mName;

        /**
         * Create a new camera source.
         *
         * @return a camera {@link Source} instance
         */
        public static Source newCameraSource() {
            return new Source("camera");
        }

        /**
         * Create a new external source.
         *
         * @return an external {@link Source} instance
         */
        public static Source newExternalSource() {
            return new Source("external");
        }

        /**
         * Create a new custom source.
         *
         * @return a custom {@link Source} instance
         */
        public static Source newSource(@NonNull final String name) {
            return new Source(name);
        }

        /**
         * Create a new unknown source.
         *
         * @return an unknown {@link Source} instance
         */
        public static Source newUnknownSource() {
            return new Source("");
        }

        private Source(@NonNull final String name) {
            mName = name;
        }

        protected Source(final Parcel in) {
            mName = in.readString();
        }

        public String getName() {
            return mName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Source that = (Source) o;

            return mName.equals(that.mName);
        }

        @Override
        public int hashCode() {
            return mName.hashCode();
        }

        public static final Creator<Source> CREATOR = new Creator<Source>() {
            @Override
            public Source createFromParcel(final Parcel in) {
                return new Source(in);
            }

            @Override
            public Source[] newArray(final int size) {
                return new Source[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull final Parcel dest, final int flags) {
            dest.writeString(mName);
        }
    }
}
