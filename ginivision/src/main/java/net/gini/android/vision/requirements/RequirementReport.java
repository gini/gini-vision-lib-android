package net.gini.android.vision.requirements;

import androidx.annotation.NonNull;

/**
 * <p>
 *     Contains the report of a requirement check result.
 * </p>
 */
public class RequirementReport {

    private final RequirementId mRequirementId;
    private final boolean mFulfilled;
    private final String mDetails;

    RequirementReport(@NonNull final RequirementId requirementId, final boolean fulfilled,
            @NonNull final String details) {
        mRequirementId = requirementId;
        mFulfilled = fulfilled;
        mDetails = details;
    }

    /**
     * <p>
     *     For identifying which requirement this report belongs to.
     * </p>
     * @return the {@link RequirementId} of the checked requirement
     */
    public RequirementId getRequirementId() {
        return mRequirementId;
    }

    /**
     * <p>
     *     Whether the requirement was fulfilled or not.
     * </p>
     * @return {@code true} if the requirement was met
     */
    public boolean isFulfilled() {
        return mFulfilled;
    }

    /**
     * <p>
     *     Details about the requirement unfulfillment.
     * </p>
     * @return unfulfillment details or an empty string, if the requirement was fulfilled
     */
    @NonNull
    public String getDetails() {
        return mDetails;
    }

    @Override
    public String toString() {
        return "RequirementReport{" + "mRequirementId=" + mRequirementId
                + ", mFulfilled=" + mFulfilled
                + ", mDetails='" + mDetails + '\''
                + '}';
    }
}
