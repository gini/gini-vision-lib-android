package net.gini.android.vision.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiniVisionSpecificExtraction extends GiniVisionExtraction {

    public static final Parcelable.Creator<GiniVisionSpecificExtraction> CREATOR =
            new Parcelable.Creator<GiniVisionSpecificExtraction>() {

                @Override
                public GiniVisionSpecificExtraction createFromParcel(final Parcel in) {
                    return new GiniVisionSpecificExtraction(in);
                }

                @Override
                public GiniVisionSpecificExtraction[] newArray(final int size) {
                    return new GiniVisionSpecificExtraction[size];
                }

            };
    private final String mName;
    private final List<GiniVisionExtraction> mCandidates;

    public GiniVisionSpecificExtraction(@NonNull final String name, @NonNull final String value,
            final @NonNull String entity,
            @Nullable final GiniVisionBox box, @NonNull final List<GiniVisionExtraction> candidates) {
        super(value, entity, box);
        mName = name;
        mCandidates = candidates;
    }

    private GiniVisionSpecificExtraction(final Parcel in) {
        super(in);
        mName = in.readString();
        final List<GiniVisionExtraction> candidates = new ArrayList<>();
        in.readTypedList(candidates, GiniVisionExtraction.CREATOR);
        mCandidates = candidates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mName);
        dest.writeTypedList(mCandidates);
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public List<GiniVisionExtraction> getCandidate() {
        return mCandidates;
    }


    @Override
    public String toString() {
        return "GiniVisionSpecificExtraction{" +
                "mName='" + mName + '\'' +
                ", mCandidates=" + mCandidates +
                "} " + super.toString();
    }
}
