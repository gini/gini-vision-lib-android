package net.gini.android.vision.internal.fileimport.providerchooser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

abstract class ProvidersItemViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    final ProvidersItem.FileProviderItemType type;

    ProvidersItemViewHolder(@NonNull final View itemView,
            @NonNull final ProvidersItem.FileProviderItemType type) {
        super(itemView);
        this.type = type;
    }
}
