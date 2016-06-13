package net.gini.android.vision.scanner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;

public class ScannerFragment {

    public static <T> T createInstance(Context context) {
        if (context instanceof Activity) {
            return (T) createStandardInstance();
        } else if (context instanceof AppCompatActivity) {
            return (T) createSupportInstance();
        }
        return null;
    }

    protected static android.app.Fragment createStandardInstance() {
        return new ScannerFragmentStandard();
    }

    protected static android.support.v4.app.Fragment createSupportInstance() {
        return new ScannerFragmentCompat();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_scanner, container, false);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
    }
}
