package com.example.smd_assignment03;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.IntentCompat;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.Project;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    ArrayList<Project> allProjects;
    ArrayList<Project> shownProjects;
    ProjectAdapter adapter;
    ListView lvProjects;
    EditText etSearchProjects;
    TextView tvShowingCount;
    TextView tvEmptyProjects;
    Button btnNewProject;
    ActivityResultLauncher<Intent> launcherProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int basePadding = (int) (16 * getResources().getDisplayMetrics().density);
            v.setPadding(basePadding + systemBars.left, basePadding + systemBars.top, basePadding + systemBars.right, basePadding + systemBars.bottom);
            return insets;
        });

        init();

        allProjects = SampleData.createProjects();
        shownProjects = new ArrayList<>(allProjects);

        adapter = new ProjectAdapter(this, shownProjects);
        lvProjects.setAdapter(adapter);

        applySearch();

        etSearchProjects.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applySearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lvProjects.setOnItemClickListener((parent, view, position, id) -> {
            Project selectedProject = shownProjects.get(position);
            Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
            intent.putExtra("key_project", selectedProject);
            launcherProject.launch(intent);
        });

        launcherProject = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Project updated = IntentCompat.getSerializableExtra(result.getData(), "key_project", Project.class);
                        if (updated != null) {
                            for (int i = 0; i < allProjects.size(); i++) {
                                if (allProjects.get(i).getId().equals(updated.getId())) {
                                    allProjects.set(i, updated);
                                    break;
                                }
                            }
                            applySearch();
                        }
                    }
                }
        );

        btnNewProject.setOnClickListener((v) -> {
            ProjectFormDialog.show(HomeActivity.this, null, (name, tag, description, picked) -> {
                ArrayList<AttachmentLink> attachments = new ArrayList<>();
                if (picked != null)
                {
                    attachments.add(new AttachmentLink(picked.getId(), picked.getResId()));
                }
                Project newProject = new Project(
                        tag,
                        name,
                        description,
                        new ArrayList<>(),
                        attachments,
                        0
                );
                allProjects.add(newProject);
                applySearch();
            });
        });
    }

    private void applySearch() {
        String query = "";
        if (etSearchProjects != null && etSearchProjects.getText() != null)
        {
            query = etSearchProjects.getText().toString().trim().toLowerCase(Locale.getDefault());
        }

        shownProjects.clear();
        if (query.isEmpty())
        {
            shownProjects.addAll(allProjects);
        }
        else
        {
            for (Project p : allProjects) {
                boolean nameMatch = p.getName() != null && p.getName().toLowerCase(Locale.getDefault()).contains(query);
                boolean tagMatch = p.getTag() != null && p.getTag().toLowerCase(Locale.getDefault()).contains(query);
                if (nameMatch || tagMatch)
                {
                    shownProjects.add(p);
                }
            }
        }

        adapter.notifyDataSetChanged();

        if (tvShowingCount != null)
        {
            tvShowingCount.setText(String.format(Locale.getDefault(), "Showing %d projects", shownProjects.size()));
        }

        if (tvEmptyProjects != null)
        {
            if (shownProjects.isEmpty())
            {
                tvEmptyProjects.setVisibility(View.VISIBLE);
            }
            else
            {
                tvEmptyProjects.setVisibility(View.GONE);
            }
        }
    }

    private void init() {
        lvProjects = findViewById(R.id.lv_projects);
        etSearchProjects = findViewById(R.id.et_search_projects);
        tvShowingCount = findViewById(R.id.tv_showing_count);
        tvEmptyProjects = findViewById(R.id.tv_empty_projects);
        btnNewProject = findViewById(R.id.btn_new_project);
    }
}
