package net.gini.android.vision.document;

import static net.gini.android.vision.util.IntentHelper.getUri;
import static net.gini.android.vision.util.IntentHelper.hasMimeType;
import static net.gini.android.vision.util.IntentHelper.hasMimeTypeWithPrefix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.gini.android.vision.Document;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.util.MimeType;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class DocumentFactory {

    /**
     * @param intent  an {@link Intent} containing an image or a pdf {@link Uri}
     * @param context Android context
     *
     * @return new {@link Document} instance with the contents of the Intent's Uri
     *
     * @throws IllegalArgumentException if the Intent's data is null or the mime type is unknown
     */
    @NonNull
    public static GiniVisionDocument newDocumentFromIntent(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final Document.ImportMethod importMethod) {
        final Uri data = getUri(intent);
        if (data == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        if (hasMimeType(intent, context, MimeType.APPLICATION_PDF.asString())) {
            return PdfDocument.fromIntent(intent, importMethod);
        } else if (hasMimeTypeWithPrefix(intent, context,
                MimeType.IMAGE_PREFIX.asString())) {
            return ImageDocument.fromIntent(intent, context, deviceOrientation, deviceType,
                    importMethod);
        }
        throw new IllegalArgumentException("Unknown Intent Uri mime type.");
    }

    @NonNull
    public static ImageDocument newImageDocumentFromUri(@NonNull final Uri externalUri,
            @NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final Document.ImportMethod importMethod) {
        return ImageDocument.fromUri(externalUri, intent, context, deviceOrientation, deviceType,
                importMethod);
    }

    public static ImageDocument newEmptyImageDocument(@NonNull final Document.Source source,
            @NonNull final Document.ImportMethod importMethod) {
        return ImageDocument.empty(source, importMethod);
    }

    public static ImageDocument newImageDocumentFromPhoto(@NonNull final Photo photo) {
        return ImageDocument.fromPhoto(photo);
    }

    public static ImageDocument newImageDocumentFromPhoto(@NonNull final Photo photo,
            @NonNull final Uri storedAtUri) {
        return ImageDocument.fromPhoto(photo, storedAtUri);
    }

    public static ImageDocument newImageDocumentFromPhotoAndDocument(@NonNull final Photo photo,
            @NonNull final GiniVisionDocument document) {
        return ImageDocument.fromPhotoAndDocument(photo, document);
    }

    public static GiniVisionMultiPageDocument newMultiPageDocument(
            @NonNull final GiniVisionDocument document) {
        if (document instanceof ImageDocument) {
            return new ImageMultiPageDocument((ImageDocument) document);
        } else if (document instanceof PdfDocument) {
            return new PdfMultiPageDocument((PdfDocument) document);
        } else if (document instanceof QRCodeDocument) {
            return new QRCodeMultiPageDocument((QRCodeDocument) document);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported document type for multi-page: " + document.getType());
        }
    }

    private DocumentFactory() {
    }
}
