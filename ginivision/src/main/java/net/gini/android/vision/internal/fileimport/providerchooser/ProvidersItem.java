package net.gini.android.vision.internal.fileimport.providerchooser;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ProvidersItem implements Parcelable {

    public static final Parcelable.Creator<ProvidersItem> CREATOR =
            new Parcelable.Creator<ProvidersItem>() {
                @Override
                public ProvidersItem createFromParcel(final Parcel in) {
                    return new ProvidersItem(in);
                }

                @Override
                public ProvidersItem[] newArray(final int size) {
                    return new ProvidersItem[size];
                }
            };

    private final FileProviderItemType mType;

    ProvidersItem(final Parcel in) {
        mType = (FileProviderItemType) in.readSerializable();
    }

    ProvidersItem(@NonNull final FileProviderItemType type) {
        mType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeSerializable(mType);
    }

    @NonNull
    FileProviderItemType getType() {
        return mType;
    }

    enum FileProviderItemType {
        SECTION,
        SEPARATOR,
        APP;

        static FileProviderItemType fromOrdinal(final int ordinal) {
            if (ordinal >= values().length) {
                throw new IllegalArgumentException(
                        "Ordinal out of bounds: ordinal (" + ordinal
                                + ") was not less than nr of values (" + values().length + ")");
            }
            return values()[ordinal];
        }
    }
}
