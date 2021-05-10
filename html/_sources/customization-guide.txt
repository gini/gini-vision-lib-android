Customization Guide
====

Customization of the Views is provided mostly via overriding of app resources: dimensions, strings,
colors, texts, etc. Onboarding can also be customized to show your own pages, each consisting of an
image and a short text.

.. contents::
   :depth: 1
   :local:

.. _onboarding:

Onboarding Screen
----

.. raw:: html

    <img src="_static/customization/Onboarding.png" usemap="#onboarding-map" width="324" height="576">

    <map id="onboarding-map" name="onboarding-map">
        <area shape="rect" alt="" title="Action Bar" coords="132,24,164,57" href="customization-guide.html#onboarding-1" target="" />
        <area shape="rect" alt="" title="Next Button" coords="282,462,311,494" href="customization-guide.html#onboarding-2" target="" />
        <area shape="rect" alt="" title="Page Indicators" coords="105,485,134,515" href="customization-guide.html#onboarding-3" target="" />
        <area shape="rect" alt="" title="Onboarding Message" coords="15,326,44,356" href="customization-guide.html#onboarding-4" target="" />
        <area shape="rect" alt="" title="Onboarding Pages" coords="141,130,173,162" href="customization-guide.html#onboarding-5" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _onboarding-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_onboarding``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshot. <onboarding>`

.. _onboarding-2:

2. Next Button
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_fab_next.png``.

- **Color**

  Via the color resources named ``gv_onboarding_fab`` and ``gv_onboarding_fab_pressed``.

:ref:`Back to screenshot. <onboarding>`

.. _onboarding-3:

3. Page Indicators
^^^^

- **Active**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_indicator_active.png``.

- **Inactive**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_indicator_inactive.png``.

:ref:`Back to screenshot. <onboarding>`

.. _onboarding-4:

4. Onboarding Message
^^^^

- **Color**

  Via the color resource named ``gv_onboarding_message``.

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Onboarding.Message.TextStyle`` (with parent style
  ``Root.GiniVisionTheme.Onboarding.Message.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Onboarding.Message.TextStyle`` (with parent style
  ``Root.GiniVisionTheme.Onboarding.Message.TextStyle``) and setting an item named ``gvCustomFont``
  with the path to the font file in your assets folder.

:ref:`Back to screenshot. <onboarding>`

.. _onboarding-5:

5. Onboarding Pages
^^^^

- **Default Pages**

  - **Phone**

    - **First Page**

      - **Image**

        Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_flat.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_flat``.

    - **Second Page**

      - **Image**

        Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_parallel.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_parallel``.

    - **Third Page**

      - **Image**

        Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_align.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_align``.

    - **Fourth Page**

      Visible only if the multi-page feature has been enabled.

      - **Image**

        Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_multipage.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_multipage``.

    :ref:`Back to screenshot. <onboarding>`

  - **Tablet**
  
    - **First Page**

      - **Image**

        Via images for sw600dp-mdpi, sw600dp-hdpi, sw600dp-xhdpi, sw600dp-xxhdpi, sw600dp-xxxhdpi
        named ``gv_onboarding_lighting.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_ligthing``.

    - **Second Page**

      - **Image**

        Via images for sw600dp-mdpi, sw600dp-hdpi, sw600dp-xhdpi, sw600dp-xxhdpi, sw600dp-xxxhdpi
        named ``gv_onboarding_flat.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_flat``.

    - **Third Page**

      - **Image**

        Via images for sw600dp-mdpi, sw600dp-hdpi, sw600dp-xhdpi, sw600dp-xxhdpi, sw600dp-xxxhdpi
        named ``gv_onboarding_parallel.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_parallel``.

    - **Fourth Page**

      - **Image**

        Via images for sw600dp-mdpi, sw600dp-hdpi, sw600dp-xhdpi, sw600dp-xxhdpi, sw600dp-xxxhdpi
        named ``gv_onboarding_align.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_align``.

    - **Fifth Page**

      Visible only if the multi-page feature has been enabled.

      - **Image**

        Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_onboarding_multipage.png``.

      - **Text**

        Via the string resource named ``gv_onboarding_multipage``.

    :ref:`Back to screenshot. <onboarding>`

- **Custom Pages**

  You can change the number of displayed pages and their content (image and short text) by setting
  an ``ArrayList`` containing ``OnboardingPage`` objects when building a ``GiniVision`` instance
  with ``setCustomOnboardingPages()``. 
  
  If you don't use ``GiniVision`` yet you can also provide the list using the extra
  ``CameraActivity.EXTRA_IN_ONBOARDING_PAGES`` for the Screen API and
  ``OnboardingFragmentCompat.createInstance(ArrayList<OnboardingPage>)`` or
  ``OnboardingFragmentStandard.createInstance(ArrayList<OnboardingPage>)`` for the Component API.

  :ref:`Back to screenshot. <onboarding>`

- **Background**

  - **Color**

    Via the color resource named ``gv_background``. **Note**: this color resource is global to all
    Activities.

  - **Transparency**

    Via the string resource named ``gv_onboarding_page_fragment_background_alpha`` which must
    contain a real number between ``[0,1]``.
    
  :ref:`Back to screenshot. <onboarding>`

.. _camera:

Camera Screen
----

.. raw:: html

    <img src="_static/customization/Camera.png" usemap="#camera-map-1" width="324" height="576">

    <map id="camera-map-1" name="camera-map-1">
        <area shape="rect" alt="" title="Action Bar" coords="229,26,257,56" href="customization-guide.html#camera-1" target="" />
        <area shape="rect" alt="" title="Document Corner Guides" coords="32,103,60,132" href="customization-guide.html#camera-2" target="" />
        <area shape="rect" alt="" title="Camera Trigger Button" coords="175,431,201,460" href="customization-guide.html#camera-3" target="" />
        <area shape="rect" alt="" title="Tap to Focus Indicator" coords="96,215,127,244" href="customization-guide.html#camera-4" target="" />
        <area shape="rect" alt="" title="Help Menu Item" coords="262,26,291,55" href="customization-guide.html#camera-5" target="" />
        <area shape="rect" alt="" title="Background" coords="199,507,227,536" href="customization-guide.html#camera-6" target="" />
        <area shape="rect" alt="" title="Document Import Button" coords="65,434,93,463" href="customization-guide.html#camera-7" target="" />
        <area shape="rect" alt="" title="Document Import Hint" coords="148,349,177,379" href="customization-guide.html#camera-8" target="" />
        <area shape="rect" alt="" title="Image Stack" coords="237,433,265,460" href="customization-guide.html#camera-9" target="" />
        <area shape="rect" alt="" title="Flash Toggle Button" coords="94,481,125,515" href="customization-guide.html#camera-14" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

    <img src="_static/customization/Camera QRCode.png" usemap="#camera-map-2" width="324" height="576">

    <map id="camera-map-2" name="camera-map-2">
        <area shape="rect" alt="" title="QRCode Detected Popup" coords="148,385,178,416" href="customization-guide.html#camera-10" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

    <img src="_static/customization/Camera Permission Dialog.png" usemap="#camera-map-3" width="324" height="576">

    <map id="camera-map-3" name="camera-map-3">
        <area shape="rect" alt="" title="Read Storage Permission Dialogs" coords="146,212,176,242" href="customization-guide.html#camera-11" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

    <img src="_static/customization/Camera Permission.png" usemap="#camera-map-4" width="324" height="576">

    <map id="camera-map-4" name="camera-map-4">
       <area shape="rect" alt="" title="No Camera Permission" coords="48,293,77,323" href="customization-guide.html#camera-12" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

  <img src="_static/customization/Camera Multi-Page Limit Alert.png" usemap="#camera-map-5" width="324" height="576">

    <map id="camera-map-5" name="camera-map-5">
      <area shape="rect" alt="" title="Multi-Page Limit Alert" coords="10,266,38,295" href="customization-guide.html#camera-13" target="" />
      <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _camera-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_camera``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshots. <camera>`

.. _camera-2:

2. Document Corner Guides
^^^^

- **Color**

  Via the color resource named ``gv_camera_preview_corners``.

:ref:`Back to screenshots. <camera>`

.. _camera-3:

3. Camera Trigger Button
^^^^

- **Normal**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_camera_trigger_default.png``.

- **Pressed**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_camera_trigger_pressed.png``.

:ref:`Back to screenshots. <camera>`

.. _camera-4:

4. Tap to Focus Indicator
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_camera_focus_indicator.png``.

:ref:`Back to screenshots. <camera>`

.. _camera-5:

5. Help Menu Item
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_help_icon.png``.

- **Title**

  Via the string resource named ``gv_show_onboarding``.

:ref:`Back to screenshots. <camera>`

.. _camera-6:

6. Background
^^^^

- **Color**

  Via the color resource named ``gv_background``. **Note**: this color resource is global to all
  Activities.

:ref:`Back to screenshots. <camera>`

.. _camera-7:

7. Document Import Button
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_document_import_icon.png``.

- **Subtitle**

  - **Text**

    Via the string resource named ``gv_camera_document_import_subtitle``.

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Camera.DocumentImportSubtitle.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.DocumentImportSubtitle.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Camera.DocumentImportSubtitle.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.DocumentImportSubtitle.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <camera>`

.. _camera-8:

8. Document Import Hint
^^^^

- **Background Color**

  Via the color resource named ``gv_document_import_hint_background``.

- **Close Icon Color**

  Via the color resource name ``gv_hint_close``.

- **Message**

  - **Text**

    Via the string resource named ``gv_document_import_hint_text``.

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Camera.DocumentImportHint.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.DocumentImportHint.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Camera.DocumentImportHint.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.DocumentImportHint.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <camera>`

.. _camera-9:

9. Images Stack
^^^^

- **Badge**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Camera.ImageStackBadge.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.ImageStackBadge.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Camera.ImageStackBadge.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.ImageStackBadge.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

  - **Background Color**

    Via the color resources named ``gv_camera_image_stack_badge_background`` and
    ``gv_camera_image_stack_badge_background_border``.

  - **Background Size**

    Via the dimension resource named ``gv_camera_image_stack_badge_size``.

- **Subtitle**

  - **Text**

    Via the string resource named ``gv_camera_image_stack_subtitle``.

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Camera.ImageStackSubtitle.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.ImageStackSubtitle.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Camera.ImageStackSubtitle.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.ImageStackSubtitle.TextStyle``) and setting an item
    named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <camera>`

.. _camera-10:

10. QRCode Detected Popup
^^^^

- **Background Color**

  Via the color resource named ``gv_qrcode_detected_popup_background``.

- **Message**

  - **Text**

    Via the string resources named ``gv_qrcode_detected_popup_message_1`` and
    ``gv_qrcode_detected_popup_message_2``.

  - **Text Style**

    Via overriding the styles named
    ``GiniVisionTheme.Camera.QRCodeDetectedPopup.Message1.TextStyle`` (with parent style
    ``Root.GiniVisionTheme.Camera.QRCodeDetectedPopup.Message1.TextStyle``) and
    ``GiniVisionTheme.Camera.QRCodeDetectedPopup.Message2.TextStyle`` (with parent style
    ``Root.GiniVisionTheme.Camera.QRCodeDetectedPopup.Message2.TextStyle``).

  - **Font**

    Via overriding the styles named
    ``GiniVisionTheme.Camera.QRCodeDetectedPopup.Message1.TextStyle`` (with parent style
    ``Root.GiniVisionTheme.Camera.QRCodeDetectedPopup.Message1.TextStyle``) and
    ``GiniVisionTheme.Camera.QRCodeDetectedPopup.Message2.TextStyle`` (with parent style
    ``Root.GiniVisionTheme.Camera.QRCodeDetectedPopup.Message2.TextStyle``). and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <camera>`

.. _camera-11:

11. Read Storage Permission Dialogs
^^^^

- **Permission Rationale Dialog**

  - **Message**

    Via the string resource named ``gv_storage_permission_rationale``.

  - **Positive Button Text**

    Via the string resource named ``gv_storage_permission_rationale_positive_button``.

  - **Negative Button Text**

    Via the string resource named ``gv_storage_permission_rationale_negative_button``.

  - **Button Color**

    Via the color resource named ``gv_accent``. **Note**: this color resource is global.

- **Permission Denied Dialog**

  - **Message**

    Via the string resource named ``gv_storage_permission_denied``.

  - **Positive Button Text**

    Via the string resource named ``gv_storage_permission_denied_positive_button``.

  - **Negative Button Text**

    Via the string resource named ``gv_storage_permission_denied_negative_button``.

  - **Button Color**

    Via the color resource named ``gv_accent``. **Note**: this color resource is global.

:ref:`Back to screenshots. <camera>`

.. _camera-12:

12. No Camera Permission
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_no_camera.png``.

- **Message**

  - **Text**

    Via the string resource named ``gv_camera_error_no_permission``.

   - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Camera.Error.NoPermission.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.Error.NoPermission.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Camera.Error.NoPermission.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.Error.NoPermission.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Button**

  - **Title**

    Via the string resource named ``gv_camera_error_no_permission_button_title``.

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Camera.Error.NoPermission.Button.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <camera>`

.. _camera-13:

13. Multi-Page Limit Alert
^^^^

- **Message**

   Via the string resource named ``gv_document_error_too_many_pages``.

 - **Positive Button Text**

  Via the string resource named ``gv_document_error_multi_page_limit_review_pages_button``.

  - **Negative Button Text**

  Via the string resource named ``gv_document_error_multi_page_limit_cancel_button``.

  - **Button Color**

  Via the color resource named ``gv_accent``. **Note**: this color resource is global.

:ref:`Back to screenshots. <camera>`

.. _camera-14:

14. Flash Toggle Button
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_camera_flash_on.png`` and ``gv_camera_flash_off.png``.

:ref:`Back to screenshots. <camera>`

.. _review:

Review Screen
----

.. raw:: html

    <img src="_static/customization/Review Screen.png" usemap="#review-map" width="324" height="576">

    <map id="review-map" name="review-map">
        <area shape="rect" alt="" title="Action Bar" coords="189,26,220,54" href="customization-guide.html#review-1" target="" />
        <area shape="rect" alt="" title="Next Button" coords="241,408,272,438" href="customization-guide.html#review-2" target="" />
        <area shape="rect" alt="" title="Rotate Button" coords="244,352,275,385" href="customization-guide.html#review-3" target="" />
        <area shape="rect" alt="" title="Advice" coords="231,490,264,520" href="customization-guide.html#review-4" target="" />
        <area shape="rect" alt="" title="Background" coords="2,288,29,319" href="customization-guide.html#review-5" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _review-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_review``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshot. <review>`

.. _review-2:

2. Next Button
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_review_fab_next.png``.

- **Color**

  Via the color resources named ``gv_review_fab`` and ``gv_review_fab_pressed``.

:ref:`Back to screenshot. <review>`

.. _review-3:

3. Rotate Button
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_review_button_rotate.png``.

- **Color**

  Via the color resources named ``gv_review_fab_mini`` and ``gv_review_fab_mini_pressed``.

:ref:`Back to screenshot. <review>`

.. _review-4:

4. Advice
^^^^

- **Text**

  Via the string resource named ``gv_review_bottom_panel_text``.

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Review.BottomPanel.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.BottomPanel.TextStyle``).

  - **Font**

  Via overriding the style named ``GiniVisionTheme.Review.BottomPanel.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.BottomPanel.TextStyle``) and setting an
  item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Background Color**

  Via the color resource named ``gv_review_bottom_panel_background``.

:ref:`Back to screenshot. <review>`

.. _review-5:

5. Background
^^^^

- **Color**

  Via the color resource named ``gv_background``. **Note**: this color resource is global to all Activities.

:ref:`Back to screenshot. <review>`

.. _analysis:

Analysis Screen
----

.. raw:: html

    <img src="_static/customization/Analysis Screen.png" usemap="#analysis-map-1" width="324" height="576">

    <map id="analysis-map-1" name="analysis-map-1">
        <area shape="rect" alt="" title="Action Bar" coords="189,24,222,55" href="customization-guide.html#analysis-1" target="" />
        <area shape="rect" alt="" title="Activity Indicator" coords="105,283,132,310" href="customization-guide.html#analysis-2" target="" />
        <area shape="rect" alt="" title="Error Snackbar" coords="190,500,219,530" href="customization-guide.html#analysis-4" target="" />
        <area shape="rect" alt="" title="Background" title" coords="74,61,105,93" href="customization-guide.html#analysis-5" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

    <img src="_static/customization/Analysis Screen PDF.png" usemap="#analysis-map-2" width="324" height="576">

    <map id="analysis-map-2" name="analysis-map-2">
        <area shape="rect" alt="" title="PDF Info Panel" coords="60,78,90,106" href="customization-guide.html#analysis-3" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>


.. _analysis-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshots. <analysis>`

.. _analysis-2:

2. Activity Indicator
^^^^

- **Color**

  Via the color resource named ``gv_analysis_activity_indicator``.

- **Message**

  - **Text**
  
    Via the string resource named ``gv_analysis_activity_indicator_message``.

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Analysis.AnalysingMessage.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Analysis.AnalysingMessage.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Analysis.AnalysingMessage.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Analysis.AnalysingMessage.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <analysis>`

.. _analysis-3:

3. PDF Info Panel
^^^^

- **Background Color**

  Via the color resource named ``gv_analysis_pdf_info_background``.

- **Filename**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Analysis.PdfFilename.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Analysis.PdfFilename.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Analysis.PdfFilename.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Analysis.PdfFilename.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Page Count**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Analysis.PdfPageCount.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Analysis.PdfPageCount.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Analysis.PdfPageCount.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Analysis.PdfPageCount.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

  :ref:`Back to screenshots. <analysis>`

.. _analysis-4:

4. Error Snackbar
^^^^

- **Message**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Snackbar.Error.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Snackbar.Error.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Button**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.Button.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Snackbar.Error.Button.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.Button.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Snackbar.Error.Button.TextStyle``) and setting an
    item named ``gvCustomFont`` with the path to the font file in your assets folder.

  - **Retry Button Text**

    Via the string resource named ``gv_document_analysis_error_retry``.

- **Background Color**

  Via the color resource named ``gv_snackbar_error_background``.

:ref:`Back to screenshots. <analysis>`

.. _analysis-5:

5. Background
^^^^

- **Color**

  Via the color resource named ``gv_background``. **Note**: this color resource is global to all Activities.

:ref:`Back to screenshots. <analysis>`

.. _multi-page-review:

Multi-Page Review Screen
----

.. raw:: html

    <img src="_static/customization/Multi-Page Review.png" usemap="#multi-page-review-map-1" width="324" height="576">

    <map id="multi-page-review-map-1" name="multi-page-review-map-1">
        <area shape="rect" alt="" title="Action Bar" coords="189,23,220,54" href="customization-guide.html#multi-page-review-1" target="" />
        <area shape="rect" alt="" title="Page Indicators" coords="174,284,207,316" href="customization-guide.html#multi-page-review-2" target="" />
        <area shape="rect" alt="" title="Next Button" coords="273,259,302,288" href="customization-guide.html#multi-page-review-3" target="" />
        <area shape="rect" alt="" title="Thumbnails Panel" coords="296,341,323,371" href="customization-guide.html#multi-page-review-4" target="" />
        <area shape="rect" alt="" title="Add Pages Card" coords="213,345,243,376" href="customization-guide.html#multi-page-review-6" target="" />
        <area shape="rect" alt="" title="Reorder Pages Tip" coords="2,478,28,508" href="customization-guide.html#multi-page-review-7" target="" />
        <area shape="rect" alt="" title="Bottom Toolbar" coords="150,502,177,532" href="customization-guide.html#multi-page-review-8" target="" />
        <area shape="rect" alt="" title="Image Error" coords="178,67,212,97" href="customization-guide.html#multi-page-review-9" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

    <img src="_static/customization/Multi-Page Review Upload Indicators.png" usemap="#multi-page-review-map-2" width="324" height="576">

    <map id="multi-page-review-map-2" name="multi-page-review-map-2">
        <area shape="rect" alt="" title="Thumbnail Card" coords="12,345,41,375" href="customization-guide.html#multi-page-review-5" target="" />
        <area shape="rect" alt="" title="Badge" coords="131,440,152,463" href="customization-guide.html#multi-page-review-5-1" target="" />
        <area shape="rect" alt="" title="Drag Indicator Bumps" coords="276,435,299,457" href="customization-guide.html#multi-page-review-5-2" target="" />
        <area shape="rect" alt="" title="Highlight Strip" coords="10,464,31,488" href="customization-guide.html#multi-page-review-5-3" target="" />
        <area shape="rect" alt="" title="Activity Indicator" coords="263,367,285,390" href="customization-guide.html#multi-page-review-5-4" target="" />
        <area shape="rect" alt="" title="Upload Success Icon" coords="59,369,84,393" href="customization-guide.html#multi-page-review-5-5" target="" />
        <area shape="rect" alt="" title="Upload Failure Icon" coords="161,371,182,394" href="customization-guide.html#multi-page-review-5-6" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. raw:: html

    <img src="_static/customization/Multi-Page Review Delete Last Page.png" usemap="#multi-page-review-map-3" width="324" height="576">

    <map id="multi-page-review-map-3" name="multi-page-review-map-3">
        <area shape="rect" alt="" title="Imported Image Delete Last Page Dialog" coords="146,213,176,249" href="customization-guide.html#multi-page-review-10" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _multi-page-review-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_multi_page_review``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-2:

2. Page Indicators
^^^^

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.PageIndicator.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.MultiPage.PageIndicator.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.PageIndicator.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.MultiPage.PageIndicator.TextStyle``) and setting an
  item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Background Color**

  Via the color resource named ``gv_multi_page_review_page_indicator_background``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-3:

3. Next Button
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_review_fab_checkmark.png``.

- **Color**

  Via the color resources named ``gv_review_fab`` and ``gv_review_fab_pressed``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-4:

4. Thumbnails Panel
^^^^

- **Background Color**

  Via the color resource named ``gv_multi_page_review_thumbnails_panel_background``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5:

5. Thumbnail Card
^^^^

- **Background Color**

  Via the color resource named ``gv_multi_page_review_thumbnail_card_background``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5-1:

5.1 Badge
~~~~

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.ThumbnailBadge.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.MultiPage.ThumbnailBadge.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.ThumbnailBadge.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.MultiPage.ThumbnailBadge.TextStyle``) and setting an
  item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Background Border Color**

  Via the color resource named ``gv_multi_page_thumbnail_badge_background_border``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5-2:

5.2 Drag Indicator Bumps
~~~~~

- **Icon**

 Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_bumps_icon.png``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5-3:

5.3 Highlight Strip
~~~~

- **Color**

  Via the color resource named ``gv_multi_page_thumbnail_highlight_strip``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5-4:

5.4 Activity Indicator
~~~~

- **Color**

 Via the color resource named ``gv_analysis_activity_indicator``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5-5:

5.5 Upload Success Icon
~~~~~

- **Background Color**

  Via the color resource named ``gv_multi_page_thumbnail_upload_success_icon_background``.

- **Tick Color**

  Via the color resource named ``gv_multi_page_thumbnail_upload_success_icon_foreground``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-5-6:

5.6 Upload Failure Icon
~~~~

- **Background Color**

  Via the color resource named ``gv_multi_page_thumbnail_upload_failure_icon_background``.

- **Cross Color**

  Via the color resource named ``gv_multi_page_thumbnail_upload_failure_icon_foreground``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-6:

6. Add Pages Card
^^^^

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_multi_page_add_page_icon.png``.

- **Subtitle**

  - **Text**

    Via the string resource named ``gv_multi_page_review_add_pages_subtitle``.

  - **Text Style**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.AddPagesSubtitle.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.MultiPage.AddPagesSubtitle.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Review.MultiPage.AddPagesSubtitle.TextStyle``
    (with parent style ``Root.GiniVisionTheme.Review.MultiPage.AddPagesSubtitle.TextStyle``) and
    setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

  :ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-7:

7. Reorder Pages Tip
^^^^

- **Text**

  Via the string resource named ``gv_multi_page_review_reorder_pages_tip``.

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.ReorderPagesTip.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Review.MultiPage.ReorderPagesTip.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Review.MultiPage.ReorderPagesTip.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Review.MultiPage.ReorderPagesTip.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-8:

8. Bottom Toolbar
^^^^

- **Rotate Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_rotate_icon.png``.

- **Delete Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_delete_icon.png``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-9:

9. Image Error
^^^^

- **Background Color**

  Via the color resource named ``gv_snackbar_error_background``.

- **Message**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Snackbar.Error.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.TextStyle``
    (with parent style ``Root.GiniVisionTheme.Snackbar.Error.TextStyle``) and
    setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Button**

  - **Text Style**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.Button.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Snackbar.Error.Button.TextStyle``).

  - **Font**

    Via overriding the style named ``GiniVisionTheme.Snackbar.Error.Button.TextStyle``
    (with parent style ``Root.GiniVisionTheme.Snackbar.Error.Button.TextStyle``) and
    setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

  - **Retry Text (Analysis)**
  
    Via the string resource named ``gv_document_analysis_error_retry``.

  - **Delete Text (Imported Image)**

    Via the string resource named ``gv_multi_page_review_delete_invalid_document``.

:ref:`Back to screenshots. <multi-page-review>`

.. _multi-page-review-10:

10. Imported Image Delete Last Page Dialog
^^^^

- **Message**

  Via the string resource named ``gv_multi_page_review_file_import_delete_last_page_dialog_message``.

- **Positive Button Title**

  Via the string resource named ``gv_multi_page_review_file_import_delete_last_page_dialog_positive_button``.

- **Negative Button Title**

  Via the string resource named ``gv_multi_page_review_file_import_delete_last_page_dialog_negative_button``.

- **Button Color**

  Via the color resource named ``gv_accent``.

:ref:`Back to screenshots. <multi-page-review>`

.. _help-screen:

Help Screen
----

.. raw:: html

    <img src="_static/customization/Help Screen.png" usemap="#help-screen-map" width="324" height="576">

    <map id="help-screen-map" name="help-screen-map">
        <area shape="rect" alt="" title="Action Bar" coords="97,23,135,56" href="customization-guide.html#help-screen-1" target="" />
        <area shape="rect" alt="" title="Background" coords="136,346,168,379" href="customization-guide.html#help-screen-2" target="" />
        <area shape="rect" alt="" title="Help List Item" coords="217,74,246,104" href="customization-guide.html#help-screen-3" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _help-screen-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_help``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshot. <help-screen>`

.. _help-screen-2:

2. Background 
^^^^

- **Color**

  Via the color resource named ``gv_help_activity_background``.

:ref:`Back to screenshot. <help-screen>`

.. _help-screen-3:

3. Help List Item
^^^^

- **Background Color**

  Via the color resource name ``gv_help_item_background``.
  
- **Text Style**

    Via overriding the style named ``GiniVisionTheme.Help.Item.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Help.Item.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.Item.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.Item.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <help-screen>`

.. _photo-tips:

Photo Tips Screen
----

.. raw:: html

    <img src="_static/customization/Photo Tips Screen.png" usemap="#photo-tips-map" width="324" height="576">

    <map id="photo-tips-map" name="photo-tips-map">
        <area shape="rect" alt="" title="Action Bar" coords="173,25,203,56" href="customization-guide.html#photo-tips-1" target="" />
        <area shape="rect" alt="" title="Background" coords="275,251,306,281" href="customization-guide.html#photo-tips-2" target="" />
        <area shape="rect" alt="" title="Header" coords="277,71,308,103" href="customization-guide.html#photo-tips-3" target="" />
        <area shape="rect" alt="" title="Tip" coords="227,138,257,171" href="customization-guide.html#photo-tips-4" target="" />
        <area shape="rect" alt="" title="Good Lighting" coords="5,124,29,145" href="customization-guide.html#photo-tips-4-1" target="" />
        <area shape="rect" alt="" title="Document Should be Flat" coords="4,198,27,220" href="customization-guide.html#photo-tips-4-2" target="" />
        <area shape="rect" alt="" title="Device Parallel to Document" coords="2,269,26,292" href="customization-guide.html#photo-tips-4-3" target="" />
        <area shape="rect" alt="" title="Document Aligned with Corner Guides" coords="5,344,28,367" href="customization-guide.html#photo-tips-4-4" target="" />
        <area shape="rect" alt="" title="Document with Multiple Pages" coords="5,420,29,441" href="customization-guide.html#photo-tips-4-5" target="" />
        <area shape="rect" alt="" title="Back To Camera Button" coords="81,489,116,520" href="customization-guide.html#photo-tips-5" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

    <map id="imgmap201874183930" name="imgmap201874183930">
    <area shape="rect" alt="" title="" coords="275,251,306,281" href="" target="" />
    <area shape="rect" alt="" title="" coords="5,420,29,441" href="" target="" />
    <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) --></map>

.. _photo-tips-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_photo_tips``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-2:

2. Background
^^^^

- **Color**

  Via the color resource named ``gv_photo_tips_activity_background``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-3:

3. Header
^^^^

- **Text Style**

    Via overriding the style named ``GiniVisionTheme.Help.PhotoTips.Header.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Help.PhotoTips.Header.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.PhotoTips.Header.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.PhotoTips.Header.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-4:

4. Tip
^^^^

- **Text Style**

    Via overriding the style named ``GiniVisionTheme.Help.PhotoTips.Tip.TextStyle`` (with
    parent style ``Root.GiniVisionTheme.Help.PhotoTips.Tip.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.PhotoTips.Tip.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.PhotoTips.Tip.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-4-1:

4.1 Good Lighting
~~~~~

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_photo_tip_lighting.png``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-4-2:

4.2 Document Should be Flat
~~~~~

- **Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_photo_tip_flat.png``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-4-3:

4.3 Device Parallel to Document
~~~~

- **Icon**

  Via images for mdpi, hdpi, xhdpi,xxhdpi, xxxhdpi named ``gv_photo_tip_parallel.png``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-4-4:

4.4 Document Aligned with Corner Guides
~~~~~

- **Icon**

  Via images for mdpi, hdpi, xhdpi,xxhdpi, xxxhdpi named ``gv_photo_tip_align.png``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-4-5:

4.5 Document with Multiple Pages
~~~~~

- **Icon**

  Via images for mdpi, hdpi, xhdpi,xxhdpi, xxxhdpi named ``gv_photo_tip_multipage.png``.

:ref:`Back to screenshot. <photo-tips>`

.. _photo-tips-5:

5. Back To Camera Button
^^^^

- **Background Color**

  Via the color resource named ``gv_photo_tips_button``.

- **Text Color**

  Via the color resource named ``gv_photo_tips_button_text``.

:ref:`Back to screenshot. <photo-tips>`

.. _supported-formats:

Supported Formats Screen
----

.. raw:: html

    <img src="_static/customization/Supported Formats Screen.png" usemap="#supported-formats-map" width="324" height="576">

    <map id="supported-formats-map" name="supported-formats-map">
        <area shape="rect" alt="" title="Action Bar" coords="215,24,246,54" href="customization-guide.html#supported-formats-1" target="" />
        <area shape="rect" alt="" title="Background" coords="144,483,178,518" href="customization-guide.html#supported-formats-2" target="" />
        <area shape="rect" alt="" title="Header" coords="239,74,269,106" href="customization-guide.html#supported-formats-3" target="" />
        <area shape="rect" alt="" title="Format Info List Item" coords="278,128,307,160" href="customization-guide.html#supported-formats-4" target="" />
        <area shape="rect" alt="" title="Supported Format Icon" coords="3,117,26,138" href="customization-guide.html#supported-formats-4-1" target="" />
        <area shape="rect" alt="" title="Unsupported Format Icon" coords="2,343,27,365" href="customization-guide.html#supported-formats-4-2" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _supported-formats-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_supported_formats``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshot. <supported-formats>`

.. _supported-formats-2:

2. Background
^^^^

- **Color**

  Via the color resource named ``gv_supported_formats_activity_background``.

:ref:`Back to screenshot. <supported-formats>`

.. _supported-formats-3:

3. Header
^^^^

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Help.SupportedFormats.Item.Header.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Help.SupportedFormats.Item.Header.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.SupportedFormats.Item.Header.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.SupportedFormats.Item.Header.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <supported-formats>`

.. _supported-formats-4:

4. Format Info List Item
^^^^

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Help.SupportedFormats.Item.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Help.SupportedFormats.Item.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.SupportedFormats.Item.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.SupportedFormats.Item.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

- **Background Color**

  Via overriding the style named ``gv_supported_formats_item_background``.

:ref:`Back to screenshot. <supported-formats>`

.. _supported-formats-4-1:

4.1 Supported Format Icon
~~~~

- **Background Color**

  Via the color resource named ``gv_supported_formats_item_supported_icon_background``.

- **Tick Color**

  Via the color resource named ``gv_supported_formats_item_supported_icon_foreground``.

:ref:`Back to screenshot. <supported-formats>`

.. _supported-formats-4-2:

4.2 Unsupported Format Icon
~~~~

- **Background Color**

  Via the color resource named ``gv_supported_formats_item_unsupported_icon_background``.

- **Cross Color**

  Via the color resource named ``gv_supported_formats_item_unsupported_icon_foreground``.

:ref:`Back to screenshot. <supported-formats>`

.. _file-import:

File Import Screen
----

.. raw:: html

    <img src="_static/customization/File Import Screen.png" usemap="#file-import-map" width="324" height="576">

    <map id="file-import-map" name="file-import-map">
        <area shape="rect" alt="" title="Action Bar" coords="288,22,317,54" href="customization-guide.html#file-import-1" target="" />
        <area shape="rect" alt="" title="Background" coords="283,157,313,190" href="customization-guide.html#file-import-2" target="" />
        <area shape="rect" alt="" title="Header" coords="284,82,315,117" href="customization-guide.html#file-import-3" target="" />
        <area shape="rect" alt="" title="Separator Line" coords="147,143,181,178" href="customization-guide.html#file-import-4" target="" />
        <area shape="rect" alt="" title="Section" coords="259,218,292,254" href="customization-guide.html#file-import-5" target="" />
        <area shape="rect" alt="" title="Section Number" coords="38,163,62,187" href="customization-guide.html#file-import-5-1" target="" />
        <area shape="rect" alt="" title="Section Title" coords="188,209,214,235" href="customization-guide.html#file-import-5-2" target="" />
        <area shape="rect" alt="" title="Section Body" coords="13,235,33,256" href="customization-guide.html#file-import-5-3" target="" />
        <area shape="rect" alt="" title="Section Illustration" coords="83,368,110,395" href="customization-guide.html#file-import-5-4" target="" />
        <area shape="rect" alt="" title="Sections" coords="274,380,303,412" href="customization-guide.html#file-import-6" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _file-import-1:

1. Action Bar
^^^^

All Action Bar customizations except the title are global to all Activities.

- **Title**

  Via the string resource named ``gv_title_file_import``.

- **Title Color**

  Via the color resource named ``gv_action_bar_title``.

- **Back Button Icon**

  Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named ``gv_action_bar_back``.

- **Background Color**

  Via the color resource named ``gv_action_bar``.

- **Status Bar Background Color**

  Via the color resource named ``gv_status_bar``.

:ref:`Back to screenshot. <file-import>`

.. _file-import-2:

2. Background
^^^^

- **Color**

  Via the color resource named ``gv_file_import_activity_background``.

:ref:`Back to screenshot. <file-import>`

.. _file-import-3:

3. Header
^^^^

- **Text**

  Via overriding the string resource named ``gv_file_import_header``.

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Help.FileImport.Header.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Help.FileImport.Header.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.FileImport.Header.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.FileImport.Header.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <file-import>`

.. _file-import-4:

4. Separator Line
^^^^

- **Color**

  Via the color resource named ``gv_file_import_separator``.

:ref:`Back to screenshot. <file-import>`

.. _file-import-5:

5. Section
^^^^

.. _file-import-5-1:

5.1 Number
~~~~

- **Background Color**

  Via the color resource named ``gv_file_import_section_number_background``.

- **Text Color**

  Via the color resource named ``gv_file_import_section_number``.

:ref:`Back to screenshot. <file-import>`

.. _file-import-5-2:

5.2 Title
~~~~

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Help.FileImport.Section.Title.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Help.FileImport.Section.Title.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.FileImport.Section.Title.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.FileImport.Section.Title.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <file-import>`

.. _file-import-5-3:

5.3 Body
~~~~

- **Text Style**

  Via overriding the style named ``GiniVisionTheme.Help.FileImport.Section.Body.TextStyle`` (with
  parent style ``Root.GiniVisionTheme.Help.FileImport.Section.Body.TextStyle``).

- **Font**

  Via overriding the style named ``GiniVisionTheme.Help.FileImport.Section.Body.TextStyle``
  (with parent style ``Root.GiniVisionTheme.Help.FileImport.Section.Body.TextStyle``) and
  setting an item named ``gvCustomFont`` with the path to the font file in your assets folder.

:ref:`Back to screenshot. <file-import>`

.. _file-import-5-4:

5.4 Illustration
~~~~~

- Image

  Via image resources as specified in the section illustrations :ref:`below <file-import-6>`.

:ref:`Back to screenshot. <file-import>`

.. _file-import-6:

6. Sections
^^^^

- **Section 1**

  - **Title**

    Via overriding the string resource named ``gv_file_import_section_1_title``.

  - **Body**

    Via overriding the string resource named ``gv_file_import_section_1_body``.
    
  - **Illustration**

    Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named
    ``gv_file_import_section_1_illustration.png``. 
    
    **Note**: For creating your custom illustration you may use `this template
    <https://github.com/gini/gini-vision-lib-assets/blob/master/Gini-Vision-Lib-Design-Elements/Illustrations/PDF/android_pdf_open_with_illustration_1.pdf>`_
    from the `Gini Vision Library UI Assets
    <https://github.com/gini/gini-vision-lib-assets>`_ repository. 

- **Section 2**

  - **Title**

    Via overriding the string resource named ``gv_file_import_section_2_title``.

  - **Body**

    Via overriding the string resource named ``gv_file_import_section_2_body``.
    
  - **Illustration**

    Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named
    ``gv_file_import_section_2_illustration.png``. 
    
    **Note**: For creating your custom illustration you may use `this template
    <https://github.com/gini/gini-vision-lib-assets/blob/master/Gini-Vision-Lib-Design-Elements/Illustrations/PDF/android_pdf_open_with_illustration_2.pdf>`_
    from the `Gini Vision Library UI Assets
    <https://github.com/gini/gini-vision-lib-assets>`_ repository. 

.. _file-import-6-3:

- **Section 3**

  - **Title**

    Via overriding the string resource named ``gv_file_import_section_3_title``.

  - **Body**

    Via overriding the string resource named ``gv_file_import_section_3_body`` and ``gv_file_import_section_3_body_2``.
    
  - **Illustration**

    Via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named
    ``gv_file_import_section_3_illustration.png``.
    **Note**: For creating your custom illustration you may use `this template
    <https://github.com/gini/gini-vision-lib-assets/blob/master/Gini-Vision-Lib-Design-Elements/Illustrations/PDF/android_pdf_open_with_illustration_3.pdf>`_
    from the `Gini Vision Library UI Assets
    <https://github.com/gini/gini-vision-lib-assets>`_ repository. 

  - **Clear app defaults section**

    - **Title**

    Via overriding the string resource named ``gv_file_import_section_3_clear_app_defaults_title``.

    - **Body**

    Via overriding the string resource named ``gv_file_import_section_3_clear_app_defaults_body``.

:ref:`Back to screenshot. <file-import>`


Clear Defaults Dialog
----

.. raw:: html

    <img src="_static/customization/Clear Defaults Dialog.png" usemap="#clear-defaults-map" width="324" height="576">

    <map id="clear-defaults-map" name="clear-defaults-map">
        <area shape="rect" alt="" title="Message" coords="236,139,260,166" href="customization-guide.html#clear-defaults-1" target="" />
        <area shape="rect" alt="" title="File Type" coords="265,223,299,257" href="customization-guide.html#clear-defaults-1-1" target="" />
        <area shape="rect" alt="" title="Positive Button Title" coords="73,329,106,362" href="customization-guide.html#clear-defaults-2" target="" />
        <area shape="rect" alt="" title="Negative Button Title" coords="74,369,105,400" href="customization-guide.html#clear-defaults-3" target="" />
        <!-- Created by Online Image Map Editor (http://www.maschek.hu/imagemap/index) -->
    </map>

.. _clear-defaults-1:

1. Message
^^^^

Via the string resource named ``gv_file_import_default_app_dialog_message``.

.. _clear-defaults-1-1:

1.1 File Type
~~~~

- **PDF**

  Via the string resources named ``gv_file_import_default_app_dialog_pdf_file_type``.

- **Image**

  Via the string resources named ``gv_file_import_default_app_dialog_image_file_type``.

- **Document (Other)**

  Via the string resources named ``gv_file_import_default_app_dialog_document_file_type``.

.. _clear-defaults-2:

2. Positive Button Title
~~~~

Via the string resources named ``gv_file_import_default_app_dialog_positive_button``.

.. _clear-defaults-3:

3. Negative Button Title
~~~~

Via the string resources named ``gv_file_import_default_app_dialog_negative_button``.

:ref:`Back to screenshot. <file-import>`