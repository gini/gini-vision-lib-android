package net.gini.android.vision.internal.fileimport;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

class FileProvidersAppItem extends FileProvidersItem {

    private final Intent mIntent;
    private final ResolveInfo mResolveInfo;

    FileProvidersAppItem(@NonNull final Intent intent, @NonNull final ResolveInfo resolveInfo) {
        super(FileProviderItemType.APP);
        mIntent = intent;
        mResolveInfo = resolveInfo;
    }

    Intent getIntent() {
        return mIntent;
    }

    ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

}
