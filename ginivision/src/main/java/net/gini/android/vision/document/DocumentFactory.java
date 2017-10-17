package net.gini.android.vision.document;

import static net.gini.android.vision.internal.util.IntentHelper.getUri;
import static net.gini.android.vision.internal.util.IntentHelper.hasMimeType;
import static net.gini.android.vision.internal.util.IntentHelper.hasMimeTypeWithPrefix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.internal.camera.photo.Photo;

/**
 * @exclude
 */
public final class DocumentFactory {

    /**
     * @param intent  an {@link Intent} containing an image or a pdf {@link Uri}
     * @param context Android context
     * @return new {@link Document} instance with the contents of the Intent's Uri
     * @throws IllegalArgumentException if the Intent's data is null or the mime type is unknown
     */
    @NonNull
    public static GiniVisionDocument newDocumentFromIntent(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final String importMethod) {
        final Uri data = getUri(intent);
        if (data == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        if (hasMimeType(intent, context, "application/pdf")) {
            return PdfDocument.fromIntent(intent);
        } else if (hasMimeTypeWithPrefix(intent, context, "image/")) {
            return ImageDocument.fromIntent(intent, context, deviceOrientation, deviceType,
                    importMethod);
        }
        throw new IllegalArgumentException("Unknown Intent Uri mime type.");
    }

    public static GiniVisionDocument newDocumentFromPhoto(@NonNull final Photo photo) {
        return ImageDocument.fromPhoto(photo);
    }

    public static GiniVisionDocument newDocumentFromPhotoAndDocument(@NonNull final Photo photo,
            @NonNull final Document document) {
        return ImageDocument.fromPhotoAndDocument(photo, document);
    }
}
