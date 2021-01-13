package net.gini.android.vision.review.multipage.thumbnails;

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
public interface ThumbnailsAdapterListener {

    void onThumbnailMoved();

    void onThumbnailSelected(final int position);

    void onPlusButtonClicked();
}
