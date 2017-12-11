package net.gini.android.vision.internal.qrcode;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

class QRCode {

    private final String mContent;

    QRCode(final String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }
}
