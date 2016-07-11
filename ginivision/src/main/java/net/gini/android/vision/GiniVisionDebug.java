package net.gini.android.vision;

import android.content.Context;

import net.gini.android.vision.camera.photo.Photo;

import java.io.File;
import java.util.Date;

/**
 * <p>
 * This class allows you to enable and disable debugging for the Gini Vision Library.
 * </p>
 * <p>
 * Debugging is disabled by default.
 * </p>
 * <p>
 * <b>Warning:</b> Don't forget to disable debugging before releasing.
 * </p>
 * <p>
 *     If debug is enabled:
 *     <ul>
 *         <li>
 *          The reviewed jpegs are written to a folder called {@code ginivisionlib} in your app's external directory.
 *         </li>
 *     </ul>
 * </p>
 */
public final class GiniVisionDebug {

    private static boolean sEnabled = false;

    /**
     * <p>
     *     Enables debugging for the Gini Vision Library.
     * </p>
     */
    public static void enable() {
        sEnabled = true;
    }

    /**
     * <p>
     *     Disables debugging for the Gini Vision Library.
     * </p>
     */
    public static void disable() {
        sEnabled = false;
    }

    /**
     * @exclude
     */
    public static void writePhotoToFile(Context context, Photo photo, String suffix) {
        if (!sEnabled) {
            return;
        }
        File giniVisionDir = createGiniVisionDir(context.getExternalFilesDir(null));
        long time = new Date().getTime();
        String jpegFilename = File.separator + time + suffix + ".jpeg";
        File jpegFile = new File(giniVisionDir, jpegFilename);
        photo.saveJpegToFile(jpegFile);
    }

    private static File createGiniVisionDir(File externalFilesDir) {
        File giniVisionDir = new File(externalFilesDir, "ginivisionlib");
        giniVisionDir.mkdir();
        return giniVisionDir;
    }

    private GiniVisionDebug() {

    }
}
