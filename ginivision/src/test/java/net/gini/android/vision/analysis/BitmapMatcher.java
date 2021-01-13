package net.gini.android.vision.analysis;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class BitmapMatcher extends TypeSafeMatcher<View> {

    private final Bitmap mBitmap;

    public static BitmapMatcher withBitmap(@Nullable final Bitmap bitmap) {
        return new BitmapMatcher(bitmap);
    }

    private BitmapMatcher(final Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("has bitmap " + mBitmap);
    }

    @Override
    protected boolean matchesSafely(final View item) {
        if (item instanceof ImageView) {
            final ImageView imageView = (ImageView) item;
            if (imageView.getDrawable() instanceof BitmapDrawable) {
                final BitmapDrawable bitmapDrawable =
                        (BitmapDrawable) imageView.getDrawable();
                return bitmapDrawable.getBitmap().sameAs(mBitmap);
            }
        }
        return false;
    }
}
