package net.gini.android.vision.review.multipage;

import static net.gini.android.vision.analysis.AnalysisActivity.RESULT_NO_EXTRACTIONS;
import static net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.review.ReviewActivity;

import java.util.List;

/**
 * Created by Alpar Szotyori on 16.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * <h3>Screen API</h3>
 *
 * When you use the Screen API and have enabled the multi-page feature, the {@code
 * MultiPageReviewActivity} displays the photographed or imported images and allows the user to
 * review them by checking the order, sharpness, quality and orientation of the images. The user can
 * correct the order by dragging the thumbnails of the images and can also correct the orientation
 * by rotating the images.
 *
 * <p> If multi-page has been enabled then the {@code MultiPageReviewActivity} is started by the
 * {@link CameraActivity} after the user has taken the first photo or imported the first image of a
 * document. For subsequent images the user has to tap on the image stack in the Camera Screen to
 * launch it.
 *
 * <p> <b>Important:</b> A {@link GiniVision} instance is required to use the {@code
 * MultiPageReviewActivity}
 *
 * <h3>Customizing the Multi-Page Review Screen</h3>
 *
 * Customizing the look of the Review Screen is done via overriding of app resources.
 *
 * <p> The following items are customizable:
 *
 * <ul>
 *
 * <li><b>Rotate button icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 * gv_review_button_rotate.png}
 *
 * <li><b>Rotate button color:</b>  via the color resources named {@code gv_review_fab_mini} and
 * {@code gv_review_fab_mini_pressed}
 *
 * <li><b>Next button icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 * gv_review_fab_next.png}
 *
 * <li><b>Next button color:</b> via the color resources named {@code gv_review_fab} and {@code
 * gv_review_fab_pressed}
 *
 * <li><b>Bottom advice text:</b> via the string resource named {@code gv_review_bottom_panel_text}
 *
 * <li><b>Bottom text color:</b> via the color resource named {@code gv_review_bottom_panel_text}
 *
 * <li><b>Bottom text font:</b> via overriding the style named {@code
 * GiniVisionTheme.Review.BottomPanel.TextStyle} and setting an item named {@code gvCustomFont} with
 * the path to the font file in your {@code assets} folder
 *
 * <li><b>Bottom text style:</b> via overriding the style named {@code
 * GiniVisionTheme.Review.BottomPanel.TextStyle} and setting an item named {@code android:textStyle}
 * to {@code normal}, {@code bold} or {@code italic}
 *
 * <li><b>Bottom text size:</b> via overriding the style named {@code
 * GiniVisionTheme.Review.BottomPanel.TextStyle} and setting an item named {@code android:textSize}
 * to the desired {@code sp} size
 *
 * <li><b>Bottom panel background color:</b> via the color resource named {@code
 * gv_review_bottom_panel_background}
 *
 * <li><b>Background color:</b> via the color resource named {@code gv_background}. <b>Note:</b>
 * this color resource is global to all Activities ({@link CameraActivity}, {@link
 * OnboardingActivity}, {@link ReviewActivity}, {@link AnalysisActivity})
 *
 * </ul>
 *
 * <p> <b>Important:</b> All overridden styles must have their respective {@code Root.} prefixed
 * style as their parent. Ex.: the parent of {@code GiniVisionTheme.Review.BottomPanel.TextStyle}
 * must be {@code Root.GiniVisionTheme.Review.BottomPanel.TextStyle}.
 *
 * <h3>Customizing the Action Bar</h3>
 *
 * Customizing the Action Bar is also done via overriding of app resources and each one - except the
 * title string resource - is global to all Activities ({@link CameraActivity}, {@link
 * OnboardingActivity}, {@link ReviewActivity}, {@link MultiPageReviewActivity}, {@link
 * AnalysisActivity}).
 *
 * <p> The following items are customizable:
 *
 * <ul>
 *
 * <li><b>Background color:</b> via the color resource named {@code gv_action_bar} (highly
 * recommended for Android 5+: customize the status bar color via {@code gv_status_bar})
 *
 * <li><b>Title:</b> via the string resource named {@code gv_title_multi_page_review}
 *
 * <li><b>Title color:</b> via the color resource named {@code gv_action_bar_title}
 *
 * <li><b>Back button (only for {@link ReviewActivity}, {@link MultiPageReviewActivity} and {@link
 * AnalysisActivity}):</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 * gv_action_bar_back}
 *
 * </ul>
 */
public class MultiPageReviewActivity extends AppCompatActivity implements
        MultiPageReviewFragmentListener {

    private static final String MP_REVIEW_FRAGMENT = "MP_REVIEW_FRAGMENT";
    private static final int ANALYSE_DOCUMENT_REQUEST = 1;

    private MultiPageReviewFragment mFragment;

    public static Intent createIntent(@NonNull final Context context) {
        return new Intent(context, MultiPageReviewActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_multi_page_review);
        if (savedInstanceState == null) {
            initFragment();
        } else {
            retainFragment();
        }
        enableHomeAsUp(this);
    }

    private void initFragment() {
        if (!isFragmentShown()) {
            createFragment();
            showFragment();
        }
    }

    private boolean isFragmentShown() {
        return getSupportFragmentManager().findFragmentByTag(MP_REVIEW_FRAGMENT) != null;
    }

    private void createFragment() {
        mFragment = MultiPageReviewFragment.createInstance();
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_review_multi_page_document, mFragment, MP_REVIEW_FRAGMENT)
                .commit();
    }

    private void retainFragment() {
        mFragment = (MultiPageReviewFragment) getSupportFragmentManager().findFragmentByTag(
                MP_REVIEW_FRAGMENT);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProceedToAnalysisScreen(
            @NonNull final GiniVisionMultiPageDocument multiPageDocument) {
        final List documents = multiPageDocument.getDocuments();
        if (documents.size() == 0) {
            return;
        }
        final Intent intent = new Intent(this, AnalysisActivity.class);
        intent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, multiPageDocument);
        startActivityForResult(intent, ANALYSE_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        if (requestCode == ANALYSE_DOCUMENT_REQUEST) {
            if (resultCode == RESULT_NO_EXTRACTIONS) {
                finish();
            } else if (resultCode != Activity.RESULT_CANCELED) {
                setResult(resultCode, data);
                finish();
            }
        }
    }

}
