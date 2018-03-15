package net.gini.android.vision.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
    public static final long ADD_IMAGE_TRANSITION_DURATION_MS =
            TRANSITION_DURATION_MS + TRANSITION_START_DELAY_MS;

    private AddImageTransitionListener addImageTransitionListener;
    private TextView badge;
    private CleanupTransitionListener cleanupTransitionListener;
    private OnClickListener clickListener;
    private Scene defaultScene;
    private Scene imageAddedScene;
    private int imageCount;
    private ImageView stackItem1;
    private ImageView stackItem2;
    private ImageView stackItem3;
    private TransitionManager transitionManager;

    public ImageStack(final Context context) {
        super(context, null, 0);
        init(context);
    }

    private void init(@NonNull final Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.gv_image_stack_default, this);

        stackItem1 = findViewById(R.id.gv_stack_item_1);
        stackItem2 = findViewById(R.id.gv_stack_item_2);
        stackItem3 = findViewById(R.id.gv_stack_item_3);
        badge = findViewById(R.id.gv_badge);
        badge.setVisibility(INVISIBLE);

        defaultScene = Scene.getSceneForLayout(ImageStack.this,
                R.layout.gv_image_stack_default, getContext());
        imageAddedScene = Scene.getSceneForLayout(ImageStack.this,
                R.layout.gv_image_stack_image_added, getContext());

        addImageTransitionListener =
                new AddImageTransitionListener(imageAddedScene, this);
        cleanupTransitionListener =
                new CleanupTransitionListener(imageAddedScene, this);

        // Set up the add image transitions
        final TransitionSet addImageTransitions = new TransitionSet();
        addImageTransitions.setDuration(TRANSITION_DURATION_MS);
        addImageTransitions.addTransition(new ChangeBounds());

        final Fade fadeOut = new Fade(Fade.OUT);
        fadeOut.addTarget(R.id.gv_stack_item_3);
        addImageTransitions.addTransition(fadeOut);

        addImageTransitions.setStartDelay(TRANSITION_START_DELAY_MS);

        addImageTransitions.addListener(addImageTransitionListener);

        // Set up the cleanup transitions
        final TransitionSet cleanupTransitions = new TransitionSet();

        final Transition changeBounds = new ChangeBounds();
        changeBounds.setDuration(0);
        cleanupTransitions.addTransition(changeBounds);

        final Fade fadeIn = new Fade(Fade.IN);
        fadeIn.addTarget(R.id.gv_badge);
        fadeIn.setDuration(BADGE_TRANSITION_DURATION_MS);
        cleanupTransitions.addTransition(fadeIn);

        cleanupTransitions.addListener(cleanupTransitionListener);

        // Set up the transition manager
        transitionManager = new TransitionManager();
        transitionManager.setTransition(imageAddedScene, addImageTransitions);
        transitionManager.setTransition(defaultScene, cleanupTransitions);
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

    @Override
    public void setOnClickListener(@Nullable final OnClickListener clickListener) {
        this.clickListener = clickListener;
        if (clickListener != null && imageCount > 0) {
            stackItem1.setOnClickListener(clickListener);
        }
    }

    public void addImage(@NonNull final Bitmap bitmap) {
        // Get the current images visible in the stack
        final Drawable drawable1 = stackItem1.getDrawable();
        final Drawable drawable2 = stackItem2.getDrawable();
        final Drawable drawable3 = stackItem3.getDrawable();

        // Prepare the transition listeners
        addImageTransitionListener.setDrawable1(drawable1);
        addImageTransitionListener.setDrawable2(drawable2);
        addImageTransitionListener.setDrawable3(drawable3);
        addImageTransitionListener.setNewImage(bitmap);

        cleanupTransitionListener.setDrawable1(drawable1);
        cleanupTransitionListener.setDrawable2(drawable2);
        cleanupTransitionListener.setNewImage(bitmap);

        // Execute the transition
        transitionManager.transitionTo(imageAddedScene);
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
        if (imageCount > 0) {
            stackItem1.setClickable(true);
            stackItem1.setFocusable(true);
            if (clickListener != null) {
                stackItem1.setOnClickListener(clickListener);
            }
        }
        if (imageCount > 2) {
            setBitmapOrBlack(stackItem3, bitmaps.get(0));
            setBitmapOrBlack(stackItem2, bitmaps.get(1));
            setBitmapOrBlack(stackItem1, bitmaps.get(2));
        } else if (imageCount > 1) {
            setBitmapOrBlack(stackItem2, bitmaps.get(0));
            setBitmapOrBlack(stackItem1, bitmaps.get(1));
        } else if (imageCount > 0) {
            setBitmapOrBlack(stackItem1, bitmaps.get(0));
        }
    }

    private static void setBitmapOrBlack(@NonNull final ImageView imageView, @Nullable final Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(null);
            imageView.setBackgroundColor(Color.BLACK);
        }
    }

    private static void setDrawableOrBlack(@NonNull final ImageView imageView, @Nullable final Drawable drawable) {
        if (drawable != null && drawable.getIntrinsicHeight() > 0) {
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setImageDrawable(null);
            imageView.setBackgroundColor(Color.BLACK);
        }
    }

    private static class AddImageTransitionListener extends TransitionListenerAdapter {

        private final Scene imageAddedScene;
        private final ImageStack imageStack;
        private Drawable drawable1;
        private Drawable drawable2;
        private Drawable drawable3;
        private Bitmap newImage;

        private AddImageTransitionListener(final Scene imageAddedScene,
                final ImageStack imageStack) {
            this.imageAddedScene = imageAddedScene;
            this.imageStack = imageStack;
        }

        @Override
        public void onTransitionStart(@NonNull final Transition transition) {
            final ViewGroup sceneRoot = imageAddedScene.getSceneRoot();
            final ImageView stackItem1View = sceneRoot.findViewById(R.id.gv_stack_item_1);
            final ImageView stackItem2View = sceneRoot.findViewById(R.id.gv_stack_item_2);
            final ImageView stackItem3View = sceneRoot.findViewById(R.id.gv_stack_item_3);
            final ImageView newImageView = sceneRoot.findViewById(R.id.gv_new_photo);
            final TextView badge = sceneRoot.findViewById(R.id.gv_badge);

            // Show the current images and badge in the image added scene
            setDrawableOrBlack(stackItem1View, drawable1);
            setDrawableOrBlack(stackItem2View, drawable2);
            setDrawableOrBlack(stackItem3View, drawable3);
            if (imageStack.imageCount > 0) {
                badge.setVisibility(VISIBLE);
                badge.setText(String.valueOf(imageStack.imageCount));
            }
            // Show the new image
            newImageView.setVisibility(VISIBLE);
            setBitmapOrBlack(newImageView, newImage);
        }

        void setDrawable1(final Drawable drawable1) {
            this.drawable1 = drawable1;
        }

        void setDrawable2(final Drawable drawable2) {
            this.drawable2 = drawable2;
        }

        void setDrawable3(final Drawable drawable3) {
            this.drawable3 = drawable3;
        }

        void setNewImage(final Bitmap newImage) {
            this.newImage = newImage;
        }

        @Override
        public void onTransitionEnd(@NonNull final Transition transition) {
            // Return to the default scene
            imageStack.cleanupTransitionListener.setDrawable1(drawable1);
            imageStack.cleanupTransitionListener.setDrawable2(drawable2);
            imageStack.cleanupTransitionListener.setNewImage(newImage);
            imageStack.transitionManager.transitionTo(imageStack.defaultScene);
        }
    }

    private static class CleanupTransitionListener extends TransitionListenerAdapter {

        private final Scene defaultScene;
        private final ImageStack imageStack;
        private Drawable drawable1;
        private Drawable drawable2;
        private Bitmap newImage;

        private CleanupTransitionListener(final Scene defaultScene, final ImageStack imageStack) {
            this.defaultScene = defaultScene;
            this.imageStack = imageStack;
        }

        void setDrawable1(final Drawable drawable1) {
            this.drawable1 = drawable1;
        }

        void setDrawable2(final Drawable drawable2) {
            this.drawable2 = drawable2;
        }

        void setNewImage(final Bitmap newImage) {
            this.newImage = newImage;
        }

        @Override
        public void onTransitionStart(@NonNull final Transition transition) {
            final ViewGroup sceneRoot = defaultScene.getSceneRoot();
            imageStack.stackItem1 = sceneRoot.findViewById(R.id.gv_stack_item_1);
            imageStack.stackItem2 = sceneRoot.findViewById(R.id.gv_stack_item_2);
            imageStack.stackItem3 = sceneRoot.findViewById(R.id.gv_stack_item_3);
            imageStack.badge = sceneRoot.findViewById(R.id.gv_badge);

            // Push the images to the left (remove last image and show image on top)
            setDrawableOrBlack(imageStack.stackItem3, drawable2);
            setDrawableOrBlack(imageStack.stackItem2, drawable1);
            setBitmapOrBlack(imageStack.stackItem1, newImage);

            // Update the badge
            imageStack.imageCount++;
            imageStack.badge.setText(String.valueOf(imageStack.imageCount));

            imageStack.stackItem1.setClickable(true);
            imageStack.stackItem1.setFocusable(true);
            if (imageStack.clickListener != null) {
                imageStack.stackItem1.setOnClickListener(imageStack.clickListener);
            }
        }
    }
}
