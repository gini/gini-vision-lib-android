package net.gini.android.vision.internal.util;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 27.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public enum MimeType {
    IMAGE_PREFIX("image/"),
    IMAGE_WILDCARD("image/*"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_JSON("application/json"),
    UNKNOWN("");

    private static final Map<String, MimeType> sLookup = new HashMap<>();

    static {
        for (final MimeType mimeType : MimeType.values()) {
            sLookup.put(mimeType.asString(), mimeType);
        }
    }

    private final String mMimeType;

    public static MimeType fromString(@NonNull final String mimeType) {
        if (sLookup.containsKey(mimeType)) {
            return sLookup.get(mimeType);
        }
        return UNKNOWN;
    }

    MimeType(@NonNull final String mimeType) {
        mMimeType = mimeType;
    }

    public String asString() {
        return mMimeType;
    }

    public boolean equals(final String mimeType) { // NOPMD
        return mMimeType.equals(mimeType);
    }
}
