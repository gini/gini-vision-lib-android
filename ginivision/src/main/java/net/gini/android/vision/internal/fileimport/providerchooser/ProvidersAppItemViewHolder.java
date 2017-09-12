package net.gini.android.vision.internal.fileimport.providerchooser;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;

class ProvidersAppItemViewHolder extends ProvidersItemViewHolder {

    @NonNull
    final ImageView icon;
    @NonNull
    final TextView label;

    ProvidersAppItemViewHolder(@NonNull final View itemView) {
        super(itemView, ProvidersItem.FileProviderItemType.APP);
        icon = itemView.findViewById(R.id.imageView);
        label = itemView.findViewById(R.id.textView);
    }
}
