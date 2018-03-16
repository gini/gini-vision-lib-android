package net.gini.android.vision.review;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.camera.photo.Photo;

public class ImageFragment extends Fragment {

    private static final String ARGS_PHOTO = "GV_ARGS_PHOTO";
    private static final String ARGS_ERROR_MESSAGE = "GV_ARGS_ERROR_MESSAGE";

    private RotatableImageViewContainer mImageViewContainer;

    private Photo mPhoto;
    private String mErrorMessage;
    private TextView mErrorView;

    public static ImageFragment createInstance(@Nullable final Photo photo,
            @Nullable final String errorMessage) {
        final ImageFragment fragment = new ImageFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARGS_PHOTO, photo);
        args.putString(ARGS_ERROR_MESSAGE, errorMessage);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mErrorMessage = arguments.getString(ARGS_ERROR_MESSAGE);
            mPhoto = arguments.getParcelable(ARGS_PHOTO);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_image, container, false);
        mImageViewContainer = view.findViewById(R.id.gv_image_container);
        mErrorView = view.findViewById(R.id.gv_text_error);
        mErrorView.setText(mErrorMessage);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPhoto != null) {
            mImageViewContainer.getImageView().setImageBitmap(mPhoto.getBitmapPreview());
            rotateImageView(mPhoto.getRotationForDisplay(), false);
        }
    }

    private void rotateImageView(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageView(degrees, animated);
    }

    void rotateImageViewBy(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageViewBy(degrees, animated);
    }
}
