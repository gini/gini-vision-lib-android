package net.gini.android.vision.help;

import static net.gini.android.vision.help.SupportedFormatsAdapter.ItemType.FORMAT_INFO;
import static net.gini.android.vision.help.SupportedFormatsAdapter.ItemType.HEADER;
import static net.gini.android.vision.internal.util.FeatureConfiguration.getDocumentImportEnabledFileTypes;
import static net.gini.android.vision.internal.util.FeatureConfiguration.isFileImportEnabled;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Internal use only.
 *
 * @suppress
 */
class SupportedFormatsAdapter extends
        RecyclerView.Adapter<SupportedFormatsAdapter.FormatItemViewHolder> {

    private final List<Enum> mItems;

    SupportedFormatsAdapter(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        mItems = setUpItems(giniVisionFeatureConfiguration);
    }

    private List<Enum> setUpItems(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        final ArrayList<Enum> items = new ArrayList<>();
        items.add(SectionHeader.SUPPORTED_FORMATS);
        items.add(SupportedFormat.PRINTED_INVOICES);
        if (isFileImportEnabled(giniVisionFeatureConfiguration)
                || getDocumentImportEnabledFileTypes(giniVisionFeatureConfiguration)
                == DocumentImportEnabledFileTypes.PDF_AND_IMAGES) {
            items.add(SupportedFormat.SINGLE_PAGE_AS_JPEG_PNG_GIF);
            items.add(SupportedFormat.PDF);
        } else if (getDocumentImportEnabledFileTypes(giniVisionFeatureConfiguration)
                == DocumentImportEnabledFileTypes.PDF) {
            items.add(SupportedFormat.PDF);
        }
        items.add(SectionHeader.UNSUPPORTED_FORMATS);
        Collections.addAll(items, UnsupportedFormat.values());
        return items;
    }

    @Override
    public FormatItemViewHolder onCreateViewHolder(final ViewGroup parent,
            final int viewType) {
        final ItemType itemType = ItemType.fromOrdinal(viewType);
        switch (itemType) {
            case HEADER:
                return createHeaderItemViewHolder(parent);
            case FORMAT_INFO:
                return createFormatInfoItemViewHolder(parent);
            default:
                throw new IllegalStateException("Unknown ItemType: " + itemType);
        }
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
            final SectionHeader sectionHeader = (SectionHeader) mItems.get(position);
            viewHolder.title.setText(sectionHeader.title);
        } else if (holder instanceof FormatInfoItemViewHolder) {
            final FormatInfoItemViewHolder viewHolder = (FormatInfoItemViewHolder) holder;
            final FormatInfo formatInfo = (FormatInfo) mItems.get(position);
            viewHolder.label.setText(formatInfo.getLabel());
            viewHolder.backgroundIcon.setImageResource(formatInfo.getIconBackground());
            viewHolder.foregroundIcon.setImageResource(formatInfo.getIconForeground());
            final int tintColor = ContextCompat.getColor(
                    viewHolder.itemView.getContext(), formatInfo.getIconForegroundTint());
            viewHolder.foregroundIcon.setColorFilter(tintColor, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        final Enum item = mItems.get(position);
        return item instanceof SectionHeader ? HEADER.ordinal() : FORMAT_INFO.ordinal();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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
        private final int mIconBackground;
        @DrawableRes
        private final int mIconForeground;
        @ColorRes
        private final int mIconForegroundTint;
        @StringRes
        private final int mLabel;

        SupportedFormat(@StringRes final int label) {
            mLabel = label;
            mIconBackground = R.drawable.gv_format_info_supported_icon_background;
            mIconForeground = R.drawable.gv_format_info_supported_icon_foreground;
            mIconForegroundTint =
                    R.color.gv_supported_formats_item_supported_icon_foreground;
        }

        @Override
        @DrawableRes
        public int getIconBackground() {
            return mIconBackground;
        }

        @Override
        @DrawableRes
        public int getIconForeground() {
            return mIconForeground;
        }

        @Override
        public int getIconForegroundTint() {
            return mIconForegroundTint;
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
        private final int mIconBackground;
        @DrawableRes
        private final int mIconForeground;
        @ColorRes
        private final int mIconForegroundTint;
        @StringRes
        private final int mLabel;

        UnsupportedFormat(@StringRes final int label) {
            mLabel = label;
            mIconBackground = R.drawable.gv_format_info_unsupported_icon_background;
            mIconForeground = R.drawable.gv_format_info_unsupported_icon_foreground;
            mIconForegroundTint =
                    R.color.gv_supported_formats_item_unsupported_icon_foreground;
        }

        @Override
        @DrawableRes
        public int getIconBackground() {
            return mIconBackground;
        }

        @Override
        @DrawableRes
        public int getIconForeground() {
            return mIconForeground;
        }

        @Override
        public int getIconForegroundTint() {
            return mIconForegroundTint;
        }


        @Override
        @StringRes
        public int getLabel() {
            return mLabel;
        }
    }

    private class FormatInfoItemViewHolder extends FormatItemViewHolder {

        final ImageView backgroundIcon;
        final ImageView foregroundIcon;
        final TextView label;

        FormatInfoItemViewHolder(final View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.gv_format_info_item_label);
            backgroundIcon = itemView.findViewById(R.id.gv_format_info_item_icon_background);
            foregroundIcon = itemView.findViewById(R.id.gv_format_info_item_icon_foreground);
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
        int getIconBackground();

        @DrawableRes
        int getIconForeground();

        @ColorRes
        int getIconForegroundTint();

        @StringRes
        int getLabel();
    }


}
