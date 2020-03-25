package net.gini.android.vision.internal.ui;

import static net.gini.android.vision.internal.ui.CustomFontHelper.parseAttributesAndSetFont;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Internal use only.
 *
 * Custom Button with an additional 'gvCustomFont' attribute. System font names or font file paths (full path in the assets folder)
 * can be used.
 *
 * <pre> {@code
 *    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *       xmlns:gini="http://schemas.android.com/apk/res-auto"
 *       android:layout_width="match_parent"
 *       android:layout_height="match_parent"
 *       android:orientation="vertical">
 *
 *       <net.gini.android.vision.ui.CustomFontButton
 *           android:id="@+id/giniTextView"
 *           android:layout_width="wrap_content"
 *           android:layout_height="wrap_content"
 *           gini:gvCustomFont="sans-serif-light" />
 *   </LinearLayout>
 * }</pre>
 *
 * <pre> {@code
 *  <net.gini.android.vision.ui.CustomFontButton
 *      gini:gvCustomFont="myFonts/Cave-Story.ttf" />
 * }</pre>
 *
 * @suppress
 */
public class CustomFontButton extends androidx.appcompat.widget.AppCompatButton {

    public CustomFontButton(final Context context) {
        super(context);
    }

    public CustomFontButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        configureFont(context, attrs, 0);
    }

    public CustomFontButton(final Context context, final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configureFont(context, attrs, defStyleAttr);
    }

    private void configureFont(final Context context, final AttributeSet attrs,
            final int defStyleAttr) {
        parseAttributesAndSetFont(this, context, attrs, defStyleAttr);
    }
}
