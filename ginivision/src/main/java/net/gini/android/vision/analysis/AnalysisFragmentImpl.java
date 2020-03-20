package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import jersey.repackaged.jsr166e.CompletableFuture;

class AnalysisFragmentImpl extends AnalysisScreenContract.View {

    protected static final Logger LOG = LoggerFactory.getLogger(AnalysisFragmentImpl.class);

    private final FragmentImplCallback mFragment;
    private TextView mAnalysisMessageTextView;
    private ImageView mImageDocumentView;
    private RelativeLayout mLayoutRoot;
    private ProgressBar mProgressActivity;
    private LinearLayout mPdfOverlayLayout;
    private TextView mPdfTitleTextView;
    private TextView mPdfPageCountTextView;
    private LinearLayout mAnalysisOverlay;
    private AnalysisHintsAnimator mHintsAnimator;

    AnalysisFragmentImpl(final FragmentImplCallback fragment,
            @NonNull final Document document,
            final String documentAnalysisErrorMessage) {
        mFragment = fragment;
        if (mFragment.getActivity() == null) {
            throw new IllegalStateException("Missing activity for fragment.");
        }
        createPresenter(mFragment.getActivity(), document, documentAnalysisErrorMessage); // NOPMD - overridable for testing
    }

    @VisibleForTesting
    void createPresenter(@NonNull final Activity activity, @NonNull final Document document,
            final String documentAnalysisErrorMessage) {
        new AnalysisScreenPresenter(activity, this, document,
                documentAnalysisErrorMessage);
    }

    @Override
    public void hideError() {
        getPresenter().hideError();
    }

    @Override
    public void onNoExtractionsFound() {
        getPresenter().onNoExtractionsFound();
    }

    @Override
    public void onDocumentAnalyzed() {
        getPresenter().onDocumentAnalyzed();
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        getPresenter().showError(message, duration);
    }

    @Override
    public void showError(@NonNull final String message, @NonNull final String buttonTitle,
            @NonNull final View.OnClickListener onClickListener) {
        getPresenter().showError(message, buttonTitle, onClickListener);
    }

    @Override
    public void startScanAnimation() {
        getPresenter().startScanAnimation();
    }

    @Override
    public void stopScanAnimation() {
        getPresenter().stopScanAnimation();
    }

    @Override
    public void setListener(@NonNull final AnalysisFragmentListener listener) {
        getPresenter().setListener(listener);
    }

    @Override
    void showScanAnimation() {
        mProgressActivity.setVisibility(View.VISIBLE);
        mAnalysisMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    void hideScanAnimation() {
        mProgressActivity.setVisibility(View.GONE);
        mAnalysisMessageTextView.setVisibility(View.GONE);
    }

    @Override
    CompletableFuture<Void> waitForViewLayout() {
        final View view = mFragment.getView();
        if (view == null) {
            return CompletableFuture.completedFuture(null);
        }
        final CompletableFuture<Void> future = new CompletableFuture<>();
        LOG.debug("Observing the view layout");
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        future.complete(null);
                        mHintsAnimator.setContainerViewHeight(view.getHeight());
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
        view.requestLayout();
        return future;
    }

    @Override
    void showPdfInfoPanel() {
        mPdfOverlayLayout.setVisibility(View.VISIBLE);
        mAnalysisOverlay.setBackgroundColor(Color.TRANSPARENT);
        mAnalysisMessageTextView.setText("");
    }

    @Override
    void showPdfTitle(@NonNull final String title) {
        mPdfTitleTextView.setText(title);
    }

    @Override
    void showPdfPageCount(@NonNull final String pageCount) {
        mPdfPageCountTextView.setVisibility(View.VISIBLE);
        mPdfPageCountTextView.setText(pageCount);
    }

    @Override
    void hidePdfPageCount() {
        mPdfPageCountTextView.setVisibility(View.GONE);
    }

    @Override
    Size getPdfPreviewSize() {
        return new Size(mImageDocumentView.getWidth(), mImageDocumentView.getHeight());
    }

    @Override
    void showBitmap(@Nullable final Bitmap bitmap, final int rotationForDisplay) {
        rotateDocumentImageView(rotationForDisplay);
        mImageDocumentView.setImageBitmap(bitmap);
    }

    @Override
    void showAlertDialog(@NonNull final String message, @NonNull final String positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @Nullable final String negativeButtonTitle,
            @Nullable final DialogInterface.OnClickListener negativeButtonClickListener,
            @Nullable final DialogInterface.OnCancelListener cancelListener) {
        mFragment.showAlertDialog(message, positiveButtonTitle, positiveButtonClickListener,
                negativeButtonTitle, negativeButtonClickListener, cancelListener);
    }

    @Override
    void showErrorSnackbar(@NonNull final String message, final int duration,
            @Nullable final String buttonTitle,
            @Nullable final View.OnClickListener onClickListener) {
        if (mFragment.getActivity() == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, buttonTitle,
                onClickListener, ErrorSnackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    void hideErrorSnackbar() {
        if (mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.hideExisting(mLayoutRoot);
    }

    @Override
    void showHints(final List<AnalysisHint> hints) {
        mHintsAnimator.stop();
        mHintsAnimator.setHints(hints);
        mHintsAnimator.start();
    }

    private void rotateDocumentImageView(final int rotationForDisplay) {
        if (rotationForDisplay == 0) {
            return;
        }
        int newWidth = mLayoutRoot.getWidth();
        int newHeight = mLayoutRoot.getHeight();
        if (rotationForDisplay == 90 || rotationForDisplay == 270) {
            newWidth = mLayoutRoot.getHeight();
            newHeight = mLayoutRoot.getWidth();
        }

        final FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) mImageDocumentView.getLayoutParams();
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        mImageDocumentView.setLayoutParams(layoutParams);
        mImageDocumentView.setRotation(rotationForDisplay);
    }

    public void onCreate(final Bundle savedInstanceState) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        forcePortraitOrientationOnPhones(activity);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_analysis, container, false);
        bindViews(view);
        createHintsAnimator(view);
        return view;
    }

    private void bindViews(@NonNull final View view) {
        mLayoutRoot = view.findViewById(R.id.gv_layout_root);
        mImageDocumentView = view.findViewById(R.id.gv_image_picture);
        mProgressActivity = view.findViewById(R.id.gv_progress_activity);
        mAnalysisMessageTextView = view.findViewById(R.id.gv_analysis_message);
        mPdfOverlayLayout = view.findViewById(R.id.gv_pdf_info);
        mPdfTitleTextView = view.findViewById(R.id.gv_pdf_filename);
        mPdfPageCountTextView = view.findViewById(R.id.gv_pdf_page_count);
        mAnalysisOverlay = view.findViewById(R.id.gv_analysis_overlay);
    }

    private void createHintsAnimator(@NonNull final View view) {
        final ImageView hintImageView = view.findViewById(R.id.gv_analysis_hint_image);
        final TextView hintTextView = view.findViewById(R.id.gv_analysis_hint_text);
        final View hintContainer = view.findViewById(R.id.gv_analysis_hint_container);
        final TextView hintHeadlineTextView = view.findViewById(R.id.gv_analysis_hint_headline);
        mHintsAnimator = new AnalysisHintsAnimator(mFragment.getActivity().getApplication(),
                hintContainer, hintImageView, hintTextView, hintHeadlineTextView);
    }

    public void onStart() {
        getPresenter().start();
    }

    public void onDestroy() {
        getPresenter().stop();
        final Activity activity = mFragment.getActivity();
        if (activity != null && activity.isFinishing()) {
            getPresenter().finish();
        }
    }

    void onStop() {
        mHintsAnimator.stop();
    }

}
