package net.gini.android.vision.document;


import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.util.IntentHelper;

/**
 * <p>
 *     A document containing a PDF.
 * </p>
 *
 */
public class PdfDocument extends GiniVisionDocument {

    private final Uri mUri;

    /**
     * Creates an instance using the resource pointed to by the Intent's Uri.
     *
     * @param intent an {@link Intent} containing a pdf {@link Uri}
     * @return new instance with the contents of the Intent's Uri
     * @throws IllegalArgumentException if the Intent's data is null
     */
    @NonNull
    static PdfDocument fromIntent(final Intent intent) {
        return new PdfDocument(intent);
    }

    /**
     * Creates an instance from the provided Intent.
     * @param intent an Intent that must point to a PDF
     * @throws IllegalArgumentException if the Intent's data is null
     */
    private PdfDocument(@NonNull final Intent intent) {
        super(Type.PDF, null, intent, false, true);
        mUri = IntentHelper.getUri(intent);
        if (mUri == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
    }

    /**
     * <p>
     *     Retrieve the Uri of the PDF.
     * </p>
     *
     * @return the Uri pointing to the PDF
     */
    @NonNull
    public Uri getUri() {
        return mUri;
    }

    /**
     * @exclude
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @exclude
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mUri, flags);
    }

    /**
     * @exclude
     */
    public static final Creator<PdfDocument> CREATOR = new Creator<PdfDocument>() {
        @Override
        public PdfDocument createFromParcel(Parcel in) {
            return new PdfDocument(in);
        }

        @Override
        public PdfDocument[] newArray(int size) {
            return new PdfDocument[size];
        }
    };

    /**
     * @exclude
     */
    private PdfDocument(Parcel in) {
        super(in);
        mUri = in.readParcelable(Uri.class.getClassLoader());
    }
}
