package net.gini.android.vision.camera;

import static net.gini.android.vision.internal.util.FeatureConfiguration.shouldShowOnboardingAtFirstRun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.gini.android.vision.Document;
import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.document.QRCodeDocument;
import net.gini.android.vision.help.HelpActivity;
import net.gini.android.vision.internal.util.ActivityHelper;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.review.ReviewActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * <h3>Screen API</h3>
 *
 * <p>
 *     {@code CameraActivity} is the main entry point to the Gini Vision Library when using the Screen
 *     API.
 * </p>
 * <p>
 *     It shows a camera preview with tap-to-focus functionality and a trigger button. The camera
 *     preview also shows document corner guides to which the user should align the document.
 * </p>
 * <p>
 *     On tablets in landscape orientation the camera trigger button is shown on the right side of the
 *     screen for easier access.
 * </p>
 * <p>
 *     If {@link CameraActivity#EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION} was set and {@link DocumentImportEnabledFileTypes} is not equal to {@link DocumentImportEnabledFileTypes#NONE}
 *     then a button for importing documents is shown next to the trigger button. A hint popup is displayed the first time the Gini Vision Library is used to inform the user about document importing.
 * </p>
 * <p>
 *     For importing documents {@code READ_EXTERNAL_STORAGE} permission is required and if the permission is not granted the Gini Vision Library will prompt the user to grant the permission. See {@code Customizing the Camera Screen} on how to override the message and button titles for the rationale and on permission denial alerts.
 * </p>
 * <p>
 *     Start the {@code CameraActivity} with {@link android.app.Activity#startActivityForResult(Intent,
 *     int)} to receive the {@link GiniVisionError} in case there was an error.
 * </p>
 * <p>
 *     These extras are mandatory:
 * <ul>
 *     <li>{@link CameraActivity#EXTRA_IN_REVIEW_ACTIVITY} - use the {@link
 *         CameraActivity#setReviewActivityExtra(Intent, Context, Class)} helper to set it. Must contain an
 *         explicit Intent to the {@link ReviewActivity} subclass from your application</li>
 *     <li>{@link CameraActivity#EXTRA_IN_ANALYSIS_ACTIVITY} - use the {@link
 *         CameraActivity#setAnalysisActivityExtra(Intent, Context, Class)} helper to set it. Must contain
 *         an explicit Intent to the {@link AnalysisActivity} subclass from your application</li>
 * </ul>
 * </p>
 * <p>
 *     Optional extras are:
 * <ul>
 *     <li>{@link CameraActivity#EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN} - the Onboarding Screen is shown
 *         by default the first time the Gini Vision Library is started. You may disable it by setting this
 *         extra to {@code false} - we highly recommend keeping the default behavior</li>
 *     <li>{@link CameraActivity#EXTRA_IN_SHOW_ONBOARDING} - if set to {@code true} the Onboarding
 *         Screen is shown when the Gini Vision Library is started</li>
 *     <li>{@link CameraActivity#EXTRA_IN_ONBOARDING_PAGES} - custom pages for the Onboarding Screen as
 *         an {@link ArrayList} containing {@link OnboardingPage} objects</li>
 *     <li><b>Deprecated</b> {@link CameraActivity#EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY} - if set to {@code true} the
 *         back button closes the Gini Vision Library from any of its activities with result code {@link
 *         CameraActivity#RESULT_CANCELED}</li>
 *     <li>{@link CameraActivity#EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION} - must contain a {@link GiniVisionFeatureConfiguration} instance to apply the feature configuration</li>
 * </ul>
 * </p>
 * <p>
 *     The following result codes need to be handled:
 * <ul>
 *     <li>{@link CameraActivity#RESULT_OK} - image of a document was taken, reviewed and analyzed</li>
 *     <li>{@link CameraActivity#RESULT_CANCELED} - image of document was not taken, user canceled the
 *         Gini Vision Library</li>
 *     <li>{@link CameraActivity#RESULT_ERROR} - an error occured</li>
 * </ul>
 * </p>
 * <p>
 *     Result extra returned by the {@code CameraActivity}:
 * <ul>
 *     <li>{@link CameraActivity#EXTRA_OUT_ERROR} - set when result is {@link
 *         CameraActivity#RESULT_ERROR}, contains a {@link GiniVisionError} object detailing what went
 *         wrong</li>
 * </ul>
 * </p>
 * <p>
 *     <b>Note:</b> For returning the extractions from the Gini API you can add your own extras in
 *     {@link ReviewActivity#onAddDataToResult(Intent)} or
 *     {@link AnalysisActivity#onAddDataToResult(Intent)}.
 * </p>
 * <p>
 *     If the camera could not be opened due to missing permissions, the content of the Camera Screen is
 *     replaced with a no-camera icon, a short message and an optional button. The button is shown only
 *     on Android 6.0+ and tapping the button leads the user to the Application Details page in the
 *     Settings. If these are shown on Android 5.0 and earlier means that the camera permission was not
 *     declared in your manifest.
 * </p>
 *
 * <h3>Customizing the Camera Screen</h3>
 *
 * <p>
 *     Customizing the look of the Camera Screen is done via overriding of app resources.
 * </p>
 * <p>
 *     The following items are customizable:
 * <ul>
 *     <li>
 *         <b>Document corner guides:</b> via the color resource named {@code gv_camera_preview_corners}
 *     </li>
 *     <li>
 *         <b>Camera trigger button:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 *         gv_camera_trigger_default.png} and {@code gv_camera_trigger_pressed.png}
 *     </li>
 *     <li>
 *         <b>Document import button:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_document_import_icon.png}
 *     </li>
 *     <li>
 *         <b>Document import hint background:</b> via the color resource named {@code gv_document_import_hint_background}
 *     </li>
 *     <li>
 *         <b>Document import hint close icon color:</b> via the color resource name {@code gv_hint_close}
 *     </li>
 *     <li>
 *         <b>Document import hint text:</b> via the string resource named {@code gv_document_import_hint_text}
 *     </li>
 *     <li>
 *         <b>Document import hint text size:</b>  via overriding the style named {@code
 *         GiniVisionTheme.Camera.DocumentImportHint.TextStyle} and setting an item named {@code
 *         android:textSize} with the desired {@code sp} size
 *     </li>
 *     <li>
 *         <b>Document import hint text color:</b> via the color resource name {@code gv_document_import_hint_text}
 *     </li>
 *     <li>
 *         <b>Document import hint font:</b>  via overriding the style named {@code
 *         GiniVisionTheme.Camera.DocumentImportHint.TextStyle} and setting an item named {@code
 *         gvCustomFont} with the path to the font file in your {@code assets} folder
 *     </li>
 *     <li>
 *         <b>QRCode detected popup background:</b> via the color resource named {@code gv_qrcode_detected_popup_background}
 *     </li>
 *     <li>
 *         <b>QRCode detected popup texts:</b> via the string resources named {@code gv_qrcode_detected_popup_message_1} and
 *         {@code gv_qrcode_detected_popup_message_2}
 *     </li>
 *     <li>
 *         <b>QRCode detected popup text sizes:</b>  via overriding the styles named {@code
 *         GiniVisionTheme.Camera.QRCodeDetectedPopup.Message1.TextStyle} and {@code
 *         GiniVisionTheme.Camera.QRCodeDetectedPopup.Message2.TextStyle} and setting an item named {@code
 *         android:textSize} with the desired {@code sp} size
 *     </li>
 *     <li>
 *         <b>QRCode detected popup text colors:</b> via the color resource name {@code gv_qrcode_detected_popup_message_1} and
 *         {@code gv_qrcode_detected_popup_message_2}
 *     </li>
 *     <li>
 *         <b>QRCode detected popup fonts:</b>  via overriding the styles named {@code
 *         GiniVisionTheme.Camera.QRCodeDetectedPopup.Message1.TextStyle} and {@code
 *         GiniVisionTheme.Camera.QRCodeDetectedPopup.Message2.TextStyle} and setting an item named {@code
 *         gvCustomFont} with the path to the font file in your {@code assets} folder
 *     </li>
 *     <li>
 *         <b>Read storage permission rationale text:</b> via the string resource named {@code gv_storage_permission_rationale}
 *     </li>
 *     <li>
 *         <b>Read storage permission rationale positive button text:</b> via the string resource named {@code gv_storage_permission_rationale_positive_button}
 *     </li>
 *     <li>
 *         <b>Read storage permission rationale negative button text:</b> via the string resource named {@code gv_storage_permission_rationale_negative_button}
 *     </li>
 *     <li>
 *         <b>Read storage permission rationale button color:</b> via the color resource named {@code gv_accent}
 *     </li>
 *     <li>
 *         <b>Read storage permission denied text:</b> via the string resource named {@code gv_storage_permission_denied}
 *     </li>
 *     <li>
 *         <b>Read storage permission denied positive button text:</b> via the string resource named {@code gv_storage_permission_denied_positive_button}
 *     </li>
 *     <li>
 *         <b>Read storage permission denied negative button text:</b> via the string resource named {@code gv_storage_permission_denied_negative_button}
 *     </li>
 *     <li>
 *         <b>Read storage permission denied button color:</b> via the color resource named {@code gv_accent}
 *     </li>
 *     <li>
 *         <b>Tap-to-focus indicator:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 *         gv_camera_focus_indicator.png}
 *     </li>
 *     <li>
 *         <b>Help menu item icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 *         gv_help_icon.png}
 *     </li>
 *     <li>
 *         <b>Onboarding menu item title:</b> via the string resource named {@code gv_show_onboarding}
 *     </li>
 *     <li>
 *         <b>Background color:</b> via the color resource named {@code gv_background}. <b>Note:</b> this
 *         color resource is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity},
 *         {@link ReviewActivity}, {@link AnalysisActivity})
 *     </li>
 *     <li>
 *         <b>No-camera icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code
 *         gv_no_camera.png}
 *     </li>
 *     <li>
 *         <b>No camera permission text:</b> via the string resource named {@code
 *         gv_camera_error_no_permission}
 *     </li>
 *     <li>
 *         <b>No camera permission text color:</b> via the color resource named {@code
 *         gv_camera_error_no_permission}
 *     </li>
 *     <li>
 *         <b>No camera permission font:</b> via overriding the style named {@code
 *         GiniVisionTheme.Camera.Error.NoPermission.TextStyle} and setting an item named {@code
 *         gvCustomFont} with the path to the font file in your {@code assets} folder
 *     </li>
 *     <li>
 *         <b>No camera permission text style:</b> via overriding the style named {@code
 *         GiniVisionTheme.Camera.Error.NoPermission.TextStyle} and setting an item named {@code
 *         android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *     </li>
 *     <li>
 *         <b>No camera permission text size:</b> via overriding the style named {@code
 *         GiniVisionTheme.Camera.Error.NoPermission.TextStyle} and setting an item named {@code
 *         android:textSize} to the desired {@code sp} size
 *     </li>
 *     <li>
 *         <b>No camera permission button title:</b> via the string resource named {@code
 *         gv_camera_error_no_permission_button_title}
 *     </li>
 *     <li>
 *         <b>No camera permission button title color:</b> via the color resources named {@code
 *         gv_camera_error_no_permission_button_title} and {@code
 *         gv_camera_error_no_permission_button_title_pressed}
 *     </li>
 *     <li>
 *         <b>No camera permission button font:</b> via overriding the style named {@code
 *         GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle} and setting an item named {@code
 *         gvCustomFont} with the path to the font file in your {@code assets} folder
 *     </li>
 *     <li>
 *         <b>No camera permission button text style:</b> via overriding the style named {@code
 *         GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle} and setting an item named {@code
 *         android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *     </li>
 *     <li>
 *         <b>No camera permission button text size:</b> via overriding the style named {@code
 *         GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle} and setting an item named {@code
 *         android:textSize} to the desired {@code sp} size
 *     </li>
 * </ul>
 * </p>
 *
 * <p>
 *     <b>Important:</b> All overriden styles must have their respective {@code Root.} prefixed style as
 *     their parent. Ex.: the parent of {@code GiniVisionTheme.Camera.Error.NoPermission.TextStyle} must
 *     be {@code Root.GiniVisionTheme.Camera.Error.NoPermission.TextStyle}.
 * </p>
 *
 * <h3>Customizing the Action Bar</h3>
 *
 * <p>
 *     Customizing the Action Bar is also done via overriding of app resources and each one - except the
 *     title string resource - is global to all Activities ({@link CameraActivity}, {@link
 *     OnboardingActivity}, {@link ReviewActivity}, {@link AnalysisActivity}).
 * </p>
 * <p>
 *     The following items are customizable:
 * <ul>
 *     <li>
 *         <b>Background color:</b> via the color resource named {@code gv_action_bar} (highly recommended
 *         for Android 5+: customize the status bar color via {@code gv_status_bar})
 *     </li>
 *     <li>
 *         <b>Title:</b> via the string resource name {@code gv_title_camera}
 *     </li>
 *     <li>
 *         <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
 *     </li>
 * </ul>
 * </p>
 **/
public class CameraActivity extends AppCompatActivity implements CameraFragmentListener,
        CameraFragmentInterface {

    /**
     * <p>
     * Mandatory extra which must contain an explicit Intent to the {@link ReviewActivity} subclass
     * from your application.
     * </p>
     * <p>
     * Use the {@link CameraActivity#setReviewActivityExtra(Intent, Context, Class)} helper to set
     * it.
     * </p>
     */
    public static final String EXTRA_IN_REVIEW_ACTIVITY = "GV_EXTRA_IN_REVIEW_ACTIVITY";
    /**
     * <p>
     * Mandatory extra which must contain an explicit Intent to the {@link AnalysisActivity}
     * subclass from your application.
     * </p>
     * <p>
     * Use the {@link CameraActivity#setAnalysisActivityExtra(Intent, Context, Class)} helper to set
     * it.
     * </p>
     */
    public static final String EXTRA_IN_ANALYSIS_ACTIVITY = "GV_EXTRA_IN_ANALYSIS_ACTIVITY";
    /**
     * Optional extra which must contain an {@code ArrayList} with {@link OnboardingPage} objects.
     *
     * @deprecated Configuration should be applied by creating a {@link GiniVision} instance using
     * {@link GiniVision#newInstance()} and the returned {@link GiniVision.Builder}.
     */
    public static final String EXTRA_IN_ONBOARDING_PAGES = "GV_EXTRA_IN_ONBOARDING_PAGES";
    /**
     * <p>
     * Optional extra which must contain a boolean and indicates whether the Onboarding Screen
     * should be shown when the Gini Vision Library is started for the first time.
     * </p>
     * <p>
     * Default value is {@code true}.
     * </p>
     *
     * @deprecated Configuration should be applied by creating a {@link GiniVision} instance using
     * {@link GiniVision#newInstance()} and the returned {@link GiniVision.Builder}.
     */
    public static final String EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN =
            "GV_EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN";
    /**
     * <p>
     * Optional extra which must contain a boolean and indicates whether the Onboarding Screen
     * should be shown when the Gini Vision Library is started.
     * </p>
     * <p>
     * Default value is {@code false}.
     * </p>
     */
    public static final String EXTRA_IN_SHOW_ONBOARDING = "GV_EXTRA_IN_SHOW_ONBOARDING";

    /**
     * <p>
     * Optional extra wich must contain a boolean and indicates whether the back button should close
     * the Gini Vision Library.
     * </p>
     * <p>
     * Default value is {@code false}.
     * </p>
     * @deprecated The option to close the library with the back button from any screen will be removed
     * in a future version.
     */
    public static final String EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY =
            "GV_EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY";

    /**
     * Optional extra which must contain a {@link GiniVisionFeatureConfiguration} instance.
     *
     * @deprecated Configuration should be applied by creating a {@link GiniVision} instance using
     * {@link GiniVision#newInstance()} and the returned {@link GiniVision.Builder}.
     */
    public static final String EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION =
            "GV_EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION";

    /**
     * <p>
     * Returned when the result code is {@link CameraActivity#RESULT_ERROR} and contains a {@link
     * GiniVisionError} object detailing what went wrong.
     * </p>
     */
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    // WIP: analyse qr code
    public static final String EXTRA_OUT_EXTRACTIONS = "GV_EXTRA_OUT_EXTRACTIONS";

    /**
     * <p>
     * Returned result code in case something went wrong. You should retrieve the {@link
     * CameraActivity#EXTRA_OUT_ERROR} extra to find out what went wrong.
     * </p>
     */
    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    @VisibleForTesting
    static final int REVIEW_DOCUMENT_REQUEST = 1;
    private static final int ONBOARDING_REQUEST = 2;
    private static final int ANALYSE_DOCUMENT_REQUEST = 3;
    private static final String CAMERA_FRAGMENT = "CAMERA_FRAGMENT";
    private static final String ONBOARDING_SHOWN_KEY = "ONBOARDING_SHOWN_KEY";

    private ArrayList<OnboardingPage> mOnboardingPages; // NOPMD
    private Intent mReviewDocumentActivityIntent;
    private Intent mAnalyzeDocumentActivityIntent;
    private boolean mShowOnboarding;
    private boolean mShowOnboardingAtFirstRun = true;
    private boolean mOnboardingShown;
    private boolean mBackButtonShouldCloseLibrary;
    private GiniVisionCoordinator mGiniVisionCoordinator;
    private Document mDocument;
    private GiniVisionFeatureConfiguration mGiniVisionFeatureConfiguration;

    private CameraFragmentCompat mFragment;

    /**
     * <p>
     * Helper for setting the {@link CameraActivity#EXTRA_IN_REVIEW_ACTIVITY}.
     * </p>
     *
     * @param target              your explicit {@link Intent} used to start the {@link
     *                            CameraActivity}
     * @param context             {@link Context} used to create the explicit {@link Intent} for
     *                            your {@link
     *                            ReviewActivity} subclass
     * @param reviewActivityClass class of your {@link ReviewActivity} subclass
     * @param <T>                 type of your {@link ReviewActivity} subclass
     */
    public static <T extends ReviewActivity> void setReviewActivityExtra(final Intent target,
            final Context context,
            final Class<T> reviewActivityClass) {
        ActivityHelper.setActivityExtra(target, EXTRA_IN_REVIEW_ACTIVITY, context,
                reviewActivityClass);
    }

    /**
     * <p>
     * Helper for setting the {@link CameraActivity#EXTRA_IN_ANALYSIS_ACTIVITY}.
     * </p>
     *
     * @param target                your explicit {@link Intent} used to start the {@link
     *                              CameraActivity}
     * @param context               {@link Context} used to create the explicit {@link Intent} for
     *                              your {@link
     *                              AnalysisActivity} subclass
     * @param analysisActivityClass class of your {@link AnalysisActivity} subclass
     * @param <T>                   type of your {@link AnalysisActivity} subclass
     */
    public static <T extends AnalysisActivity> void setAnalysisActivityExtra(final Intent target,
            final Context context,
            final Class<T> analysisActivityClass) {
        ActivityHelper.setActivityExtra(target, EXTRA_IN_ANALYSIS_ACTIVITY, context,
                analysisActivityClass);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_camera);
        readExtras();
        createGiniVisionCoordinator();
        if (savedInstanceState == null) {
            initFragment();
        } else {
            restoreSavedState(savedInstanceState);
            retainFragment();
        }
        showOnboardingIfRequested();
    }

    private void restoreSavedState(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        mOnboardingShown = savedInstanceState.getBoolean(ONBOARDING_SHOWN_KEY);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ONBOARDING_SHOWN_KEY, mOnboardingShown);
    }

    private void createFragment() {
        if (mGiniVisionFeatureConfiguration != null) {
            mFragment = createCameraFragmentCompat(mGiniVisionFeatureConfiguration);
        } else {
            mFragment = createCameraFragmentCompat();
        }
    }

    protected CameraFragmentCompat createCameraFragmentCompat() {
        return CameraFragmentCompat.createInstance();
    }

    protected CameraFragmentCompat createCameraFragmentCompat(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return CameraFragmentCompat.createInstance(giniVisionFeatureConfiguration);
    }

    private void initFragment() {
        if (!isFragmentShown()) {
            createFragment();
            showFragment();
        }
    }

    private boolean isFragmentShown() {
        return getSupportFragmentManager().findFragmentByTag(CAMERA_FRAGMENT) != null;
    }

    private void retainFragment() {
        mFragment = (CameraFragmentCompat) getSupportFragmentManager().findFragmentByTag(
                CAMERA_FRAGMENT);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_camera, mFragment, CAMERA_FRAGMENT)
                .commit();
    }

    private void showOnboardingIfRequested() {
        if (mShowOnboarding) {
            startOnboardingActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGiniVisionCoordinator.onCameraStarted();
        if (mOnboardingShown) {
            hideInterface();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    @VisibleForTesting
    void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mOnboardingPages = extras.getParcelableArrayList(EXTRA_IN_ONBOARDING_PAGES);
            mReviewDocumentActivityIntent = extras.getParcelable(EXTRA_IN_REVIEW_ACTIVITY);
            mAnalyzeDocumentActivityIntent = extras.getParcelable(EXTRA_IN_ANALYSIS_ACTIVITY);
            mShowOnboarding = extras.getBoolean(EXTRA_IN_SHOW_ONBOARDING, false);
            mShowOnboardingAtFirstRun = extras.getBoolean(EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN,
                    true);
            mBackButtonShouldCloseLibrary = extras.getBoolean(
                    EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, false);
            mGiniVisionFeatureConfiguration =
                    extras.getParcelable(EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mReviewDocumentActivityIntent == null) {
            throw new IllegalStateException(
                    "CameraActivity requires a ReviewActivity class. Call "
                            + "setReviewDocumentActivityExtra() to set it.");
        }
        if (mAnalyzeDocumentActivityIntent == null) {
            throw new IllegalStateException(
                    "CameraActivity requires an AnalyzeDocumentActivity class. Call "
                            + "setAnalyzeDocumentActivityExtra() to set it.");
        }
    }

    private void createGiniVisionCoordinator() {
        mGiniVisionCoordinator = GiniVisionCoordinator.createInstance(this);
        mGiniVisionCoordinator
                .setShowOnboardingAtFirstRun(
                        shouldShowOnboardingAtFirstRun(mShowOnboardingAtFirstRun))
                .setListener(new GiniVisionCoordinator.Listener() {
                    @Override
                    public void onShowOnboarding() {
                        startOnboardingActivity();
                    }
                });
    }

    /**
     * @exclude
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.gv_camera, menu);
        return true;
    }

    /**
     * @exclude
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.gv_action_show_onboarding) {
            startHelpActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startHelpActivity() {
        final Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
                mGiniVisionFeatureConfiguration);
        startActivity(intent);
    }

    @VisibleForTesting
    void startOnboardingActivity() {
        if (mOnboardingShown) {
            return;
        }
        final Intent intent = new Intent(this, OnboardingActivity.class);
        if (mOnboardingPages != null) {
            intent.putParcelableArrayListExtra(OnboardingActivity.EXTRA_ONBOARDING_PAGES,
                    mOnboardingPages);
        }
        hideInterface();
        startActivityForResult(intent, ONBOARDING_REQUEST);
        mOnboardingShown = true;
    }

    @Override
    public void onDocumentAvailable(@NonNull final Document document) {
        mDocument = document;
        if (mDocument.isReviewable()) {
            startReviewActivity(document);
        } else {
            startAnalysisActivity(document);
        }
    }

    @Override
    public void onQRCodeAvailable(@NonNull final QRCodeDocument qrCodeDocument) {

    }

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        callback.documentAccepted();
    }

    private void startReviewActivity(@NonNull final Document document) {
        final Intent reviewIntent = new Intent(mReviewDocumentActivityIntent);
        reviewIntent.putExtra(ReviewActivity.EXTRA_IN_DOCUMENT, document);
        reviewIntent.putExtra(EXTRA_IN_ANALYSIS_ACTIVITY,
                mAnalyzeDocumentActivityIntent);
        reviewIntent.putExtra(
                ReviewActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY,
                mBackButtonShouldCloseLibrary);
        startActivityForResult(reviewIntent, REVIEW_DOCUMENT_REQUEST);
    }

    private void startAnalysisActivity(@NonNull final Document document) {
        final Intent analysisIntent = new Intent(mAnalyzeDocumentActivityIntent);
        analysisIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, document);
        startActivityForResult(analysisIntent, ANALYSE_DOCUMENT_REQUEST);
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        final Intent result = new Intent();
        result.putExtra(EXTRA_OUT_ERROR, error);
        setResult(RESULT_ERROR, result);
        finish();
    }

    @Override
    public void onExtractionsAvailable(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        final Intent result = new Intent();
        final Bundle extractionsBundle = new Bundle();
        for (final Map.Entry<String, GiniVisionSpecificExtraction> extraction : extractions.entrySet()) {
            extractionsBundle.putParcelable(extraction.getKey(), extraction.getValue());
        }
        result.putExtra(CameraActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        switch (requestCode) {
            case REVIEW_DOCUMENT_REQUEST:
            case ANALYSE_DOCUMENT_REQUEST:
                if (mBackButtonShouldCloseLibrary
                        || (resultCode != Activity.RESULT_CANCELED
                        && resultCode != AnalysisActivity.RESULT_NO_EXTRACTIONS
                        && resultCode != ReviewActivity.RESULT_NO_EXTRACTIONS)) {
                    setResult(resultCode, data);
                    finish();
                    clearMemory();
                }
                break;
            case ONBOARDING_REQUEST:
                mOnboardingShown = false;
                showInterface();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showDocumentCornerGuides() {
        mFragment.showDocumentCornerGuides();
    }

    @Override
    public void hideDocumentCornerGuides() {
        mFragment.hideDocumentCornerGuides();
    }

    @Override
    public void showCameraTriggerButton() {
        mFragment.showCameraTriggerButton();
    }

    @Override
    public void hideCameraTriggerButton() {
        mFragment.hideCameraTriggerButton();
    }

    @Override
    public void showInterface() {
        mFragment.showInterface();
    }

    @Override
    public void hideInterface() {
        mFragment.hideInterface();
    }

    @Override
    public void showActivityIndicatorAndDisableInteraction() {
        mFragment.showActivityIndicatorAndDisableInteraction();
    }

    @Override
    public void hideActivityIndicatorAndEnableInteraction() {
        mFragment.hideActivityIndicatorAndEnableInteraction();
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        mFragment.showError(message, duration);
    }

    private void clearMemory() {
        mDocument = null; // NOPMD
    }
}
