package net.gini.android.vision.network.model;

/**
 * Created by Alpar Szotyori on 14.09.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Contains a return reason.
 *
 * <p> Return reasons are shown to the user when a line item is deselected in the return assistant.
 */
public class GiniVisionReturnReason implements Parcelable {

    private final String mId;
    private final Map<String, String> mLocalizedLabels;

    /**
     * @param id the id of the return reason
     * @param localizedLabels a map of labels where the keys are two letter language codes (ISO 639-1)
     */
    public GiniVisionReturnReason(@NonNull final String id, @NonNull final Map<String, String> localizedLabels) {
        mId = id;
        mLocalizedLabels = localizedLabels;
    }

    protected GiniVisionReturnReason(Parcel in) {
        mId = in.readString();
        final int mapSize = in.readInt();
        mLocalizedLabels = new HashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            mLocalizedLabels.put(in.readString(), in.readString());
        }
    }

    @NonNull
    public String getId() {
        return mId;
    }

    /**
     * @return a map of labels where the keys are two letter language codes (ISO 639-1)
     */
    @NonNull
    public Map<String, String> getLocalizedLabels() {
        return mLocalizedLabels;
    }

    @Nullable
    public String getLabelInLocalLanguageOrGerman() {
        final String label = mLocalizedLabels.get(Locale.getDefault().getLanguage());
        return label != null ? label : mLocalizedLabels.get("de");
    }

    @Override
    public String toString() {
        return "GiniVisionReturnReason{" +
                "mId='" + mId + '\'' +
                ", mLocalizedLabels=" + mLocalizedLabels +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(mLocalizedLabels.size());
        for (final Map.Entry<String, String> entry : mLocalizedLabels.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public static final Parcelable.Creator<GiniVisionReturnReason> CREATOR = new Parcelable.Creator<GiniVisionReturnReason>() {
        @Override
        public GiniVisionReturnReason createFromParcel(Parcel in) {
            return new GiniVisionReturnReason(in);
        }

        @Override
        public GiniVisionReturnReason[] newArray(int size) {
            return new GiniVisionReturnReason[size];
        }
    };
}
