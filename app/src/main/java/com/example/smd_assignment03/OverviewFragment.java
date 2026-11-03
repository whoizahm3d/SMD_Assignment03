package com.example.smd_assignment03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;
import com.example.smd_assignment03.models.Project;

import java.util.ArrayList;
import java.util.Locale;

public class OverviewFragment extends Fragment {

    private static final String ARG_PROJECT_ID = "arg_project_id";
    private String projectId;

    TextView tvEditProject;
    TextView tvBrief;
    TextView tvProgressPercent;
    TextView tvProgressText;
    ProgressBar pbProgress;
    LinearLayout cardTodo;
    LinearLayout cardInProgress;
    LinearLayout cardDone;
    LinearLayout cardOverdue;
    TextView tvCountTodo;
    TextView tvCountInProgress;
    TextView tvCountDone;
    TextView tvCountOverdue;
    RelativeLayout cardAttachments;
    TextView tvAttachmentsSubtitle;
    RelativeLayout cardDiscussion;
    Button btnAddTask;

    public OverviewFragment() {
    }

    public static OverviewFragment newInstance(String projectId) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getString(ARG_PROJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        refresh();

        tvEditProject.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            Project project = host.getProject();
            if (project != null) {
                ProjectFormDialog.show(requireContext(), project, (name, tag, description, picked) -> {
                    project.setName(name);
                    project.setTag(tag);
                    project.setDescription(description);
                    host.onProjectChanged();
                    refresh();
                });
            }
        });

        cardTodo.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.openTasksWithFilter("To do", "All dates");
        });

        cardInProgress.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.openTasksWithFilter("In progress", "All dates");
        });

        cardDone.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.openTasksWithFilter("Done", "All dates");
        });

        cardOverdue.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.openTasksWithFilter("All statuses", "Overdue");
        });

        cardAttachments.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.selectTab(2);
        });

        cardDiscussion.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.selectTab(3);
        });

        btnAddTask.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.showTaskDialog(null);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        if (!isAdded()) {
            return;
        }
        if (getActivity() instanceof ProjectHost) {
            ProjectHost host = (ProjectHost) getActivity();
            Project project = host.getProject();
            if (project != null) {
                tvBrief.setText(project.getDescription());

                int percent = project.getProgressPercent();
                tvProgressPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
                tvProgressText.setText(String.format(Locale.getDefault(), "%d of %d tasks completed", project.getDoneCount(), project.getTotalTasks()));
                pbProgress.setProgress(percent);

                tvCountTodo.setText(String.valueOf(project.getTodoCount()));
                tvCountInProgress.setText(String.valueOf(project.getInProgressCount()));
                tvCountDone.setText(String.valueOf(project.getDoneCount()));
                tvCountOverdue.setText(String.valueOf(project.getOverdueCount()));

                ArrayList<AttachmentLink> links = project.getAttachments();
                if (links == null || links.isEmpty()) {
                    tvAttachmentsSubtitle.setText("No files");
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(links.size()).append(links.size() == 1 ? " file" : " files");
                    int displayCount = Math.min(2, links.size());
                    for (int i = 0; i < displayCount; i++) {
                        if (i == 0) {
                            sb.append(" - ");
                        } else {
                            sb.append(", ");
                        }
                        CatalogItem item = AttachmentCatalog.findById(links.get(i).getCatalogId());
                        if (item != null) {
                            sb.append(item.getDisplayName());
                        }
                    }
                    tvAttachmentsSubtitle.setText(sb.toString());
                }
            }
        }
    }

    private void init(View view) {
        tvEditProject = view.findViewById(R.id.tv_edit_project);
        tvBrief = view.findViewById(R.id.tv_brief);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvProgressText = view.findViewById(R.id.tv_progress_text);
        pbProgress = view.findViewById(R.id.pb_progress);
        cardTodo = view.findViewById(R.id.card_todo);
        cardInProgress = view.findViewById(R.id.card_in_progress);
        cardDone = view.findViewById(R.id.card_done);
        cardOverdue = view.findViewById(R.id.card_overdue);
        tvCountTodo = view.findViewById(R.id.tv_count_todo);
        tvCountInProgress = view.findViewById(R.id.tv_count_in_progress);
        tvCountDone = view.findViewById(R.id.tv_count_done);
        tvCountOverdue = view.findViewById(R.id.tv_count_overdue);
        cardAttachments = view.findViewById(R.id.card_attachments);
        tvAttachmentsSubtitle = view.findViewById(R.id.tv_attachments_subtitle);
        cardDiscussion = view.findViewById(R.id.card_discussion);
        btnAddTask = view.findViewById(R.id.btn_add_task);
    }
}
