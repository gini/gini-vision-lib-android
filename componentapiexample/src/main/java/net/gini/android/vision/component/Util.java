package net.gini.android.vision.component;

import android.content.Intent;
import android.support.annotation.NonNull;

public class Util {

    public static boolean isIntentActionViewOrSend(@NonNull final Intent intent) {
        String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action);
    }

}
