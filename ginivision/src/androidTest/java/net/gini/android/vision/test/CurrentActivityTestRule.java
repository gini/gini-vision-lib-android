package net.gini.android.vision.test;


import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;

import java.util.concurrent.atomic.AtomicReference;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

public class CurrentActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    public CurrentActivityTestRule(final Class<T> activityClass) {
        super(activityClass, false);
    }

    public CurrentActivityTestRule(final Class<T> activityClass, final boolean initialTouchMode) {
        super(activityClass, initialTouchMode, true);
    }

    public CurrentActivityTestRule(final Class<T> activityClass, final boolean initialTouchMode,
            final boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    public T getCurrentActivity() {
        getInstrumentation().waitForIdleSync();
        final AtomicReference<Activity> activity = new AtomicReference<>();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                final java.util.Collection<Activity> activities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                                Stage.RESUMED);
                activity.set(Iterables.getOnlyElement(activities));
            }
        });
        //noinspection unchecked
        return (T) activity.get();
    }
}
