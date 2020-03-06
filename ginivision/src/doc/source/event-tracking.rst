Event Tracking
====

In version `3.12.0 <https://github.com/gini/gini-vision-lib-android/releases/tag/3.12.0>`_ we introduced the possibility to track various
events which occur during the usage of the Gini Vision Library.

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

     If you use the Component API some events will not be triggered (for ex. events which rely on ``Activity#onBackPressed()``). You can
     check in the table below whether all the events you are interested in are triggered.

     To manually trigger events just call the relevant method of your ``EventTracker`` implementation with the required event.

Events
----

Event types are partitioned into different domains according to the screens that they appear at. Each domain has a number of event types.
Some events may supply additional details in a map.

========================  ========================  =========================================================  =====================================================  =============
API                       Domain                    Event enum value and details map keys                      Comment                                                Introduced in
========================  ========================  =========================================================  =====================================================  =============
Screen + Component        Onboarding                ``OnboardingScreenEvent.START``                            Onboarding started                                     3.12.0
Screen + Component        Onboarding                ``OnboardingScreenEvent.FINISH``                           User completes onboarding                              3.12.0
Screen                    Camera Screen             ``CameraScreenEvent.EXIT``                                 User closes the camera screen                          3.12.0
Screen                    Camera Screen             ``CameraScreenEvent.HELP``                                 User taps "Help" on the camera screen                  3.12.0
Screen + Component        Camera Screen             ``CameraScreenEvent.TAKE_PICTURE``                         User takes a picture                                   3.12.0
Screen                    Review Screen             ``ReviewScreenEvent.BACK``                                 User goes back from the review screen                  3.12.0
Screen + Component        Review Screen             ``ReviewScreenEvent.NEXT``                                 User advances from the review screen                   3.12.0
Screen                    Analysis Screen           ``AnalysisScreenEvent.CANCEL``                             User cancels the process during analysis               3.12.0
Screen + Component        Analysis Screen           ``AnalysisScreenEvent.ERROR``                              The analysis ended with an error.                      3.12.0
                                                    ``AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.MESSAGE``
Screen + Component        Analysis Screen           ``AnalysisScreenEvent.RETRY``                              The user decides to retry after an analysis error.     3.12.0
========================  ========================  =========================================================  =====================================================  =============

The supported events are listed for each screen in a dedicated enum. You can view these enums in our `Javadoc <http://developer.gini.net/gini-vision-lib-android/javadoc/net/gini/android/vision/tracking/package-summary.html>`_.
