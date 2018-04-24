package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.os.Parcelable;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageDocument.ImportMethod;
import net.gini.android.vision.document.ImageDocument.Source;

import java.io.File;

/**
 * @exclude
 */
public interface Photo extends Parcelable {

    boolean isImported();

    byte[] getData();

    void setData(byte[] data);

    int getRotationForDisplay();

    int getRotationDelta();

    void setRotationForDisplay(int rotationDegrees);

    String getDeviceOrientation();

    String getDeviceType();

    Source getSource();

    ImportMethod getImportMethod();

    Bitmap getBitmapPreview();

    ImageDocument.ImageFormat getImageFormat();

    PhotoEdit edit();

    void updateBitmapPreview();

    void updateExif();

    void updateRotationDeltaBy(int i);

    void saveToFile(File file);
}
