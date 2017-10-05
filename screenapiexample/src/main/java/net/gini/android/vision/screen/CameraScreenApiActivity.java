package net.gini.android.vision.screen;

import android.support.annotation.NonNull;

import net.gini.android.ginivisiontest.R;
import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraActivity;

public class CameraScreenApiActivity extends CameraActivity {

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        // We can apply custom checks here to an imported document and notify the Gini Vision
        // Library about the result
        // As an example we check the document size and allow documents smaller than 5MB
        if (document.getData() != null) {
            if (document.getData().length <= 5 * 1024 * 1024) {
                callback.documentAccepted();
            } else {
                callback.documentRejected("Diese Datei ist leider größer als 5MB.");
            }
        } else {
            callback.documentRejected(getString(R.string.gv_document_import_error));
        }
    }
}
