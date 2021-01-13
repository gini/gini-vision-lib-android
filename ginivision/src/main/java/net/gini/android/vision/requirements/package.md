# Package net.gini.android.vision.requirements

Contains classes to check if the device meets all the requirements for using the Gini Vision Library.

Run [net.gini.android.vision.requirements.GiniVisionRequirements.checkRequirements(android.content.Context)] and check the returned
[net.gini.android.vision.requirements.RequirementsReport] to find out, if the requirements were met. If requirements were not met you can
iterate through the [net.gini.android.vision.requirements.RequirementsReport.getRequirementReports()] and check each
[net.gini.android.vision.requirements.RequirementReport] to find out which requirements were not met.

The checked requirements are listed in the [net.gini.android.vision.requirements.RequirementId] enum.
