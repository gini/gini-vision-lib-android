package net.gini.android.vision.onboarding;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class OnboardingPage implements Parcelable {

    private final int mTextResId;
    private final int mImageResId;

    public OnboardingPage(@StringRes int textResId, @DrawableRes int imageResId) {
        mTextResId = textResId;
        mImageResId = imageResId;
    }

    @StringRes
    public int getTextResId() {
        return mTextResId;
    }

    @DrawableRes
    public int getImageResId() {
        return mImageResId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTextResId);
        dest.writeInt(mImageResId);
    }

    public static final Creator<OnboardingPage> CREATOR = new Creator<OnboardingPage>() {
        @Override
        public OnboardingPage createFromParcel(Parcel in) {
            return new OnboardingPage(in);
        }

        @Override
        public OnboardingPage[] newArray(int size) {
            return new OnboardingPage[size];
        }
    };

    private OnboardingPage(Parcel in) {
        mTextResId = in.readInt();
        mImageResId = in.readInt();
    }
}
