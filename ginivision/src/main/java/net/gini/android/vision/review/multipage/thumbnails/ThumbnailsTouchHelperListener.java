package net.gini.android.vision.review.multipage.thumbnails;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.recyclerview.widget.RecyclerView;

/**
 * Internal use only.
 *
 * @suppress
 */
public interface ThumbnailsTouchHelperListener {

    void onDragFinished();

    boolean onItemMove(final RecyclerView.ViewHolder viewHolder, final int fromPos,
            final RecyclerView.ViewHolder target, final int toPos);
}
