package net.gini.android.vision.review;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.camera.photo.Photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageFragment extends Fragment {

    private static final Logger LOG = LoggerFactory.getLogger(ImageFragment.class);

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";
    private static final String ARGS_ERROR_MESSAGE = "GV_ARGS_ERROR_MESSAGE";

    private RotatableImageViewContainer mImageViewContainer;

    private ImageDocument mDocument;
    private String mErrorMessage;
    private TextView mErrorView;

    public static ImageFragment createInstance(@Nullable final ImageDocument document,
            @Nullable final String errorMessage) {
        final ImageFragment fragment = new ImageFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARGS_DOCUMENT, document);
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
            mDocument = arguments.getParcelable(ARGS_DOCUMENT);
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
        final Context context = getContext();
        if (context != null && mDocument != null) {
            // WIP-MM: show loading indicator
            GiniVision.getInstance().internal().getPhotoMemoryCache()
                    .getPhoto(context, mDocument, new AsyncCallback<Photo>() {
                        @Override
                        public void onSuccess(final Photo result) {
                            mImageViewContainer.getImageView().setImageBitmap(
                                    result.getBitmapPreview());
                            rotateImageView(mDocument.getRotationForDisplay(), false);
                        }

                        @Override
                        public void onError(final Exception exception) {
                            LOG.error("Failed to create preview bitmap", exception);
                        }
                    });
        }
    }

    private void rotateImageView(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageView(degrees, animated);
    }

    void rotateImageViewBy(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageViewBy(degrees, animated);
    }
}
