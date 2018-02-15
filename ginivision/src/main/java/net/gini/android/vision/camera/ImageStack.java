package net.gini.android.vision.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionListenerAdapter;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.R;

import java.util.List;

/**
 * Created by Alpar Szotyori on 13.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ImageStack extends RelativeLayout {

    private static final long BADGE_TRANSITION_DURATION_MS = 150;
    private static final long TRANSITION_DURATION_MS = 300;
    private static final long TRANSITION_START_DELAY_MS = 150;
    private TextView badge;
    private OnClickListener clickListener;
    private ImageView newImage;
    private ImageView stackItem1;
    private ImageView stackItem2;
    private ImageView stackItem3;
    private int imageCount;

    public ImageStack(final Context context) {
        super(context, null, 0);
        init(context);
    }

    public ImageStack(final Context context,
            @Nullable final AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public ImageStack(final Context context, @Nullable final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ImageStack(final Context context, @Nullable final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull final Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.gv_image_stack_default, this);

        newImage = findViewById(R.id.gv_new_photo);
        stackItem1 = findViewById(R.id.gv_stack_item_1);
        stackItem2 = findViewById(R.id.gv_stack_item_2);
        stackItem3 = findViewById(R.id.gv_stack_item_3);
        badge = findViewById(R.id.gv_badge);
        badge.setVisibility(INVISIBLE);
    }

    public void removeImages() {
        setImages(null);
    }

    public void setImages(@Nullable final List<Bitmap> bitmaps) {
        imageCount = 0;
        stackItem1.setImageDrawable(null);
        stackItem1.setClickable(false);
        stackItem1.setFocusable(false);
        stackItem2.setImageDrawable(null);
        stackItem3.setImageDrawable(null);
        badge.setText("");
        badge.setVisibility(INVISIBLE);
        if (bitmaps == null) {
            return;
        }
        imageCount = bitmaps.size();
        if (imageCount > 0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(imageCount));
        }
        if (bitmaps.size() > 0) {
            stackItem1.setClickable(true);
            stackItem1.setFocusable(true);
            if (clickListener != null) {
                stackItem1.setOnClickListener(clickListener);
            }
        }
        if (bitmaps.size() > 2) {
            stackItem3.setImageBitmap(bitmaps.get(0));
            stackItem2.setImageBitmap(bitmaps.get(1));
            stackItem1.setImageBitmap(bitmaps.get(2));
        } else if (bitmaps.size() > 1) {
            stackItem2.setImageBitmap(bitmaps.get(0));
            stackItem1.setImageBitmap(bitmaps.get(1));
        } else if (bitmaps.size() > 0) {
            stackItem1.setImageBitmap(bitmaps.get(0));
        }
    }

    public void addImage(@NonNull final Bitmap bitmap) {
        // Create the default and the image added scenes
        final Scene defaultScene = Scene.getSceneForLayout(ImageStack.this,
                R.layout.gv_image_stack_default, getContext());
        final Scene imageAddedScene = Scene.getSceneForLayout(ImageStack.this,
                R.layout.gv_image_stack_image_added, getContext());

        // Get the current images visible in the stack
        final Drawable drawable1 = stackItem1.getDrawable();
        final Drawable drawable2 = stackItem2.getDrawable();
        final Drawable drawable3 = stackItem3.getDrawable();

        // Set up the transitions
        final TransitionSet transitions = new TransitionSet();
        transitions.setDuration(TRANSITION_DURATION_MS);
        transitions.addTransition(new ChangeBounds());

        final Fade fadeOut = new Fade(Fade.OUT);
        fadeOut.addTarget(R.id.gv_stack_item_3);
        transitions.addTransition(fadeOut);

        transitions.setStartDelay(TRANSITION_START_DELAY_MS);

        transitions.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(@NonNull final Transition transition) {
                final ViewGroup sceneRoot = imageAddedScene.getSceneRoot();
                final ImageView stackItem1View = sceneRoot.findViewById(R.id.gv_stack_item_1);
                final ImageView stackItem2View = sceneRoot.findViewById(R.id.gv_stack_item_2);
                final ImageView stackItem3View = sceneRoot.findViewById(R.id.gv_stack_item_3);
                final ImageView newImageView = sceneRoot.findViewById(R.id.gv_new_photo);
                final TextView badge = sceneRoot.findViewById(R.id.gv_badge);

                // Show the current images and badge in the image added scene
                stackItem1View.setImageDrawable(drawable1);
                stackItem2View.setImageDrawable(drawable2);
                stackItem3View.setImageDrawable(drawable3);
                if (imageCount > 0) {
                    badge.setVisibility(VISIBLE);
                    badge.setText(String.valueOf(imageCount));
                }
                // Show the new image
                newImageView.setVisibility(VISIBLE);
                newImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onTransitionEnd(@NonNull final Transition transition) {
                // Return to the default scene
                final TransitionSet cleanupTransitions = new TransitionSet();

                final Transition changeBounds = new ChangeBounds();
                changeBounds.setDuration(0);
                cleanupTransitions.addTransition(changeBounds);

                final Fade fadeIn = new Fade(Fade.IN);
                fadeIn.addTarget(R.id.gv_badge);
                fadeIn.setDuration(BADGE_TRANSITION_DURATION_MS);
                cleanupTransitions.addTransition(fadeIn);

                cleanupTransitions.addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionStart(@NonNull final Transition transition) {
                        final ViewGroup sceneRoot = defaultScene.getSceneRoot();
                        stackItem1 = sceneRoot.findViewById(R.id.gv_stack_item_1);
                        stackItem2 = sceneRoot.findViewById(R.id.gv_stack_item_2);
                        stackItem3 = sceneRoot.findViewById(R.id.gv_stack_item_3);
                        newImage = sceneRoot.findViewById(R.id.gv_new_photo);
                        badge = sceneRoot.findViewById(R.id.gv_badge);

                        // Push the images to the left (remove last image and show image on top)
                        stackItem3.setImageDrawable(drawable2);
                        stackItem2.setImageDrawable(drawable1);
                        stackItem1.setImageBitmap(bitmap);

                        // Update the badge
                        imageCount++;
                        badge.setText(String.valueOf(imageCount));

                        stackItem1.setClickable(true);
                        stackItem1.setFocusable(true);
                        if (clickListener != null) {
                            stackItem1.setOnClickListener(clickListener);
                        }
                    }
                });
                TransitionManager.go(defaultScene, cleanupTransitions);
            }
        });
        TransitionManager.go(imageAddedScene, transitions);
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener clickListener) {
        this.clickListener = clickListener;
        if (clickListener != null && imageCount > 0) {
            stackItem1.setOnClickListener(clickListener);
        }
    }
}
