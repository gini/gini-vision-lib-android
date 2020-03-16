package net.gini.android.vision.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

/**
 * Created by Alpar Szotyori on 13.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ImageStack extends RelativeLayout {

    private static final long BADGE_TRANSITION_DURATION_MS = 600;
    private static final long TRANSITION_DURATION_MS = 600;
    private static final long TRANSITION_START_DELAY_MS = 500;
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
        subtitle = findViewById(R.id.gv_stack_subtitle);
        if (!isInEditMode()) {
            badge.setVisibility(INVISIBLE);
            subtitle.setVisibility(INVISIBLE);
        }
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
            final int defStyleRes) { // NOPMD
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener clickListener) {
        this.clickListener = clickListener;
        if (imageCount > 0) {
            addClickListener();
        }
    }

    private void addClickListener() {
        addClickListener(stackItem1);
        addClickListener(stackItem2);
        addClickListener(stackItem3);
        addClickListener(subtitle);
    }

    private void addClickListener(@NonNull final View view) {
        final boolean clickable = clickListener != null;
        view.setClickable(clickable);
        view.setFocusable(clickable);
        view.setOnClickListener(clickListener);
    }

    public void addImage(@NonNull final StackBitmap bitmap) {
        addImage(bitmap, null);
    }

    public void addImage(@NonNull final StackBitmap bitmap,
            @Nullable final Transition.TransitionListener transitionListener) {
        // Get the current images visible in the stack
        final StackDrawable stackDrawable1 = new StackDrawable(stackItem1);
        final StackDrawable stackDrawable2 = new StackDrawable(stackItem2);
        final StackDrawable stackDrawable3 = new StackDrawable(stackItem3);

        imageCount++;

        final Scene imageAddedScene = Scene.getSceneForLayout(ImageStack.this,
                R.layout.gv_image_stack_image_added, getContext());

        // Show the current images in the image added scene
        final AddImageTransitionListener addImageTransitionListener =
                createAddImageTransitionListener(imageAddedScene, bitmap, stackDrawable1,
                        stackDrawable2,
                        stackDrawable3,
                        transitionListener);

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

        // Badge and subtitle have to be faded in
        final Fade fadeIn = new Fade(Fade.IN);
        fadeIn.addTarget(R.id.gv_badge);
        fadeIn.addTarget(R.id.gv_stack_subtitle);
        fadeIn.setDuration(BADGE_TRANSITION_DURATION_MS);
        addImageTransitions.addTransition(fadeIn);

        addImageTransitions.setStartDelay(TRANSITION_START_DELAY_MS);

        addImageTransitions.addListener(addImageTransitionListener);
        return addImageTransitions;
    }

    @NonNull
    private AddImageTransitionListener createAddImageTransitionListener(final Scene imageAddedScene,
            @NonNull final StackBitmap bitmap, final StackDrawable drawable1,
            final StackDrawable drawable2, final StackDrawable drawable3,
            @Nullable final Transition.TransitionListener transitionListener) {
        final AddImageTransitionListener addImageTransitionListener =
                new AddImageTransitionListener(imageAddedScene, this, transitionListener);
        addImageTransitionListener.setStackDrawable1(drawable1);
        addImageTransitionListener.setStackDrawable2(drawable2);
        addImageTransitionListener.setStackDrawable3(drawable3);
        addImageTransitionListener.setNewStackBitmap(bitmap);
        return addImageTransitionListener;
    }

    private static void transitionTo(final Scene scene, final Transition transition) {
        final TransitionManager transitionManager = new TransitionManager();
        transitionManager.setTransition(scene, transition);
        transitionManager.transitionTo(scene);
    }

    public void removeImages() {
        removeClickListeners();
        setImages(null);
    }

    private void removeClickListeners() {
        removeClickListener(stackItem1);
        removeClickListener(stackItem2);
        removeClickListener(stackItem3);
        removeClickListener(subtitle);
    }

    private void removeClickListener(
            @NonNull final View view) {
        view.setOnClickListener(null);
        view.setClickable(false);
        view.setFocusable(false);
    }

    public void setImages(@Nullable final List<StackBitmap> bitmaps) {
        imageCount = 0;
        resetImageViewContainers();
        if (bitmaps == null || bitmaps.isEmpty()) {
            badge.setText("");
            badge.setVisibility(INVISIBLE);
            subtitle.setVisibility(INVISIBLE);
        } else {
            imageCount = bitmaps.size();
            badge.setVisibility(VISIBLE);
            badge.setText(String.valueOf(imageCount));
            subtitle.setVisibility(VISIBLE);

            addClickListener();

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

    private void resetImageViewContainers() {
        resetImageView(stackItem1);
        resetImageView(stackItem2);
        resetImageView(stackItem3);
    }

    private void resetImageView(@NonNull final ImageView imageView) {
        imageView.setImageDrawable(null);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        rotateImageView(imageView, 0);
    }

    private static void rotateImageView(final ImageView imageView, final int rotation) {
        imageView.setRotation(rotation);
    }

    private static void setBitmapOrBlack(@NonNull final ImageView imageView,
            @Nullable final StackBitmap stackBitmap) {
        if (stackBitmap != null) {
            imageView.setImageBitmap(stackBitmap.bitmap);
            rotateImageView(imageView, stackBitmap.rotation);
        } else {
            imageView.setImageBitmap(null);
            imageView.setBackgroundColor(Color.BLACK);
            rotateImageView(imageView, 0);
        }
    }

    public void setImage(@Nullable final StackBitmap stackBitmap,
            @NonNull final Position position) {
        addClickListener();
        switch (position) {
            case TOP:
                setBitmapOrBlack(stackItem1, stackBitmap);
                break;
            case MIDDLE:
                setBitmapOrBlack(stackItem2, stackBitmap);
                break;
            case BOTTOM:
                setBitmapOrBlack(stackItem3, stackBitmap);
                break;
            default:
                throw new UnsupportedOperationException("Unknown position: " + position);
        }
    }

    public void setImageCount(final int count) {
        imageCount = count;
        badge.setText(String.valueOf(count));
        badge.setVisibility(VISIBLE);
        subtitle.setVisibility(VISIBLE);
    }

    private static void setDrawableOrBlack(@NonNull final ImageView imageView,
            @Nullable final StackDrawable stackDrawable) {
        if (stackDrawable != null && stackDrawable.drawable.getIntrinsicHeight() > 0) {
            imageView.setImageDrawable(stackDrawable.drawable);
            rotateImageView(imageView, stackDrawable.rotation);
        } else {
            imageView.setImageDrawable(null);
            imageView.setBackgroundColor(Color.BLACK);
            rotateImageView(imageView, 0);
        }
    }

    enum Position {
        TOP,
        MIDDLE,
        BOTTOM
    }

    private static class StackDrawable {

        Drawable drawable;
        int rotation;

        StackDrawable(@NonNull final ImageView imageView) {
            drawable = imageView.getDrawable();
            rotation = (int) imageView.getRotation();
        }
    }

    static class StackBitmap {

        Bitmap bitmap;
        int rotation;

        StackBitmap(final Bitmap bitmap, final int rotation) {
            this.bitmap = bitmap;
            this.rotation = rotation;
        }
    }

    private static class AddImageTransitionListener extends TransitionListenerAdapter {

        private final ImageStack imageStack;
        private final Scene imageAddedScene;
        private final Transition.TransitionListener transitionListener;
        private StackDrawable stackDrawable1;
        private StackDrawable stackDrawable2;
        private StackDrawable stackDrawable3;
        private StackBitmap newStackBitmap;

        private AddImageTransitionListener(final Scene imageAddedScene,
                final ImageStack imageStack,
                @Nullable final Transition.TransitionListener transitionListener) {
            this.imageAddedScene = imageAddedScene;
            this.imageStack = imageStack;
            this.transitionListener = transitionListener;
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
                setDrawableOrBlack(stackItem1View, stackDrawable1);
            }
            // When we have at least three images it means
            // there was an image in the middle stack item
            if (imageStack.imageCount >= 3) {
                setDrawableOrBlack(stackItem2View, stackDrawable2);
            }
            // When we have at least four images it means
            // there was an image in the bottom stack item
            if (imageStack.imageCount >= 4) {
                setDrawableOrBlack(stackItem3View, stackDrawable3);
            }
            if (imageStack.imageCount > 1) {
                badge.setVisibility(VISIBLE);
                subtitle.setVisibility(VISIBLE);
            }
            badge.setText(String.valueOf(imageStack.imageCount));

            // Show the new image
            setBitmapOrBlack(newImageView, newStackBitmap);

            if (transitionListener != null) {
                transitionListener.onTransitionStart(transition);
            }
        }

        void setStackDrawable1(final StackDrawable stackDrawable1) {
            this.stackDrawable1 = stackDrawable1;
        }

        void setStackDrawable2(final StackDrawable stackDrawable2) {
            this.stackDrawable2 = stackDrawable2;
        }

        void setStackDrawable3(final StackDrawable stackDrawable3) {
            this.stackDrawable3 = stackDrawable3;
        }

        void setNewStackBitmap(final StackBitmap newStackBitmap) {
            this.newStackBitmap = newStackBitmap;
        }

        @Override
        public void onTransitionEnd(@NonNull final Transition transition) {
            // Return to the default scene
            final Scene defaultScene = Scene.getSceneForLayout(imageStack,
                    R.layout.gv_image_stack_default, imageStack.getContext());

            // Pass the new image and the previous top 2 images to the final state
            final CleanupTransitionListener cleanupTransitionListener =
                    createCleanupTransitionListener(defaultScene, transitionListener);

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

            cleanupTransitions.addListener(cleanupTransitionListener);
            return cleanupTransitions;
        }

        @NonNull
        private CleanupTransitionListener createCleanupTransitionListener(
                final Scene defaultScene,
                @Nullable final Transition.TransitionListener transitionListener) {
            final CleanupTransitionListener cleanupTransitionListener =
                    new CleanupTransitionListener(defaultScene, imageStack, transitionListener);
            cleanupTransitionListener.setStackDrawable1(stackDrawable1);
            cleanupTransitionListener.setStackDrawable2(stackDrawable2);
            cleanupTransitionListener.setNewStackBitmap(newStackBitmap);
            return cleanupTransitionListener;
        }

        private void cleanUp() {
            stackDrawable1 = null; // NOPMD
            stackDrawable2 = null; // NOPMD
            stackDrawable3 = null; // NOPMD
            newStackBitmap = null; // NOPMD
        }
    }

    private static class CleanupTransitionListener extends TransitionListenerAdapter {

        private final ImageStack imageStack;
        private final Scene defaultScene;
        private final Transition.TransitionListener transitionListener;
        private StackDrawable stackDrawable1;
        private StackDrawable stackDrawable2;
        private StackBitmap newStackBitmap;

        private CleanupTransitionListener(final Scene defaultScene, final ImageStack imageStack,
                @Nullable final Transition.TransitionListener transitionListener) {
            this.defaultScene = defaultScene;
            this.imageStack = imageStack;
            this.transitionListener = transitionListener;
        }

        void setStackDrawable1(final StackDrawable stackDrawable1) {
            this.stackDrawable1 = stackDrawable1;
        }

        void setStackDrawable2(final StackDrawable stackDrawable2) {
            this.stackDrawable2 = stackDrawable2;
        }

        void setNewStackBitmap(final StackBitmap newStackBitmap) {
            this.newStackBitmap = newStackBitmap;
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
                setDrawableOrBlack(imageStack.stackItem3, stackDrawable2);
            }
            // WHen we have at least 2 images it means that there was an image in the top
            // item which can be moved to the middle item
            if (imageStack.imageCount >= 2) {
                setDrawableOrBlack(imageStack.stackItem2, stackDrawable1);
            }
            setBitmapOrBlack(imageStack.stackItem1, newStackBitmap);

            // Update the badge
            imageStack.badge.setVisibility(VISIBLE);
            imageStack.badge.setText(String.valueOf(imageStack.imageCount));
            imageStack.subtitle.setVisibility(VISIBLE);

            // Make stack items clickable
            imageStack.addClickListener();

            cleanUp();
        }

        @Override
        public void onTransitionEnd(@NonNull final Transition transition) {
            if (transitionListener != null) {
                transitionListener.onTransitionEnd(transition);
            }
        }

        private void cleanUp() {
            stackDrawable1 = null; // NOPMD
            stackDrawable2 = null; // NOPMD
            newStackBitmap = null; // NOPMD
        }
    }
}
