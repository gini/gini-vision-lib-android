package net.gini.android.vision.internal.fileimport;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

abstract class FileProvidersItemViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    final FileProvidersItem.FileProviderItemType type;

    FileProvidersItemViewHolder(@NonNull final View itemView,
            @NonNull final FileProvidersItem.FileProviderItemType type) {
        super(itemView);
        this.type = type;
    }
}
