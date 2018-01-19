package net.gini.android.vision.internal.fileimport.providerchooser;

import android.support.annotation.NonNull;

/**
 * @exclude
 */
public class ProvidersItem {

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

    private final FileProviderItemType mType;

    ProvidersItem(@NonNull final FileProviderItemType type) {
        mType = type;
    }

    @NonNull
    FileProviderItemType getType() {
        return mType;
    }
}
