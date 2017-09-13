package net.gini.android.vision;

import android.os.Parcelable;
import android.support.annotation.NonNull;


/**
 * <p>
 * This class is the container for transferring the image of a document as a JPEG between the client
 * application and
 * the
 * Gini Vision Library and between the Fragments of the Gini Vision Library.
 * </p>
 *
 * <p>
 * Due to the size limitations of the {@link android.os.Bundle}, the JPEG has to be stored in a
 * memory cache when
 * parceling and read from the cache when unparceling.
 * </p>
 *
 * <p>
 * <b>Warning:</b> Always retrieve the {@link Document} extras from a Bundle to force unparceling
 * and removing of the
 * reference to
 * the JPEG byte array from the memory cache. Failing to do so will lead to memory leaks.
 * </p>
 */
public interface Document extends Parcelable {

    /**
     * <p>
     * The image of a document as a JPEG.
     * </p>
     *
     * @return a byte array containg a JPEG
     * @deprecated Use {@link Document#getData()} instead.
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
     * instead, if {@link Document#getType()} is {@link Document.Type#IMAGE} instead.
     */
    @Deprecated
    int getRotationForDisplay();

    /**
     * Find out the concrete document type.
     *
     * @return the document type
     */
    Type getType();

    /**
     * <p>
     * The contents of a document.
     * </p>
     *
     * @return a byte array containing one of the supported document types
     */
    @NonNull
    byte[] getData();

    enum Type {
        IMAGE,
        PDF
    }
}
