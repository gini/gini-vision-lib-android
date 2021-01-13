package net.gini.android.vision.internal.fileimport.providerchooser;

import static net.gini.android.vision.internal.fileimport.providerchooser.ProvidersItem.FileProviderItemType.SECTION;
import static net.gini.android.vision.internal.fileimport.providerchooser.ProvidersItem.FileProviderItemType.SEPARATOR;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ProvidersSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final List<ProvidersItem> mItems;
    private final int mGridSpanCount;

    public ProvidersSpanSizeLookup(@NonNull final List<ProvidersItem> items, final int gridSpanCount) {
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
