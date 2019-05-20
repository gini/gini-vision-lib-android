package net.gini.android.vision.onboarding;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import net.gini.android.vision.internal.util.ContextHelper;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class OnboardingPagePresenter extends OnboardingPageContract.Presenter {

    private OnboardingPage mPage;

    OnboardingPagePresenter(
            @NonNull final Application app,
            @NonNull final OnboardingPageContract.View view) {
        super(app, view);
        view.setPresenter(this);
    }

    @Override
    void setPage(@NonNull final OnboardingPage page) {
        mPage = page;
    }

    @Override
    public void start() {
        getView().showImage(getImageDrawable());
        getView().showText(getText());
        if (mPage.isTransparent()) {
            getView().showTransparentBackground();
        }
    }

    @Override
    public void stop() {

    }

    @Nullable
    private Drawable getImageDrawable() {
        if (mPage.getImageResId() == 0) {
            return null;
        }
        final Drawable drawable = ContextCompat.getDrawable(getApp(), mPage.getImageResId());
        if (!ContextHelper.isPortraitOrientation(getApp())
                && mPage.shouldRotateImageForLandscape()) {
            return createRotatedDrawableForLandscape(drawable);
        } else {
            return drawable;
        }
    }

    private Drawable createRotatedDrawableForLandscape(final Drawable drawable) {
        final Bitmap bitmap = BitmapFactory.decodeResource(getApp().getResources(),
                mPage.getImageResId());
        if (bitmap == null) {
            return drawable;
        }
        final Matrix matrix = new Matrix();
        matrix.postRotate(270f);
        final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(getApp().getResources(), rotatedBitmap);
    }

    @Nullable
    private String getText() {
        if (mPage.getTextResId() == 0) {
            return null;
        }
        return getApp().getText(mPage.getTextResId()).toString();
    }
}
