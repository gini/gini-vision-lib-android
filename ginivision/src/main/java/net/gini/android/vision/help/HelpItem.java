package net.gini.android.vision.help;

import androidx.annotation.StringRes;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.R;

/**
 * <p>
 *     This enum declares the items which are shown in the Help Screen.
 * </p>
 */
public enum HelpItem {
    /**
     * <p>
     *     Shows tips for taking better pictures.
     * </p>
     * <p>
     *     Item label customizable by overriding the string resource named {@code gv_help_item_photo_tips_title}
     * </p>
     */
    PHOTO_TIPS(R.string.gv_help_item_photo_tips_title),
    /**
     * <p>
     *     Shows a guide for importing files from other apps via "open with".
     * </p>
     * <p>
     *     <b>Important:</b> This item is shown only if file import was enabled with the {@link GiniVisionFeatureConfiguration}.
     * </p>
     * <p>
     *     Item label customizable by overriding the string resource named {@code gv_help_item_file_import_guide_title}
     * </p>
     */
    FILE_IMPORT_GUIDE(R.string.gv_help_item_file_import_guide_title),
    /**
     * <p>
     *     Shows information about the document formats supported by the Gini Vision Library.
     * </p>
     * <p>
     *     Item label customizable by overriding the string resource named {@code gv_help_item_supported_formats_title}
     * </p>
     */
    SUPPORTED_FORMATS(R.string.gv_help_item_supported_formats_title);

    @StringRes
    final int title;

    HelpItem(@StringRes final int title) {
        this.title = title;
    }
}
