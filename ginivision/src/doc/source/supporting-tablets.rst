Supporting Tablets
==================

The Gini Vision Library can be used on tablets, too. Some UI elements have been adapted to offer the best user experience for tablet users. Requirements and resources were also adapted.

You may skip to the `Quick Checklist`_ to get an overview of the steps required for supporting tablets.

Extraction Quality Considerations
---------------------------------

Tablets have generally lower camera resolutions and some (even popular ones) lack an LED flash. Please note that you may experience worse extraction quality on tablets compared to phones.

Hardware Requirements
---------------------

We disabled the camera flash requirement for tablets. Camera flash is not a standard feature for tablets and even some popular models like the Samsung Galaxy Tab S2 don't have an LED flash.

You can view the Gini Vision Library's hardware requirements `here <http://developer.gini.net/gini-vision-lib-android/javadoc/net/gini/android/vision/requirements/RequirementId.html>`_.

Supporting All Orientations
---------------------------

On tablets landscape orientations are also supported (smartphones are portrait only). 

Previously we recommended limiting the orientation to portrait for Activities extending the Screen API's abstract Activities and Activities hosting the Component API's Fragment. If you are updating from a previous version you should remove the portrait limitation. The Gini Vision Library limited the orientation to portrait by adding ``android:screenOrientation="portrait"`` to the Activities in earlier versions. This has been removed and you should also remove it from your Activities, too.

Please note that on orientation change Activites will be restarted and the listener methods will be invoked again on restart. You should make sure your Activity implementations handle additional listener method invocations gracefully on orientation change.

The Gini Vision Library Screen API Activities and Component API Fragments keep their internal state between orientation changes. We recommend you to check that your Activity implementations also maintain their state.

UI Considerations
^^^^^^^^^^^^^^^^^

On tablets in landscape the Camera Screen's UI displays the camera trigger button on the right side of the screen. Users can reach the camera trigger easier this way. The camera preview along with the document corner guides are shown in landscape to match the device's orientation.

Other UI elements on all the screens maintain their relative position and the screen layouts are scaled automatically to fit the current orientation.

Customizing Tablet Screens
--------------------------

Tablet specific images are required only for the Camera Screen for tablets. The following images should be customized and added to your drawable resource folder with the ``sw600dp`` qualifier for mdpi, hdpi, xhdpi, xxhdpi and xxxhdpi (for ex. ``drawable-sw600dp-mdpi``):

* ``gv_camera_preview_corners.png`` - Document corner guides
* ``gv_camera_preview_corners_land.png`` - Document corner guides for landscape (it's sufficient to use the portrait one rotated by 90 degrees)
* ``gv_onboarding_flat.png`` - First onboarding page image
* ``gv_onboarding_parallel.png`` - Second onboarding page image
* ``gv_onboarding_align.png`` - Third onboarding page image

These images are higher resolution versions of the same images that are used for phones.

Quick Checklist
---------------

#. Remove portrait orientation limitation from your Activities like ``android:screenOrientation="portrait"``.
#. Handle multiple listener method invocations on Activity restarts due to orientation change.
#. Preserve state between orientation change related Activity restarts.
#. Customize the tablet specific images and add them to ``drawable-sw600dp-*`` resource folders for mdpi, hdpi, xhdpi, xxhdpi and xxxhdpi:

    * ``gv_camera_preview_corners.png`` - Document corner guides
    * ``gv_camera_preview_corners_land.png`` - Document corner guides for landscape (it's sufficient to use the portrait one rotated by 90 degrees)
    * ``gv_onboarding_flat.png`` - First onboarding page image
    * ``gv_onboarding_parallel.png`` - Second onboarding page image
    * ``gv_onboarding_align.png`` - Third onboarding page image