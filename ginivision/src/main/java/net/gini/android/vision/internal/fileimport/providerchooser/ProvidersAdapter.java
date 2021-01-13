package net.gini.android.vision.internal.fileimport.providerchooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ProvidersAdapter extends RecyclerView.Adapter<ProvidersItemViewHolder> {

    private final Context mContext;
    private final List<ProvidersItem> mItems;
    private final ProvidersAppItemSelectedListener mItemSelectedListener;

    public ProvidersAdapter(@NonNull final Context context,
            @NonNull final List<ProvidersItem> items,
            @NonNull final ProvidersAppItemSelectedListener itemSelectedListener) {
        mContext = context;
        mItems = items;
        mItemSelectedListener = itemSelectedListener;
    }

    @Override
    public int getItemViewType(final int position) {
        return mItems.get(position).getType().ordinal();
    }

    @Override
    public ProvidersItemViewHolder onCreateViewHolder(final ViewGroup parent,
            final int viewType) {
        switch (ProvidersItem.FileProviderItemType.fromOrdinal(viewType)) {
            case SECTION:
                return createSectionItemViewHolder(parent);
            case APP:
                return createAppItemViewHolder(parent);
            case SEPARATOR:
                return createSeparatorItemViewHolder(parent);
            default:
                return null;
        }
    }

    @NonNull
    private ProvidersItemViewHolder createSectionItemViewHolder(
            @NonNull final ViewGroup parent) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_item_file_provider_section, parent, false);
        return new ProvidersSectionItemViewHolder(itemView);
    }

    @NonNull
    private ProvidersItemViewHolder createAppItemViewHolder(@NonNull final ViewGroup parent) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_item_file_provider_app, parent, false);
        return new ProvidersAppItemViewHolder(itemView);
    }

    private ProvidersItemViewHolder createSeparatorItemViewHolder(@NonNull final ViewGroup parent) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_item_file_provider_separator, parent, false);
        return new ProvidersSeparatorItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProvidersItemViewHolder holder, final int position) {
        switch (holder.type) {
            case SECTION:
                bindSectionItemViewHolder((ProvidersSectionItemViewHolder) holder, position);
                break;
            case APP:
                bindAppItemViewHolder((ProvidersAppItemViewHolder) holder, position);
                break;
            case SEPARATOR:
                break;
            default:
                throw new IllegalStateException("Unknown FileProviderItemType: " + holder.type);
        }
    }

    private void bindSectionItemViewHolder(@NonNull final ProvidersSectionItemViewHolder holder,
            final int position) {
        final ProvidersSectionItem item = (ProvidersSectionItem) mItems.get(position);
        holder.sectionTitle.setText(item.getSectionTitle());
    }

    private void bindAppItemViewHolder(@NonNull final ProvidersAppItemViewHolder holder,
            final int position) {
        final ProvidersAppItem item = (ProvidersAppItem) mItems.get(position);
        holder.icon.setImageDrawable(item.getResolveInfo().loadIcon(mContext.getPackageManager()));
        holder.label.setText(item.getResolveInfo().loadLabel(mContext.getPackageManager()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ProvidersAppItem item =
                        (ProvidersAppItem) mItems.get(holder.getAdapterPosition());
                mItemSelectedListener.onItemSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class ProvidersAppItemViewHolder extends ProvidersItemViewHolder {

        @NonNull
        final ImageView icon;
        @NonNull
        final TextView label;

        ProvidersAppItemViewHolder(@NonNull final View itemView) {
            super(itemView, ProvidersItem.FileProviderItemType.APP);
            icon = itemView.findViewById(R.id.gv_app_icon);
            label = itemView.findViewById(R.id.gv_app_label);
        }
    }

    private static class ProvidersSectionItemViewHolder extends ProvidersItemViewHolder {

        @NonNull
        final TextView sectionTitle;

        ProvidersSectionItemViewHolder(final View itemView) {
            super(itemView, ProvidersItem.FileProviderItemType.SECTION);
            sectionTitle = itemView.findViewById(R.id.gv_section_title);
        }
    }

    private static class ProvidersSeparatorItemViewHolder extends ProvidersItemViewHolder {

        ProvidersSeparatorItemViewHolder(final View itemView) {
            super(itemView, ProvidersItem.FileProviderItemType.SEPARATOR);
        }
    }
}
