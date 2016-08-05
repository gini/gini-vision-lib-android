package net.gini.android.vision.test;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

public class Helpers {

    public static <T extends Parcelable, C extends Parcelable.Creator<T>> T doParcelingRoundTrip(T payload, C creator) {
        Parcel parcel = Parcel.obtain();
        payload.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return creator.createFromParcel(parcel);
    }

    public static void prepareLooper() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }
}
