package net.gini.android.vision.internal.camera.view;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.internal.camera.view.FlashButtonHelper.FlashButtonPosition;
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
        final FlashButtonPosition position = getFlashButtonPosition(true, true);
        assertThat(position).isEqualTo(FlashButtonPosition.LEFT_OF_CAMERA_TRIGGER);
    }

    @Test
    public void should_returnPositionBottomLeft_whenDocumentImport_isDisabled_andFileImport_isEnabled() {
        final FlashButtonPosition position = getFlashButtonPosition(false, true);
        assertThat(position).isEqualTo(FlashButtonPosition.BOTTOM_LEFT);
    }

    @Test
    public void should_returnPositionBottomRight_whenDocumentImport_isEnabled_andFileImport_isDisabled() {
        final FlashButtonPosition position = getFlashButtonPosition(true, false);
        assertThat(position).isEqualTo(FlashButtonPosition.BOTTOM_RIGHT);
    }

    @Test
    public void should_returnPositionBottomLeft_whenDocumentImport_andFileImport_areDisabled() {
        final FlashButtonPosition position = getFlashButtonPosition(false, false);
        assertThat(position).isEqualTo(FlashButtonPosition.BOTTOM_LEFT);
    }
}