package net.gini.android.vision.review;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.network.GiniVisionNetworkService;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Methods which both Review Fragments must implement.
 */
public interface ReviewFragmentInterface {

    /**
     * You should call this method after you've received the analysis results from the Gini API.
     *
     * <p> This is important for managing the behavior of the Review Document Fragment when the Next
     * button was clicked.
     *
     * <p> If the document has already been analyzed and the image wasn't changed when the user
     * tapped the Next button, {@link ReviewFragmentListener#onDocumentReviewedAndAnalyzed(Document)}
     * is called and there is no need to show an {@link AnalysisActivity} or {@link
     * AnalysisFragmentStandard} or {@link AnalysisFragmentCompat}.
     *
     * <p> If the document wasn't analyzed or the image was changed when the user tapped the Next
     * button, {@link ReviewFragmentListener#onProceedToAnalysisScreen(Document)} is called.
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed
     * internally by using the configured {@link GiniVisionNetworkService} implementation. The
     * extractions will be returned in the Analysis Screen in
     * {@link AnalysisFragmentListener#onExtractionsAvailable(Map, Map)}.
     */
    @Deprecated
    void onDocumentAnalyzed();

    /**
     * You should call this method after you've received the analysis results from the Gini API
     * without the required extractions.
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed
     * internally by using the configured {@link GiniVisionNetworkService} implementation.
     */
    @Deprecated
    void onNoExtractionsFound();

    /**
     * Set a listener for review events.
     *
     * <p> By default the hosting Activity is expected to implement the {@link
     * ReviewFragmentListener}. In case that is not feasible you may set the listener using this
     * method.
     *
     * <p> <b>Note:</b> the listener is expected to be available until the fragment is attached to
     * an activity. Make sure to set the listener before that.
     *
     * @param listener {@link ReviewFragmentListener} instance
     */
    void setListener(@NonNull final ReviewFragmentListener listener);
}
