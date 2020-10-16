package net.gini.android.vision;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class GiniVisionActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        if (GiniVision.hasInstance()) {
            Locale customLocale = GiniVision.getInstance().getCustomLocale();                    
            if (customLocale != null) {
                newBase.getResources().getConfiguration().setLocale(customLocale);
                newBase = newBase.createConfigurationContext(newBase.getResources().getConfiguration());
                newBase.getResources().updateConfiguration(newBase.getResources().getConfiguration(), newBase.getResources().getDisplayMetrics());
            }
        }
        super.attachBaseContext(newBase);        
    }

    public void translateTitle() {
        try {
            int labelRes = getPackageManager().getActivityInfo(getComponentName(), 0).labelRes;
            setTitle(getResources().getString(labelRes));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("CameraActivity", "Error getting label resource for Activity.", e);
        }
    }

}