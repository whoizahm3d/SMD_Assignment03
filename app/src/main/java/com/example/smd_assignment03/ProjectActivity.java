package com.example.smd_assignment03;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.IntentCompat;

import com.example.smd_assignment03.models.Project;

public class ProjectActivity extends AppCompatActivity {

    ImageView ivBack;
    TextView tvTag;
    TextView tvName;
    Project currentProject;

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

        currentProject = IntentCompat.getSerializableExtra(getIntent(), "key_project", Project.class);
        if (currentProject != null) {
            tvTag.setText(currentProject.getTag());
            tvName.setText(currentProject.getName());
        }

        ivBack.setOnClickListener((v) -> {
            returnBack();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnBack();
            }
        });
    }

    private void returnBack() {
        Intent data = new Intent();
        data.putExtra("key_project", currentProject);
        setResult(RESULT_OK, data);
        finish();
    }

    private void init() {
        ivBack = findViewById(R.id.iv_back);
        tvTag = findViewById(R.id.tv_project_tag);
        tvName = findViewById(R.id.tv_project_name);
    }
}
