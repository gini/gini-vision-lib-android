package net.gini.android.vision.internal.fileimport;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;

class FileProvidersAppItemViewHolder extends FileProvidersItemViewHolder {

    @NonNull
    final ImageView icon;
    @NonNull
    final TextView label;

    FileProvidersAppItemViewHolder(@NonNull final View itemView) {
        super(itemView, FileProvidersItem.FileProviderItemType.APP);
        icon = itemView.findViewById(R.id.imageView);
        label = itemView.findViewById(R.id.textView);
    }
}
