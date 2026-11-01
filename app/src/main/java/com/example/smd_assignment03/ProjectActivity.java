package com.example.smd_assignment03;

import android.app.Dialog;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.example.smd_assignment03.models.Project;
import com.example.smd_assignment03.models.TaskItem;

import java.util.ArrayList;
import java.util.Locale;

public class ProjectActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTag, tvName, tvDescription, tvAttachmentMore;
    private ImageView ivAttachment1, ivAttachment2;
    private Button btnAddTask;
    private LinearLayout llTaskContainer;

    private Project currentProject;
    private int projectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Project data MUST come in via Intent extras — never hardcoded here.
        currentProject = IntentCompat.getSerializableExtra(getIntent(), "extra_project", Project.class);
        projectIndex = getIntent().getIntExtra("extra_project_index", -1);

        if (currentProject == null) {
            finish();
            return;
        }

        ivBack = findViewById(R.id.iv_back);
        tvTag = findViewById(R.id.tv_project_tag);
        tvName = findViewById(R.id.tv_project_name);
        tvDescription = findViewById(R.id.tv_project_description);
        ivAttachment1 = findViewById(R.id.iv_attachment_1);
        ivAttachment2 = findViewById(R.id.iv_attachment_2);
        tvAttachmentMore = findViewById(R.id.tv_attachment_more);
        btnAddTask = findViewById(R.id.btn_add_task);
        llTaskContainer = findViewById(R.id.ll_task_container);
        
        Button btnInviteTop = findViewById(R.id.btn_invite_top);
        if (btnInviteTop != null) {
            btnInviteTop.setOnClickListener(v -> showInviteDialog());
        }

        bindProject();
        renderTasks();

        ivBack.setOnClickListener(v -> returnUpdatedProject());
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        // System back button behaves the same as the back arrow.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnUpdatedProject();
            }
        });
    }

    private void bindProject() {
        tvTag.setText(currentProject.getTag());
        if ("Base Interface".equalsIgnoreCase(currentProject.getTag())) {
            tvTag.setBackgroundResource(R.drawable.bg_chip_blue);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.chip_blue_text));
        } else if ("UI/UX".equalsIgnoreCase(currentProject.getTag())) {
            tvTag.setBackgroundResource(R.drawable.bg_chip_green);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.chip_green_text));
        } else {
            tvTag.setBackgroundResource(R.drawable.bg_chip_grey);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.chip_grey_text));
        }
        tvName.setText(currentProject.getName());
        tvDescription.setText(currentProject.getDescription());

        ArrayList<Integer> attachments = currentProject.getAttachmentResIds();
        int count = attachments == null ? 0 : attachments.size();

        if (count > 0) {
            ivAttachment1.setVisibility(View.VISIBLE);
            ivAttachment1.setImageResource(attachments.get(0));
            ivAttachment1.setOnClickListener(v -> showAttachment(attachments.get(0)));
        } else {
            ivAttachment1.setVisibility(View.GONE);
        }

        if (count > 1) {
            ivAttachment2.setVisibility(View.VISIBLE);
            ivAttachment2.setImageResource(attachments.get(1));
            ivAttachment2.setOnClickListener(v -> showAttachment(attachments.get(1)));
        } else {
            ivAttachment2.setVisibility(View.GONE);
        }

        if (count > 2) {
            tvAttachmentMore.setVisibility(View.VISIBLE);
            tvAttachmentMore.setText(String.format(Locale.getDefault(), "+%d", count - 2));
        } else {
            tvAttachmentMore.setVisibility(View.GONE);
        }
    }

    private void renderTasks() {
        llTaskContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        ArrayList<TaskItem> tasks = currentProject.getTasks();
        if (tasks == null) return;

        for (TaskItem task : tasks) {
            View row = inflater.inflate(R.layout.item_task, llTaskContainer, false);
            CheckBox cbTask = row.findViewById(R.id.cb_task);
            TextView tvTaskDescription = row.findViewById(R.id.tv_task_description);
            TextView tvTaskPriority = row.findViewById(R.id.tv_task_priority);

            cbTask.setOnCheckedChangeListener(null);
            cbTask.setChecked(task.isDone());
            tvTaskDescription.setText(task.getDescription());
            applyTaskDoneStyle(tvTaskDescription, task.isDone());
            
            if (tvTaskPriority != null) {
                String p = task.getPriority();
                tvTaskPriority.setText(p);
                if ("High".equalsIgnoreCase(p)) {
                    tvTaskPriority.setBackgroundResource(R.drawable.bg_chip_green);
                    tvTaskPriority.setTextColor(ContextCompat.getColor(this, R.color.chip_green_text));
                } else {
                    tvTaskPriority.setBackgroundResource(R.drawable.bg_chip_grey);
                    tvTaskPriority.setTextColor(ContextCompat.getColor(this, R.color.chip_grey_text));
                }
            }

            cbTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setDone(isChecked);
                applyTaskDoneStyle(tvTaskDescription, isChecked);
            });

            // Long click to remove individual tasks feature
            row.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Remove this task item?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Remove", (d, w) -> {
                        currentProject.getTasks().remove(task);
                        renderTasks();
                    })
                    .show();
                return true;
            });

            llTaskContainer.addView(row);
        }
    }

    private void applyTaskDoneStyle(TextView textView, boolean done) {
        if (done) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTaskDescription = dialogView.findViewById(R.id.et_task_description);
        etTaskDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        
        Spinner spPriority = dialogView.findViewById(R.id.sp_task_priority);
        if (spPriority != null) {
            String[] priorities = {"Low", "Medium", "High"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priorities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPriority.setAdapter(adapter);
            spPriority.setSelection(1); // Default to Medium
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_add_task_title)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add, null)
                .create();

        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String description = etTaskDescription.getText().toString().trim();
                    if (TextUtils.isEmpty(description)) {
                        etTaskDescription.setError("Task description cannot be empty");
                        return;
                    }
                    
                    String selectedPriority = "Medium";
                    if (spPriority != null) {
                        selectedPriority = spPriority.getSelectedItem().toString();
                    }

                    if (currentProject.getTasks() == null) {
                        currentProject.setTasks(new ArrayList<>());
                    }
                    currentProject.getTasks().add(
                            new TaskItem("t" + System.currentTimeMillis(), description, false, selectedPriority)
                    );

                    renderTasks();
                    dialog.dismiss();
                }));

        dialog.show();
    }

    private void showAttachment(int resId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_view_attachment, null);
        ImageView ivFull = dialogView.findViewById(R.id.iv_attachment_full);
        ivFull.setImageResource(resId);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void returnUpdatedProject() {
        Intent data = new Intent();
        data.putExtra("extra_project", currentProject);
        data.putExtra("extra_project_index", projectIndex);
        setResult(RESULT_OK, data);
        finish();
    }

    private void showInviteDialog() {
        final EditText input = new EditText(this);
        input.setHint("colleague@example.com");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        
        new AlertDialog.Builder(this)
                .setTitle("Invite Team Member")
                .setMessage("Enter email to invite to project:")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send Invite", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Invite sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
