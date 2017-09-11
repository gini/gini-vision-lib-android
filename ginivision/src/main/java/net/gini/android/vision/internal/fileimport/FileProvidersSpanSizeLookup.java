package net.gini.android.vision.internal.fileimport;

import static net.gini.android.vision.internal.fileimport.FileChooserActivity.GRID_SPAN_COUNT;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;

import java.util.List;

class FileProvidersSpanSizeLookup extends SpanSizeLookup {

    private final List<FileProvidersItem> mItems;

    FileProvidersSpanSizeLookup(@NonNull final List<FileProvidersItem> items) {
        mItems = items;
    }

    @Override
    public int getSpanSize(final int position) {
        switch (mItems.get(position).getType()) {
            case SECTION:
                return GRID_SPAN_COUNT;
            default:
                return 1;
        }
    }
}
