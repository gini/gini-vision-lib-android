package net.gini.android.vision.internal.fileimport.providerchooser;

import static net.gini.android.vision.internal.fileimport.FileChooserActivity.GRID_SPAN_COUNT;
import static net.gini.android.vision.internal.fileimport.providerchooser.ProvidersItem
        .FileProviderItemType.SECTION;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;

import java.util.List;

public class ProvidersSpanSizeLookup extends SpanSizeLookup {

    private final List<ProvidersItem> mItems;

    public ProvidersSpanSizeLookup(@NonNull final List<ProvidersItem> items) {
        mItems = items;
    }

    @Override
    public int getSpanSize(final int position) {
        if (mItems.get(position).getType() == SECTION) {
            return GRID_SPAN_COUNT;
        }
        return 1;
    }
}
