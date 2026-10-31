package com.example.smd_assignment_2;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import android.os.Bundle;

import com.example.smd_assignment_2.models.Project;
import com.example.smd_assignment_2.models.TaskItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ImageView ivSettings;
    private LinearLayout llUpcomingContainer;
    private Button btnNewProject;
    private Button btnInvite;
    private ImageButton btnNavHome, btnNavSearch, btnNavAdd, btnNavNotifications, btnNavProfile;

    private final ArrayList<Project> upcomingProjects = new ArrayList<>();

    // Attachment drawables available for the "New Project" picker.
    private final int[] pickableAttachments = {
            R.drawable.img_attachment_1,
            R.drawable.img_attachment_2,
            R.drawable.img_attachment_3
    };

    private final ActivityResultLauncher<Intent> projectLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Project updated = IntentCompat.getSerializableExtra(result.getData(), "extra_project", Project.class);
                    int index = result.getData().getIntExtra("extra_project_index", -1);
                    if (updated != null) {
                        if (index >= 0 && index < upcomingProjects.size()) {
                            upcomingProjects.set(index, updated);
                        } else {
                            upcomingProjects.add(updated);
                        }
                        renderProjectList();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ivSettings = findViewById(R.id.iv_settings);
        llUpcomingContainer = findViewById(R.id.ll_upcoming_container);
        btnNewProject = findViewById(R.id.btn_new_project);
        btnInvite = findViewById(R.id.btn_invite);
        btnNavHome = findViewById(R.id.btn_nav_home);
        btnNavSearch = findViewById(R.id.btn_nav_search);
        btnNavAdd = findViewById(R.id.btn_nav_add);
        btnNavNotifications = findViewById(R.id.btn_nav_notifications);
        btnNavProfile = findViewById(R.id.btn_nav_profile);

        seedDefaultProjects();
        renderProjectList();

        ivSettings.setOnClickListener(v -> showSettingsDialog());
        btnNewProject.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateProjectActivity.class);
            projectLauncher.launch(intent);
        });
        btnInvite.setOnClickListener(v -> showInviteDialog());

        btnNavSearch.setOnClickListener(v -> showSearchDialog());
        btnNavAdd.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateProjectActivity.class);
            projectLauncher.launch(intent);
        });
        btnNavNotifications.setOnClickListener(v -> showNotificationsDialog());
        btnNavProfile.setOnClickListener(v -> showProfileDialog());
        // Home is the active tab already; no navigation needed on tap.
        btnNavHome.setOnClickListener(v -> { /* already on Home */ });
    }

    private void seedDefaultProjects() {
        if (!upcomingProjects.isEmpty()) return;

        ArrayList<TaskItem> tasks1 = new ArrayList<>();
        tasks1.add(new TaskItem("t1", "Create layout guidelines for website design", true));
        tasks1.add(new TaskItem("t2", "Create low fidelity service page website", false));

        ArrayList<Integer> attachments1 = new ArrayList<>();
        attachments1.add(R.drawable.img_attachment_1);
        attachments1.add(R.drawable.img_attachment_2);

        Project project1 = new Project(
                "p1",
                "Base Interface",
                "Service Page Website",
                "Make a page display about services for the company website in lines and provide some price information each template card",
                attachments1,
                12,
                tasks1
        );

        ArrayList<Integer> attachments2 = new ArrayList<>();
        attachments2.add(R.drawable.img_attachment_2);
        attachments2.add(R.drawable.img_attachment_3);
        ArrayList<TaskItem> tasks2 = new ArrayList<>();
        tasks2.add(new TaskItem("t5", "Sketch illustration concepts", false));
        tasks2.add(new TaskItem("t6", "Finalize illustration style guide", false));
        Project project2 = new Project(
                "p2",
                "Illustration",
                "Mobile App Illustrations",
                "Create a set of custom illustrations for onboarding and empty states.",
                attachments2,
                1,
                tasks2
        );

        ArrayList<Integer> attachments3 = new ArrayList<>();
        attachments3.add(R.drawable.img_attachment_3);
        attachments3.add(R.drawable.img_attachment_1);
        ArrayList<TaskItem> tasks3 = new ArrayList<>();
        tasks3.add(new TaskItem("t7", "Draft landing page copy", false));
        tasks3.add(new TaskItem("t8", "Design hero banner", false));
        tasks3.add(new TaskItem("t9", "Set up analytics tracking", false));
        Project project3 = new Project(
                "p3",
                "Web Design",
                "Marketing Landing Page",
                "Build a marketing landing page to promote the new product launch.",
                attachments3,
                5,
                tasks3
        );

        upcomingProjects.add(project1);
        upcomingProjects.add(project2);
        upcomingProjects.add(project3);
    }

    private void renderProjectList() {
        llUpcomingContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < upcomingProjects.size(); i++) {
            Project project = upcomingProjects.get(i);
            View card = inflater.inflate(R.layout.item_project, llUpcomingContainer, false);
            bindProjectCard(card, project, i);
            llUpcomingContainer.addView(card);
        }
    }

    private void bindProjectCard(View card, Project project, int index) {
        TextView tvTag = card.findViewById(R.id.tv_project_tag);
        TextView tvName = card.findViewById(R.id.tv_project_name);
        TextView tvDescription = card.findViewById(R.id.tv_project_description);
        ImageView ivAttachment1 = card.findViewById(R.id.iv_attachment_1);
        ImageView ivAttachment2 = card.findViewById(R.id.iv_attachment_2);
        TextView tvAttachmentMore = card.findViewById(R.id.tv_attachment_more);
        TextView tvTaskCount = card.findViewById(R.id.tv_task_count);
        TextView tvCommentCount = card.findViewById(R.id.tv_comment_count);

        tvTag.setText(project.getTag());
        if ("UI/UX".equalsIgnoreCase(project.getTag())) {
            tvTag.setBackgroundResource(R.drawable.bg_chip_green);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.chip_green_text));
        } else if ("Base Interface".equalsIgnoreCase(project.getTag())) {
            tvTag.setBackgroundResource(R.drawable.bg_chip_blue);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.chip_blue_text));
        } else {
            tvTag.setBackgroundResource(R.drawable.bg_chip_grey);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.chip_grey_text));
        }

        tvName.setText(project.getName());
        tvDescription.setText(project.getDescription());
        tvTaskCount.setText(project.getTaskCountLabel());
        tvCommentCount.setText(String.valueOf(project.getCommentCount()));

        TextView tvDueDate = card.findViewById(R.id.tv_due_date);
        if (tvDueDate != null) {
            tvDueDate.setText("Due " + project.getDueDate());
        }

        ProgressBar pbProgress = card.findViewById(R.id.pb_project_progress);
        TextView tvProgressPercent = card.findViewById(R.id.tv_progress_percent);
        int percentage = project.getProgressPercentage();
        if (pbProgress != null) {
            pbProgress.setProgress(percentage);
        }
        if (tvProgressPercent != null) {
            tvProgressPercent.setText(percentage + "%");
        }

        // Long press to remove a project completely feature
        card.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete '" + project.getName() + "'?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    upcomingProjects.remove(index);
                    renderProjectList();
                    Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show();
                })
                .show();
            return true;
        });

        ArrayList<Integer> attachments = project.getAttachmentResIds();
        int attachmentCount = attachments == null ? 0 : attachments.size();

        if (attachmentCount > 0) {
            ivAttachment1.setVisibility(View.VISIBLE);
            ivAttachment1.setImageResource(attachments.get(0));
        } else {
            ivAttachment1.setVisibility(View.GONE);
        }

        if (attachmentCount > 1) {
            ivAttachment2.setVisibility(View.VISIBLE);
            ivAttachment2.setImageResource(attachments.get(1));
        } else {
            ivAttachment2.setVisibility(View.GONE);
        }

        if (attachmentCount > 2) {
            tvAttachmentMore.setVisibility(View.VISIBLE);
            tvAttachmentMore.setText("+" + (attachmentCount - 2));
        } else {
            tvAttachmentMore.setVisibility(View.GONE);
        }

        card.setOnClickListener(v -> openProjectScreen(project, index));
    }

    private void openProjectScreen(Project project, int index) {
        Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
        intent.putExtra("extra_project", project);
        intent.putExtra("extra_project_index", index);
        projectLauncher.launch(intent);
    }

    private void showAddProjectDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_project, null);

        EditText etTag = dialogView.findViewById(R.id.et_project_tag);
        EditText etName = dialogView.findViewById(R.id.et_project_name);
        EditText etDescription = dialogView.findViewById(R.id.et_project_description);
        ImageView ivPick1 = dialogView.findViewById(R.id.iv_pick_att_1);
        ImageView ivPick2 = dialogView.findViewById(R.id.iv_pick_att_2);
        ImageView ivPick3 = dialogView.findViewById(R.id.iv_pick_att_3);

        final int[] selectedAttachmentIndex = {0};
        ImageView[] pickers = {ivPick1, ivPick2, ivPick3};

        View.OnClickListener pickListener = v -> {
            for (int i = 0; i < pickers.length; i++) {
                if (pickers[i] == v) {
                    selectedAttachmentIndex[0] = i;
                    pickers[i].setBackgroundResource(R.drawable.bg_pick_attachment_selected);
                } else {
                    pickers[i].setBackgroundResource(R.drawable.bg_attachment);
                }
            }
        };
        ivPick1.setOnClickListener(pickListener);
        ivPick2.setOnClickListener(pickListener);
        ivPick3.setOnClickListener(pickListener);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_new_project_title)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add, null) // set below so we can validate before dismissing
                .create();

        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String name = etName.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        etName.setError("Project name is required");
                        return;
                    }

                    String tag = etTag.getText().toString().trim();
                    if (TextUtils.isEmpty(tag)) {
                        tag = getString(R.string.default_tag);
                    }

                    String description = etDescription.getText().toString().trim();

                    ArrayList<Integer> attachments = new ArrayList<>();
                    attachments.add(pickableAttachments[selectedAttachmentIndex[0]]);

                    Project newProject = new Project(
                            "p" + System.currentTimeMillis(),
                            tag,
                            name,
                            description,
                            attachments,
                            0,
                            new ArrayList<>()
                    );

                    upcomingProjects.add(newProject);
                    renderProjectList();
                    dialog.dismiss();
                }));

        dialog.show();
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Settings")
                .setMessage("TaskFlow App\nVersion: 1.0\nTheme: Day/Night Auto\nNotifications: Enabled")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showInviteDialog() {
        final EditText input = new EditText(this);
        input.setHint("colleague@example.com");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        
        new AlertDialog.Builder(this)
                .setTitle("Invite Team Member")
                .setMessage("Enter email to invite to workspace:")
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

    private void showSearchDialog() {
        final EditText input = new EditText(this);
        input.setHint("Search project name or tag...");
        
        new AlertDialog.Builder(this)
                .setTitle("Search Projects")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Search", (dialog, which) -> {
                    String query = input.getText().toString().trim().toLowerCase();
                    if (TextUtils.isEmpty(query)) return;
                    
                    int foundCount = 0;
                    for (Project p : upcomingProjects) {
                        if (p.getName().toLowerCase().contains(query) || p.getTag().toLowerCase().contains(query)) {
                            foundCount++;
                        }
                    }
                    Toast.makeText(this, "Found " + foundCount + " matching projects", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showNotificationsDialog() {
        StringBuilder sb = new StringBuilder();
        if (upcomingProjects.isEmpty()) {
            sb.append("No active notifications.");
        } else {
            sb.append("• Project '").append(upcomingProjects.get(0).getName()).append("' is due soon.\n");
            sb.append("• ").append(upcomingProjects.size()).append(" active projects in your workspace.\n");
            sb.append("• All task syncs up-to-date.");
        }
        new AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showProfileDialog() {
        new AlertDialog.Builder(this)
                .setTitle("User Profile")
                .setMessage("Name: Ali\nRole: Senior Android Developer\nWorkspace: TaskFlow Studio\nActive Projects: " + upcomingProjects.size())
                .setPositiveButton("Close", null)
                .show();
    }
}
