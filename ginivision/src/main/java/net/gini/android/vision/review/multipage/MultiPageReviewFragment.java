package net.gini.android.vision.review.multipage;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.review.multipage.thumbnails.ThumbnailsAdapter.getNewPositionAfterDeletion;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.cache.DocumentDataMemoryCache;
import net.gini.android.vision.internal.cache.PhotoMemoryCache;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoEdit;
import net.gini.android.vision.internal.network.NetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.review.multipage.previews.PreviewsAdapter;
import net.gini.android.vision.review.multipage.previews.PreviewsPageChangeHandler;
import net.gini.android.vision.review.multipage.previews.PreviewsPageChangeListener;
import net.gini.android.vision.review.multipage.thumbnails.ThumbnailChangeListener;
import net.gini.android.vision.review.multipage.thumbnails.ThumbnailsAdapter;
import net.gini.android.vision.review.multipage.thumbnails.ThumbnailsTouchHelperCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 07.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class MultiPageReviewFragment extends Fragment implements MultiPageReviewFragmentInterface {

    private static final String ARGS_MP_DOCUMENT = "ARGS_MP_DOCUMENT";

    private static final Logger LOG = LoggerFactory.getLogger(MultiPageReviewFragment.class);
    private static final String MP_DOCUMENT_KEY = "MP_DOCUMENT_KEY";

    private ImageMultiPageDocument mMultiPageDocument;
    private MultiPageReviewFragmentListener mListener;

    private RelativeLayout mRootView;
    private ViewPager mPreviewsPager;
    private PreviewsAdapter mPreviewsAdapter;
    private PreviewsPageChangeHandler mPreviewsPageChangeHandler;
    private TextView mPageIndicator;
    private RecyclerView mThumbnailsRecycler;
    private ThumbnailsAdapter mThumbnailsAdapter;
    private RecyclerView.SmoothScroller mThumbnailsScroller;
    private ThumbnailChangeListener mThumbnailChangeListener;
    private ImageButton mButtonNext;
    private ImageButton mRotateButton;
    private ImageButton mReorderButton;
    private ImageButton mDeleteButton;

    private boolean mNextClicked;
    private boolean mPreviewsShown;

    public static MultiPageReviewFragment createInstance(
            @NonNull final GiniVisionMultiPageDocument document) {
        final MultiPageReviewFragment fragment = new MultiPageReviewFragment();
        fragment.setArguments(createArguments(document));
        return fragment;
    }

    private static Bundle createArguments(final GiniVisionMultiPageDocument document) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_MP_DOCUMENT, document);
        return arguments;
    }

    /**
     * @exclude
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forcePortraitOrientationOnPhones(getActivity());
        readArguments();
        initListener();
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }
    }

    private void restoreSavedState(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        LOG.debug("Restoring saved state");
        mMultiPageDocument = savedInstanceState.getParcelable(MP_DOCUMENT_KEY);
        if (mMultiPageDocument == null) {
            throw new IllegalStateException(
                    "Missing required instances for restoring saved instance state.");
        }
    }

    private void readArguments() {
        final Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalStateException(
                    "MultiPageReviewFragment requires a GiniVisionMultiPageDocument. Use the createInstance() method for instantiating.");
        }
        final GiniVisionMultiPageDocument document = arguments.getParcelable(ARGS_MP_DOCUMENT);
        if (document != null && document instanceof ImageMultiPageDocument) {
            mMultiPageDocument = (ImageMultiPageDocument) document;
        } else {
            throw new IllegalStateException(
                    "MultiPageReviewFragment requires an ImageMultiPageDocuments. Use the createInstance() method for instantiating.");
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
        return view;
    }

    private void setupPreviewsViewPager() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        mPreviewsAdapter = new PreviewsAdapter(getChildFragmentManager(), mMultiPageDocument);
        mPreviewsPager.setAdapter(mPreviewsAdapter);

        mPreviewsPageChangeHandler = new PreviewsPageChangeHandler(
                new PreviewsPageChangeListener() {
                    @Override
                    public void onPageSelected(final int position) {
                        updatePageIndicator(position);
                        if (!mThumbnailsAdapter.isThumbnailHighlighted(position)) {
                            mThumbnailsAdapter.highlightPosition(position);
                            mThumbnailsScroller.setTargetPosition(position);
                            mThumbnailsRecycler.getLayoutManager().startSmoothScroll(
                                    mThumbnailsScroller);
                        }
                    }
                });
        mPreviewsPager.addOnPageChangeListener(mPreviewsPageChangeHandler);
    }

    private void setupThumbnailsRecyclerView() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false);
        mThumbnailsRecycler.setLayoutManager(layoutManager);

        mThumbnailChangeListener = new ThumbnailChangeListener() {
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
        };

        mThumbnailsAdapter = new ThumbnailsAdapter(activity, mMultiPageDocument,
                mThumbnailChangeListener);
        mThumbnailsRecycler.setAdapter(mThumbnailsAdapter);

        mThumbnailsScroller = new LinearSmoothScroller(activity);

        final ItemTouchHelper.Callback callback =
                new ThumbnailsTouchHelperCallback(mThumbnailsAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mThumbnailsRecycler);
        mThumbnailsAdapter.setItemTouchHelper(touchHelper);
    }

    private void bindViews(final View view) {
        mRootView = view.findViewById(R.id.root_view);
        mButtonNext = view.findViewById(R.id.gv_button_next);
        mPreviewsPager = view.findViewById(R.id.gv_view_pager);
        mPageIndicator = view.findViewById(R.id.gv_page_indicator);
        mThumbnailsRecycler = view.findViewById(R.id.gv_thumbnails_panel);
        mReorderButton = view.findViewById(R.id.gv_button_reorder);
        mRotateButton = view.findViewById(R.id.gv_button_rotate);
        mDeleteButton = view.findViewById(R.id.gv_button_delete);
    }

    private void setInputHandlers() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onNextButtonClicked();
            }
        });
        mReorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onReorderButtonClicked(v);
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
        final List<ImageDocument> documents = mMultiPageDocument.getDocuments();
        final ImageDocument deletedDocument = documents.get(deletedItem);
        documents.remove(deletedItem);
        final GiniVision.Internal gvInternal = GiniVision.getInstance().internal();
        gvInternal.getDocumentDataMemoryCache().invalidate(deletedDocument);
        gvInternal.getPhotoMemoryCache().invalidate(deletedDocument);
        final Uri uri = deletedDocument.getUri();
        if (uri != null) {
            gvInternal.getImageDiskStore().delete(uri);
        }
        final NetworkRequestsManager networkRequestsManager =
                gvInternal.getNetworkRequestsManager();
        if (networkRequestsManager != null) {
            networkRequestsManager.delete(deletedDocument);
        }
        final int newPosition = getNewPositionAfterDeletion(deletedItem, documents.size());
        updatePageIndicator(newPosition);
        mPreviewsAdapter.notifyDataSetChanged();
        mThumbnailsAdapter.removeThumbnail(deletedItem);
        mThumbnailsScroller.setTargetPosition(newPosition);
        mThumbnailsRecycler.getLayoutManager().startSmoothScroll(mThumbnailsScroller);
        if (documents.size() == 1) {
            mDeleteButton.setEnabled(false);
            mDeleteButton.setAlpha(0.2f);
        }
    }

    private void updatePageIndicator(final int position) {
        mPageIndicator.setText(String.format("%d von %d", position + 1,
                mMultiPageDocument.getDocuments().size()));
    }

    private void onRotateButtonClicked() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final int currentItem = mPreviewsPager.getCurrentItem();
        final ImageDocument document =
                mMultiPageDocument.getDocuments().get(currentItem);
        final ImageDiskStore imageDiskStore =
                GiniVision.getInstance().internal().getImageDiskStore();
        final PhotoMemoryCache photoMemoryCache =
                GiniVision.getInstance().internal().getPhotoMemoryCache();
        final DocumentDataMemoryCache documentDataMemoryCache =
                GiniVision.getInstance().internal().getDocumentDataMemoryCache();
        photoMemoryCache
                .get(activity, document, new AsyncCallback<Photo>() {
                    @Override
                    public void onSuccess(final Photo photo) {
                        final int rotationStep = 90;
                        final int degrees = document.getRotationForDisplay() + rotationStep;
                        document.setRotationForDisplay(degrees);
                        document.updateRotationDeltaBy(rotationStep);
                        final PreviewsAdapter previewsAdapter =
                                (PreviewsAdapter) mPreviewsPager.getAdapter();
                        final ThumbnailsAdapter thumbnailsAdapter =
                                (ThumbnailsAdapter) mThumbnailsRecycler.getAdapter();
                        previewsAdapter.rotateImageInCurrentItemBy(mPreviewsPager, rotationStep);
                        thumbnailsAdapter.rotateHighlightedThumbnailBy(rotationStep);
                        photo.edit().rotateTo(degrees).applyAsync(
                                new PhotoEdit.PhotoEditCallback() {
                                    @Override
                                    public void onDone(@NonNull final Photo photo) {
                                        imageDiskStore.update(document.getUri(),
                                                photo.getData());
                                        photoMemoryCache.invalidate(document);
                                        documentDataMemoryCache.invalidate(document);
                                    }

                                    @Override
                                    public void onFailed() {
                                        LOG.error("Failed to rotate the jpeg");
                                    }
                                });
                    }

                    @Override
                    public void onError(final Exception exception) {
                        LOG.error("Failed to create Photo from Document", exception);
                    }
                });
    }

    private void onReorderButtonClicked(final View v) {
        final boolean isSelected = !v.isSelected();
        v.setSelected(isSelected);

        TransitionManager.beginDelayedTransition(mRootView);
        final RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) mThumbnailsRecycler.getLayoutParams();
        if (isSelected) {
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, 0);
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.gv_toolbar);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.gv_toolbar);
            layoutParams.addRule(RelativeLayout.ABOVE, 0);
        }
        mThumbnailsRecycler.requestLayout();
    }

    private void onNextButtonClicked() {
        mNextClicked = true;
        mListener.onProceedToAnalysisScreen(mMultiPageDocument);
    }

    @Override
    public void onStart() {
        super.onStart();
        mNextClicked = false;
        if (!mPreviewsShown) {
            observeViewTree();
        }
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager =
                    GiniVision.getInstance().internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                final Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                for (final ImageDocument imageDocument : mMultiPageDocument.getDocuments()) {
                    // WIP-MPA: start activity indicator for imageDocument
                    networkRequestsManager.upload(activity, imageDocument)
                            .handle(new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionDocument>, Throwable, Void>() {
                                @Override
                                public Void apply(
                                        final NetworkRequestResult<GiniVisionDocument> requestResult,
                                        final Throwable throwable) {
                                    // WIP-MPA: stop activity indicator for imageDocument
                                    if (throwable != null &&
                                            !NetworkRequestsManager.isCancellation(throwable)) {
                                        // WIP-MPA: show error for imageDocument on ViewPager page
                                        // WIP-MPA: show upload failure for imageDocument
                                    } else if (requestResult != null) {
                                        // WIP-MPA: show upload success for imageDocument
                                    }
                                    return null;
                                }
                            });
                }
            }
        }
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

        if (mMultiPageDocument.getDocuments().size() == 1) {
            mDeleteButton.setEnabled(false);
            mDeleteButton.setAlpha(0.2f);
        }

        mPreviewsPager.setCurrentItem(0);
        updatePageIndicator(0);
        mThumbnailsAdapter.highlightPosition(0);
        mThumbnailsScroller.setTargetPosition(0);
        mThumbnailsRecycler.getLayoutManager().startSmoothScroll(mThumbnailsScroller);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putParcelable(MP_DOCUMENT_KEY, mMultiPageDocument);
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
                        .handle(new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionDocument>, Throwable, Void>() {
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
    public GiniVisionMultiPageDocument getMultiPageDocument() {
        return mMultiPageDocument;
    }

    @Override
    public void setListener(@NonNull final MultiPageReviewFragmentListener listener) {

    }

}
