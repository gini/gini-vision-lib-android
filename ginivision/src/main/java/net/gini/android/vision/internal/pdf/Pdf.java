package net.gini.android.vision.internal.pdf;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
public class Pdf implements Parcelable {

    // Default preview size is set to be tolerably small and has a DIN A4 aspect ratio
    static final int DEFAULT_PREVIEW_HEIGHT = 1500;
    static final int DEFAULT_PREVIEW_WIDTH = 1080;

    private final Uri mUri;

    public static Pdf fromDocument(@NonNull PdfDocument document) {
        return new Pdf(document.getUri());
    }

    public static Pdf fromUri(@NonNull Uri uri) {
        return new Pdf(uri);
    }

    private Pdf(@NonNull final Uri uri) {
        mUri = uri;
    }

    public void toBitmap(@NonNull Size targetSize, @NonNull final Context context,
            @NonNull final Renderer.Callback callback) {
        final Renderer renderer = getRenderer(context);
        renderer.toBitmap(targetSize, callback);
    }

    public int getPageCount(@NonNull final Context context) {
        final Renderer renderer = getRenderer(context);
        return renderer.getPageCount();
    }

    private Renderer getRenderer(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RendererLollipop(mUri, context);
        }
        return new RendererPreLollipop();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(mUri, flags);
    }

    public static final Creator<Pdf> CREATOR = new Creator<Pdf>() {
        @Override
        public Pdf createFromParcel(Parcel in) {
            return new Pdf(in);
        }

        @Override
        public Pdf[] newArray(int size) {
            return new Pdf[size];
        }
    };

    private Pdf(Parcel in) {
        mUri = in.readParcelable(Uri.class.getClassLoader());
    }
}
