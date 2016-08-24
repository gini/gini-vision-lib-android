package net.gini.android.vision;

import android.content.Context;

import net.gini.android.vision.internal.camera.photo.Photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionDebug.class);

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
     * <p>
     *     Helper for writing a document to file. Has no effect if debugging is disabled.
     * </p>
     * <p>
     *     The filename consists of a timestamp concatenated with the suffix. Ex.: if suffix is "_original" then {@code 1469541253_original.jpeg}
     * </p>
     * <p>
     *     Destination directory is {@code ginivisionlib} inside your apps external files directory: {@code /sdcard/Android/data/your.app.id/files/ginivisionlib/}
     * </p>
     */
    public static void writeDocumentToFile(Context context, Document document, String suffix) {
        if (!sEnabled) {
            return;
        }
        File giniVisionDir = createGiniVisionDir(context);
        long time = new Date().getTime();
        String jpegFilename = time + suffix + ".jpeg";
        File jpegFile = new File(giniVisionDir, jpegFilename);
        Photo.fromDocument(document).saveJpegToFile(jpegFile);
        LOG.debug("Document written to {}", jpegFile.getAbsolutePath());
    }

    private static File createGiniVisionDir(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        File giniVisionDir = new File(externalFilesDir, "ginivisionlib");
        giniVisionDir.mkdir();
        return giniVisionDir;
    }

    private GiniVisionDebug() {

    }
}
