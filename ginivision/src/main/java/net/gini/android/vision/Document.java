package net.gini.android.vision;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


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
     * The image of a document as a JPEG.
     *
     * @return a byte array containg a JPEG
     * @deprecated Use {@link Document#getData()} instead. This method might return a byte array
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
     * @deprecated Use
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
        IMAGE_MULTI_PAGE
    }
}
