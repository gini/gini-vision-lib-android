package net.gini.android.vision.review.multipage.thumbnails;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ThumbnailsTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ThumbnailsTouchHelperListener mListener;

    public ThumbnailsTouchHelperCallback(
            final ThumbnailsTouchHelperListener listener) {
        mListener = listener;
    }

    @Override
    public int getMovementFlags(final RecyclerView recyclerView,
            final RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ThumbnailsAdapter.ViewHolder) {
            final ThumbnailsAdapter.ViewHolder adapterViewHolder =
                    (ThumbnailsAdapter.ViewHolder) viewHolder;
            if (adapterViewHolder.isDragAllowed()) {
                return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                        0);
            } else {
                return makeMovementFlags(0, 0);
            }
        } else {
            throw new IllegalStateException("Unkown ViewHolder type" + viewHolder);
        }
    }

    @Override
    public boolean onMove(final RecyclerView recyclerView,
            final RecyclerView.ViewHolder viewHolder,
            final RecyclerView.ViewHolder target) {
        return mListener.onItemMove(viewHolder, viewHolder.getAdapterPosition(), target,
                target.getAdapterPosition());
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
