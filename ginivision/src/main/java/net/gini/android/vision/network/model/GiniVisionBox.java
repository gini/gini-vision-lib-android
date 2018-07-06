package net.gini.android.vision.network.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contains a <a href="http://developer.gini.net/gini-api/html/document_extractions.html#bounding-box">bounding
 * box</a> for a Gini API extraction. The bounding box describes the page and the position where the
 * extraction originates.
 */
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

    /**
     * Create a new bounding box for an extraction.
     *
     * @param pageNumber page on which the box can be found, starting with 1
     * @param left       distance from the left edge of the page.
     * @param top        distance from the top edge of the page
     * @param width      horizontal dimension of the box
     * @param height     vertical dimension of the box
     */
    public GiniVisionBox(final int pageNumber, final double left, final double top,
            final double width,
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

    /**
     * @return page on which the box can be found, starting with 1
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    /**
     * @return distance from the left edge of the page
     */
    public double getLeft() {
        return mLeft;
    }

    /**
     * @return distance from the top edge of the page
     */
    public double getTop() {
        return mTop;
    }

    /**
     * @return horizontal dimension of the box
     */
    public double getWidth() {
        return mWidth;
    }

    /**
     * @return vertical dimension of the box
     */
    public double getHeight() {
        return mHeight;
    }

    @Override
    public String toString() {
        return "GiniVisionBox{"
                + "mPageNumber=" + mPageNumber
                + ", mLeft=" + mLeft
                + ", mTop=" + mTop
                + ", mWidth=" + mWidth
                + ", mHeight=" + mHeight
                + '}';
    }
}
