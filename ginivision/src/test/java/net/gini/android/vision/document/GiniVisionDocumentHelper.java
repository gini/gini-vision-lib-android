package net.gini.android.vision.document;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpar Szotyori on 16.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDocumentHelper {

    public static ImageDocument newEmptyImageDocument() {
        return ImageDocument.empty();
    }

    public static ImageDocument newImageDocument(@NonNull final byte[] data) {
        return new ImageDocument(data);
    }

    public static ImageMultiPageDocument newMultiPageDocument() {
        final List<ImageDocument> imageDocuments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final ImageDocument imageDocument = GiniVisionDocumentHelper.newImageDocument(
                    new byte[]{
                            (byte) ((i + 1) * 5), (byte) ((i + 2) * 5), (byte) ((i + 3) * 5)});
            imageDocuments.add(imageDocument);
        }
        return GiniVisionDocumentHelper.newMultiPageDocument(imageDocuments);
    }

    private static ImageMultiPageDocument newMultiPageDocument(@NonNull final List<ImageDocument> imageDocuments) {
        if (imageDocuments.size() == 0) {
            throw new IllegalArgumentException("Empty image documents list.");
        }
        final ImageMultiPageDocument multiPageDocument = new ImageMultiPageDocument(
                imageDocuments.get(0));
        for (int i = 1; i < imageDocuments.size(); i++) {
            multiPageDocument.addDocument(imageDocuments.get(i));
        }
        return multiPageDocument;
    }
}
