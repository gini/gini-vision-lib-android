package net.gini.android.vision.analysis;


import net.gini.android.vision.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

enum AnalysisHint {

    FLAT(R.drawable.gv_hint_icon, R.string.gv_photo_tip_flat),
    ALIGN(R.drawable.gv_hint_icon, R.string.gv_photo_tip_align),
    PARALLEL(R.drawable.gv_hint_icon, R.string.gv_photo_tip_parallel);

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
        final List<AnalysisHint> arrayList = new ArrayList<>(values().length);
        Collections.addAll(arrayList, values());
        return arrayList;
    }
}
