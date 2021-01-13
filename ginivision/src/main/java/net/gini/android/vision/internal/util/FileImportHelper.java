package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.ApplicationHelper.isDefaultForMimeType;
import static net.gini.android.vision.internal.util.ApplicationHelper.startApplicationDetailsSettings;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.document.GiniVisionDocument;

import java.util.concurrent.CancellationException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 04.02.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public final class FileImportHelper {

    public static CompletableFuture<Void> showAlertIfOpenWithDocumentAndAppIsDefault(
            @NonNull final Activity activity,
            @NonNull final GiniVisionDocument document,
            @NonNull final ShowAlertCallback showAlertCallback) {
        final CompletableFuture<Void> alertCompletion = new CompletableFuture<>();
        if (document.getImportMethod() == Document.ImportMethod.OPEN_WITH
                && isDefaultForMimeType(activity.getApplication(), document.getMimeType())) {
            final String fileType = fileTypeForMimeType(activity.getApplication(),
                    document.getMimeType());
            showAlertCallback.showAlertDialog(
                    activity.getString(R.string.gv_file_import_default_app_dialog_message,
                            fileType),
                    activity.getString(R.string.gv_file_import_default_app_dialog_positive_button),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            startApplicationDetailsSettings(activity);
                        }
                    },
                    activity.getString(R.string.gv_file_import_default_app_dialog_negative_button),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            alertCompletion.complete(null);
                        }
                    },
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(final DialogInterface dialog) {
                            alertCompletion.completeExceptionally(new CancellationException());
                        }
                    });
        } else {
            alertCompletion.complete(null);
        }
        return alertCompletion;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public interface ShowAlertCallback {

        void showAlertDialog(@NonNull final String message,
                @NonNull final String positiveButtonTitle,
                @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
                @Nullable final String negativeButtonTitle,
                @Nullable final DialogInterface.OnClickListener negativeButtonClickListener,
                @Nullable final DialogInterface.OnCancelListener cancelListener);
    }

    private static String fileTypeForMimeType(@NonNull final Application app,
            @NonNull final String mimeType) {
        if (mimeType.equals(MimeType.APPLICATION_PDF.asString())) {
            return app.getString(R.string.gv_file_import_default_app_dialog_pdf_file_type);
        } else if (mimeType.startsWith(MimeType.IMAGE_PREFIX.asString())) {
            return app.getString(R.string.gv_file_import_default_app_dialog_image_file_type);
        }
        return app.getString(R.string.gv_file_import_default_app_dialog_document_file_type);
    }

    private FileImportHelper() {
    }

}
