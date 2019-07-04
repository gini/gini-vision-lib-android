package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import androidx.annotation.NonNull;

@RunWith(JUnit4.class)
public class RequirementsCheckerTest {

    @Test
    public void should_reportFulfilled_ifAllRequirements_wereMet() {
        final RequirementsChecker checker = new RequirementsChecker(Arrays.asList(
                new FulfilledRequirement(),
                new FulfilledRequirement(),
                new FulfilledRequirement()
        ));

        assertThat(checker.checkRequirements().isFulfilled()).isTrue();
    }

    @Test
    public void should_reportUnfulfilled_ifAtLeastOneRequirement_wasNotMet() {
        final RequirementsChecker checker = new RequirementsChecker(Arrays.asList(
                new FulfilledRequirement(),
                new FulfilledRequirement(),
                new UnfulfilledRequirement()
        ));

        assertThat(checker.checkRequirements().isFulfilled()).isFalse();
    }

    @Test
    public void should_returnReports_forCheckedRequirements() {
        final RequirementsChecker checker = new RequirementsChecker(Arrays.asList(
                new FulfilledRequirement(),
                new UnfulfilledRequirement()
        ));

        final RequirementsReport report = checker.checkRequirements();

        for (final RequirementReport requirementReport : report.getRequirementReports()) {
            switch (requirementReport.getRequirementId()) {
                case CAMERA:
                    assertThat(requirementReport.getRequirementId()).isEqualTo(
                            RequirementId.CAMERA);
                    assertThat(requirementReport.isFulfilled()).isTrue();
                    assertThat(requirementReport.getDetails()).isEqualTo("fulfilled");
                    break;
                case CAMERA_PERMISSION:
                    assertThat(requirementReport.getRequirementId()).isEqualTo(
                            RequirementId.CAMERA_PERMISSION);
                    assertThat(requirementReport.isFulfilled()).isFalse();
                    assertThat(requirementReport.getDetails()).isEqualTo("unfulfilled");
                    break;
            }
        }
    }

    private static class FulfilledRequirement implements Requirement {

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

    private static class UnfulfilledRequirement implements Requirement {

        @NonNull
        @Override
        public RequirementId getId() {
            // Irrelevant
            return RequirementId.CAMERA_PERMISSION;
        }

        @NonNull
        @Override
        public RequirementReport check() {
            return new RequirementReport(getId(), false, "unfulfilled");
        }
    }
}
