package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.github.mdraihan27.routinemanager.R;

public final class AppDialog {

    private AppDialog() {
    }

    @NonNull
    public static MaterialAlertDialogBuilder createBuilder(@NonNull Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.Widget_App_Dialog);
    }

    @NonNull
    public static AlertDialog create(@NonNull Context context,
                                      @NonNull CharSequence title,
                                      @NonNull CharSequence message,
                                      @NonNull CharSequence positiveText,
                                      @NonNull Runnable onPositive,
                                      @NonNull CharSequence negativeText,
                                      @NonNull Runnable onNegative) {
        return createBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> onPositive.run())
                .setNegativeButton(negativeText, (dialog, which) -> onNegative.run())
                .create();
    }
}
