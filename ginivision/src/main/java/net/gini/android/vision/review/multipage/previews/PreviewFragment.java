package net.gini.android.vision.review.multipage.previews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.review.RotatableImageViewContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @exclude
 */
public class PreviewFragment extends Fragment {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewFragment.class);

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";
    private static final String ARGS_ERROR_MESSAGE = "GV_ARGS_ERROR_MESSAGE";
    private static final String ARGS_ERROR_BUTTON_ACTION = "ARGS_ERROR_BUTTON_ACTION";

    private RotatableImageViewContainer mImageViewContainer;

    private ImageDocument mDocument;
    private String mErrorMessage;
    private ProgressBar mActivityIndicator;
    private boolean mStopped = true;
    private ErrorButtonAction mErrorButtonAction;

    public static PreviewFragment createInstance(@Nullable final ImageDocument document,
            @Nullable final String errorMessage,
            @Nullable final ErrorButtonAction errorButtonAction) {
        final PreviewFragment fragment = new PreviewFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARGS_DOCUMENT, document);
        args.putString(ARGS_ERROR_MESSAGE, errorMessage);
        args.putSerializable(ARGS_ERROR_BUTTON_ACTION, errorButtonAction);
        fragment.setArguments(args);
        return fragment;
    }

    public PreviewFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mErrorMessage = arguments.getString(ARGS_ERROR_MESSAGE);
            mDocument = arguments.getParcelable(ARGS_DOCUMENT);
            mErrorButtonAction = (ErrorButtonAction) arguments.getSerializable(
                    ARGS_ERROR_BUTTON_ACTION);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_item_multi_page_preview, container,
                false);
        mImageViewContainer = view.findViewById(R.id.gv_image_container);
        mActivityIndicator = view.findViewById(R.id.gv_activity_indicator);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mStopped = false;
        LOG.debug("Started ({})", this);
        final Context context = getContext();
        if (context == null) {
            return;
        }
        if (shouldShowPreviewImage()) {
            LOG.debug("Loading preview bitmap ({})", this);
            showActivityIndicator();
            if (GiniVision.hasInstance()) {
                GiniVision.getInstance().internal().getPhotoMemoryCache()
                        .get(context, mDocument, new AsyncCallback<Photo, Exception>() {
                            @Override
                            public void onSuccess(final Photo result) {
                                LOG.debug("Preview bitmap received ({})", this);
                                if (mStopped) {
                                    LOG.debug("Stopped: preview discarded ({})", this);
                                    return;
                                }
                                hideActivityIndicator();
                                LOG.debug("Showing preview ({})", this);
                                mImageViewContainer.getImageView().setImageBitmap(
                                        result.getBitmapPreview());
                                LOG.debug("Applying rotation ({})", this);
                                rotateImageView(mDocument.getRotationForDisplay(), false);
                            }

                            @Override
                            public void onError(final Exception exception) {
                                LOG.error("Failed to create preview bitmap ({})", this, exception);
                                if (mStopped) {
                                    LOG.debug("Stopped: ignoring error ({})", this);
                                    return;
                                }
                                hideActivityIndicator();
                                LOG.debug("Showing error ({})", this);
                                showPreviewError(context);
                            }

                            @Override
                            public void onCancelled() {
                                // Not used
                            }
                        });
            } else {
                LOG.error(
                        "Cannot show preview. GiniVision instance not available. Create it with GiniVision.newInstance().");
            }
        }
        if (!TextUtils.isEmpty(mErrorMessage)) {
            showErrorMessage(context);
        }
    }

    private void showPreviewError(final Context context) {
        final View view = getView();
        if (view == null) {
            return;
        }
        ErrorSnackbar.make(context, (RelativeLayout) view, ErrorSnackbar.Position.TOP,
                context.getString(R.string.gv_multi_page_review_image_preview_error),
                null, null, ErrorSnackbar.LENGTH_INDEFINITE)
                .showWithoutAnimation();
    }

    private void showErrorMessage(final Context context) {
        final View view = getView();
        if (view == null) {
            return;
        }
        final String buttonTitle = getErrorButtonTitle(context);
        ErrorSnackbar.make(context, (RelativeLayout) view, ErrorSnackbar.Position.TOP,
                mErrorMessage,
                buttonTitle,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final PreviewFragmentListener listener = getListener();
                        if (listener != null && mErrorButtonAction != null) {
                            switch (mErrorButtonAction) {
                                case RETRY:
                                    listener.onRetryUpload(mDocument);
                                    break;
                                case DELETE:
                                    listener.onDeleteDocument(mDocument);
                                    break;
                            }
                        }
                    }
                }, ErrorSnackbar.LENGTH_INDEFINITE)
                .showWithoutAnimation();
    }

    private String getErrorButtonTitle(@NonNull final Context context) {
        switch (mErrorButtonAction) {
            case RETRY:
                return context.getString(R.string.gv_multi_page_review_upload_error_retry);
            case DELETE:
                return context.getString(R.string.gv_multi_page_review_delete_invalid_document);
        }
        return null;
    }

    @Nullable
    private PreviewFragmentListener getListener() {
        if (getParentFragment() instanceof PreviewFragmentListener) {
            return (PreviewFragmentListener) getParentFragment();
        }
        return null;
    }

    private boolean shouldShowPreviewImage() {
        return mDocument != null
                && mImageViewContainer.getImageView().getDrawable() == null;
    }

    private void showActivityIndicator() {
        mActivityIndicator.setVisibility(View.VISIBLE);
    }

    private void hideActivityIndicator() {
        mActivityIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        LOG.debug("Stopped ({})", this);
        mStopped = true;
    }

    private void rotateImageView(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageView(degrees, animated);
    }

    public void rotateImageViewBy(final int degrees, final boolean animated) {
        mImageViewContainer.rotateImageViewBy(degrees, animated);
    }

    public enum ErrorButtonAction {
        RETRY,
        DELETE
    }
}
