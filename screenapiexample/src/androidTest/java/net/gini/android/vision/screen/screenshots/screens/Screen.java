package net.gini.android.vision.screen.screenshots.screens;

/**
 * <p>
 * Declares common functionality for classes which represent screens in the application.
 * </p>
 * <p>Inspired by the
 * <a href="https://martinfowler.com/bliki/PageObject.html">Page
 * Object Pattern</a>, but in our case we have screens instead of pages.
 * </p>
 */
public interface Screen {
    boolean isVisible();
}
