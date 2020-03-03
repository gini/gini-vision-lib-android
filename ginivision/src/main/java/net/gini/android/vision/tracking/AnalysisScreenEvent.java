package net.gini.android.vision.tracking;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 *
 */
public enum AnalysisScreenEvent {
    CANCEL,
    ERROR,
    RETRY;

    public static class ERROR_DETAILS_MAP_KEY {

        public static String MESSAGE = "MESSAGE";
    }
}
