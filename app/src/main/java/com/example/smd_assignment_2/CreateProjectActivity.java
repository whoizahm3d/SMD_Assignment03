package com.example.smd_assignment_2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smd_assignment_2.models.Project;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateProjectActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etTag;
    private EditText etDescription;
    private EditText etDueDate;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        etName = findViewById(R.id.et_project_name);
        etTag = findViewById(R.id.et_project_tag);
        etDescription = findViewById(R.id.et_project_description);
        etDueDate = findViewById(R.id.et_project_duedate);
        btnSubmit = findViewById(R.id.btn_add_project_submit);

        LinearLayout llAttachmentPicker = findViewById(R.id.ll_attachment_picker);
        TextView tvAttachmentLabel = findViewById(R.id.tv_attachment_label);
        if (llAttachmentPicker != null) {
            llAttachmentPicker.setOnClickListener(v -> {
                Toast.makeText(this, "Attachments added", Toast.LENGTH_SHORT).show();
                if (tvAttachmentLabel != null) {
                    tvAttachmentLabel.setText(R.string.files_attached_label);
                }
            });
        }

        // Simple built-in calendar date picker selection dialog trigger logic
        if (etDueDate != null) {
            etDueDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, dayOfMonth) -> {
                        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                        String formattedDate = String.format(Locale.getDefault(), "%d %s", dayOfMonth, months[selectedMonth]);
                        etDueDate.setText(formattedDate);
                    }, year, month, day);
                picker.show();
            });
        }

        btnSubmit.setOnClickListener(v -> submitProject());
    }

    private void submitProject() {
        String name = etName.getText().toString().trim();
        String tag = etTag.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dateStr = etDueDate != null ? etDueDate.getText().toString().trim() : "30 May";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Project name is required");
            return;
        }

        if (TextUtils.isEmpty(tag)) {
            tag = "Base Interface";
        }

        ArrayList<Integer> attachments = new ArrayList<>();
        attachments.add(R.drawable.img_attachment_1);
        attachments.add(R.drawable.img_attachment_2);

        Project newProject = new Project(
                "p" + System.currentTimeMillis(),
                tag,
                name,
                description,
                attachments,
                0,
                new ArrayList<>(),
                dateStr
        );

        Intent data = new Intent();
        data.putExtra("extra_project", newProject);
        setResult(RESULT_OK, data);
        finish();
    }
}