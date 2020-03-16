package net.gini.android.vision.network.model;

import static net.gini.android.vision.internal.util.BundleHelperKt.fromMapList;
import static net.gini.android.vision.internal.util.BundleHelperKt.toMapList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 13.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 * Contains a Gini API compound extraction.
 *
 * <p> A compound extraction contains one or more specific extraction maps. For example line items are compound extractions where each line
 * is represented by a specific extraction map. Each specific extraction represents a column on that line.
 */
public class GiniVisionCompoundExtraction implements Parcelable {

    public static final Parcelable.Creator<GiniVisionCompoundExtraction> CREATOR = new Parcelable.Creator<GiniVisionCompoundExtraction>() {
        @Override
        public GiniVisionCompoundExtraction createFromParcel(final Parcel in) {
            return new GiniVisionCompoundExtraction(in);
        }

        @Override
        public GiniVisionCompoundExtraction[] newArray(final int size) {
            return new GiniVisionCompoundExtraction[size];
        }
    };
    private final String mName;
    private final List<Map<String, GiniVisionSpecificExtraction>> mSpecificExtractionMaps;

    /**
     * Value object for a compound extraction from the Gini API.
     *
     * @param name                   The compound extraction's name, e.g. "amountToPay".
     * @param specificExtractionMaps A list of specific extractions bundled into separate maps.
     */
    public GiniVisionCompoundExtraction(@NonNull final String name,
            @NonNull final List<Map<String, GiniVisionSpecificExtraction>> specificExtractionMaps) {
        mName = name;
        mSpecificExtractionMaps = specificExtractionMaps;
    }

    protected GiniVisionCompoundExtraction(final Parcel in) {
        mName = in.readString();
        final List<Bundle> bundleList = new ArrayList<>();
        in.readTypedList(bundleList, Bundle.CREATOR);
        mSpecificExtractionMaps = toMapList(bundleList, getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(mName);
        dest.writeTypedList(fromMapList(mSpecificExtractionMaps));
    }

    @NonNull
    @Override
    public String toString() {
        return "GiniVisionCompoundExtraction{" +
                "mName='" + mName + '\'' +
                ", mSpecificExtractionMaps=" + mSpecificExtractionMaps +
                '}';
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public List<Map<String, GiniVisionSpecificExtraction>> getSpecificExtractionMaps() {
        return mSpecificExtractionMaps;
    }
}
