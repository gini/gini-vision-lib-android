package net.gini.android.vision.review.multipage.thumbnails;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public interface ThumbnailsTouchHelperListener {

    void onDragFinished();

    boolean onItemMove(final RecyclerView.ViewHolder viewHolder, final int fromPos,
            final RecyclerView.ViewHolder target, final int toPos);
}
