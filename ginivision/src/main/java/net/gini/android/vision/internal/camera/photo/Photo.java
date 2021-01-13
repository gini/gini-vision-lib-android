package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.os.Parcelable;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.ImageDocument;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
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

    Document.Source getSource();

    Document.ImportMethod getImportMethod();

    Bitmap getBitmapPreview();

    ImageDocument.ImageFormat getImageFormat();

    PhotoEdit edit();

    void updateBitmapPreview();

    void updateExif();

    void updateRotationDeltaBy(int i);

    void saveToFile(File file);

    void setParcelableMemoryCacheTag(@NonNull final String tag);
    
    @Nullable
    String getParcelableMemoryCacheTag();
}
