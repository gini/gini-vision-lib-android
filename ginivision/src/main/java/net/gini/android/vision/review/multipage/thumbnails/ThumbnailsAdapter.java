package net.gini.android.vision.review.multipage.thumbnails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.review.RotatableImageViewContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class ThumbnailsAdapter extends
        RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> implements
        ThumbnailsTouchHelperListener {

    private final Context mContext;
    private final ImageMultiPageDocument mMultiPageDocument;
    private final ThumbnailsAdapterListener mListener;
    private final List<Thumbnail> mThumbnails;
    private final boolean mShowPlusButton;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;

    public ThumbnailsAdapter(@NonNull final Context context,
            @NonNull final ImageMultiPageDocument multiPageDocument,
            @NonNull final ThumbnailsAdapterListener listener,
            final boolean showPlusButton) {
        mContext = context;
        mMultiPageDocument = multiPageDocument;
        mShowPlusButton = showPlusButton;
        final List<ImageDocument> documents = mMultiPageDocument.getDocuments();
        mThumbnails = new ArrayList<>(documents.size());
        for (final ImageDocument document : documents) {
            final Thumbnail thumbnail = new Thumbnail(); // NOPMD
            thumbnail.rotation = document.getRotationForDisplay();
            if (multiPageDocument.hasDocumentError(document)) {
                thumbnail.uploadState = UploadState.FAILED;
            }
            mThumbnails.add(thumbnail);
        }
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull final ViewGroup parent, final int viewType) {
        final ViewType type = ViewType.values()[viewType];
        final View view;
        switch (type) {
            case THUMBNAIL:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gv_item_multi_page_thumbnail, parent, false);
                break;
            case PLUS_BUTTON:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gv_item_multi_page_plus_button, parent, false);
                break;
            default:
                throw new IllegalStateException("Unknown view type " + type);
        }
        return new ViewHolder(view, type);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,
            @SuppressLint("RecyclerView") final int position) {
        switch (holder.viewType) {
            case THUMBNAIL:
                bindThumbnail(holder, position);
                break;
            case PLUS_BUTTON:
                bindPlusButton(holder);
                break;
            default:
                throw new IllegalStateException("Unknown view type " + holder.viewType);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return position < mThumbnails.size() ? ViewType.THUMBNAIL.ordinal()
                : ViewType.PLUS_BUTTON.ordinal();
    }

    @Override
    public int getItemCount() {
        return mThumbnails.size() + (mShowPlusButton ? 1 : 0);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null; // NOPMD
    }

    private void bindThumbnail(@NonNull final ViewHolder holder,
            @SuppressLint("RecyclerView") final int position) {
        holder.resetImageView();
        holder.showActivityIndicator();
        updateThumbnail(position, holder);
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getPhotoMemoryCache()
                    .get(mContext, mMultiPageDocument.getDocuments().get(position),
                            new AsyncCallback<Photo, Exception>() {
                                @Override
                                public void onSuccess(final Photo result) {
                                    // Only update if the holder still points to the position for
                                    // which the Photo was loaded
                                    if (holder.getAdapterPosition() == position) {
                                        showPhoto(result, holder);
                                    }
                                }

                                @Override
                                public void onError(final Exception exception) {
                                    // Only update if the holder still points to the position for
                                    // which the Photo was loaded
                                    if (holder.getAdapterPosition() == position) {
                                        final ImageView imageView =
                                                holder.thumbnailContainer.getImageView();
                                        imageView.setBackgroundColor(Color.TRANSPARENT);
                                        imageView.setImageBitmap(null);
                                    }
                                }

                                @Override
                                public void onCancelled() {
                                    // Not used
                                }
                            });
        }
    }

    private void showPhoto(@NonNull final Photo photo,
            @NonNull final ViewHolder holder) {
        final ImageView imageView = holder.thumbnailContainer.getImageView();
        final Bitmap bitmap = photo.getBitmapPreview();
        if (bitmap != null) {
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setImageBitmap(null);
        }
    }

    private void updateThumbnail(final int position, @NonNull final ViewHolder holder) {
        holder.badge.setText(String.valueOf(position + 1));
        final Thumbnail thumbnail = mThumbnails.get(position);
        holder.highlight.setAlpha(thumbnail.highlighted ? 1f : 0f);
        holder.thumbnailContainer.rotateImageView(thumbnail.rotation, false);
        showUploadState(holder, thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int adapterPosition = holder.getAdapterPosition();
                highlightPosition(adapterPosition);
                mListener.onThumbnailSelected(adapterPosition);
            }
        });
        holder.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    final int adapterPosition = holder.getAdapterPosition();
                    setHighlightedThumbnail(adapterPosition);
                    mListener.onThumbnailSelected(adapterPosition);
                    if (mItemTouchHelper != null) {
                        mItemTouchHelper.startDrag(holder);
                    }
                }
                return true;
            }
        });
    }

    private void showUploadState(@NonNull final ViewHolder holder,
            final Thumbnail thumbnail) {
        switch (thumbnail.uploadState) {
            case NOT_STARTED:
                holder.hideUploadIndicators();
                break;
            case IN_PROGRESS:
                holder.showActivityIndicator();
                break;
            case COMPLETED:
                holder.showUploadSuccess();
                break;
            case FAILED:
                holder.showUploadFailure();
                break;
            default:
                holder.hideUploadIndicators();
                break;
        }
    }

    public void highlightPosition(final int position) {
        if (mThumbnails.isEmpty()
                || mThumbnails.get(position).highlighted) {
            return;
        }
        for (int i = 0; i < mThumbnails.size(); i++) {
            final Thumbnail thumbnail = mThumbnails.get(i);
            if (thumbnail.highlighted) {
                thumbnail.highlighted = false;
                notifyItemChanged(i);
            }
        }
        mThumbnails.get(position).highlighted = true;
        notifyItemChanged(position);
    }

    private void setHighlightedThumbnail(final int position) {
        if (mThumbnails.get(position).highlighted) {
            return;
        }
        for (int i = 0; i < mThumbnails.size(); i++) {
            final Thumbnail thumbnail = mThumbnails.get(i);
            if (thumbnail.highlighted) {
                thumbnail.highlighted = false;
            }
            setHighlightAlpha(i, 0f);
        }
        mThumbnails.get(position).highlighted = true;
        setHighlightAlpha(position, 1.0f);
    }

    private void setHighlightAlpha(final int position, final float alpha) {
        final ViewHolder holder =
                (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.highlight.setAlpha(alpha);
        }
    }

    private void bindPlusButton(@NonNull final ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onPlusButtonClicked();
            }
        });
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
    public boolean onItemMove(final RecyclerView.ViewHolder viewHolder, final int fromPos,
            final RecyclerView.ViewHolder target, final int toPos) {
        if (toPos >= mThumbnails.size()) {
            return false;
        }
        Collections.swap(mThumbnails, fromPos, toPos);
        Collections.swap(mMultiPageDocument.getDocuments(), fromPos, toPos);
        notifyItemMoved(fromPos, toPos);
        ((ViewHolder) viewHolder).badge.setText(String.valueOf(toPos + 1));
        ((ViewHolder) target).badge.setText(String.valueOf(fromPos + 1));
        setHighlightedThumbnail(toPos);
        mListener.onThumbnailMoved();
        mListener.onThumbnailSelected(toPos);
        return true;
    }

    public int getScrollTargetPosition(final int position) {
        // When scrolling to the last thumbnail scroll to the plus button item instead
        if (mShowPlusButton && position == mThumbnails.size() - 1) {
            return position + 1;
        } else {
            return position;
        }
    }

    public boolean isThumbnailHighlighted(final int position) {
        return mThumbnails.get(position).highlighted;
    }

    public void removeThumbnail(final int deletedPosition) {
        mThumbnails.remove(deletedPosition);
        notifyItemRemoved(deletedPosition);
        final int newPosition = getNewPositionAfterDeletion(deletedPosition,
                mThumbnails.size());
        highlightPosition(newPosition);
        notifyItemChanged(newPosition);
    }

    public static int getNewPositionAfterDeletion(final int deletedPosition, final int newSize) {
        final int newPosition;
        if (deletedPosition == newSize) {
            // Last item was removed, highlight the new last item
            newPosition = Math.max(0, deletedPosition - 1);
        } else {
            // Non-last item deletion moves the right neighbour to the same position
            newPosition = deletedPosition;
        }
        return newPosition;
    }

    public void rotateHighlightedThumbnailBy(final int degrees) {
        int highlightedPosition = -1;
        for (int i = 0; i < mThumbnails.size(); i++) {
            final Thumbnail thumbnail = mThumbnails.get(i);
            if (thumbnail.highlighted) {
                thumbnail.rotation += degrees;
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

    public void setItemTouchHelper(final ItemTouchHelper itemTouchHelper) {
        mItemTouchHelper = itemTouchHelper;
    }

    public void setUploadState(final UploadState uploadState,
            @NonNull final ImageDocument document) {
        for (int i = 0; i < mMultiPageDocument.getDocuments().size(); i++) {
            final ImageDocument imageDocument = mMultiPageDocument.getDocuments().get(i);
            if (imageDocument.equals(document)) {
                final Thumbnail thumbnail = mThumbnails.get(i);
                thumbnail.uploadState = uploadState;
                notifyItemChanged(i);
                break;
            }
        }
    }

    enum ViewType {
        THUMBNAIL,
        PLUS_BUTTON
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public enum UploadState {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    private static class Thumbnail {

        boolean highlighted;
        UploadState uploadState = UploadState.NOT_STARTED;
        int rotation;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView badge;
        final View handle;
        final View highlight;
        final RotatableImageViewContainer thumbnailContainer;
        final ProgressBar activityIndicator;
        final ViewType viewType;
        final ImageView uploadResultIconBackground;
        final ImageView uploadResultIconForeground;

        ViewHolder(@NonNull final View itemView, @NonNull final ViewType viewType) {
            super(itemView);
            thumbnailContainer = itemView.findViewById(R.id.gv_thumbnail_container);
            badge = itemView.findViewById(R.id.gv_badge);
            highlight = itemView.findViewById(R.id.gv_highlight);
            handle = itemView.findViewById(R.id.gv_handle);
            activityIndicator = itemView.findViewById(R.id.gv_activity_indicator);
            this.viewType = viewType;
            uploadResultIconBackground = itemView.findViewById(
                    R.id.gv_upload_result_icon_background);
            uploadResultIconForeground = itemView.findViewById(
                    R.id.gv_upload_result_icon_foreground);
        }

        void showActivityIndicator() {
            if (activityIndicator == null) {
                return;
            }
            activityIndicator.setVisibility(View.VISIBLE);
        }

        void resetImageView() {
            if (thumbnailContainer == null) {
                return;
            }
            thumbnailContainer.rotateImageView(0, false);
            thumbnailContainer.getImageView().setImageDrawable(null);
            hideUploadIndicators();
        }

        private void hideUploadIndicators() {
            hideActivityIndicator();
            hideUploadIcon();
        }

        void hideActivityIndicator() {
            if (activityIndicator == null) {
                return;
            }
            activityIndicator.setVisibility(View.INVISIBLE);
        }

        void hideUploadIcon() {
            if (uploadResultIconBackground == null
                    || uploadResultIconForeground == null) {
                return;
            }
            uploadResultIconBackground.setVisibility(View.INVISIBLE);
            uploadResultIconForeground.setVisibility(View.INVISIBLE);
        }

        boolean isDragAllowed() {
            return viewType == ViewType.THUMBNAIL;
        }

        void showUploadSuccess() {
            if (uploadResultIconBackground == null
                    || uploadResultIconForeground == null) {
                return;
            }
            uploadResultIconBackground.setVisibility(View.VISIBLE);
            uploadResultIconBackground.setImageResource(
                    R.drawable.gv_multi_page_upload_success_icon_background);
            uploadResultIconForeground.setVisibility(View.VISIBLE);
            uploadResultIconForeground.setImageResource(
                    R.drawable.gv_multi_page_upload_success_icon_foreground);
            final int tintColor = ContextCompat.getColor(
                    itemView.getContext(),
                    R.color.gv_multi_page_thumbnail_upload_success_icon_foreground);
            uploadResultIconForeground.setColorFilter(tintColor, PorterDuff.Mode.MULTIPLY);
        }

        void showUploadFailure() {
            if (uploadResultIconBackground == null
                    || uploadResultIconForeground == null) {
                return;
            }
            uploadResultIconBackground.setVisibility(View.VISIBLE);
            uploadResultIconBackground.setImageResource(
                    R.drawable.gv_multi_page_upload_failure_icon_background);
            uploadResultIconForeground.setVisibility(View.VISIBLE);
            uploadResultIconForeground.setImageResource(
                    R.drawable.gv_multi_page_upload_failure_icon_foreground);
            final int tintColor = ContextCompat.getColor(
                    itemView.getContext(),
                    R.color.gv_multi_page_thumbnail_upload_failure_icon_foreground);
            uploadResultIconForeground.setColorFilter(tintColor, PorterDuff.Mode.MULTIPLY);
        }
    }

}
