package net.gini.android.vision.requirements;

import androidx.annotation.NonNull;

interface Requirement {

    @NonNull
    RequirementId getId();

    @NonNull
    RequirementReport check();
}
