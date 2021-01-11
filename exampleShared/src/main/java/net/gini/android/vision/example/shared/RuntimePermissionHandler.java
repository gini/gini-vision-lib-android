package net.gini.android.vision.example.shared;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alpar Szotyori on 05.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class RuntimePermissionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimePermissionHandler.class);

    private final Activity mActivity;
    private final String mCameraPermissionDeniedMessage;
    private final String mCameraPermissionRationale;
    private final String mCancelButtonTitle;
    private final String mGrantAccessButtonTitle;
    private final String mStoragePermissionDeniedMessage;
    private final String mStoragePermissionRationale;

    private RuntimePermissionHandler(final Builder builder) {
        mActivity = builder.mActivity;
        mStoragePermissionDeniedMessage = builder.mStoragePermissionDeniedMessage;
        mStoragePermissionRationale = builder.mStoragePermissionRationale;
        mCameraPermissionDeniedMessage = builder.mCameraPermissionDeniedMessage;
        mCameraPermissionRationale = builder.mCameraPermissionRationale;
        mGrantAccessButtonTitle = builder.mGrantAccessButtonTitle;
        mCancelButtonTitle = builder.mCancelButtonTitle;
    }

    public static Builder forActivity(final Activity activity) {
        return new Builder().forActivity(activity);
    }

    public void requestCameraPermission(final Listener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.permissionGranted();
            return;
        }
        Dexter.withActivity(mActivity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(final PermissionGrantedResponse response) {
                        listener.permissionGranted();
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse response) {
                        showCameraPermissionDeniedDialog(listener);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            final PermissionRequest permission,
                            final PermissionToken token) {
                        showCameraPermissionRationale(token);
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(final DexterError error) {
                        LOG.error("Permission error: {}", error.name());
                    }
                })
                .check();
    }

    private void showCameraPermissionDeniedDialog(final Listener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setMessage(mCameraPermissionDeniedMessage)
                .setPositiveButton(mGrantAccessButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        showAppDetailsSettingsScreen();
                    }
                })
                .setNegativeButton(mCancelButtonTitle, null)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        listener.permissionDenied();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void showAppDetailsSettingsScreen() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        mActivity.startActivity(intent);
    }

    private void showCameraPermissionRationale(final PermissionToken token) {
        final AlertDialog
                alertDialog = new AlertDialog.Builder(mActivity)
                .setMessage(mCameraPermissionRationale)
                .setPositiveButton(mGrantAccessButtonTitle,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                token.continuePermissionRequest();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .create();
        alertDialog.show();
    }

    @SuppressLint("InlinedApi")
    public void requestStoragePermission(final Listener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.permissionGranted();
            return;
        }
        Dexter.withActivity(mActivity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(final PermissionGrantedResponse response) {
                        listener.permissionGranted();
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse response) {
                        showStoragePermissionDeniedDialog(listener);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            final PermissionRequest permission,
                            final PermissionToken token) {
                        showStoragePermissionRationale(token);
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(final DexterError error) {
                        LOG.error("Permission error: {}", error.name());
                    }
                })
                .check();
    }

    private void showStoragePermissionDeniedDialog(final Listener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setMessage(mStoragePermissionDeniedMessage)
                .setPositiveButton(mGrantAccessButtonTitle,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                showAppDetailsSettingsScreen();
                            }
                        })
                .setNegativeButton(mCancelButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        listener.permissionDenied();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        listener.permissionDenied();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void showStoragePermissionRationale(final PermissionToken token) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setMessage(mStoragePermissionRationale)
                .setPositiveButton(mGrantAccessButtonTitle,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                token.continuePermissionRequest();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .create();
        alertDialog.show();
    }

    public interface Listener {
        void permissionDenied();

        void permissionGranted();
    }

    public static final class Builder {

        private Activity mActivity;
        private String mCameraPermissionDeniedMessage;
        private String mCameraPermissionRationale;
        private String mCancelButtonTitle;
        private String mGrantAccessButtonTitle;
        private String mStoragePermissionDeniedMessage;
        private String mStoragePermissionRationale;

        public RuntimePermissionHandler build() {
            return new RuntimePermissionHandler(this);
        }

        public Builder withCameraPermissionDeniedMessage(
                final String cameraPermissionDeniedMessage) {
            mCameraPermissionDeniedMessage = cameraPermissionDeniedMessage;
            return this;
        }

        public Builder withCameraPermissionRationale(final String cameraPermissionRationale) {
            mCameraPermissionRationale = cameraPermissionRationale;
            return this;
        }

        public Builder withCancelButtonTitle(final String cancelButtonTitle) {
            mCancelButtonTitle = cancelButtonTitle;
            return this;
        }

        public Builder withGrantAccessButtonTitle(final String grantAccessButtonTitle) {
            mGrantAccessButtonTitle = grantAccessButtonTitle;
            return this;
        }

        public Builder withStoragePermissionDeniedMessage(
                final String storagePermissionDeniedMessage) {
            mStoragePermissionDeniedMessage = storagePermissionDeniedMessage;
            return this;
        }

        public Builder withStoragePermissionRationale(final String storagePermissionRationale) {
            mStoragePermissionRationale = storagePermissionRationale;
            return this;
        }

        private Builder forActivity(final Activity activity) {
            mActivity = activity;
            return this;
        }
    }
}
