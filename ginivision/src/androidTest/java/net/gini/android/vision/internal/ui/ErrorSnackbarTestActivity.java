package net.gini.android.vision.internal.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import net.gini.android.vision.test.R;

public class ErrorSnackbarTestActivity extends AppCompatActivity {

    private RelativeLayout mRootLayout;
    private RelativeLayout mSubviewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_snackbar);
        bindViews();
    }

    private void bindViews() {
        mRootLayout = (RelativeLayout)findViewById(R.id.layout_root);
        mSubviewLayout = (RelativeLayout)findViewById(R.id.layout_subview);
    }

    public RelativeLayout getRootLayout() {
        return mRootLayout;
    }

    public RelativeLayout getSubviewLayout() {
        return mSubviewLayout;
    }
}
