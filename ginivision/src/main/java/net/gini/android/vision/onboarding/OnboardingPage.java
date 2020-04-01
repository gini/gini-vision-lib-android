package net.gini.android.vision.onboarding;

import android.os.Parcel;
import android.os.Parcelable;

import net.gini.android.vision.camera.CameraActivity;

import java.util.ArrayList;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * <p>
 *     The {@code OnboardingPage} is used by the Onboarding Fragment to display an image and a short text.
 * </p>
 * <p>
 *     Use this class to show a different number of pages in the Onboarding Screen. Customizing the default onboarding pages can be done via overriding of app resources.
 * </p>
 * <p>
 *     When using the Screen API set an {@link java.util.ArrayList} containing {@code OnboardingPage} objects as the {@link CameraActivity#EXTRA_IN_ONBOARDING_PAGES} when starting the {@link CameraActivity}.
 * </p>
 * <p>
 *     When using the Componenent API provide an {@link java.util.ArrayList} containing {@code OnboardingPage} objects as the argument for the Onboarding Fragment factory method {@link OnboardingFragmentStandard#createInstance(ArrayList)} or {@link OnboardingFragmentCompat#createInstance(ArrayList)}.
 * </p>
 */
public class OnboardingPage implements Parcelable {

    private final int mTextResId;
    private final int mImageResId;
    private final boolean mTransparent;
    private final boolean mRotateImageForLandscape;

    /**
     * <p>
     *     Create a new onboarding page with the desired string resource and drawable resource.
     * </p>
     * <p>
     *     <b>Note:</b> the string should be a short sentence.
     * </p>
     * @param textResId a string resource id which will be shown in the onboarding page
     * @param imageResId a drawable resource id which will be shown in the onboarding page
     */
    public OnboardingPage(@StringRes final int textResId, @DrawableRes final int imageResId) {
        this(textResId, imageResId, false);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    OnboardingPage(@StringRes final int textResId, @DrawableRes final int imageResId, final boolean transparent) {
        this(textResId, imageResId, transparent, false);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    OnboardingPage(@StringRes final int textResId, @DrawableRes final int imageResId, final boolean transparent,
            final boolean rotateImageForLandscape) {
        mTextResId = textResId;
        mImageResId = imageResId;
        mTransparent = transparent;
        mRotateImageForLandscape = rotateImageForLandscape;
    }

    /**
     * @return the string resource id of the text shown on the onboarding page
     */
    @StringRes
    public int getTextResId() {
        return mTextResId;
    }

    /**
     * @return the drawable resource id of the text shown on the onboarding page
     */
    @DrawableRes
    public int getImageResId() {
        return mImageResId;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public boolean isTransparent() {
        return mTransparent;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public boolean shouldRotateImageForLandscape() {
        return mRotateImageForLandscape;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(mTextResId);
        dest.writeInt(mImageResId);
        dest.writeInt(mTransparent ? 1 : 0);
        dest.writeInt(mRotateImageForLandscape ? 1 : 0);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final Creator<OnboardingPage> CREATOR = new Creator<OnboardingPage>() {
        @Override
        public OnboardingPage createFromParcel(final Parcel in) {
            return new OnboardingPage(in);
        }

        @Override
        public OnboardingPage[] newArray(final int size) {
            return new OnboardingPage[size];
        }
    };

    private OnboardingPage(@NonNull final Parcel in) {
        mTextResId = in.readInt();
        mImageResId = in.readInt();
        mTransparent = in.readInt() == 1;
        mRotateImageForLandscape = in.readInt() == 1;
    }
}
