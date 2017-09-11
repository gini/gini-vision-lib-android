package net.gini.android.vision.internal.fileimport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;

import java.util.List;

class FileProvidersAdapter extends RecyclerView.Adapter<FileProvidersItemViewHolder> {

    private final Context mContext;
    private final List<FileProvidersItem> mItems;
    private final FileProvidersAppItemSelectedListener mItemSelectedListener;

    FileProvidersAdapter(@NonNull final Context context,
            @NonNull final List<FileProvidersItem> items,
            @NonNull final FileProvidersAppItemSelectedListener itemSelectedListener) {
        mContext = context;
        mItems = items;
        mItemSelectedListener = itemSelectedListener;
    }

    @Override
    public int getItemViewType(final int position) {
        return mItems.get(position).getType().ordinal();
    }

    @Override
    public FileProvidersItemViewHolder onCreateViewHolder(final ViewGroup parent,
            final int viewType) {
        switch (FileProvidersItem.FileProviderItemType.fromOrdinal(viewType)) {
            case SECTION:
                return createSectionItemViewHolder(parent);
            case APP:
                return createAppItemViewHolder(parent);
            default:
                return null;
        }
    }

    @NonNull
    private FileProvidersItemViewHolder createSectionItemViewHolder(
            @NonNull final ViewGroup parent) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_item_file_provider_section, parent, false);
        return new FileProvidersSectionItemViewHolder(itemView);
    }

    @NonNull
    private FileProvidersItemViewHolder createAppItemViewHolder(@NonNull final ViewGroup parent) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_item_file_provider_app, parent, false);
        return new FileProvidersAppItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FileProvidersItemViewHolder holder, final int position) {
        switch (holder.type) {
            case SECTION:
                bindSectionItemViewHolder((FileProvidersSectionItemViewHolder) holder, position);
                break;
            case APP:
                bindAppItemViewHolder((FileProvidersAppItemViewHolder) holder, position);
                break;
        }
    }

    private void bindSectionItemViewHolder(@NonNull final FileProvidersSectionItemViewHolder holder,
            final int position) {
        FileProvidersSectionItem item = (FileProvidersSectionItem) mItems.get(position);
        holder.sectionTitle.setText(item.getSectionTitle());
    }

    private void bindAppItemViewHolder(@NonNull final FileProvidersAppItemViewHolder holder,
            final int position) {
        FileProvidersAppItem item = (FileProvidersAppItem) mItems.get(position);
        holder.icon.setImageDrawable(item.getResolveInfo().loadIcon(mContext.getPackageManager()));
        holder.label.setText(item.getResolveInfo().loadLabel(mContext.getPackageManager()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                FileProvidersAppItem item =
                        (FileProvidersAppItem) mItems.get(holder.getAdapterPosition());
                mItemSelectedListener.onItemSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
