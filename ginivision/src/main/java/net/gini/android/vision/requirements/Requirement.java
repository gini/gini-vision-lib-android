package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

interface Requirement {

    @NonNull
    RequirementId getId();

    @NonNull
    RequirementReport check();
}
