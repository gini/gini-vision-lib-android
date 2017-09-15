package net.gini.android.vision.document;


import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionDocument;
import net.gini.android.vision.internal.util.IntentHelper;

import java.io.IOException;

public class PdfDocument extends GiniVisionDocument {

    /**
     * Creates an instance using the resource pointed to by the Intent's Uri.
     *
     * @param intent an {@link Intent} containing a pdf {@link Uri}
     * @param context Android context
     * @return new instance with the contents of the Intent's Uri
     * @throws IOException              if there is an issue with the input stream from the Uri
     * @throws IllegalArgumentException if the Intent's data is null
     * @throws IllegalStateException    if null input stream was returned by the Context's Content
     *                                  Resolver
     */
    @NonNull
    public static PdfDocument fromIntent(final Intent intent, final Context context) throws IOException {
        final byte[] fileData = IntentHelper.getBytesFromIntentUri(intent, context);
        return new PdfDocument(fileData);
    }

    protected PdfDocument(@NonNull final byte[] data) {
        super(Type.PDF, data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }

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

    private PdfDocument(Parcel in) {
        super(in);
    }
}
