package net.gini.android.vision;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Using this class the features of the Gini Vision Library can be configured.
 * <p>
 * Use the builder by invoking {@link GiniVisionFeatureConfiguration#buildNewConfiguration()} to
 * configure and create a new instance.
 *
 * @Deprecated Use {@link GiniVision#newInstance()} and the returned {@link GiniVision.Builder} instead.
 */
public class GiniVisionFeatureConfiguration implements Parcelable {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final Creator<GiniVisionFeatureConfiguration> CREATOR =
            new Creator<GiniVisionFeatureConfiguration>() {
                @Override
                public GiniVisionFeatureConfiguration createFromParcel(final Parcel in) {
                    return new GiniVisionFeatureConfiguration(in);
                }

                @Override
                public GiniVisionFeatureConfiguration[] newArray(final int size) {
                    return new GiniVisionFeatureConfiguration[size];
                }
            };

    private final DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes;
    private final boolean mFileImportEnabled;
    private final boolean mQRCodeScanningEnabled;

    protected GiniVisionFeatureConfiguration(final Parcel in) {
        mDocumentImportEnabledFileTypes = (DocumentImportEnabledFileTypes) in.readSerializable();
        mFileImportEnabled = in.readByte() != 0;
        mQRCodeScanningEnabled = in.readByte() != 0;
    }

    protected GiniVisionFeatureConfiguration(final Builder builder) {
        mDocumentImportEnabledFileTypes = builder.getDocumentImportEnabledFileTypes();
        mFileImportEnabled = builder.isFileImportEnabled();
        mQRCodeScanningEnabled = builder.isQRCodeScanningEnabled();
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeSerializable(mDocumentImportEnabledFileTypes);
        parcel.writeByte((byte) (mFileImportEnabled ? 1 : 0));
        parcel.writeByte((byte) (mQRCodeScanningEnabled ? 1 : 0));
    }

    /**
     * <p>
     *     Builder for creating a new feature configuration.
     * </p>
     * @return a builder for creating a new feature configuration
     */
    @NonNull
    public static Builder buildNewConfiguration() {
        return new Builder();
    }

    /**
     * <p>
     *     Retrieve the file types enabled for document import.
     * </p>
     * <p>
     *     Disabled by default.
     * </p>
     * @return enabled file types
     */
    @NonNull
    public DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes() {
        return mDocumentImportEnabledFileTypes;
    }

    /**
     * <p>
     *     Find out whether file import has been enabled.
     * </p>
     * <p>
     *     Disabled by default.
     * </p>
     * @return {@code true} if file import was enabled
     */
    public boolean isFileImportEnabled() {
        return mFileImportEnabled;
    }

    /**
     * <p>
     *     Find out whether QRCode scanning has been enabled.
     * </p>
     * <p>
     *     Disabled by default.
     * </p>
     * @return {@code true} if QRCode scanning was enabled
     */
    public boolean isQRCodeScanningEnabled() {
        return mQRCodeScanningEnabled;
    }

    /**
     * <p>
     *     Feature configuration builder.
     * </p>
     */
    public static class Builder {

        private DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes =
                DocumentImportEnabledFileTypes.NONE;
        private boolean mFileImportEnabled;
        private boolean mQRCodeScanningEnabled;

        protected Builder() {
        }

        /**
         * <p>
         *     Create a new {@link GiniVisionFeatureConfiguration} instance.
         * </p>
         * @return a new {@link GiniVisionFeatureConfiguration} instance
         */
        public GiniVisionFeatureConfiguration build() {
            return new GiniVisionFeatureConfiguration(this);
        }

        @Nullable
        private DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes() {
            return mDocumentImportEnabledFileTypes;
        }

        /**
         * <p>
         *     Enable and configure the document import feature or disable it by passing in
         *     {@link DocumentImportEnabledFileTypes#NONE}.
         * </p>
         * <p>
         *     Disabled by default.
         * </p>
         * @param documentImportEnabledFileTypes file types to be enabled for document import
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setDocumentImportEnabledFileTypes(
                @NonNull final DocumentImportEnabledFileTypes documentImportEnabledFileTypes) {
            mDocumentImportEnabledFileTypes = documentImportEnabledFileTypes;
            return this;
        }

        private boolean isFileImportEnabled() {
            return mFileImportEnabled;
        }

        private boolean isQRCodeScanningEnabled() {
            return mQRCodeScanningEnabled;
        }

        /**
         * <p>
         *     Enable/disable the file import feature.
         * </p>
         * <p>
         *     Disabled by default.
         * </p>
         * @param fileImportEnabled {@code true} to enable file import
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setFileImportEnabled(final boolean fileImportEnabled) {
            mFileImportEnabled = fileImportEnabled;
            return this;
        }

        /**
         * <p>
         *     Enable/disable the QRCode scanning feature.
         * </p>
         * <p>
         *     Disabled by default.
         * </p>
         * @param qrCodeScanningEnabled {@code true} to enable QRCode scanning
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setQRCodeScanningEnabled(final boolean qrCodeScanningEnabled) {
            mQRCodeScanningEnabled = qrCodeScanningEnabled;
            return this;
        }
    }

}
