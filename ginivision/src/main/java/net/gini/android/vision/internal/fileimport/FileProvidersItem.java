package net.gini.android.vision.internal.fileimport;

import android.support.annotation.NonNull;

abstract class FileProvidersItem {

    enum FileProviderItemType {
        SECTION,
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

    FileProvidersItem(@NonNull final FileProviderItemType type) {
        mType = type;
    }

    @NonNull
    FileProviderItemType getType() {
        return mType;
    }
}
