package net.gini.android.vision.network.model;


import android.os.Parcel;
import android.os.Parcelable;

public class GiniVisionBox implements Parcelable {

    public static final Creator<GiniVisionBox> CREATOR = new Creator<GiniVisionBox>() {

        @Override
        public GiniVisionBox createFromParcel(final Parcel in) {
            final int pageNumber = in.readInt();
            final double left = in.readDouble();
            final double top = in.readDouble();
            final double width = in.readDouble();
            final double height = in.readDouble();
            return new GiniVisionBox(pageNumber, left, top, width, height);
        }

        @Override
        public GiniVisionBox[] newArray(final int size) {
            return new GiniVisionBox[size];
        }
    };

    private final int mPageNumber;
    private final double mLeft;
    private final double mTop;
    private final double mWidth;
    private final double mHeight;

    public GiniVisionBox(final int pageNumber, final double left, final double top, final double width,
            final double height) {
        mPageNumber = pageNumber;
        mLeft = left;
        mTop = top;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(mPageNumber);
        dest.writeDouble(mLeft);
        dest.writeDouble(mTop);
        dest.writeDouble(mWidth);
        dest.writeDouble(mHeight);
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public double getLeft() {
        return mLeft;
    }

    public double getTop() {
        return mTop;
    }

    public double getWidth() {
        return mWidth;
    }

    public double getHeight() {
        return mHeight;
    }

    @Override
    public String toString() {
        return "GiniVisionBox{" +
                "mPageNumber=" + mPageNumber +
                ", mLeft=" + mLeft +
                ", mTop=" + mTop +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                '}';
    }
}
