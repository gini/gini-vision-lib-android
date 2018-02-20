package net.gini.android.vision.review;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.camera.photo.Photo;

public class ImageFragment extends Fragment {

    private static final String ARGS_PHOTO = "GV_ARGS_PHOTO";

    private RotatableImageViewContainer mImageViewContainer;

    private Photo photo;

    public static ImageFragment createInstance(@NonNull final Photo photo) {
        final ImageFragment fragment = new ImageFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARGS_PHOTO, photo);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getParcelable(ARGS_PHOTO) != null) {
            photo = getArguments().getParcelable(ARGS_PHOTO);
        } else {
            throw new IllegalStateException(
                    "Missing bitmap argument. Use the factory method to create instances.");
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_image, container, false);
        mImageViewContainer = view.findViewById(R.id.gv_image_container);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mImageViewContainer.getImageView().setImageBitmap(photo.getBitmapPreview());
        rotateImageView(photo.getRotationForDisplay(), false);
    }

    private void rotateImageView(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageView(degrees, animated);
    }

    void rotateImageViewBy(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageViewBy(degrees, animated);
    }
}
