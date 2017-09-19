package net.gini.android.vision.analysis;


import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import net.gini.android.vision.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

enum AnalysisHint {

    //TODO use actual assets and strings
    FLAT(R.drawable.gv_onboarding_flat, R.string.gv_noresults_flat),
    ALIGN(R.drawable.gv_onboarding_align, R.string.gv_noresults_align),
    PARALLEL(R.drawable.gv_onboarding_parallel, R.string.gv_noresults_parallel),
    LIGHT(R.drawable.gv_action_bar_back, R.string.gv_camera_error_no_permission);

    public int getDrawableResource() {
        return mDrawableResource;
    }

    public int getTextResource() {
        return mTextResource;
    }

    private final int mDrawableResource;
    private final int mTextResource;

    AnalysisHint(@DrawableRes final int drawableResource, @StringRes final int textResource) {
        mDrawableResource = drawableResource;
        mTextResource = textResource;
    }

    static List<AnalysisHint> getArray() {
        List<AnalysisHint> arrayList = new ArrayList<>(values().length);
        Collections.addAll(arrayList, values());
        return arrayList;
    }
}
