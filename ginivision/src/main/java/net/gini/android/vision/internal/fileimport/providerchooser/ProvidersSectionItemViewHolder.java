package net.gini.android.vision.internal.fileimport.providerchooser;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import net.gini.android.vision.R;

class ProvidersSectionItemViewHolder extends ProvidersItemViewHolder {

    @NonNull
    final TextView sectionTitle;

    ProvidersSectionItemViewHolder(final View itemView) {
        super(itemView, ProvidersItem.FileProviderItemType.SECTION);
        sectionTitle = itemView.findViewById(R.id.gv_section_title);
    }
}
