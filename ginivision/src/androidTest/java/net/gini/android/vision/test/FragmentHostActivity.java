package net.gini.android.vision.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public abstract class FragmentHostActivity<F extends Fragment> extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    private F mFragment;

    public F getFragment() {
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (savedInstanceState == null) {
            mFragment = createFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment, FRAGMENT_TAG)
                    .commit();
        } else {
            //noinspection unchecked
            mFragment = (F) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_TAG);
        }

        setListener();
    }

    protected abstract void setListener();

    protected abstract F createFragment();

}
