package com.example.smd_assignment03;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.smd_assignment03.models.TaskItem;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    public interface TaskActionListener {
        void onTaskClicked(TaskItem task);
        void onTaskToggled(TaskItem task, boolean checked);
    }

    private Context context;
    private ArrayList<TaskItem> shownTasks;
    private TaskActionListener listener;
    private LayoutInflater inflater;

    public TaskAdapter(Context context, ArrayList<TaskItem> shownTasks, TaskActionListener listener) {
        this.context = context;
        this.shownTasks = shownTasks;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shownTasks == null ? 0 : shownTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return shownTasks.get(position);
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
            convertView = inflater.inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();
            holder.cbTask = convertView.findViewById(R.id.cb_task);
            holder.tvTaskDescription = convertView.findViewById(R.id.tv_task_description);
            holder.tvTaskMeta = convertView.findViewById(R.id.tv_task_meta);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskItem task = shownTasks.get(position);

        holder.tvTaskDescription.setText(task.getDescription());

        // Format metadata: <status> • <priority> • <due text>
        String dueText;
        if (!task.hasDueDate())
        {
            dueText = "No due date";
        }
        else if (task.isDueToday())
        {
            dueText = "Due today";
        }
        else if (task.isOverdue())
        {
            dueText = DateUtils.format(task.getDueDateMillis()) + " (Overdue)";
        }
        else
        {
            dueText = DateUtils.format(task.getDueDateMillis());
        }

        String metaBase = task.getStatus() + " • " + task.getPriority() + " • ";
        SpannableString metaSpan = new SpannableString(metaBase + dueText);

        if (task.isOverdue())
        {
            int start = metaBase.length();
            int end = metaSpan.length();
            int redColor = ContextCompat.getColor(context, R.color.overdue_red);
            metaSpan.setSpan(new ForegroundColorSpan(redColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (task.isDueToday())
        {
            int start = metaBase.length();
            int end = metaSpan.length();
            int orangeColor = ContextCompat.getColor(context, R.color.due_today_orange);
            metaSpan.setSpan(new ForegroundColorSpan(orangeColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.tvTaskMeta.setText(metaSpan);

        // Checkbox rule: remove listener before setChecked
        holder.cbTask.setOnCheckedChangeListener(null);
        holder.cbTask.setChecked(task.isDone());

        if (task.isDone())
        {
            holder.tvTaskDescription.setPaintFlags(holder.tvTaskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTaskDescription.setTextColor(ContextCompat.getColor(context, R.color.text_muted));
            convertView.setAlpha(0.7f);
        }
        else
        {
            holder.tvTaskDescription.setPaintFlags(holder.tvTaskDescription.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTaskDescription.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            convertView.setAlpha(1.0f);
        }

        holder.cbTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null)
            {
                listener.onTaskToggled(task, isChecked);
            }
        });

        convertView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onTaskClicked(task);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        CheckBox cbTask;
        TextView tvTaskDescription;
        TextView tvTaskMeta;
    }
}
