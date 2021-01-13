package net.gini.android.vision.requirements;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * <p>
 *     Contains the report of the requirements check result.
 * </p>
 */
public class RequirementsReport {

    private final boolean mFulfilled;
    private final List<RequirementReport> mRequirementReports;

    RequirementsReport(final boolean fulfilled,
            @NonNull final List<RequirementReport> requirementReports) {
        mFulfilled = fulfilled;
        mRequirementReports = requirementReports;
    }

    /**
     * <p>
     *     Whether the requirements were fulfilled or not.
     * </p>
     * @return {@code true} if the requirements were met
     */
    public boolean isFulfilled() {
        return mFulfilled;
    }

    /**
     * <p>
     *     Reports for all the checked requirements.
     * </p>
     * @return a list of reports for all the checked requirements.
     */
    @NonNull
    public List<RequirementReport> getRequirementReports() {
        return mRequirementReports;
    }

    @Override
    public String toString() {
        return "RequirementsReport{" + "mFulfilled=" + mFulfilled
                + ", mRequirementReports=" + mRequirementReports
                + '}';
    }
}
