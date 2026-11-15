package com.example.smd_assignment03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smd_assignment03.models.Project;

import java.util.Locale;
import java.util.Objects;

public class ProjectAdapter extends ListAdapter<Project, ProjectAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private final OnProjectClickListener listener;

    public ProjectAdapter(OnProjectClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public static final DiffUtil.ItemCallback<Project> DIFF_CALLBACK = new DiffUtil.ItemCallback<Project>() {
        @Override
        public boolean areItemsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return Objects.equals(oldItem.getTag(), newItem.getTag())
                    && Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDescription(), newItem.getDescription())
                    && oldItem.getDoneCount() == newItem.getDoneCount()
                    && oldItem.getTotalTasks() == newItem.getTotalTasks()
                    && oldItem.getProgressPercent() == newItem.getProgressPercent()
                    && oldItem.getAttachmentCount() == newItem.getAttachmentCount()
                    && oldItem.getCommentCount() == newItem.getCommentCount();
        }
    };

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = getItem(position);
        holder.bind(project, listener);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag;
        TextView tvName;
        TextView tvDescription;
        TextView tvTaskCount;
        TextView tvProgressPercent;
        ProgressBar pbProgress;
        TextView tvAttachmentCount;
        TextView tvCommentCount;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tv_project_tag);
            tvName = itemView.findViewById(R.id.tv_project_name);
            tvDescription = itemView.findViewById(R.id.tv_project_description);
            tvTaskCount = itemView.findViewById(R.id.tv_task_count);
            tvProgressPercent = itemView.findViewById(R.id.tv_progress_percent);
            pbProgress = itemView.findViewById(R.id.pb_project_progress);
            tvAttachmentCount = itemView.findViewById(R.id.tv_attachment_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        public void bind(Project project, OnProjectClickListener listener) {
            if (project == null)
            {
                return;
            }

            tvTag.setText(project.getTag());
            tvName.setText(project.getName());
            tvDescription.setText(project.getDescription());

            int done = project.getDoneCount();
            int total = project.getTotalTasks();
            tvTaskCount.setText(String.format(Locale.getDefault(), "%d/%d tasks", done, total));

            int percent = project.getProgressPercent();
            tvProgressPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
            pbProgress.setProgress(percent);

            tvAttachmentCount.setText(String.format(Locale.getDefault(), "%d Attachments", project.getAttachmentCount()));
            tvCommentCount.setText(String.format(Locale.getDefault(), "%d Comments", project.getCommentCount()));

            itemView.setOnClickListener(v -> {
                if (listener != null)
                {
                    listener.onProjectClick(project);
                }
            });
        }
    }
}
