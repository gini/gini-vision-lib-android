package net.gini.android.vision.help;

import static net.gini.android.vision.help.SupportedFormatsAdapter.ItemType.FORMAT_INFO;
import static net.gini.android.vision.help.SupportedFormatsAdapter.ItemType.HEADER;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gini.android.vision.R;

/**
 * @exclude
 */
class SupportedFormatsAdapter extends
        RecyclerView.Adapter<SupportedFormatsAdapter.FormatItemViewHolder> {

    private final Enum[] mItems;

    SupportedFormatsAdapter() {
        mItems = new Enum[getCount()];
        int i = 0;
        mItems[i] = SectionHeader.SUPPORTED_FORMATS;
        for (final SupportedFormat supportedFormat : SupportedFormat.values()) {
            i++;
            mItems[i] = supportedFormat;
        }
        i++;
        mItems[i] = SectionHeader.UNSUPPORTED_FORMATS;
        for (final UnsupportedFormat unsupportedFormat : UnsupportedFormat.values()) {
            i++;
            mItems[i] = unsupportedFormat;
        }
    }

    private int getCount() {
        return SectionHeader.values().length
                + SupportedFormat.values().length
                + UnsupportedFormat.values().length;
    }

    @Override
    public FormatItemViewHolder onCreateViewHolder(final ViewGroup parent,
            final int viewType) {
        switch (ItemType.fromOrdinal(viewType)) {
            case HEADER:
                return createHeaderItemViewHolder(parent);
            case FORMAT_INFO:
                return createFormatInfoItemViewHolder(parent);
        }
        return null;
    }

    private FormatItemViewHolder createHeaderItemViewHolder(final ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.gv_item_format_header, parent, false);
        return new HeaderItemViewHolder(view);
    }

    private FormatItemViewHolder createFormatInfoItemViewHolder(final ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.gv_item_format_info, parent, false);
        return new FormatInfoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FormatItemViewHolder holder,
            final int position) {
        if (holder instanceof HeaderItemViewHolder) {
            final HeaderItemViewHolder viewHolder = (HeaderItemViewHolder) holder;
            final SectionHeader sectionHeader = (SectionHeader) mItems[position];
            viewHolder.title.setText(sectionHeader.title);
        } else if (holder instanceof FormatInfoItemViewHolder) {
            final FormatInfoItemViewHolder viewHolder = (FormatInfoItemViewHolder) holder;
            final FormatInfo formatInfo = (FormatInfo) mItems[position];
            viewHolder.label.setText(formatInfo.getLabel());
            final Drawable iconDrawable = ContextCompat.getDrawable(
                    viewHolder.itemView.getContext(), formatInfo.getIcon());
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(viewHolder.label,
                    iconDrawable, null, null, null);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        final Enum item = mItems[position];
        return item instanceof SectionHeader ? HEADER.ordinal() : FORMAT_INFO.ordinal();
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    enum ItemType {
        HEADER,
        FORMAT_INFO;

        static ItemType fromOrdinal(final int ordinal) {
            if (ordinal >= values().length) {
                throw new IllegalArgumentException(
                        "Ordinal out of bounds: ordinal (" + ordinal
                                + ") was not less than nr of values (" + values().length + ")");
            }
            return values()[ordinal];
        }
    }

    private enum SectionHeader {
        SUPPORTED_FORMATS(R.string.gv_supported_format_section_header),
        UNSUPPORTED_FORMATS(R.string.gv_unsupported_format_section_header);

        @StringRes
        final int title;

        SectionHeader(@StringRes final int title) {
            this.title = title;
        }
    }

    private enum SupportedFormat implements FormatInfo {
        PRINTED_INVOICES(R.string.gv_supported_format_printed_invoices),
        SINGLE_PAGE_AS_JPEG_PNG_GIF(R.string.gv_supported_format_single_page_as_jpeg_png_gif),
        PDF(R.string.gv_supported_format_pdf);

        @DrawableRes
        private final int mIcon;
        @StringRes
        private final int mLabel;

        SupportedFormat(@StringRes final int label) {
            this.mLabel = label;
            this.mIcon = R.drawable.gv_alert_icon;
        }

        @Override
        @DrawableRes
        public int getIcon() {
            return mIcon;
        }

        @Override
        @StringRes
        public int getLabel() {
            return mLabel;
        }
    }

    private enum UnsupportedFormat implements FormatInfo {
        HANDWRITING(R.string.gv_unsupported_format_handwriting),
        PHOTOS_OF_SCREENS(R.string.gv_unsupported_format_photos_of_screens);

        @DrawableRes
        private final int mIcon;
        @StringRes
        private final int mLabel;

        UnsupportedFormat(@StringRes final int label) {
            this.mLabel = label;
            this.mIcon = R.drawable.gv_alert_icon;
        }

        @Override
        @DrawableRes
        public int getIcon() {
            return mIcon;
        }

        @Override
        @StringRes
        public int getLabel() {
            return mLabel;
        }
    }

    private class FormatInfoItemViewHolder extends FormatItemViewHolder {

        final TextView label;

        FormatInfoItemViewHolder(final View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.gv_supported_formats_item_label);
        }
    }

    class FormatItemViewHolder extends RecyclerView.ViewHolder {

        FormatItemViewHolder(final View itemView) {
            super(itemView);
        }
    }

    private class HeaderItemViewHolder extends FormatItemViewHolder {

        final TextView title;

        HeaderItemViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.gv_supported_formats_item_header);
        }
    }

    interface FormatInfo {
        @DrawableRes
        int getIcon();

        @StringRes
        int getLabel();
    }
}
