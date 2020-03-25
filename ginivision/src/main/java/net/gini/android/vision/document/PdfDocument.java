package net.gini.android.vision.document;


import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;

import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.util.IntentHelper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * <p>
 *     A document containing a PDF.
 * </p>
 *
 */
public class PdfDocument extends GiniVisionDocument {

    /**
     * Creates an instance using the resource pointed to by the Intent's Uri.
     *
     * @param intent an {@link Intent} containing a pdf {@link Uri}
     * @return new instance with the contents of the Intent's Uri
     * @throws IllegalArgumentException if the Intent's data is null
     */
    @NonNull
    static PdfDocument fromIntent(@NonNull final Intent intent,
            @NonNull final ImportMethod importMethod) {
        final Uri uri = IntentHelper.getUri(intent);
        if (uri == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        return new PdfDocument(intent, uri, Source.newExternalSource(), importMethod);
    }

    /**
     * Creates an instance from the provided Intent.
     * @param intent an Intent that must point to a PDF
     * @throws IllegalArgumentException if the Intent's data is null
     */
    @VisibleForTesting
    PdfDocument(@NonNull final Intent intent, @NonNull final Uri uri,
            @NonNull final Source source, @NonNull final ImportMethod importMethod) {
        super(Type.PDF, source, importMethod, MimeType.APPLICATION_PDF.asString(), null, intent,
                uri, false);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }

    /**
     * Internal use only.
     *
     * @suppress
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
     * Internal use only.
     *
     * @suppress
     */
    private PdfDocument(final Parcel in) {
        super(in);
    }
}
