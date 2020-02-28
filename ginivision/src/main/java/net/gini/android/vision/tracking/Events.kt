package net.gini.android.vision.tracking

/**
 * Created by Alpar Szotyori on 27.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */


enum class OnboardingScreenEvent {
    START,
    FINISH
}

enum class CameraScreenEvent {
    EXIT,
    HELP,
    TAKE_PICTURE
}

enum class ReviewScreenEvent {
    BACK,
    NEXT
}

enum class AnalysisScreenEvent {
    CANCEL,
    ERROR,
    RETRY;

    @Suppress("ClassName")
    class ERROR_DETAILS_MAP_KEY {
        companion object {
            const val MESSAGE = "MESSAGE"
        }
    }
}
