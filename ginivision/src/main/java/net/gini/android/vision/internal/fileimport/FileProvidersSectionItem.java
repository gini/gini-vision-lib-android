package net.gini.android.vision.internal.fileimport;

import android.support.annotation.NonNull;

class FileProvidersSectionItem extends FileProvidersItem {

    private final String mSectionTitle;

    FileProvidersSectionItem(@NonNull final String sectionTitle) {
        super(FileProviderItemType.SECTION);
        mSectionTitle = sectionTitle;
    }

    @NonNull
    String getSectionTitle() {
        return mSectionTitle;
    }
}
