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
    private TextView badge;
    private OnClickListener clickListener;
    private int imageCount;
    private ImageView stackItem1;
    private ImageView stackItem2;
    private ImageView stackItem3;
    private TextView subtitle;

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
        subtitle = findViewById(R.id.gv_stack_subtitle);
        subtitle.setVisibility(INVISIBLE);
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

        imageCount++;

        final Scene imageAddedScene = Scene.getSceneForLayout(ImageStack.this,
                R.layout.gv_image_stack_image_added, getContext());

        // Show the current images in the image added scene
        final AddImageTransitionListener addImageTransitionListener =
                createAddImageTransitionListener(imageAddedScene, bitmap, drawable1, drawable2,
                        drawable3);

        // Set up the add image transitions
        final Transition addImageTransitions = createAddImageTransition(
                addImageTransitionListener);

        // Execute the transition
        transitionTo(imageAddedScene, addImageTransitions);
    }

    @NonNull
    private Transition createAddImageTransition(
            final AddImageTransitionListener addImageTransitionListener) {
        final TransitionSet addImageTransitions = new TransitionSet();
        addImageTransitions.setDuration(TRANSITION_DURATION_MS);
        addImageTransitions.addTransition(new ChangeBounds());

        // Bottom stack item has to fade out because the stack is pushed down
        // when a new image is added
        final Fade fadeOut = new Fade(Fade.OUT);
        fadeOut.addTarget(R.id.gv_stack_item_3);
        addImageTransitions.addTransition(fadeOut);

        addImageTransitions.setStartDelay(TRANSITION_START_DELAY_MS);

        addImageTransitions.addListener(addImageTransitionListener);
        return addImageTransitions;
    }

    @NonNull
    private AddImageTransitionListener createAddImageTransitionListener(final Scene imageAddedScene,
            final @NonNull Bitmap bitmap, final Drawable drawable1, final Drawable drawable2,
            final Drawable drawable3) {
        final AddImageTransitionListener addImageTransitionListener =
                new AddImageTransitionListener(imageAddedScene, this);
        addImageTransitionListener.setDrawable1(drawable1);
        addImageTransitionListener.setDrawable2(drawable2);
        addImageTransitionListener.setDrawable3(drawable3);
        addImageTransitionListener.setNewImage(bitmap);
        return addImageTransitionListener;
    }

    private static void transitionTo(final Scene scene, final Transition transition) {
        final TransitionManager transitionManager = new TransitionManager();
        transitionManager.setTransition(scene, transition);
        transitionManager.transitionTo(scene);
    }

    public void removeImages() {
        setImages(null);
    }

    public void setImages(@Nullable final List<Bitmap> bitmaps) {
        imageCount = 0;
        stackItem1.setImageDrawable(null);
        stackItem1.setClickable(false);
        stackItem1.setFocusable(false);
        stackItem1.setBackgroundColor(Color.TRANSPARENT);
        stackItem2.setImageDrawable(null);
        stackItem2.setBackgroundColor(Color.TRANSPARENT);
        stackItem3.setImageDrawable(null);
        stackItem3.setBackgroundColor(Color.TRANSPARENT);
        if (bitmaps == null || bitmaps.size() == 0) {
            badge.setText("");
            badge.setVisibility(INVISIBLE);
            subtitle.setVisibility(INVISIBLE);
        } else {
            imageCount = bitmaps.size();
            badge.setVisibility(VISIBLE);
            badge.setText(String.valueOf(imageCount));
            subtitle.setVisibility(VISIBLE);

            stackItem1.setClickable(true);
            stackItem1.setFocusable(true);
            if (clickListener != null) {
                stackItem1.setOnClickListener(clickListener);
            }

            if (imageCount > 2) {
                setBitmapOrBlack(stackItem3, bitmaps.get(0));
                setBitmapOrBlack(stackItem2, bitmaps.get(1));
                setBitmapOrBlack(stackItem1, bitmaps.get(2));
            } else if (imageCount > 1) {
                setBitmapOrBlack(stackItem2, bitmaps.get(0));
                setBitmapOrBlack(stackItem1, bitmaps.get(1));
            } else {
                setBitmapOrBlack(stackItem1, bitmaps.get(0));
            }
        }
    }

    private static void setBitmapOrBlack(@NonNull final ImageView imageView,
            @Nullable final Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(null);
            imageView.setBackgroundColor(Color.BLACK);
        }
    }

    public void setImage(@Nullable final Bitmap bitmap, @NonNull final Position position) {
        switch (position) {
            case TOP:
                stackItem1.setClickable(true);
                stackItem1.setFocusable(true);
                if (clickListener != null) {
                    stackItem1.setOnClickListener(clickListener);
                }
                setBitmapOrBlack(stackItem1, bitmap);
                break;
            case MIDDLE:
                setBitmapOrBlack(stackItem2, bitmap);
                break;
            case BOTTOM:
                setBitmapOrBlack(stackItem3, bitmap);
                break;
        }
    }

    public void setImageCount(final int count) {
        imageCount = count;
        badge.setText(String.valueOf(count));
        badge.setVisibility(VISIBLE);
        subtitle.setVisibility(VISIBLE);
    }

    private static void setDrawableOrBlack(@NonNull final ImageView imageView,
            @Nullable final Drawable drawable) {
        if (drawable != null && drawable.getIntrinsicHeight() > 0) {
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setImageDrawable(null);
            imageView.setBackgroundColor(Color.BLACK);
        }
    }

    public enum Position {
        TOP,
        MIDDLE,
        BOTTOM
    }

    private static class AddImageTransitionListener extends TransitionListenerAdapter {

        private final ImageStack imageStack;
        private final Scene imageAddedScene;
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
            final TextView subtitle = sceneRoot.findViewById(R.id.gv_stack_subtitle);

            // Show the current images and badge in the image added scene
            // Image count was already increased so when we have at least two images it means
            // there was an image in the top stack item
            if (imageStack.imageCount >= 2) {
                setDrawableOrBlack(stackItem1View, drawable1);
            }
            // When we have at least three images it means
            // there was an image in the middle stack item
            if (imageStack.imageCount >= 3) {
                setDrawableOrBlack(stackItem2View, drawable2);
            }
            // When we have at least four images it means
            // there was an image in the bottom stack item
            if (imageStack.imageCount >= 4) {
                setDrawableOrBlack(stackItem3View, drawable3);
            }
            if (imageStack.imageCount > 0) {
                badge.setVisibility(VISIBLE);
                badge.setText(String.valueOf(imageStack.imageCount));
                subtitle.setVisibility(VISIBLE);
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
            final Scene defaultScene = Scene.getSceneForLayout(imageStack,
                    R.layout.gv_image_stack_default, imageStack.getContext());

            // Pass the new image and the previous top 2 images to the final state
            final CleanupTransitionListener cleanupTransitionListener =
                    createCleanupTransitionListener(defaultScene);

            // Set up the cleanup transitions
            final Transition cleanupTransitions = createCleanupTransition(
                    cleanupTransitionListener);

            transitionTo(defaultScene, cleanupTransitions);

            cleanUp();
        }

        @NonNull
        private Transition createCleanupTransition(
                final CleanupTransitionListener cleanupTransitionListener) {
            final TransitionSet cleanupTransitions = new TransitionSet();

            // Instantly move all items to the end position, effectively resetting
            // the view layout to its original state
            final Transition changeBounds = new ChangeBounds();
            changeBounds.setDuration(0);
            cleanupTransitions.addTransition(changeBounds);

            // Only the badge has to be faded in
            final Fade fadeIn = new Fade(Fade.IN);
            fadeIn.addTarget(R.id.gv_badge);
            fadeIn.setDuration(BADGE_TRANSITION_DURATION_MS);
            cleanupTransitions.addTransition(fadeIn);

            cleanupTransitions.addListener(cleanupTransitionListener);
            return cleanupTransitions;
        }

        @NonNull
        private CleanupTransitionListener createCleanupTransitionListener(
                final Scene defaultScene) {
            final CleanupTransitionListener cleanupTransitionListener =
                    new CleanupTransitionListener(defaultScene, imageStack);
            cleanupTransitionListener.setDrawable1(drawable1);
            cleanupTransitionListener.setDrawable2(drawable2);
            cleanupTransitionListener.setNewImage(newImage);
            return cleanupTransitionListener;
        }

        private void cleanUp() {
            drawable1 = null;
            drawable2 = null;
            drawable3 = null;
            newImage = null;
        }
    }

    private static class CleanupTransitionListener extends TransitionListenerAdapter {

        private final ImageStack imageStack;
        private final Scene defaultScene;
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
            // The default scene will be the new view layout so we have to reset our
            // view fields to point to the new views (also allows the previous default layout to be
            // garbage collected)
            imageStack.stackItem1 = sceneRoot.findViewById(R.id.gv_stack_item_1);
            imageStack.stackItem2 = sceneRoot.findViewById(R.id.gv_stack_item_2);
            imageStack.stackItem3 = sceneRoot.findViewById(R.id.gv_stack_item_3);
            imageStack.badge = sceneRoot.findViewById(R.id.gv_badge);
            imageStack.subtitle = sceneRoot.findViewById(R.id.gv_stack_subtitle);

            // Push the images to the left (remove last image and show image on top)
            // Image count was already increased so when we have at least 3 images it means
            // that there was an image in the middle item which can be moved to the bottom item
            if (imageStack.imageCount >= 3) {
                setDrawableOrBlack(imageStack.stackItem3, drawable2);
            }
            // WHen we have at least 2 images it means that there was an image in the top
            // item which can be moved to the middle item
            if (imageStack.imageCount >= 2) {
                setDrawableOrBlack(imageStack.stackItem2, drawable1);
            }
            setBitmapOrBlack(imageStack.stackItem1, newImage);

            // Update the badge
            imageStack.badge.setText(String.valueOf(imageStack.imageCount));

            // Make the top item clickable
            imageStack.stackItem1.setClickable(true);
            imageStack.stackItem1.setFocusable(true);
            if (imageStack.clickListener != null) {
                imageStack.stackItem1.setOnClickListener(imageStack.clickListener);
            }

            cleanUp();
        }

        private void cleanUp() {
            drawable1 = null;
            drawable2 = null;
            newImage = null;
        }
    }
}
