package com.example.smd_assignment03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.smd_assignment03.models.Project;

import java.util.ArrayList;
import java.util.Locale;

public class ProjectAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Project> projects;
    private LayoutInflater inflater;

    public ProjectAdapter(Context context, ArrayList<Project> projects) {
        this.context = context;
        this.projects = projects;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return projects == null ? 0 : projects.size();
    }

    @Override
    public Object getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_project, parent, false);
            holder = new ViewHolder();
            holder.tvTag = convertView.findViewById(R.id.tv_project_tag);
            holder.tvName = convertView.findViewById(R.id.tv_project_name);
            holder.tvDescription = convertView.findViewById(R.id.tv_project_description);
            holder.tvTaskCount = convertView.findViewById(R.id.tv_task_count);
            holder.tvProgressPercent = convertView.findViewById(R.id.tv_progress_percent);
            holder.pbProgress = convertView.findViewById(R.id.pb_project_progress);
            holder.tvAttachmentCount = convertView.findViewById(R.id.tv_attachment_count);
            holder.tvCommentCount = convertView.findViewById(R.id.tv_comment_count);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Project project = projects.get(position);

        holder.tvTag.setText(project.getTag());
        holder.tvName.setText(project.getName());
        holder.tvDescription.setText(project.getDescription());

        int done = project.getDoneCount();
        int total = project.getTotalTasks();
        holder.tvTaskCount.setText(String.format(Locale.getDefault(), "%d/%d tasks", done, total));

        int percent = project.getProgressPercent();
        holder.tvProgressPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
        holder.pbProgress.setProgress(percent);

        holder.tvAttachmentCount.setText(String.format(Locale.getDefault(), "%d Attachments", project.getAttachmentCount()));
        holder.tvCommentCount.setText(String.format(Locale.getDefault(), "%d Comments", project.getCommentCount()));

        return convertView;
    }

    static class ViewHolder {
        TextView tvTag;
        TextView tvName;
        TextView tvDescription;
        TextView tvTaskCount;
        TextView tvProgressPercent;
        ProgressBar pbProgress;
        TextView tvAttachmentCount;
        TextView tvCommentCount;
    }
}
