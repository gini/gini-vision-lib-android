package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

public class RequirementReport {

    private final RequirementId mRequirementId;
    private final boolean mFulfilled;
    private final String mDetails;

    public RequirementReport(@NonNull RequirementId requirementId, boolean fulfilled, @NonNull String details) {
        mRequirementId = requirementId;
        mFulfilled = fulfilled;
        mDetails = details;
    }

    public RequirementId getRequirementId() {
        return mRequirementId;
    }

    public boolean isFulfilled() {
        return mFulfilled;
    }

    @NonNull
    public String getDetails() {
        return mDetails;
    }
}
