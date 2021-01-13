package net.gini.android.vision.test;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
            setListener();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment, FRAGMENT_TAG)
                    .commit();
        } else {
            //noinspection unchecked
            mFragment = (F) getSupportFragmentManager()
                    .findFragmentByTag(FRAGMENT_TAG);
            setListener();
        }
    }

    protected abstract void setListener();

    protected abstract F createFragment();

}
