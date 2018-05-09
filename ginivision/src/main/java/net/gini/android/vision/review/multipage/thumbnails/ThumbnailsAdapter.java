package net.gini.android.vision.review.multipage.thumbnails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.review.RotatableImageViewContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class ThumbnailsAdapter extends
        RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> implements
        ThumbnailsTouchHelperListener {

    enum ViewType {
        THUMBNAIL,
        PLUS_BUTTON
    }

    private final Context mContext;
    private final ImageMultiPageDocument mMultiPageDocument;
    private final ThumnailsAdapterListener mListener;
    private final List<Thumbnail> mThumbnails;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;

    public ThumbnailsAdapter(@NonNull final Context context,
            @NonNull final ImageMultiPageDocument multiPageDocument,
            @NonNull final ThumnailsAdapterListener listener) {
        mContext = context;
        mMultiPageDocument = multiPageDocument;
        final List<ImageDocument> documents = mMultiPageDocument.getDocuments();
        mThumbnails = new ArrayList<>(documents.size());
        for (final ImageDocument document : documents) {
            mThumbnails.add(new Thumbnail());
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
    public int getItemViewType(final int position) {
        return position < mThumbnails.size() ? ViewType.THUMBNAIL.ordinal()
                : ViewType.PLUS_BUTTON.ordinal();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,
            @SuppressLint("RecyclerView") final int position) {
        switch (holder.mViewType) {
            case THUMBNAIL:
                bindThumbnail(holder, position);
                break;
            case PLUS_BUTTON:
                bindPlusButton(holder);
                break;
                default:
                    throw new IllegalStateException("Unknown view type " + holder.mViewType);
        }
    }

    private void bindThumbnail(final @NonNull ViewHolder holder,
            final @SuppressLint("RecyclerView") int position) {
        holder.resetImageView();
        holder.showActivityIndicator();
        showPosition(position, holder);
        GiniVision.getInstance().internal().getPhotoMemoryCache()
                .get(mContext, mMultiPageDocument.getDocuments().get(position),
                        new AsyncCallback<Photo>() {
                            @Override
                            public void onSuccess(final Photo result) {
                                // Only update if the holder still points to the position for
                                // which the Photo was loaded
                                if (holder.getAdapterPosition() == position) {
                                    holder.hideActivityIndicator();
                                    showPhoto(result, holder);
                                }
                            }

                            @Override
                            public void onError(final Exception exception) {
                                // Only update if the holder still points to the position for
                                // which the Photo was loaded
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

    private void bindPlusButton(final @NonNull ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onPlusButtonClicked();
            }
        });
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
        holder.thumbnailContainer.rotateImageView(
                photo.getRotationForDisplay(), false);
    }

    private void showPosition(final int position,
            final @NonNull ViewHolder holder) {
        holder.badge.setText(String.valueOf(position + 1));
        holder.highlight.setAlpha(mThumbnails.get(position).highlighted ? 1f : 0f);
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
                    holder.highlight.setAlpha(0.5f);
                    highlightPosition(adapterPosition);
                    mListener.onThumbnailSelected(adapterPosition);
                    if (mItemTouchHelper != null) {
                        mItemTouchHelper.startDrag(holder);
                    }
                }
                return false;
            }
        });
    }

    public boolean isThumbnailHighlighted(final int position) {
        return mThumbnails.get(position).highlighted;
    }

    public void highlightPosition(final int position) {
        for (int i = 0; i < mThumbnails.size(); i++) {
            final Thumbnail thumbnail = mThumbnails.get(i);
            if (thumbnail.highlighted) {
                thumbnail.highlighted = false;
                notifyItemChanged(i);
            }
            final ViewHolder holder =
                    (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(
                            i);
            if (holder != null) {
                holder.highlight.setAlpha(0f);
            }
        }
        if (!mThumbnails.get(position).highlighted) {
            mThumbnails.get(position).highlighted = true;
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        // Plus button is always shown at the end
        return mThumbnails.size() + 1;
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
        mListener.onThumbnailMoved();
        mListener.onThumbnailSelected(toPos);
    }

    public void removeThumbnail(final int deletedPosition) {
        mThumbnails.remove(deletedPosition);
        notifyItemRemoved(deletedPosition);
        final int newPosition = getNewPositionAfterDeletion(deletedPosition,
                mThumbnails.size());
        highlightPosition(newPosition);
        notifyItemChanged(newPosition);
        notifyDataSetChanged();
    }

    public static int getNewPositionAfterDeletion(final int deletedPosition, final int newSize) {
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

    public void rotateHighlightedThumbnailBy(final int degrees) {
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

    public void setItemTouchHelper(final ItemTouchHelper itemTouchHelper) {
        mItemTouchHelper = itemTouchHelper;
    }

    private static class Thumbnail {

        boolean highlighted;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView badge;
        final View handle;
        final View highlight;
        final RotatableImageViewContainer thumbnailContainer;
        final ProgressBar activityIndicator;
        final ViewType mViewType;

        ViewHolder(@NonNull final View itemView, @NonNull final ViewType viewType) {
            super(itemView);
            thumbnailContainer = itemView.findViewById(R.id.gv_thumbnail_container);
            badge = itemView.findViewById(R.id.gv_badge);
            highlight = itemView.findViewById(R.id.gv_highlight);
            handle = itemView.findViewById(R.id.gv_handle);
            activityIndicator = itemView.findViewById(R.id.gv_activity_indicator);
            mViewType = viewType;
        }

        void showActivityIndicator() {
            if (activityIndicator == null) {
                return;
            }
            activityIndicator.setVisibility(View.VISIBLE);
        }

        void hideActivityIndicator() {
            if (activityIndicator == null) {
                return;
            }
            activityIndicator.setVisibility(View.INVISIBLE);
        }

        void resetImageView() {
            if (thumbnailContainer == null) {
                return;
            }
            thumbnailContainer.rotateImageView(0, false);
            thumbnailContainer.getImageView().setImageDrawable(null);
        }

        boolean isDragAllowed() {
            return mViewType == ViewType.THUMBNAIL;
        }
    }

}
