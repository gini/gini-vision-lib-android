package net.gini.android.vision.requirements;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

class RequirementsChecker {

    private final List<? extends Requirement> mRequirements;

    RequirementsChecker(@NonNull final List<? extends Requirement> requirements) {
        mRequirements = requirements;
    }

    RequirementsReport checkRequirements() {
        boolean result = true;
        final List<RequirementReport> requirementsReports = new ArrayList<>(mRequirements.size());

        for (final Requirement requirement : mRequirements) {
            final RequirementReport report = requirement.check();
            result = result && report.isFulfilled();
            requirementsReports.add(report);
        }

        return new RequirementsReport(result, requirementsReports);
    }
}
