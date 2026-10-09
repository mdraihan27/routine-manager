package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import io.github.mdraihan27.routinemanager.R;

public final class AppBottomSheet {

    private AppBottomSheet() {
    }

    @NonNull
    public static BottomSheetDialog create(@NonNull Context context, @NonNull View contentView) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.Widget_App_BottomSheet);
        dialog.setContentView(contentView);
        return dialog;
    }
}
