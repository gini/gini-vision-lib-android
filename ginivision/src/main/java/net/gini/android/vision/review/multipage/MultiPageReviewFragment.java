package net.gini.android.vision.review.multipage;

import static net.gini.android.vision.document.GiniVisionDocumentError.ErrorCode.FILE_VALIDATION_FAILED;
import static net.gini.android.vision.document.GiniVisionDocumentError.ErrorCode.UPLOAD_FAILED;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.internal.util.FileImportHelper.showAlertIfOpenWithDocumentAndAppIsDefault;
import static net.gini.android.vision.review.multipage.previews.PreviewFragment.ErrorButtonAction.DELETE;
import static net.gini.android.vision.review.multipage.previews.PreviewFragment.ErrorButtonAction.RETRY;
import static net.gini.android.vision.review.multipage.thumbnails.ThumbnailsAdapter.getNewPositionAfterDeletion;
import static net.gini.android.vision.tracking.EventTrackingHelper.trackReviewScreenEvent;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.network.NetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.AlertDialogHelperCompat;
import net.gini.android.vision.internal.util.FileImportHelper;
import net.gini.android.vision.review.multipage.previews.PreviewFragment;
import net.gini.android.vision.review.multipage.previews.PreviewFragmentListener;
import net.gini.android.vision.review.multipage.previews.PreviewsAdapter;
import net.gini.android.vision.review.multipage.previews.PreviewsAdapterListener;
import net.gini.android.vision.review.multipage.previews.PreviewsPageChangeHandler;
import net.gini.android.vision.review.multipage.previews.PreviewsPageChangeListener;
import net.gini.android.vision.review.multipage.thumbnails.ThumbnailsAdapter;
import net.gini.android.vision.review.multipage.thumbnails.ThumbnailsAdapterListener;
import net.gini.android.vision.review.multipage.thumbnails.ThumbnailsTouchHelperCallback;
import net.gini.android.vision.tracking.ReviewScreenEvent;
import net.gini.android.vision.tracking.ReviewScreenEvent.UPLOAD_ERROR_DETAILS_MAP_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 07.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * <h3>Component API</h3>
 *
 * <p> When you use the Compontent API and have enabled the multi-page feature, the {@code
 * MultiPageReviewFragment} displays the photographed or imported images and allows the user to
 * review them by checking the order, sharpness, quality and orientation of the images. The user can
 * correct the order by dragging the thumbnails of the images and can also correct the orientation
 * by rotating the images.
 *
 * <p> <b>Important:</b>
 *
 * <ul>
 *
 * <li> A {@link GiniVision} instance is required to use the {@code MultiPageReviewFragment}
 *
 * <li> Your Activity hosting this Fragment must extend the {@link androidx.appcompat.app.AppCompatActivity}
 * and use an AppCompat Theme.
 *
 * </ul>
 *
 * <p> Include the {@code MultiPageReviewFragment} into your layout by using the {@link
 * MultiPageReviewFragment#createInstance()} factory method to create an instance and display it
 * using the {@link androidx.fragment.app.FragmentManager}.
 *
 * <p> A {@link MultiPageReviewFragmentListener} instance must be available until the {@code
 * MultiPageReviewFragment} is attached to an activity. Failing to do so will throw an exception.
 * The listener instance can be provided either implicitly by making the hosting Activity implement
 * the {@link MultiPageReviewFragmentListener} interface or explicitly by setting the listener using
 * {@link MultiPageReviewFragment#setListener(MultiPageReviewFragmentListener)}.
 *
 * <p> Your Activity is automatically set as the listener in {@link MultiPageReviewFragment#onCreate(Bundle)}.
 *
 * <h3>Customizing the Multi-Page Review Screen</h3>
 *
 * See the {@link MultiPageReviewActivity} for details.
 */
public class MultiPageReviewFragment extends Fragment implements MultiPageReviewFragmentInterface,
        PreviewFragmentListener, FragmentImplCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MultiPageReviewFragment.class);

    @VisibleForTesting
    Map<String, Boolean> mDocumentUploadResults = new HashMap<>();
    @VisibleForTesting
    ImageMultiPageDocument mMultiPageDocument;
    private MultiPageReviewFragmentListener mListener;
    private ViewPager mPreviewsPager;
    private PreviewsAdapter mPreviewsAdapter;
    private TextView mPageIndicator;
    private RecyclerView mThumbnailsRecycler;
    @VisibleForTesting
    ThumbnailsAdapter mThumbnailsAdapter;
    private RecyclerView.SmoothScroller mThumbnailsScroller;
    private ImageButton mButtonNext;
    private ImageButton mRotateButton;
    private ImageButton mDeleteButton;
    private TextView mReorderPagesTip;
    private boolean mNextClicked;
    private boolean mPreviewsShown;

    public static MultiPageReviewFragment createInstance() {
        return new MultiPageReviewFragment();
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forcePortraitOrientationOnPhones(getActivity());
        initMultiPageDocument();
        initListener();
    }

    @Override
    public void showAlertDialog(@NonNull final String message,
            @NonNull final String positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @Nullable final String negativeButtonTitle,
            @Nullable final DialogInterface.OnClickListener negativeButtonClickListener,
            @Nullable final DialogInterface.OnCancelListener cancelListener) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        AlertDialogHelperCompat.showAlertDialog(activity, message, positiveButtonTitle,
                positiveButtonClickListener, negativeButtonTitle, negativeButtonClickListener,
                cancelListener);
    }

    private void initMultiPageDocument() {
        if (GiniVision.hasInstance()) {
            mMultiPageDocument = GiniVision.getInstance().internal()
                    .getImageMultiPageDocumentMemoryStore().getMultiPageDocument();
        }
        if (mMultiPageDocument == null) {
            throw new IllegalStateException(
                    "MultiPageReviewFragment requires an ImageMultiPageDocuments.");
        }
        initUploadResults();
    }

    private void initUploadResults() {
        for (final ImageDocument imageDocument : mMultiPageDocument.getDocuments()) {
            mDocumentUploadResults.put(imageDocument.getId(), false);
        }
    }

    private void initListener() {
        if (getActivity() instanceof MultiPageReviewFragmentListener) {
            mListener = (MultiPageReviewFragmentListener) getActivity();
        } else if (mListener == null) {
            throw new IllegalStateException(
                    "MultiPageReviewFragmentListener not set. "
                            + "You can set it with MultiPageReviewFragment#setListener() or "
                            + "by making the host activity implement the MultiPageReviewFragmentListener.");
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_multi_page_review, container,
                false);
        bindViews(view);
        setInputHandlers();
        setupPreviewsViewPager();
        setupThumbnailsRecyclerView();
        updateNextButtonVisibility();
        return view;
    }

    private void setupPreviewsViewPager() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final PreviewsAdapterListener previewsAdapterListener = new PreviewsAdapterListener() {
            @Override
            public PreviewFragment.ErrorButtonAction getErrorButtonAction(
                    @NonNull final GiniVisionDocumentError documentError) {
                if (documentError.getErrorCode() == UPLOAD_FAILED) {
                    return RETRY;
                } else if (documentError.getErrorCode() == FILE_VALIDATION_FAILED) {
                    return DELETE;
                }
                return null;
            }
        };

        mPreviewsAdapter = new PreviewsAdapter(getChildFragmentManager(), mMultiPageDocument,
                previewsAdapterListener);
        mPreviewsPager.setAdapter(mPreviewsAdapter);

        final PreviewsPageChangeHandler previewsPageChangeHandler = new PreviewsPageChangeHandler(
                new PreviewsPageChangeListener() {
                    @Override
                    public void onPageSelected(final int position) {
                        updatePageIndicator(position);
                        if (!mThumbnailsAdapter.isThumbnailHighlighted(position)) {
                            highlightThumbnail(position);
                        }
                    }
                });
        mPreviewsPager.addOnPageChangeListener(previewsPageChangeHandler);
    }

    private void highlightThumbnail(final int position) {
        mThumbnailsAdapter.highlightPosition(position);
        scrollToThumbnail(position);
    }

    private void setupThumbnailsRecyclerView() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false);
        mThumbnailsRecycler.setLayoutManager(layoutManager);

        final ThumbnailsAdapterListener thumbnailsAdapterListener =
                new ThumbnailsAdapterListener() {
                    @Override
                    public void onThumbnailMoved() {
                        final PagerAdapter adapter = mPreviewsPager.getAdapter();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onThumbnailSelected(final int position) {
                        mPreviewsPager.setCurrentItem(position);
                    }

                    @Override
                    public void onPlusButtonClicked() {
                        mListener.onReturnToCameraScreen();
                    }
                };

        mThumbnailsAdapter = new ThumbnailsAdapter(activity, mMultiPageDocument,
                thumbnailsAdapterListener, shouldShowPlusButton());
        mThumbnailsRecycler.setAdapter(mThumbnailsAdapter);

        mThumbnailsScroller = new LinearSmoothScroller(activity);

        final ItemTouchHelper.Callback callback =
                new ThumbnailsTouchHelperCallback(mThumbnailsAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mThumbnailsRecycler);
        mThumbnailsAdapter.setItemTouchHelper(touchHelper);

        // Disable item change animations to remove flickering when highlighting a thumbnail
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setChangeDuration(0);
        mThumbnailsRecycler.setItemAnimator(itemAnimator);
    }

    private boolean shouldShowPlusButton() {
        return mMultiPageDocument.getImportMethod() != Document.ImportMethod.OPEN_WITH;
    }

    private void bindViews(final View view) {
        mButtonNext = view.findViewById(R.id.gv_button_next);
        mPreviewsPager = view.findViewById(R.id.gv_view_pager);
        mPageIndicator = view.findViewById(R.id.gv_page_indicator);
        mThumbnailsRecycler = view.findViewById(R.id.gv_thumbnails_panel);
        mRotateButton = view.findViewById(R.id.gv_button_rotate);
        mDeleteButton = view.findViewById(R.id.gv_button_delete);
        mReorderPagesTip = view.findViewById(R.id.gv_reorder_pages_tip);
    }

    private void setInputHandlers() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onNextButtonClicked();
            }
        });
        mRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onRotateButtonClicked();
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onDeleteButtonClicked();
            }
        });
    }

    private void onDeleteButtonClicked() {
        final int deletedItem = mPreviewsPager.getCurrentItem();
        deleteDocumentAndUpdateUI(deletedItem);
    }

    private void deleteDocumentAndUpdateUI(final int position) {
        final ImageDocument document = mMultiPageDocument.getDocuments().get(position);
        deleteDocumentAndUpdateUI(document);
    }

    private void deleteDocumentAndUpdateUI(@NonNull final ImageDocument document) {
        if (mMultiPageDocument.getDocuments().size() == 1) {
            final FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            if (mMultiPageDocument.getImportMethod() == Document.ImportMethod.OPEN_WITH) {
                new AlertDialog.Builder(activity)
                        .setMessage(
                                R.string.gv_multi_page_review_file_import_delete_last_page_dialog_message)
                        .setPositiveButton(
                                R.string.gv_multi_page_review_file_import_delete_last_page_dialog_positive_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog,
                                            final int which) {
                                        mListener.onImportedDocumentReviewCancelled();
                                    }
                                })
                        .setNegativeButton(
                                R.string.gv_multi_page_review_file_import_delete_last_page_dialog_negative_button,
                                null)
                        .create().show();
            } else {
                doDeleteDocumentAndUpdateUI(document);
                mListener.onReturnToCameraScreen();
            }
        } else {
            doDeleteDocumentAndUpdateUI(document);
        }
    }

    private void doDeleteDocumentAndUpdateUI(@NonNull final ImageDocument document) {
        final int deletedPosition = mMultiPageDocument.getDocuments().indexOf(document);

        deleteDocument(document);

        final int nrOfDocuments = mMultiPageDocument.getDocuments().size();
        final int newPosition = getNewPositionAfterDeletion(deletedPosition, nrOfDocuments);
        updatePageIndicator(newPosition);
        updateReorderPagesTip();

        mPreviewsAdapter.notifyDataSetChanged();
        mThumbnailsAdapter.removeThumbnail(deletedPosition);
        scrollToThumbnail(newPosition);

        updateNextButtonVisibility();

        updateDeleteButtonVisibility();
        updateRotateButtonVisibility();
    }

    private void scrollToThumbnail(final int position) {
        final int scrollTargetPosition = mThumbnailsAdapter.getScrollTargetPosition(position);
        mThumbnailsScroller.setTargetPosition(scrollTargetPosition);
        mThumbnailsRecycler.getLayoutManager().startSmoothScroll(mThumbnailsScroller);
    }

    private void deleteDocument(@NonNull final ImageDocument document) {
        deleteFromMultiPageDocument(document);
        deleteFromCaches(document);
        deleteFromDisk(document);
        deleteFromGiniApi(document);
        mDocumentUploadResults.remove(document.getId());
    }

    private void deleteFromMultiPageDocument(@NonNull final ImageDocument document) {
        mMultiPageDocument.getDocuments().remove(document);
        if (mMultiPageDocument.getDocuments().size() == 0
                && GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getImageMultiPageDocumentMemoryStore().clear();
        }
    }

    private void deleteFromGiniApi(final ImageDocument document) {
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager =
                    GiniVision.getInstance().internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.delete(document);
            }
        }
    }

    private void deleteFromDisk(final ImageDocument document) {
        if (GiniVision.hasInstance()) {
            final Uri uri = document.getUri();
            if (uri != null) {
                GiniVision.getInstance().internal().getImageDiskStore().delete(uri);
            }
        }
    }

    @NonNull
    private void deleteFromCaches(final ImageDocument document) {
        if (GiniVision.hasInstance()) {
            final GiniVision.Internal gvInternal = GiniVision.getInstance().internal();
            gvInternal.getDocumentDataMemoryCache().invalidate(document);
            gvInternal.getPhotoMemoryCache().invalidate(document);
        }
    }

    private void updatePageIndicator(final int position) {
        final int nrOfDocuments = mMultiPageDocument.getDocuments().size();
        String text = null;
        if (nrOfDocuments > 0) {
            text = getString(R.string.gv_multi_page_review_page_indicator, position + 1,
                    nrOfDocuments);
        }
        mPageIndicator.setText(text);
    }

    private void updateReorderPagesTip() {
        if (mMultiPageDocument.getDocuments().size() > 1) {
            mReorderPagesTip.setText(getText(R.string.gv_multi_page_review_reorder_pages_tip));
        } else {
            mReorderPagesTip.setText("");
        }
    }

    private void updateNextButtonVisibility() {
        if (mMultiPageDocument.getDocuments().size() == 0) {
            setNextButtonEnabled(false);
            return;
        }

        boolean uploadFailed = false;
        for (final Boolean uploadSuccess : mDocumentUploadResults.values()) {
            if (!uploadSuccess) {
                uploadFailed = true;
                break;
            }
        }
        setNextButtonEnabled(!uploadFailed);
    }

    private void setNextButtonEnabled(final boolean enabled) {
        mButtonNext.setEnabled(enabled);
        if (enabled) {
            mButtonNext.animate().alpha(1.0f).start();
        } else {
            mButtonNext.animate().alpha(0.5f).start();
        }
    }

    private void updateRotateButtonVisibility() {
        if (mMultiPageDocument.getDocuments().size() == 0) {
            mRotateButton.setEnabled(false);
            mRotateButton.setAlpha(0.2f);
        }
    }

    private void updateDeleteButtonVisibility() {
        if (mMultiPageDocument.getDocuments().size() == 0) {
            mDeleteButton.setEnabled(false);
            mDeleteButton.setAlpha(0.2f);
        }
    }

    private void onRotateButtonClicked() {
        if (!GiniVision.hasInstance()) {
            LOG.error(
                    "Cannot rotate document. GiniVision instance not available. Create it with GiniVision.newInstance().");
            return;
        }
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final int currentItem = mPreviewsPager.getCurrentItem();
        final ImageDocument document =
                mMultiPageDocument.getDocuments().get(currentItem);
        final int rotationStep = 90;
        final int degrees = document.getRotationForDisplay() + rotationStep;
        document.setRotationForDisplay(degrees);
        document.updateRotationDeltaBy(rotationStep);
        mPreviewsAdapter.rotateImageInCurrentItemBy(mPreviewsPager, rotationStep);
        mThumbnailsAdapter.rotateHighlightedThumbnailBy(rotationStep);
    }

    @VisibleForTesting
    void onNextButtonClicked() {
        trackReviewScreenEvent(ReviewScreenEvent.NEXT);
        mNextClicked = true;
        mListener.onProceedToAnalysisScreen(mMultiPageDocument);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        initMultiPageDocument();
        mNextClicked = false;
        if (!mPreviewsShown) {
            observeViewTree();
        }
        showAlertIfOpenWithDocumentAndAppIsDefault(activity,
                mMultiPageDocument, new FileImportHelper.ShowAlertCallback() {
                    @Override
                    public void showAlertDialog(@NonNull final String message,
                            @NonNull final String positiveButtonTitle,
                            @NonNull final DialogInterface.OnClickListener
                                    positiveButtonClickListener,
                            @Nullable final String negativeButtonTitle,
                            @Nullable final DialogInterface.OnClickListener
                                    negativeButtonClickListener,
                            @Nullable final DialogInterface.OnCancelListener cancelListener) {
                        MultiPageReviewFragment.this.showAlertDialog(message, positiveButtonTitle,
                                positiveButtonClickListener,
                                negativeButtonTitle, negativeButtonClickListener, cancelListener);
                    }
                })
                .thenRun(new Runnable() {
                    @Override
                    public void run() {
                        uploadDocuments();
                    }
                });
    }

    private void uploadDocuments() {
        for (final ImageDocument imageDocument : mMultiPageDocument.getDocuments()) {
            if (!mMultiPageDocument.hasDocumentError(imageDocument)) {
                // Documents with a an error should not be uploaded automatically
                uploadDocument(imageDocument);
            }
        }
    }

    @VisibleForTesting
    void uploadDocument(final ImageDocument document) {
        if (!GiniVision.hasInstance()) {
            return;
        }
        final NetworkRequestsManager networkRequestsManager =
                GiniVision.getInstance().internal().getNetworkRequestsManager();
        if (networkRequestsManager == null) {
            return;
        }
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mThumbnailsAdapter.setUploadState(ThumbnailsAdapter.UploadState.IN_PROGRESS,
                document);
        mMultiPageDocument.removeErrorForDocument(document);
        mDocumentUploadResults.put(document.getId(), false);
        networkRequestsManager.upload(activity, document)
                .handle(new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionDocument>,
                        Throwable, Void>() {
                    @Override
                    public Void apply(
                            final NetworkRequestResult<GiniVisionDocument> requestResult,
                            final Throwable throwable) {
                        if (throwable != null
                                && !NetworkRequestsManager.isCancellation(throwable)) {
                            trackUploadError(throwable);
                            final String errorMessage = getString(
                                    R.string.gv_document_analysis_error);
                            showErrorOnPreview(errorMessage, document);
                            mThumbnailsAdapter.setUploadState(
                                    ThumbnailsAdapter.UploadState.FAILED,
                                    document);
                        } else if (requestResult != null) {
                            mDocumentUploadResults.put(document.getId(), true);
                            mThumbnailsAdapter.setUploadState(
                                    ThumbnailsAdapter.UploadState.COMPLETED,
                                    document);
                        }
                        updateNextButtonVisibility();
                        return null;
                    }
                });
    }

    private void trackUploadError(@NonNull final Throwable throwable) {
        final Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(UPLOAD_ERROR_DETAILS_MAP_KEY.MESSAGE, throwable.getMessage());
        errorDetails.put(UPLOAD_ERROR_DETAILS_MAP_KEY.ERROR_OBJECT, throwable);
        trackReviewScreenEvent(ReviewScreenEvent.UPLOAD_ERROR, errorDetails);
    }

    private void showErrorOnPreview(final String errorMessage, final ImageDocument imageDocument) {
        mMultiPageDocument.setErrorForDocument(imageDocument,
                new GiniVisionDocumentError(errorMessage,
                        UPLOAD_FAILED));
        mPreviewsAdapter.notifyDataSetChanged();
    }

    private void observeViewTree() {
        final View view = getView();
        if (view == null) {
            return;
        }
        LOG.debug("Observing the view layout");
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onViewLayoutFinished();
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
        view.requestLayout();
    }

    private void onViewLayoutFinished() {
        LOG.debug("View layout finished");
        showPreviews();
    }

    private void showPreviews() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        mPreviewsShown = true;

        updateReorderPagesTip();

        updateDeleteButtonVisibility();
        updateRotateButtonVisibility();

        mPreviewsPager.setCurrentItem(0);
        updatePageIndicator(0);
        highlightThumbnail(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getImageMultiPageDocumentMemoryStore()
                    .setMultiPageDocument(mMultiPageDocument);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mNextClicked
                && mMultiPageDocument.getImportMethod() == Document.ImportMethod.OPEN_WITH) {
            // Delete documents imported using "open with" because the
            // Camera Screen is not launched for "open with"
            deleteUploadedDocuments();
        }
    }

    private void deleteUploadedDocuments() {
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.cancel(mMultiPageDocument);
                networkRequestsManager.delete(mMultiPageDocument)
                        .handle(new CompletableFuture.BiFun<NetworkRequestResult<
                                GiniVisionDocument>, Throwable, Void>() {
                            @Override
                            public Void apply(
                                    final NetworkRequestResult<GiniVisionDocument> requestResult,
                                    final Throwable throwable) {
                                for (final Object document : mMultiPageDocument.getDocuments()) {
                                    final GiniVisionDocument giniVisionDocument =
                                            (GiniVisionDocument) document;
                                    networkRequestsManager.cancel(giniVisionDocument);
                                    networkRequestsManager.delete(giniVisionDocument);
                                }
                                return null;
                            }
                        });
            }
        }
    }

    @Override
    public void onRetryUpload(@NonNull final ImageDocument document) {
        uploadDocument(document);
    }

    @Override
    public void onDeleteDocument(@NonNull final ImageDocument document) {
        deleteDocumentAndUpdateUI(document);
    }

    @Override
    public void setListener(@NonNull final MultiPageReviewFragmentListener listener) {
        mListener = listener;
    }

}
