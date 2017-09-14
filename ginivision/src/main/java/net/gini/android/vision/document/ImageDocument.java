package net.gini.android.vision.document;

import static net.gini.android.vision.internal.util.StreamHelper.inputStreamToByteArray;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionDocument;
import net.gini.android.vision.internal.camera.photo.Photo;

import java.io.IOException;
import java.io.InputStream;

public class ImageDocument extends GiniVisionDocument {

    private final int mRotationForDisplay;

    @NonNull
    public static ImageDocument fromPhoto(@NonNull final Photo photo) {
        return new ImageDocument(photo.getJpeg(), photo.getRotationForDisplay());
    }

    @NonNull
    public static ImageDocument fromIntent(@NonNull final Intent intent, @NonNull Context context)
            throws IOException {
        if (intent.getData() == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(intent.getData());
            if (inputStream == null) {
                throw new IllegalStateException("Couldn't open input stream from intent data");
            }
            final byte[] fileData = inputStreamToByteArray(inputStream);
            return new ImageDocument(fileData, 0);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ImageDocument(@NonNull final byte[] data, int rotationForDisplay) {
        super(Type.IMAGE, data);
        mRotationForDisplay = rotationForDisplay;
    }

    /**
     * <p>
     * The amount of clockwise rotation needed to display the image in the correct orientation.
     * </p>
     * <p>
     * Degrees are positive and multiples of 90.
     * </p>
     *
     * @return degrees by which the image should be rotated clockwise before displaying
     */
    public int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mRotationForDisplay);
    }

    public static final Creator<ImageDocument> CREATOR = new Creator<ImageDocument>() {
        @Override
        public ImageDocument createFromParcel(Parcel in) {
            return new ImageDocument(in);
        }

        @Override
        public ImageDocument[] newArray(int size) {
            return new ImageDocument[size];
        }
    };

    private ImageDocument(Parcel in) {
        super(in);
        mRotationForDisplay = in.readInt();
    }
}
