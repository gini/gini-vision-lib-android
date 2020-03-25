package net.gini.android.vision.internal.fileimport.providerchooser;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcel;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ProvidersAppItem extends ProvidersItem {

    public static final Creator<ProvidersAppItem> CREATOR =
            new Creator<ProvidersAppItem>() {
                @Override
                public ProvidersAppItem createFromParcel(final Parcel in) {
                    return new ProvidersAppItem(in);
                }

                @Override
                public ProvidersAppItem[] newArray(final int size) {
                    return new ProvidersAppItem[size];
                }
            };

    private final Intent mIntent;
    private final ResolveInfo mResolveInfo;

    protected ProvidersAppItem(final Parcel in) {
        super(in);
        mIntent = in.readParcelable(getClass().getClassLoader());
        mResolveInfo = in.readParcelable(getClass().getClassLoader());
    }

    public ProvidersAppItem(@NonNull final Intent intent, @NonNull final ResolveInfo resolveInfo) {
        super(FileProviderItemType.APP);
        mIntent = intent;
        mResolveInfo = resolveInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mIntent, flags);
        dest.writeParcelable(mResolveInfo, flags);
    }

    public Intent getIntent() {
        return mIntent;
    }

    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

}
