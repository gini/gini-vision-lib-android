package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.ApplicationHelper.isDefaultForMimeType;
import static net.gini.android.vision.internal.util.ApplicationHelper.startApplicationDetailsSettings;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 04.02.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * @exclude
 */
public final class FileImportHelper {

    public static CompletableFuture<Void> showAlertIfOpenWithDocument(
            @Nullable final Activity activity,
            @NonNull final GiniVisionDocument document,
            @NonNull final FragmentImplCallback fragmentImplCallback) {
        if (activity == null) {
            return CompletableFuture.completedFuture(null);
        }
        final CompletableFuture<Void> alertCompletion = new CompletableFuture<>();
        if (document.getImportMethod() == Document.ImportMethod.OPEN_WITH
                && isDefaultForMimeType(activity, document.getMimeType())) {
            final String fileType = fileTypeForMimeType(activity, document.getMimeType());
            fragmentImplCallback.showAlertDialog(
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
                            activity.finish();
                        }
                    });
        } else {
            alertCompletion.complete(null);
        }
        return alertCompletion;
    }

    private static String fileTypeForMimeType(@NonNull final Activity activity,
            @NonNull final String mimeType) {
        if (mimeType.equals(MimeType.APPLICATION_PDF.asString())) {
            return activity.getString(R.string.gv_file_import_default_app_dialog_pdf_file_type);
        } else if (mimeType.startsWith(MimeType.IMAGE_PREFIX.asString())) {
            return activity.getString(R.string.gv_file_import_default_app_dialog_image_file_type);
        }
        return activity.getString(R.string.gv_file_import_default_app_dialog_document_file_type);
    }

    private FileImportHelper() {
    }

}
