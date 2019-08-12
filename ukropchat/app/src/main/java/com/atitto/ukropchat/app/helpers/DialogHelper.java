package com.atitto.ukropchat.app.helpers;

import android.app.AlertDialog;
import android.content.Context;

public class DialogHelper {

    public static void showErrorAlert(Context context, String error) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(error)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
