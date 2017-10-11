package net.gini.android.vision.internal.fileimport.providerchooser;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

/**
 * @exclude
 */
public class ProvidersAppItem extends ProvidersItem {

    private final Intent mIntent;
    private final ResolveInfo mResolveInfo;

    public ProvidersAppItem(@NonNull final Intent intent, @NonNull final ResolveInfo resolveInfo) {
        super(FileProviderItemType.APP);
        mIntent = intent;
        mResolveInfo = resolveInfo;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

}
