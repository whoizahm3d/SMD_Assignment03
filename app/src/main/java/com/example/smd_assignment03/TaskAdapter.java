package com.example.smd_assignment03;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smd_assignment03.models.TaskItem;
import com.example.smd_assignment03.models.TaskListItem;

import java.util.Objects;

public class TaskAdapter extends ListAdapter<TaskListItem, RecyclerView.ViewHolder> {

    public interface TaskActionListener {
        void onTaskClicked(TaskItem task);
        void onTaskToggled(TaskItem task, boolean checked);
    }

    private final Context context;
    private final TaskActionListener listener;

    public TaskAdapter(Context context, TaskActionListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    public static final DiffUtil.ItemCallback<TaskListItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<TaskListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull TaskListItem oldItem, @NonNull TaskListItem newItem) {
            if (oldItem.getType() != newItem.getType()) {
                return false;
            }
            if (oldItem.getType() == TaskListItem.TYPE_HEADER) {
                return Objects.equals(oldItem.getHeaderTitle(), newItem.getHeaderTitle());
            } else {
                TaskItem t1 = oldItem.getTaskItem();
                TaskItem t2 = newItem.getTaskItem();
                if (t1 == null || t2 == null) return false;
                return Objects.equals(t1.getId(), t2.getId());
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskListItem oldItem, @NonNull TaskListItem newItem) {
            if (oldItem.getType() != newItem.getType()) {
                return false;
            }
            if (oldItem.getType() == TaskListItem.TYPE_HEADER) {
                return Objects.equals(oldItem.getHeaderTitle(), newItem.getHeaderTitle())
                        && oldItem.getHeaderCount() == newItem.getHeaderCount();
            } else {
                TaskItem t1 = oldItem.getTaskItem();
                TaskItem t2 = newItem.getTaskItem();
                if (t1 == null || t2 == null) return false;
                return Objects.equals(t1.getDescription(), t2.getDescription())
                        && Objects.equals(t1.getStatus(), t2.getStatus())
                        && Objects.equals(t1.getPriority(), t2.getPriority())
                        && t1.getDueDateMillis() == t2.getDueDateMillis()
                        && t1.getCreatedAt() == t2.getCreatedAt()
                        && t1.isDone() == t2.isDone();
            }
        }
    };

    @Override
    public int getItemViewType(int position) {
        TaskListItem item = getItem(position);
        return item != null ? item.getType() : TaskListItem.TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TaskListItem.TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_task_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskListItem item = getItem(position);
        if (item == null) return;

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).bind(item.getTaskItem(), context, listener);
        }
    }

    public boolean isTaskRow(int position) {
        if (position < 0 || position >= getItemCount()) {
            return false;
        }
        TaskListItem item = getItem(position);
        return item != null && item.getType() == TaskListItem.TYPE_TASK;
    }

    public TaskListItem getItemAt(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        return getItem(position);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderTitle;
        TextView tvHeaderCount;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderTitle = itemView.findViewById(R.id.tv_header_title);
            tvHeaderCount = itemView.findViewById(R.id.tv_header_count);
        }

        public void bind(TaskListItem item) {
            if (item == null) return;
            tvHeaderTitle.setText(item.getHeaderTitle());
            tvHeaderCount.setText(String.valueOf(item.getHeaderCount()));
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbTask;
        TextView tvTaskDescription;
        TextView tvTaskMeta;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTask = itemView.findViewById(R.id.cb_task);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            tvTaskMeta = itemView.findViewById(R.id.tv_task_meta);
        }

        public void bind(TaskItem task, Context context, TaskActionListener listener) {
            if (task == null) return;

            tvTaskDescription.setText(task.getDescription());

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

            tvTaskMeta.setText(metaSpan);

            // Checkbox rule: remove listener before setChecked
            cbTask.setOnCheckedChangeListener(null);
            cbTask.setChecked(task.isDone());

            if (task.isDone())
            {
                tvTaskDescription.setPaintFlags(tvTaskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskDescription.setTextColor(ContextCompat.getColor(context, R.color.text_muted));
                itemView.setAlpha(0.7f);
            }
            else
            {
                tvTaskDescription.setPaintFlags(tvTaskDescription.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskDescription.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                itemView.setAlpha(1.0f);
            }

            cbTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null)
                {
                    listener.onTaskToggled(task, isChecked);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null)
                {
                    listener.onTaskClicked(task);
                }
            });
        }
    }
}
