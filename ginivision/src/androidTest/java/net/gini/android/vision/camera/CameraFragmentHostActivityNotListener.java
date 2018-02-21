package net.gini.android.vision.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.test.R;


/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class CameraFragmentHostActivityNotListener extends AppCompatActivity {

    private static final String CAMERA_FRAGMENT = "CAMERA_FRAGMENT";

    static CameraFragmentListener sListener;

    private CameraFragmentCompatFake mCameraFragmentCompatFake;

    public CameraFragmentCompatFake getCameraFragmentCompatFake() {
        return mCameraFragmentCompatFake;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_fragment_host);

        if (savedInstanceState == null) {
            mCameraFragmentCompatFake = CameraFragmentCompatFake.createInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mCameraFragmentCompatFake, CAMERA_FRAGMENT)
                    .commit();
        } else {
            mCameraFragmentCompatFake =
                    (CameraFragmentCompatFake) getSupportFragmentManager()
                            .findFragmentByTag(CAMERA_FRAGMENT);
        }

        if (sListener != null) {
            mCameraFragmentCompatFake.setListener(sListener);
        }
    }

}
