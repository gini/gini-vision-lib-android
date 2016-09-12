package net.gini.android.vision.requirements;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

class LargeHeapRequirement implements Requirement {

    private final Context mContext;

    LargeHeapRequirement(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.LARGE_HEAP;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean result = true;
        String details = "";

        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            if ((appInfo.flags & ApplicationInfo.FLAG_LARGE_HEAP) == 0) {
                result = false;
                details = "Large heap was not enabled. Please enable large heap in your 'AndroidManifest.xml'.";
            }
        } catch (PackageManager.NameNotFoundException e) {
            result = false;
            details = "Could not determine large heap setting. Package '" + mContext.getPackageName() +"' was not found.";
        }

        return new RequirementReport(getId(), result, details);
    }
}
