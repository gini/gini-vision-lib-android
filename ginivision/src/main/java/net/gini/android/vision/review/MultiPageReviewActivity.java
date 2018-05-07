package net.gini.android.vision.review;

import static net.gini.android.vision.analysis.AnalysisActivity.RESULT_NO_EXTRACTIONS;
import static net.gini.android.vision.review.ReviewActivity.ANALYSE_DOCUMENT_REQUEST;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionDocumentError;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jersey.repackaged.jsr166e.CompletableFuture;

public class MultiPageReviewActivity extends AppCompatActivity {

    public static final Logger LOG = LoggerFactory.getLogger(MultiPageReviewActivity.class);

    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";
    public static final String EXTRA_OUT_DOCUMENT = "GV_EXTRA_OUT_DOCUMENT";
    private ViewPager mImagesPager;
    private ImagesPagerChangeListener mImagesPagerChangeListener;
    private ImageMultiPageDocument mMultiPageDocument;
    private TextView mPageIndicator;
    private RelativeLayout mRootView;
    private RecyclerView mThumbnailsRV;
    private RecyclerView.SmoothScroller mThumbnailsScroller;
    private ImageButton mButtonNext;
    private ImageButton mDeleteButton;

    public static final int RESULT_MULTI_PAGE_DOCUMENT = RESULT_FIRST_USER + 1001;
    private boolean mNextClicked;

    public static Intent createIntent(@NonNull final Context context,
            @NonNull final ImageMultiPageDocument multiPageDocument) {
        final Intent intent = new Intent(context, MultiPageReviewActivity.class);
        intent.putExtra(EXTRA_IN_DOCUMENT, multiPageDocument);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_multi_page_review);

        readExtras();

        mButtonNext = findViewById(R.id.gv_button_next);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mNextClicked = true;
                proceedToAnalysisScreen();
            }
        });

        mImagesPager = findViewById(R.id.gv_view_pager);

        mPageIndicator = findViewById(R.id.gv_page_indicator);
        mThumbnailsRV = findViewById(R.id.gv_thumbnails_panel);

        mThumbnailsScroller = new LinearSmoothScroller(this);

        mImagesPagerChangeListener = new ImagesPagerChangeListener(this,
                new ImagesPagerChangeListener.UpdateThumbnailsListener() {
                    @Override
                    public void setCurrentThumbnail(final int position) {
                        final ThumbnailsAdapter thumbnailsAdapter =
                                (ThumbnailsAdapter) mThumbnailsRV.getAdapter();
                        thumbnailsAdapter.highlightPosition(position);
                        mThumbnailsScroller.setTargetPosition(position);
                        mThumbnailsRV.getLayoutManager().startSmoothScroll(mThumbnailsScroller);
                    }
                });
        mImagesPager.addOnPageChangeListener(mImagesPagerChangeListener);

        mRootView = findViewById(R.id.root_view);

        final ImageButton reorderButton = findViewById(R.id.gv_button_reorder);
        reorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final boolean isSelected = !v.isSelected();
                v.setSelected(isSelected);

                TransitionManager.beginDelayedTransition(mRootView);
                final RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mThumbnailsRV.getLayoutParams();
                if (isSelected) {
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ABOVE, R.id.gv_toolbar);
                } else {
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.gv_toolbar);
                    layoutParams.addRule(RelativeLayout.ABOVE, 0);
                }
                mThumbnailsRV.requestLayout();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mThumbnailsRV.setLayoutManager(layoutManager);

        findViewById(R.id.gv_button_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int currentItem = mImagesPager.getCurrentItem();
                final ImageDocument document =
                        mMultiPageDocument.getDocuments().get(currentItem);
                final ImageDiskStore imageDiskStore =
                        GiniVision.getInstance().internal().getImageDiskStore();
                final PhotoMemoryCache photoMemoryCache =
                        GiniVision.getInstance().internal().getPhotoMemoryCache();
                final DocumentDataMemoryCache documentDataMemoryCache =
                        GiniVision.getInstance().internal().getDocumentDataMemoryCache();
                photoMemoryCache
                        .get(MultiPageReviewActivity.this, document, new AsyncCallback<Photo>() {
                            @Override
                            public void onSuccess(final Photo photo) {
                                final int rotationStep = 90;
                                final int degrees = document.getRotationForDisplay() + rotationStep;
                                document.setRotationForDisplay(degrees);
                                document.updateRotationDeltaBy(rotationStep);
                                final ImagesPagerAdapter imagesPagerAdapter =
                                        (ImagesPagerAdapter) mImagesPager.getAdapter();
                                final ThumbnailsAdapter thumbnailsAdapter =
                                        (ThumbnailsAdapter) mThumbnailsRV.getAdapter();
                                imagesPagerAdapter.rotateImageInCurrentItemBy(mImagesPager,
                                        rotationStep);
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
        });

        mDeleteButton = findViewById(R.id.gv_button_delete);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int deletedItem = mImagesPager.getCurrentItem();
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
                final ImagesPagerAdapter imagesPagerAdapter =
                        (ImagesPagerAdapter) mImagesPager.getAdapter();
                final ThumbnailsAdapter thumbnailsAdapter =
                        (ThumbnailsAdapter) mThumbnailsRV.getAdapter();
                imagesPagerAdapter.notifyDataSetChanged();
                thumbnailsAdapter.removeThumbnail(deletedItem);
                mThumbnailsScroller.setTargetPosition(newPosition);
                mThumbnailsRV.getLayoutManager().startSmoothScroll(mThumbnailsScroller);
                if (documents.size() == 1) {
                    mDeleteButton.setEnabled(false);
                    mDeleteButton.setAlpha(0.2f);
                }
            }
        });

        showPhotos();
    }

    private void proceedToAnalysisScreen() {
        final List<ImageDocument> documents = mMultiPageDocument.getDocuments();
        if (documents.size() == 0) {
            return;
        }
        final Intent intent = new Intent(MultiPageReviewActivity.this, AnalysisActivity.class);
        intent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, mMultiPageDocument);
        startActivityForResult(intent, ANALYSE_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        if (requestCode == ANALYSE_DOCUMENT_REQUEST) {
            if (resultCode == RESULT_NO_EXTRACTIONS) {
                finish();
            } else if (resultCode != Activity.RESULT_CANCELED) {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNextClicked = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager =
                    GiniVision.getInstance().internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                for (final ImageDocument imageDocument : mMultiPageDocument.getDocuments()) {
                    // WIP-MPA: start activity indicator for imageDocument
                    networkRequestsManager.upload(this, imageDocument)
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

    @Override
    protected void onDestroy() {
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
    public void onBackPressed() {
        final Intent data = new Intent();
        data.putExtra(EXTRA_OUT_DOCUMENT, mMultiPageDocument);
        setResult(RESULT_MULTI_PAGE_DOCUMENT, data);
        finish();
    }

    private void showPhotos() {
        final ImagesPagerAdapter imagesPagerAdapter = new ImagesPagerAdapter(
                getSupportFragmentManager(), mMultiPageDocument);

        final ThumbnailChangeListener thumbnailChangeListener = new ThumbnailChangeListener() {
            @Override
            public void onThumbnailMoved() {
                final PagerAdapter adapter = mImagesPager.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onThumbnailSelected(final int position) {
                mImagesPagerChangeListener.skipNextThumbnailsUpdate();
                mImagesPager.setCurrentItem(position);
            }
        };

        if (mMultiPageDocument.getDocuments().size() == 1) {
            mDeleteButton.setEnabled(false);
            mDeleteButton.setAlpha(0.2f);
        }

        mImagesPager.setAdapter(imagesPagerAdapter);
        final ThumbnailsAdapter thumbnailsAdapter = new ThumbnailsAdapter(this, mMultiPageDocument,
                thumbnailChangeListener);
        mThumbnailsRV.setAdapter(thumbnailsAdapter);

        final ItemTouchHelper.Callback callback =
                new ThumbnailsTouchHelperCallback(thumbnailsAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mThumbnailsRV);
        thumbnailsAdapter.setItemTouchHelper(touchHelper);

        mImagesPager.setCurrentItem(0);
        updatePageIndicator(0);
        thumbnailsAdapter.highlightPosition(0);
        mThumbnailsScroller.setTargetPosition(0);
        mThumbnailsRV.getLayoutManager().startSmoothScroll(mThumbnailsScroller);
    }

    private static int getNewPositionAfterDeletion(final int deletedPosition, final int newSize) {
        final int newPosition;
        if (deletedPosition == newSize) {
            // Last item was removed, highlight the new last item
            newPosition = deletedPosition - 1;
        } else {
            // Non-last item deletion moves the right neighbour to the same position
            newPosition = deletedPosition;
        }
        return newPosition;
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMultiPageDocument = extras.getParcelable(EXTRA_IN_DOCUMENT);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mMultiPageDocument == null) {
            throw new IllegalStateException(
                    "MultiPageReviewActivity requires a GiniVisionMultiPageDocument. Set it as an extra using the EXTRA_IN_DOCUMENT key.");
        }
    }

    private void updatePageIndicator(final int position) {
        mPageIndicator.setText(String.format("%d von %d", position + 1,
                mMultiPageDocument.getDocuments().size()));
    }

    public interface ThumbnailsTouchHelperListener {

        void onDragFinished();

        void onItemMove(final RecyclerView.ViewHolder viewHolder, final int fromPos,
                final RecyclerView.ViewHolder target, final int toPos);
    }

    private interface ThumbnailChangeListener {

        void onThumbnailMoved();

        void onThumbnailSelected(final int position);
    }

    private static class ImagesPagerAdapter extends FragmentStatePagerAdapter {

        private final ImageMultiPageDocument mMultiPageDocument;

        ImagesPagerAdapter(@NonNull final FragmentManager fm,
                @NonNull final ImageMultiPageDocument multiPageDocument) {
            super(fm);
            mMultiPageDocument = multiPageDocument;
        }

        @Override
        public int getCount() {
            return mMultiPageDocument.getDocuments().size();
        }

        @Override
        public int getItemPosition(final Object object) {
            // Required for reloading the visible fragment
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(final int position) {
            final ImageDocument document =
                    mMultiPageDocument.getDocuments().get(position);
            final GiniVisionDocumentError documentError =
                    mMultiPageDocument.getErrorForDocument(document);
            String errorMessage = null;
            if (documentError != null) {
                errorMessage = documentError.getMessage();
            }
            return ImageFragment.createInstance(document, errorMessage);
        }

        void rotateImageInCurrentItemBy(@NonNull final ViewPager viewPager, final int degrees) {
            final ImageFragment fragment = (ImageFragment) instantiateItem(viewPager,
                    viewPager.getCurrentItem());
            fragment.rotateImageViewBy(degrees, true);
        }
    }

    private static class ImagesPagerChangeListener implements ViewPager.OnPageChangeListener {

        private final MultiPageReviewActivity mActivity;
        private final UpdateThumbnailsListener mUpdateThumbnailsListener;
        private int mLastPosition = -1;
        private boolean mSkipNextThumbnailsUpdate;

        private ImagesPagerChangeListener(
                final MultiPageReviewActivity activity,
                final UpdateThumbnailsListener updateThumbnailsListener) {
            mActivity = activity;
            mUpdateThumbnailsListener = updateThumbnailsListener;
        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                final int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            if (mLastPosition != position) {
                mLastPosition = position;
                mActivity.updatePageIndicator(position);
                if (!mSkipNextThumbnailsUpdate) {
                    mUpdateThumbnailsListener.setCurrentThumbnail(position);
                }
                mSkipNextThumbnailsUpdate = false;
            }
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
        }

        void skipNextThumbnailsUpdate() {
            mSkipNextThumbnailsUpdate = true;
        }

        interface UpdateThumbnailsListener {

            void setCurrentThumbnail(final int position);
        }
    }

    private static class Thumbnail {

        boolean highlighted;
    }

    private static class ThumbnailsAdapter extends
            RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> implements
            ThumbnailsTouchHelperListener {

        private final Context mContext;
        private final ImageMultiPageDocument mMultiPageDocument;
        private final ThumbnailChangeListener mThumbnailChangeListener;
        private final List<Thumbnail> mThumbnails;
        private ItemTouchHelper mItemTouchHelper;
        private RecyclerView mRecyclerView;

        ThumbnailsAdapter(@NonNull final Context context,
                @NonNull final ImageMultiPageDocument multiPageDocument,
                @NonNull final ThumbnailChangeListener thumbnailChangeListener) {
            mContext = context;
            mMultiPageDocument = multiPageDocument;
            final List<ImageDocument> documents = mMultiPageDocument.getDocuments();
            mThumbnails = new ArrayList<>(documents.size());
            for (final ImageDocument document : documents) {
                mThumbnails.add(new Thumbnail());
            }
            mThumbnailChangeListener = thumbnailChangeListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(
                final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gv_item_thumbnail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,
                final int position) {
            holder.resetImageView();
            holder.showActivityIndicator();
            showPosition(position, holder);
            GiniVision.getInstance().internal().getPhotoMemoryCache()
                    .get(mContext, mMultiPageDocument.getDocuments().get(position),
                            new AsyncCallback<Photo>() {
                                @Override
                                public void onSuccess(final Photo result) {
                                    if (holder.getAdapterPosition() == position) {
                                        holder.hideActivityIndicator();
                                        showPhoto(result, holder);
                                    }
                                }

                                @Override
                                public void onError(final Exception exception) {
                                    if (holder.getAdapterPosition() == position) {
                                        holder.hideActivityIndicator();
                                        final ImageView imageView =
                                                holder.thumbnailContainer.getImageView();
                                        imageView.setBackgroundColor(Color.TRANSPARENT);
                                        imageView.setImageBitmap(null);
                                    }
                                }
                            });
        }

        private void showPhoto(@NonNull final Photo photo, @NonNull final ViewHolder holder) {
            final ImageView imageView = holder.thumbnailContainer.getImageView();
            final Bitmap bitmap = photo.getBitmapPreview();
            if (bitmap != null) {
                imageView.setBackgroundColor(Color.TRANSPARENT);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setBackgroundColor(Color.BLACK);
                imageView.setImageBitmap(null);
            }
            holder.thumbnailContainer.rotateImageView(
                    photo.getRotationForDisplay(), false);
        }

        private void showPosition(final int position, final @NonNull ViewHolder holder) {
            holder.badge.setText(String.valueOf(position + 1));
            holder.highlight.setAlpha(mThumbnails.get(position).highlighted ? 1f : 0f);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int adapterPosition = holder.getAdapterPosition();
                    highlightPosition(adapterPosition);
                    mThumbnailChangeListener.onThumbnailSelected(adapterPosition);
                }
            });
            holder.handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, final MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        final int adapterPosition = holder.getAdapterPosition();
                        holder.highlight.setAlpha(0.5f);
                        highlightPosition(adapterPosition);
                        mThumbnailChangeListener.onThumbnailSelected(adapterPosition);
                        if (mItemTouchHelper != null) {
                            mItemTouchHelper.startDrag(holder);
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mThumbnails.size();
        }

        @Override
        public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mRecyclerView = recyclerView;
        }

        @Override
        public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mRecyclerView = null;
        }

        @Override
        public void onDragFinished() {
            if (mRecyclerView != null) {
                if (mRecyclerView.isComputingLayout()) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onItemMove(final RecyclerView.ViewHolder viewHolder, final int fromPos,
                final RecyclerView.ViewHolder target, final int toPos) {
            Collections.swap(mThumbnails, fromPos, toPos);
            Collections.swap(mMultiPageDocument.getDocuments(), fromPos, toPos);
            notifyItemMoved(fromPos, toPos);
            ((ViewHolder) viewHolder).badge.setText(String.valueOf(toPos + 1));
            ((ViewHolder) target).badge.setText(String.valueOf(fromPos + 1));
            highlightPosition(toPos);
            mThumbnailChangeListener.onThumbnailMoved();
            mThumbnailChangeListener.onThumbnailSelected(toPos);
        }

        void removeThumbnail(final int deletedPosition) {
            mThumbnails.remove(deletedPosition);
            notifyItemRemoved(deletedPosition);
            final int newPosition = getNewPositionAfterDeletion(deletedPosition,
                    mThumbnails.size());
            highlightPosition(newPosition);
            notifyItemChanged(newPosition);
            notifyDataSetChanged();
        }

        void highlightPosition(final int position) {
            for (int i = 0; i < mThumbnails.size(); i++) {
                final Thumbnail thumbnail = mThumbnails.get(i);
                thumbnail.highlighted = false;
                final ThumbnailsAdapter.ViewHolder holder =
                        (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.highlight.setAlpha(0f);
                }
            }
            mThumbnails.get(position).highlighted = true;
            if (mRecyclerView != null) {
                for (int i = 0; i < mThumbnails.size(); i++) {
                    final Thumbnail thumbnail = mThumbnails.get(i);
                    final ThumbnailsAdapter.ViewHolder holder =
                            (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                    if (holder != null) {
                        holder.highlight.setAlpha(thumbnail.highlighted ? 1f : 0f);
                    }
                }
            }
        }

        void rotateHighlightedThumbnailBy(final int degrees) {
            int highlightedPosition = -1;
            for (int i = 0; i < mThumbnails.size(); i++) {
                if (mThumbnails.get(i).highlighted) {
                    highlightedPosition = i;
                    break;
                }
            }
            if (mRecyclerView != null) {
                final ViewHolder viewHolder =
                        (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(
                                highlightedPosition);
                if (viewHolder != null) {
                    viewHolder.thumbnailContainer.rotateImageViewBy(degrees, true);
                }
            }
        }

        void setItemTouchHelper(final ItemTouchHelper itemTouchHelper) {
            mItemTouchHelper = itemTouchHelper;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            final TextView badge;
            final View handle;
            final View highlight;
            final RotatableImageViewContainer thumbnailContainer;
            final ProgressBar activityIndicator;

            ViewHolder(final View itemView) {
                super(itemView);
                thumbnailContainer = itemView.findViewById(R.id.gv_thumbnail_container);
                badge = itemView.findViewById(R.id.gv_badge);
                highlight = itemView.findViewById(R.id.gv_highlight);
                handle = itemView.findViewById(R.id.gv_handle);
                activityIndicator = itemView.findViewById(R.id.gv_activity_indicator);
            }

            void showActivityIndicator() {
                activityIndicator.setVisibility(View.VISIBLE);
            }

            void hideActivityIndicator() {
                activityIndicator.setVisibility(View.INVISIBLE);
            }

            void resetImageView() {
                thumbnailContainer.rotateImageView(0, false);
                thumbnailContainer.getImageView().setImageDrawable(null);
            }
        }

    }

    private static class ThumbnailsTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ThumbnailsTouchHelperListener mListener;

        ThumbnailsTouchHelperCallback(
                final ThumbnailsTouchHelperListener listener) {
            mListener = listener;
        }

        @Override
        public int getMovementFlags(final RecyclerView recyclerView,
                final RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                    0);
        }

        @Override
        public boolean onMove(final RecyclerView recyclerView,
                final RecyclerView.ViewHolder viewHolder,
                final RecyclerView.ViewHolder target) {
            mListener.onItemMove(viewHolder, viewHolder.getAdapterPosition(), target,
                    target.getAdapterPosition());
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction) {
        }

        @Override
        public void onSelectedChanged(final RecyclerView.ViewHolder viewHolder,
                final int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                mListener.onDragFinished();
            }
        }
    }
}
