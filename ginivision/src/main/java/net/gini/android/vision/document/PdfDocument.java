package net.gini.android.vision.document;


import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;

import net.gini.android.vision.util.IntentHelper;

/**
 * <p>
 *     A document containing a PDF.
 * </p>
 *
 */
public final class PdfDocument extends GiniVisionDocument {

    /**
     * Creates an instance using the resource pointed to by the Intent's Uri.
     *
     * @param intent an {@link Intent} containing a pdf {@link Uri}
     * @return new instance with the contents of the Intent's Uri
     * @throws IllegalArgumentException if the Intent's data is null
     */
    @NonNull
    static PdfDocument fromIntent(final Intent intent) {
        final Uri uri = IntentHelper.getUri(intent);
        if (uri == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        return new PdfDocument(intent, uri);
    }

    /**
     * Creates an instance from the provided Intent.
     * @param intent an Intent that must point to a PDF
     * @throws IllegalArgumentException if the Intent's data is null
     */
    private PdfDocument(@NonNull final Intent intent, @NonNull final Uri uri) {
        super(Type.PDF, null, intent, uri, false, true);
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
    }

    /**
     * @exclude
     */
    public static final Creator<PdfDocument> CREATOR = new Creator<PdfDocument>() {
        @Override
        public PdfDocument createFromParcel(final Parcel in) {
            return new PdfDocument(in);
        }

        @Override
        public PdfDocument[] newArray(final int size) {
            return new PdfDocument[size];
        }
    };

    /**
     * @exclude
     */
    private PdfDocument(final Parcel in) {
        super(in);
    }
}
