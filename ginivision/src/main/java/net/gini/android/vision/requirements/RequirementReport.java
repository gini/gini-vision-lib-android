package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

/**
 * <p>
 *     Contains the report of a requirement check result.
 * </p>
 */
public class RequirementReport {

    private final RequirementId mRequirementId;
    private final boolean mFulfilled;
    private final String mDetails;

    RequirementReport(@NonNull RequirementId requirementId, boolean fulfilled,
            @NonNull String details) {
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
        final StringBuilder sb = new StringBuilder("RequirementReport{");
        sb.append("mRequirementId=").append(mRequirementId);
        sb.append(", mFulfilled=").append(mFulfilled);
        sb.append(", mDetails='").append(mDetails).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
