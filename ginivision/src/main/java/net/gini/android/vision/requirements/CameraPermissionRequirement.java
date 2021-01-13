package net.gini.android.vision.requirements;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

class CameraPermissionRequirement implements Requirement {

    private final Context mContext;

    CameraPermissionRequirement(final Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.CAMERA_PERMISSION;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean result = true;
        String details = "";

        final int check = mContext.getPackageManager().checkPermission(Manifest.permission.CAMERA,
                mContext.getPackageName());
        if (check != PackageManager.PERMISSION_GRANTED) {
            result = false;
            details = "Camera permission was not granted";
        }

        return new RequirementReport(getId(), result, details);
    }
}
