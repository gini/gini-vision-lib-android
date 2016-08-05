package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

import java.util.List;

public class RequirementsReport {

    private final boolean mFulfilled;
    private final List<RequirementReport> mRequirementsReports;

    public RequirementsReport(boolean fulfilled, @NonNull List<RequirementReport> requirementsReports) {
        mFulfilled = fulfilled;
        mRequirementsReports = requirementsReports;
    }

    public boolean isFulfilled() {
        return mFulfilled;
    }

    @NonNull
    public List<RequirementReport> getRequirementsReports() {
        return mRequirementsReports;
    }
}
