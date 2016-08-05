package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class RequirementsCheckerTest {

    @Test
    public void should_reportFulfilled_ifAllRequirements_wereMet() {
        RequirementsChecker checker = new RequirementsChecker(Arrays.asList(
                new FulfilledRequirement(),
                new FulfilledRequirement(),
                new FulfilledRequirement()
        ));

        assertThat(checker.checkRequirements().isFulfilled()).isTrue();
    }

    @Test
    public void should_reportUnfulfilled_ifAtLeastOneRequirement_wasNotMet() {
        RequirementsChecker checker = new RequirementsChecker(Arrays.asList(
                new FulfilledRequirement(),
                new FulfilledRequirement(),
                new UnfulfilledRequirement()
        ));

        assertThat(checker.checkRequirements().isFulfilled()).isFalse();
    }

    @Test
    public void should_returnReports_forCheckedRequirements() {
        RequirementsChecker checker = new RequirementsChecker(Arrays.asList(
                new FulfilledRequirement(),
                new UnfulfilledRequirement()
        ));

        RequirementsReport report = checker.checkRequirements();

        for (RequirementReport requirementReport : report.getRequirementsReports()) {
            switch (requirementReport.getRequirementId()) {
                case CAMERA:
                    assertThat(requirementReport.getRequirementId()).isEqualTo(RequirementId.CAMERA);
                    assertThat(requirementReport.isFulfilled()).isTrue();
                    assertThat(requirementReport.getDetails()).isEqualTo("fulfilled");
                    break;
                case MANIFEST_CAMERA_PERMISSION:
                    assertThat(requirementReport.getRequirementId()).isEqualTo(RequirementId.MANIFEST_CAMERA_PERMISSION);
                    assertThat(requirementReport.isFulfilled()).isFalse();
                    assertThat(requirementReport.getDetails()).isEqualTo("unfulfilled");
                    break;
            }
        }
    }

    private class FulfilledRequirement implements Requirement {

        @NonNull
        @Override
        public RequirementId getId() {
            return RequirementId.CAMERA;
        }

        @NonNull
        @Override
        public RequirementReport check() {
            return new RequirementReport(getId(), true, "fulfilled");
        }
    }

    private class UnfulfilledRequirement implements Requirement {

        @NonNull
        @Override
        public RequirementId getId() {
            // Irrelevant
            return RequirementId.MANIFEST_CAMERA_PERMISSION;
        }

        @NonNull
        @Override
        public RequirementReport check() {
            return new RequirementReport(getId(), false, "unfulfilled");
        }
    }
}
