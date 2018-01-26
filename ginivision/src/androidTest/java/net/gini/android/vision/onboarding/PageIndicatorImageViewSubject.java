package net.gini.android.vision.onboarding;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import javax.annotation.Nullable;

public class PageIndicatorImageViewSubject extends
        Subject<PageIndicatorImageViewSubject, ImageView> {

    public static SubjectFactory<PageIndicatorImageViewSubject, ImageView> pageIndicatorImageView() {
        return new SubjectFactory<PageIndicatorImageViewSubject, ImageView>() {
            @Override
            public PageIndicatorImageViewSubject getSubject(FailureStrategy fs, ImageView that) {
                return new PageIndicatorImageViewSubject(fs, that);
            }
        };
    }

    public PageIndicatorImageViewSubject(FailureStrategy failureStrategy,
            @Nullable ImageView subject) {
        super(failureStrategy, subject);
    }

    public void showsDrawable(@DrawableRes int drawableResId) {
        ImageView imageView = getSubject();

        BitmapDrawable expectedDrawable = (BitmapDrawable) imageView.getResources().getDrawable(
                drawableResId);
        if (expectedDrawable == null || expectedDrawable.getBitmap() == null) {
            fail("shows drawable with id " + drawableResId + " - no such drawable");
        }
        // NullPointerException warning is not relevant, fail() above will prevent it
        //noinspection ConstantConditions
        Bitmap expectedBitmap = expectedDrawable.getBitmap();

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        if (!bitmap.sameAs(expectedBitmap)) {
            fail("shows drawable with id " + drawableResId);
        }
    }
}
