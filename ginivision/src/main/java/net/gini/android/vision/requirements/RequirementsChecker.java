package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class RequirementsChecker {

    private final List<? extends Requirement> mRequirements;

    RequirementsChecker(@NonNull List<? extends Requirement> requirements) {
        mRequirements = requirements;
    }

    RequirementsReport checkRequirements() {
        boolean result = true;
        List<RequirementReport> requirementsReports = new ArrayList<>(mRequirements.size());

        for (Requirement requirement : mRequirements) {
            RequirementReport report = requirement.check();
            result = result && report.isFulfilled();
            requirementsReports.add(report);
        }

        return new RequirementsReport(result, requirementsReports);
    }
}
