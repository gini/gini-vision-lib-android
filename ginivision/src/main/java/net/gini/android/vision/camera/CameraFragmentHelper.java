package net.gini.android.vision.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.DocumentImportEnabledFileTypes;

class CameraFragmentHelper {

    private static final String ARGS_DOCUMENT_IMPORT_FILE_TYPES = "GV_ARGS_DOCUMENT_IMPORT_FILE_TYPES";

    public static Bundle createArguments(
            @NonNull final DocumentImportEnabledFileTypes docImportEnabledFileTypes) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGS_DOCUMENT_IMPORT_FILE_TYPES, docImportEnabledFileTypes);
        return arguments;
    }

    static CameraFragmentImpl createFragmentImpl(@NonNull CameraFragmentImplCallback fragment,
            @Nullable Bundle arguments) {
        DocumentImportEnabledFileTypes docImportEnabledFileTypes =
                DocumentImportEnabledFileTypes.NONE;
        if (arguments != null) {
            final DocumentImportEnabledFileTypes enabledFileTypes =
                    (DocumentImportEnabledFileTypes) arguments.getSerializable(
                            ARGS_DOCUMENT_IMPORT_FILE_TYPES);
            if (enabledFileTypes != null) {
                docImportEnabledFileTypes = enabledFileTypes;
            }
        }
        return new CameraFragmentImpl(fragment, docImportEnabledFileTypes);
    }

    public static void setListener(@NonNull CameraFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement CameraFragmentListener.");
        }
    }
}
