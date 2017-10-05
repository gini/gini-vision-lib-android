package net.gini.android.vision.internal.fileimport.providerchooser;

import static net.gini.android.vision.internal.fileimport.FileChooserActivity.GRID_SPAN_COUNT_PHONE;
import static net.gini.android.vision.internal.fileimport.providerchooser.ProvidersItem
        .FileProviderItemType.SECTION;
import static net.gini.android.vision.internal.fileimport.providerchooser.ProvidersItem
        .FileProviderItemType.SEPARATOR;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;

import java.util.List;

public class ProvidersSpanSizeLookup extends SpanSizeLookup {

    private final List<ProvidersItem> mItems;
    private final int mGridSpanCount;

    public ProvidersSpanSizeLookup(@NonNull final List<ProvidersItem> items, int gridSpanCount) {
        mItems = items;
        mGridSpanCount = gridSpanCount;
    }

    @Override
    public int getSpanSize(final int position) {
        final ProvidersItem.FileProviderItemType providerItemType = mItems.get(position).getType();
        if (providerItemType == SECTION || providerItemType == SEPARATOR) {
            return mGridSpanCount;
        }
        return 1;
    }
}
