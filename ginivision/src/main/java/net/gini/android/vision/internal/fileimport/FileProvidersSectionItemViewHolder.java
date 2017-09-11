package net.gini.android.vision.internal.fileimport;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import net.gini.android.vision.R;

class FileProvidersSectionItemViewHolder extends FileProvidersItemViewHolder {

    @NonNull
    final TextView sectionTitle;

    FileProvidersSectionItemViewHolder(final View itemView) {
        super(itemView, FileProvidersItem.FileProviderItemType.SECTION);
        sectionTitle = itemView.findViewById(R.id.sectionTitle);
    }
}
