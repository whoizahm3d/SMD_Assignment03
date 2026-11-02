package com.example.smd_assignment03;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.smd_assignment03.models.CatalogItem;
import com.example.smd_assignment03.models.Project;

public class ProjectFormDialog {

    public interface OnProjectSaved {
        void onSaved(String name, String tag, String description, CatalogItem pickedOrNull);
    }

    public static void show(Context context, Project existingOrNull, OnProjectSaved listener) {
        boolean isEdit = (existingOrNull != null);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_project_form, null);

        EditText etName = dialogView.findViewById(R.id.et_project_name);
        EditText etTag = dialogView.findViewById(R.id.et_project_tag);
        EditText etDescription = dialogView.findViewById(R.id.et_project_description);
        LinearLayout sectionAttachment = dialogView.findViewById(R.id.section_attachment);
        TextView tvSelectedAttachment = dialogView.findViewById(R.id.tv_selected_attachment);
        Button btnPickAttachment = dialogView.findViewById(R.id.btn_pick_attachment);

        etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
        etTag.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        etDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});

        final CatalogItem[] pickedItem = {null};

        if (isEdit) {
            etName.setText(existingOrNull.getName());
            etTag.setText(existingOrNull.getTag());
            etDescription.setText(existingOrNull.getDescription());
            if (sectionAttachment != null) {
                sectionAttachment.setVisibility(View.GONE);
            }
        }

        if (btnPickAttachment != null) {
            btnPickAttachment.setOnClickListener(v -> {
                CatalogPickerDialog.show(context, AttachmentCatalog.getItems(), item -> {
                    pickedItem[0] = item;
                    if (tvSelectedAttachment != null) {
                        tvSelectedAttachment.setText(item.getDisplayName());
                    }
                });
            });
        }

        String title = isEdit ? "Edit Project" : "Add New Project";
        String positiveButtonText = isEdit ? context.getString(R.string.save) : context.getString(R.string.add);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(positiveButtonText, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                String tag = etTag.getText() != null ? etTag.getText().toString().trim() : "";
                String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

                if (TextUtils.isEmpty(name)) {
                    etName.setError("Project name is required");
                    return;
                }
                if (name.length() > 60) {
                    etName.setError("Name must be at most 60 characters");
                    return;
                }

                if (TextUtils.isEmpty(tag)) {
                    etTag.setError("Project tag is required");
                    return;
                }
                if (tag.length() > 20) {
                    etTag.setError("Tag must be at most 20 characters");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    etDescription.setError("Project description is required");
                    return;
                }
                if (description.length() > 500) {
                    etDescription.setError("Description must be at most 500 characters");
                    return;
                }

                listener.onSaved(name, tag, description, pickedItem[0]);
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}
