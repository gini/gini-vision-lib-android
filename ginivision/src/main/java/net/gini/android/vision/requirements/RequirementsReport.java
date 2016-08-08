package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

import java.util.List;

public class RequirementsReport {

    private final boolean mFulfilled;
    private final List<RequirementReport> mRequirementReports;

    public RequirementsReport(boolean fulfilled, @NonNull List<RequirementReport> requirementReports) {
        mFulfilled = fulfilled;
        mRequirementReports = requirementReports;
    }

    public boolean isFulfilled() {
        return mFulfilled;
    }

    @NonNull
    public List<RequirementReport> getRequirementReports() {
        return mRequirementReports;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RequirementsReport{");
        sb.append("mFulfilled=").append(mFulfilled);
        sb.append(", mRequirementReports=").append(mRequirementReports);
        sb.append('}');
        return sb.toString();
    }
}
