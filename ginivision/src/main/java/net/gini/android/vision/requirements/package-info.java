/**
 * <p>
 * Contains classes to check if the device meets all the requirements for using the Gini Vision Library.
 * </p>
 * <p>
 *     Run {@link net.gini.android.vision.requirements.GiniVisionRequirements#checkRequirements(android.content.Context)} and check the returned
 *     {@link net.gini.android.vision.requirements.RequirementsReport} to find out, if the requirements were met. If
 *     requirements were not met you can iterate through the {@link net.gini.android.vision.requirements.RequirementsReport#getRequirementReports()} and
 *     check each {@link net.gini.android.vision.requirements.RequirementReport} to find out which requirements were not met.
 * </p>
 * <p>
 *     The checked requirements are listed in the {@link net.gini.android.vision.requirements.RequirementId} enum.
 * </p>
 */
package net.gini.android.vision.requirements;