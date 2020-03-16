package net.gini.android.vision.document;

import net.gini.android.vision.Document;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 16.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDocumentHelper {

    public static ImageDocument newImageDocument() {
        final int random = (int) (1 + Math.round(Math.random() * 5));
        return new ImageDocument(new byte[]{
                (byte) ((random + 1) * 5), (byte) ((random + 2) * 5), (byte) ((random + 3) * 5)},
                Document.Source.newUnknownSource(), Document.ImportMethod.NONE);
    }

    public static ImageMultiPageDocument newMultiPageDocument() {
        final List<ImageDocument> imageDocuments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final ImageDocument imageDocument = GiniVisionDocumentHelper.newImageDocument();
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
