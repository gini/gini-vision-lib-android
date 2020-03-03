package net.gini.android.vision.tracking;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 27.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

public class Event<T extends Enum<T>> {

    private final T type;
    private final Map<String, String> details;

    public Event(@NonNull final T type, @NonNull final Map<String, String> details) {
        this.type = type;
        this.details = details;
    }

    public Event(@NonNull final T type) {
        this(type, Collections.<String, String>emptyMap());
    }

    @NonNull
    public T getType() {
        return type;
    }

    @NonNull
    public Map<String, String> getDetails() {
        return details;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Event<?> event = (Event<?>) o;

        if (!type.equals(event.type)) {
            return false;
        }
        return details.equals(event.details);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + details.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", details=" + details +
                '}';
    }
}