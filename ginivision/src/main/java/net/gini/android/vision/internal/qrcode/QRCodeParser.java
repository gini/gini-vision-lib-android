package net.gini.android.vision.internal.qrcode;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

import androidx.annotation.NonNull;

/**
 * Interface for parsing QRCode content strings.
 *
 * @param <T> parsing output class
 */
interface QRCodeParser<T> {

    T parse(@NonNull final String qrCodeContent) throws IllegalArgumentException;
}
