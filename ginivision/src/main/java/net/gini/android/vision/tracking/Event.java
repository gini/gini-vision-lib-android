package net.gini.android.vision.tracking;

import java.util.Collections;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 27.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 * A tracking event.
 *
 * @param <T> an event enum value (for ex. an {@link AnalysisScreenEvent} value)
 */
public class Event<T extends Enum<T>> {

    private final T type;
    private final Map<String, Object> details;

    /**
     * Internal use only.
     *
     * @suppress
     */
    public Event(@NonNull final T type, @NonNull final Map<String, Object> details) {
        this.type = type;
        this.details = details;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public Event(@NonNull final T type) {
        this(type, Collections.<String, Object>emptyMap());
    }

    /**
     * @return the type of the event which is an event enum value (for ex. an {@link AnalysisScreenEvent} value)
     */
    @NonNull
    public T getType() {
        return type;
    }

    /**
     * Details about the event. You can find the possible keys in the event enum.
     *
     * <p> The map is empty if the event has no details.
     *
     * @return a map containing details about the event
     */
    @NonNull
    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
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

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + details.hashCode();
        return result;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @NonNull
    @Override
    public String toString() {
        return "Event{"
                + "type=" + type
                + ", details=" + details
                + '}';
    }
}