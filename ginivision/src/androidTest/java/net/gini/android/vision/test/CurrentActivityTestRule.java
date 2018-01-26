package net.gini.android.vision.test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;
import android.support.test.espresso.core.internal.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class CurrentActivityTestRule<T extends Activity> extends ActivityTestRule<T> {
    public CurrentActivityTestRule(Class<T> activityClass) {
        super(activityClass, false);
    }

    public CurrentActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode, true);
    }

    public CurrentActivityTestRule(Class<T> activityClass, boolean initialTouchMode,
            boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    public T getCurrentActivity() {
        getInstrumentation().waitForIdleSync();
        final AtomicReference<Activity> activity = new AtomicReference<>();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                java.util.Collection<Activity> activities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                                Stage.RESUMED);
                activity.set(Iterables.getOnlyElement(activities));
            }
        });
        //noinspection unchecked
        return (T) activity.get();
    }
}
