package net.gini.android.vision.internal.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by Alpar Szotyori on 05.06.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_POSITIVE_BUTTON_TITLE = "ARG_POSITIVE_BUTTON_TITLE";
    private static final String ARG_NEGATIVE_BUTTON_TITLE = "ARG_NEGATIVE_BUTTON_TITLE";
    private static final String ARG_DIALOG_ID = "ARG_DIALOG_ID";
    private static final String ARG_DISABLE_CANCEL_ON_TOUCH_OUTSIDE
            = "ARG_CANCELED_ON_TOUCH_OUTSIDE";

    @StringRes
    private int mTitle;
    @StringRes
    private int mMessage;
    @StringRes
    private int mPositiveButtonTitle;
    @StringRes
    private int mNegativeButtonTitle;
    private int mDialogId;
    private boolean mDisableCancelOnTouchOutside;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArguments();
    }

    private void readArguments() {
        final Bundle args = getArguments();
        if (args == null) {
            return;
        }
        mTitle = args.getInt(ARG_TITLE);
        mMessage = args.getInt(ARG_MESSAGE);
        mPositiveButtonTitle = args.getInt(ARG_POSITIVE_BUTTON_TITLE);
        mNegativeButtonTitle = args.getInt(ARG_NEGATIVE_BUTTON_TITLE);
        mDialogId = args.getInt(ARG_DIALOG_ID);
        mDisableCancelOnTouchOutside = args.getBoolean(ARG_DISABLE_CANCEL_ON_TOUCH_OUTSIDE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mTitle != 0) {
            builder.setTitle(mTitle);
        }
        if (mMessage != 0) {
            builder.setMessage(mMessage);
        }
        if (mPositiveButtonTitle != 0) {
            builder.setPositiveButton(mPositiveButtonTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final FragmentActivity activity = getActivity();
                    if (activity instanceof AlertDialogFragmentListener) {
                        ((AlertDialogFragmentListener) activity).onPositiveButtonClicked(
                                dialog, mDialogId);
                    }
                }
            });
        }
        if (mNegativeButtonTitle != 0) {
            builder.setNegativeButton(mNegativeButtonTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final FragmentActivity activity = getActivity();
                    if (activity instanceof AlertDialogFragmentListener) {
                        ((AlertDialogFragmentListener) activity).onNegativeButtonClicked(dialog,
                                mDialogId);
                    }
                }
            });
        }
        final AlertDialog alertDialog = builder.create();
        if (mDisableCancelOnTouchOutside) {
            alertDialog.setCanceledOnTouchOutside(false);
        }
        return alertDialog;
    }

    /**
 * Internal use only.
 *
 * @suppress
 */
    public static class Builder {

        private final Bundle args = new Bundle();

        public Builder setTitle(@StringRes final int title) {
            args.putInt(ARG_TITLE, title);
            return this;
        }

        public Builder setMessage(@StringRes final int message) {
            args.putInt(ARG_MESSAGE, message);
            return this;
        }

        public Builder setPositiveButton(@StringRes final int positiveButtonTitle) {
            args.putInt(ARG_POSITIVE_BUTTON_TITLE, positiveButtonTitle);
            return this;
        }

        public Builder setNegativeButton(@StringRes final int negativeButtonTitle) {
            args.putInt(ARG_NEGATIVE_BUTTON_TITLE, negativeButtonTitle);
            return this;
        }

        public Builder setDialogId(final int dialogId) {
            args.putInt(ARG_DIALOG_ID, dialogId);
            return this;
        }

        public Builder disableCancelOnTouchOutside() {
            args.putBoolean(ARG_DISABLE_CANCEL_ON_TOUCH_OUTSIDE, true);
            return this;
        }

        public AlertDialogFragment create() {
            final AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }
}
