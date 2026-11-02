package com.example.smd_assignment03;

import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.smd_assignment03.models.CatalogItem;

import java.util.ArrayList;
import java.util.Locale;

public class CatalogPickerDialog {

    public interface OnCatalogPicked {
        void onPicked(CatalogItem item);
    }

    public static void show(Context context, ArrayList<CatalogItem> choices, OnCatalogPicked listener) {
        if (choices == null || choices.isEmpty()) {
            Toast.makeText(context, "All catalog files are already linked", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = new String[choices.size()];
        for (int i = 0; i < choices.size(); i++) {
            CatalogItem ci = choices.get(i);
            items[i] = String.format(Locale.getDefault(), "%s (%s)", ci.getDisplayName(), ci.getTypeLabel());
        }

        final int[] selectedIndex = {-1};

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Choose Attachment")
                .setSingleChoiceItems(items, -1, (d, which) -> {
                    selectedIndex[0] = which;
                    AlertDialog ad = (AlertDialog) d;
                    Button positiveBtn = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                    if (positiveBtn != null) {
                        positiveBtn.setEnabled(true);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add, (d, which) -> {
                    if (selectedIndex[0] >= 0 && selectedIndex[0] < choices.size()) {
                        listener.onPicked(choices.get(selectedIndex[0]));
                    }
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveBtn != null) {
                positiveBtn.setEnabled(false);
            }
        });

        dialog.show();
    }
}
