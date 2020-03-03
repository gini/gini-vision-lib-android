Event Tracking
====

In version `3.12.0 <https://github.com/gini/gini-vision-lib-android/releases/tag/3.12.0>`_ we introduced the possibility to track various events which occur during the usage 
of the Gini Vision Library.

To subscribe to the events you need to implement the ``EventTracker`` interface and pass it to the
builder when creating a new ``GiniVision`` instance.

.. code-block:: java

    GiniVision.newInstance()
        .setEventTracker(new MyEventTracker());
        .build();

In ``MyEventTracker`` you can handle the events you are interested in.

.. code-block:: java

    class MyEventTracker implements EventTracker {

        @Override
        public void onCameraScreenEvent(final Event<CameraScreenEvent> event) {
            switch (event.getType()) {
                case TAKE_PICTURE:
                    // handle the picture taken event
                    break;
                case HELP:
                    // handle the show help event
                    break;
                case EXIT:
                    // handle the exit event
                    break;
            }
        }

        @Override
        public void onOnboardingScreenEvent(final Event<OnboardingScreenEvent> event) {
            (...)
        }

        @Override
        public void onAnalysisScreenEvent(final Event<AnalysisScreenEvent> event) {
            (...)
        }

        @Override
        public void onReviewScreenEvent(final Event<ReviewScreenEvent> event) {
            (...)
        }

    }

.. note::

     If you use the Screen API all events will be triggered automatically.

     If you use the Component API some events will not be triggered (for ex. events which rely on ``Activity#onBackPressed()``). You need to check whether all the events you are interested in are triggered.

The supported events are listed for each screen in a dedicated enum. You can view these enums in our `Javadoc  <http://developer.gini.net/gini-vision-lib-android/javadoc/net/gini/android/vision/tracking/package-summary.html>`_.