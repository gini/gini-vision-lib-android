package net.gini.android.vision.internal.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import net.gini.android.vision.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class CustomFontHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFontTextView.class);

    /**
     * Extracts the custom font attribute, the android typeface attribute and sets the font.
     *
     * @param context      Android context
     * @param attributeSet the attributes of the XML tag that is inflating the view.
     * @param defStyleAttr an attribute in the current theme that contains a reference to a style resource that supplies
     *                     default values for the view. Can be 0 to not look for defaults.
     */
    static void parseAttributesAndSetFont(@NonNull final TextView textView,
            @NonNull final Context context, @NonNull final AttributeSet attributeSet,
            final int defStyleAttr) {
        final TypedArray giniTypedArray = context.getTheme().obtainStyledAttributes(attributeSet,
                R.styleable.CustomFont, defStyleAttr, 0);
        String fontFamily = null;
        try {
            fontFamily = giniTypedArray.getString(R.styleable.CustomFont_gvCustomFont);
        } finally {
            giniTypedArray.recycle();
        }

        final TypedArray typefaceTypedArray = context.getTheme().obtainStyledAttributes(
                attributeSet,
                new int[]{android.R.attr.textStyle}, defStyleAttr, 0);
        int fontStyle = Typeface.NORMAL;
        try {
            fontStyle = typefaceTypedArray.getInteger(0, Typeface.NORMAL);
        } finally {
            typefaceTypedArray.recycle();
        }

        setFont(textView, context, fontFamily, fontStyle);
    }

    /**
     * Custom font is set, if the font file was found and is valid. If the font file is not valid, the default font is
     * set. If the font file was not found a system font is assumed. If the system font is not valid, the default font
     * is set automatically.
     *
     * @param context    Android context
     * @param fontFamily system font name or custom font file name with extension
     * @param fontStyle  Typeface style constant
     */
    private static void setFont(@NonNull final TextView textView, @NonNull final Context context,
            @Nullable final String fontFamily, final int fontStyle) {
        if (TextUtils.isEmpty(fontFamily)) {
            // No font family: use default font with the font style
            setSystemFont(textView, null, fontStyle);
        } else if (!setCustomFont(textView, context, fontFamily, fontStyle)) {
            // Custom font couldn't be set (non-existing font family, system font or error)
            // Try it as a system font
            if (!setSystemFont(textView, fontFamily, fontStyle)) { // NOPMD
                // Font family couldn't be used: fall back to the default font with the font style
                setSystemFont(textView, null, fontStyle);
            }
        }
    }

    /**
     * Sets the custom font, if the font file can be loaded.
     *
     * @param context    Android context
     * @param fontPath custom font file name with extension
     * @param fontStyle  Typeface style constant
     * @return true, if custom font could be set, false otherwise
     */
    private static boolean setCustomFont(@NonNull final TextView textView,
            @NonNull final Context context,
            @NonNull final String fontPath, final int fontStyle) {
        boolean success = false;
        try {
            final Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            final Typeface styledTypeface = Typeface.create(typeface, fontStyle);
            textView.setTypeface(styledTypeface);
            success = true;
        } catch (final Exception e) {
            LOG.error("Typeface couldn't be created. Font file '{}' not usable.", fontPath, e);
        }
        return success;
    }

    /**
     * Sets the given system font family or the default one, if not available or null.
     *
     * @param fontFamily system font family name
     * @param fontStyle  Typeface style constant
     */
    private static boolean setSystemFont(@NonNull final TextView textView,
            @Nullable final String fontFamily,
            final int fontStyle) {
        boolean success = false;
        try {
            final Typeface typeface = Typeface.create(fontFamily, fontStyle);
            textView.setTypeface(typeface);
            success = true;
        } catch (final Exception e) {
            LOG.error("Typeface couldn't be created. Font family '{}' not found.", fontFamily, e);
        }
        return success;
    }

    private CustomFontHelper() {
    }
}
