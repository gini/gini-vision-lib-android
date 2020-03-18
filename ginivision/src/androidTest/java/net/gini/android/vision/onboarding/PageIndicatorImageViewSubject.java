package net.gini.android.vision.onboarding;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import javax.annotation.Nullable;

import androidx.annotation.DrawableRes;

public class PageIndicatorImageViewSubject extends
        Subject<PageIndicatorImageViewSubject, ImageView> {

    public static SubjectFactory<PageIndicatorImageViewSubject, ImageView> pageIndicatorImageView() {
        return new SubjectFactory<PageIndicatorImageViewSubject, ImageView>() {
            @Override
            public PageIndicatorImageViewSubject getSubject(final FailureStrategy fs, final ImageView that) {
                return new PageIndicatorImageViewSubject(fs, that);
            }
        };
    }

    public PageIndicatorImageViewSubject(final FailureStrategy failureStrategy,
            @Nullable final ImageView subject) {
        super(failureStrategy, subject);
    }

    public void showsDrawable(@DrawableRes final int drawableResId) {
        final ImageView imageView = getSubject();

        final BitmapDrawable expectedDrawable = (BitmapDrawable) imageView.getResources().getDrawable(
                drawableResId);
        if (expectedDrawable == null || expectedDrawable.getBitmap() == null) {
            fail("shows drawable with id " + drawableResId + " - no such drawable");
        }
        // NullPointerException warning is not relevant, fail() above will prevent it
        //noinspection ConstantConditions
        final Bitmap expectedBitmap = expectedDrawable.getBitmap();

        final BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        final Bitmap bitmap = bitmapDrawable.getBitmap();

        if (!bitmap.sameAs(expectedBitmap)) {
            fail("shows drawable with id " + drawableResId);
        }
    }
}
