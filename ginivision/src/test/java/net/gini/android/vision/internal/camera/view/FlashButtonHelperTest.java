package net.gini.android.vision.internal.camera.view;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.internal.camera.view.FlashButtonHelper.FLASH_BUTTON_POSITION;
import static net.gini.android.vision.internal.camera.view.FlashButtonHelper.getFlashButtonPosition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by Alpar Szotyori on 18.02.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(JUnit4.class)
public class FlashButtonHelperTest {

    @Test
    public void should_returnPositionLeftOfCameraTrigger_whenDocumentImport_andFileImport_areEnabled() {
        final FLASH_BUTTON_POSITION position = getFlashButtonPosition(true, true);
        assertThat(position).isEqualTo(FLASH_BUTTON_POSITION.LEFT_OF_CAMERA_TRIGGER);
    }

    @Test
    public void should_returnPositionBottomLeft_whenDocumentImport_isDisabled_andFileImport_isEnabled() {
        final FLASH_BUTTON_POSITION position = getFlashButtonPosition(false, true);
        assertThat(position).isEqualTo(FLASH_BUTTON_POSITION.BOTTOM_LEFT);
    }

    @Test
    public void should_returnPositionBottomRight_whenDocumentImport_isEnabled_andFileImport_isDisabled() {
        final FLASH_BUTTON_POSITION position = getFlashButtonPosition(true, false);
        assertThat(position).isEqualTo(FLASH_BUTTON_POSITION.BOTTOM_RIGHT);
    }

    @Test
    public void should_returnPositionBottomLeft_whenDocumentImport_andFileImport_areDisabled() {
        final FLASH_BUTTON_POSITION position = getFlashButtonPosition(false, false);
        assertThat(position).isEqualTo(FLASH_BUTTON_POSITION.BOTTOM_LEFT);
    }
}