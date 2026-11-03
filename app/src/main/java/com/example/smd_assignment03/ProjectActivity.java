package com.example.smd_assignment03;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smd_assignment03.models.Project;
import com.example.smd_assignment03.models.TaskItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProjectActivity extends AppCompatActivity implements ProjectHost {

    Project project;
    TaskFilterState taskFilterState = new TaskFilterState();
    FileFilterState fileFilterState = new FileFilterState();

    ImageView ivBack;
    TextView tvTag;
    TextView tvName;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ProjectPagerAdapter pagerAdapter;
    TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        if (savedInstanceState != null && savedInstanceState.containsKey("key_project")) {
            project = (Project) savedInstanceState.getSerializable("key_project");
        }
        if (project == null) {
            project = IntentCompat.getSerializableExtra(getIntent(), "key_project", Project.class);
        }
        if (project == null) {
            finish();
            return;
        }

        tvTag.setText(project.getTag());
        tvName.setText(project.getName());

        pagerAdapter = new ProjectPagerAdapter(this, project.getId());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            updateSingleTabTitle(tab, position);
        });
        tabLayoutMediator.attach();

        ivBack.setOnClickListener((v) -> {
            returnUpdatedProject();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnUpdatedProject();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("key_project", project);
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public TaskFilterState getTaskFilterState() {
        return taskFilterState;
    }

    @Override
    public FileFilterState getFileFilterState() {
        return fileFilterState;
    }

    @Override
    public void onProjectChanged() {
        if (project != null) {
            tvTag.setText(project.getTag());
            tvName.setText(project.getName());
        }
        updateTabTitles();
    }

    @Override
    public void selectTab(int index) {
        viewPager.setCurrentItem(index, true);
    }

    @Override
    public void showTaskDialog(TaskItem taskOrNull) {
        boolean isEdit = (taskOrNull != null);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);

        EditText etDescription = dialogView.findViewById(R.id.et_task_description);
        TextView tvCharCounter = dialogView.findViewById(R.id.tv_char_counter);
        Spinner spStatus = dialogView.findViewById(R.id.sp_status);
        Spinner spPriority = dialogView.findViewById(R.id.sp_priority);
        TextView tvDueDate = dialogView.findViewById(R.id.tv_due_date);
        Button btnClearDate = dialogView.findViewById(R.id.btn_clear_date);

        etDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});

        String[] statuses = {TaskItem.STATUS_TODO, TaskItem.STATUS_IN_PROGRESS, TaskItem.STATUS_DONE};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        String[] priorities = {TaskItem.PRIORITY_HIGH, TaskItem.PRIORITY_NORMAL, TaskItem.PRIORITY_LOW};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(priorityAdapter);

        final long[] chosenDateMillis = {isEdit ? taskOrNull.getDueDateMillis() : -1};

        if (chosenDateMillis[0] > 0) {
            tvDueDate.setText(DateUtils.format(chosenDateMillis[0]));
        } else {
            tvDueDate.setText("No due date");
        }

        tvDueDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (chosenDateMillis[0] > 0) {
                cal.setTimeInMillis(chosenDateMillis[0]);
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(this, (view, selectedYear, selectedMonth, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(selectedYear, selectedMonth, dayOfMonth, 0, 0, 0);
                c.set(Calendar.MILLISECOND, 0);
                chosenDateMillis[0] = c.getTimeInMillis();
                tvDueDate.setText(DateUtils.format(chosenDateMillis[0]));
            }, year, month, day);
            picker.show();
        });

        btnClearDate.setOnClickListener(v -> {
            chosenDateMillis[0] = -1;
            tvDueDate.setText("No due date");
        });

        if (isEdit) {
            etDescription.setText(taskOrNull.getDescription());
            tvCharCounter.setText(taskOrNull.getDescription().length() + "/500");

            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(taskOrNull.getStatus())) {
                    spStatus.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equals(taskOrNull.getPriority())) {
                    spPriority.setSelection(i);
                    break;
                }
            }
        } else {
            tvCharCounter.setText("0/500");
            spStatus.setSelection(0);
            spPriority.setSelection(1);
        }

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s != null ? s.length() : 0;
                tvCharCounter.setText(len + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String title = isEdit ? "Edit Task" : "Add New Task";
        String positiveText = isEdit ? "Save" : "Add Task";

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(positiveText, null);

        if (isEdit) {
            builder.setNeutralButton("Delete", (d, w) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            project.removeTaskById(taskOrNull.getId());
                            onProjectChanged();
                            refreshVisibleFragments();
                        })
                        .show();
            });
        }

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
                if (TextUtils.isEmpty(desc)) {
                    etDescription.setError("Task description is required");
                    return;
                }
                if (desc.length() > 500) {
                    etDescription.setError("Description must be at most 500 characters");
                    return;
                }

                String status = spStatus.getSelectedItem().toString();
                String priority = spPriority.getSelectedItem().toString();
                long dueDate = chosenDateMillis[0];

                if (isEdit) {
                    taskOrNull.setDescription(desc);
                    taskOrNull.setStatus(status);
                    taskOrNull.setPriority(priority);
                    taskOrNull.setDueDateMillis(dueDate);
                } else {
                    if (project.getTasks() == null) {
                        project.setTasks(new ArrayList<>());
                    }
                    project.getTasks().add(new TaskItem(desc, status, priority, dueDate));
                }

                onProjectChanged();
                refreshVisibleFragments();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    @Override
    public void openTasksWithFilter(String status, String dateFilter) {
        taskFilterState.status = status;
        taskFilterState.date = dateFilter;
        taskFilterState.priority = "All priorities";
        taskFilterState.query = "";
        selectTab(1);
    }

    public void refreshVisibleFragments() {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof OverviewFragment) {
                ((OverviewFragment) f).refresh();
            } else if (f instanceof TasksFragment) {
                ((TasksFragment) f).refresh();
            } else if (f instanceof FilesFragment) {
                ((FilesFragment) f).refresh();
            }
        }
    }

    private void updateTabTitles() {
        if (tabLayout == null) {
            return;
        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                updateSingleTabTitle(tab, i);
            }
        }
    }

    private void updateSingleTabTitle(TabLayout.Tab tab, int position) {
        if (position == 0) {
            tab.setText("Overview");
        } else if (position == 1) {
            tab.setText(String.format(Locale.getDefault(), "Tasks (%d)", project.getTotalTasks()));
        } else if (position == 2) {
            tab.setText(String.format(Locale.getDefault(), "Files (%d)", project.getAttachmentCount()));
        } else if (position == 3) {
            tab.setText(String.format(Locale.getDefault(), "Discussion (%d)", project.getCommentCount()));
        }
    }

    private void returnUpdatedProject() {
        Intent data = new Intent();
        data.putExtra("key_project", project);
        setResult(RESULT_OK, data);
        finish();
    }

    private void init() {
        ivBack = findViewById(R.id.iv_back);
        tvTag = findViewById(R.id.tv_project_tag);
        tvName = findViewById(R.id.tv_project_name);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }
}
