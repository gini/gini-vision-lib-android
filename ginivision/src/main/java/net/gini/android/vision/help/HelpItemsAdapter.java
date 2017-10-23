package net.gini.android.vision.help;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gini.android.vision.R;

/**
 * @exclude
 */

class HelpItemsAdapter extends Adapter<HelpItemsAdapter.HelpItemsViewHolder> {

    private final HelpItemSelectedListener mItemSelectedListener;

    HelpItemsAdapter(final HelpItemSelectedListener itemSelectedListener) {
        mItemSelectedListener = itemSelectedListener;
    }

    @Override
    public HelpItemsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gv_item_help,
                parent, false);
        return new HelpItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HelpItemsViewHolder holder, final int position) {
        final HelpItem helpItem = HelpItem.values()[position];
        holder.title.setText(helpItem.title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int actualPosition = holder.getAdapterPosition();
                mItemSelectedListener.onItemSelected(HelpItem.values()[actualPosition]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return HelpItem.values().length;
    }

    enum HelpItem {
        PHOTO_TIPS(R.string.gv_help_item_photo_tips_title),
        FILE_IMPORT_GUIDE(R.string.gv_help_item_file_import_guide_title),
        SUPPORTED_FORMATS(R.string.gv_help_item_supported_formats_title);

        @StringRes
        final int title;

        HelpItem(@StringRes final int title) {
            this.title = title;
        }
    }

    class HelpItemsViewHolder extends RecyclerView.ViewHolder {

        final TextView title;

        HelpItemsViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.gv_help_item_title);
        }
    }

    interface HelpItemSelectedListener {
        void onItemSelected(@NonNull final HelpItem helpItem);
    }
}
