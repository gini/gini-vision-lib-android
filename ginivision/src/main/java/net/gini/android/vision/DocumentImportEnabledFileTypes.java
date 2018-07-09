package net.gini.android.vision;

/**
 * Use this enum to select the file types to be allowed for document import in the Camera Screen.
 */
public enum DocumentImportEnabledFileTypes {
    /**
     * No file types allowed and disables document import.
     */
    NONE,
    /**
     * Only PDFs can be imported.
     */
    PDF,
    /**
     * Only images (jpeg, png and gif) can be imported.
     */
    IMAGES,
    /**
     * PDFs and images (jpeg, png and gif) can be imported.
     */
    PDF_AND_IMAGES
}
