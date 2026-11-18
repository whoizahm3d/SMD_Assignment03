package com.example.smd_assignment03;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smd_assignment03.models.Project;
import com.example.smd_assignment03.models.TaskItem;
import com.example.smd_assignment03.models.TaskListItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class TasksFragment extends Fragment implements TaskAdapter.TaskActionListener {

    private static final String ARG_PROJECT_ID = "arg_project_id";
    private String projectId;

    EditText etSearchTasks;
    TextView tvFilterStatus;
    TextView tvFilterPriority;
    TextView tvFilterDate;
    TextView tvFilterSort;
    TextView tvShowing;
    TextView tvResetFilters;
    RecyclerView rvTasks;
    LinearLayout layoutEmptyNone;
    LinearLayout layoutEmptyFiltered;
    Button btnAddTask;
    Button btnEmptyAddTask;
    Button btnEmptyResetFilters;

    TaskAdapter adapter;
    ArrayList<TaskItem> shownTasks = new ArrayList<>();
    boolean isUpdatingText = false;

    public TasksFragment() {
    }

    public static TasksFragment newInstance(String projectId) {
        TasksFragment fragment = new TasksFragment();
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
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        adapter = new TaskAdapter(requireContext(), this);
        rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTasks.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            private boolean isDragging = false;

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int sourcePos = viewHolder.getAbsoluteAdapterPosition();
                int targetPos = target.getAbsoluteAdapterPosition();
                if (sourcePos == RecyclerView.NO_POSITION || targetPos == RecyclerView.NO_POSITION) {
                    return false;
                }
                if (!adapter.isTaskRow(sourcePos) || !adapter.isTaskRow(targetPos)) {
                    return false;
                }

                TaskListItem sourceItem = adapter.getItemAt(sourcePos);
                TaskListItem targetItem = adapter.getItemAt(targetPos);
                if (sourceItem == null || targetItem == null || sourceItem.getTaskItem() == null || targetItem.getTaskItem() == null) {
                    return false;
                }

                String sourceGroup = TaskGroupingHelper.getTaskGroup(sourceItem.getTaskItem());
                String targetGroup = TaskGroupingHelper.getTaskGroup(targetItem.getTaskItem());
                if (!Objects.equals(sourceGroup, targetGroup)) {
                    return false; // Do not allow crossing groups
                }

                List<TaskListItem> currentList = new ArrayList<>(adapter.getCurrentList());
                if (sourcePos < 0 || sourcePos >= currentList.size() || targetPos < 0 || targetPos >= currentList.size()) {
                    return false;
                }

                TaskListItem moved = currentList.remove(sourcePos);
                currentList.add(targetPos, moved);
                adapter.submitList(currentList);
                adapter.notifyItemMoved(sourcePos, targetPos);
                return true;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    isDragging = true;
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (isDragging) {
                    isDragging = false;
                    saveTaskOrderToProject();
                    applyFilters();
                }
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION || !adapter.isTaskRow(position)) {
                    return makeMovementFlags(0, 0); // Headers return no drag or swipe flags
                }
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION || !adapter.isTaskRow(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                TaskListItem item = adapter.getItemAt(position);
                if (item == null || item.getType() != TaskListItem.TYPE_TASK) {
                    adapter.notifyItemChanged(position);
                    return;
                }
                TaskItem task = item.getTaskItem();
                if (task == null) {
                    adapter.notifyItemChanged(position);
                    return;
                }

                if (direction == ItemTouchHelper.LEFT) {
                    if (task.isDone()) {
                        Toast.makeText(requireContext(), "Task already completed", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position);
                        return;
                    }

                    task.setStatus(TaskItem.STATUS_DONE);
                    if (getActivity() instanceof ProjectHost) {
                        ProjectHost host = (ProjectHost) getActivity();
                        host.onProjectChanged();
                        host.refreshVisibleFragments();
                    }
                    applyFilters();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    final boolean[] actionTaken = {false};

                    AlertDialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("Delete task")
                            .setMessage("Are you sure you want to delete this task?")
                            .setNegativeButton("Cancel", (d, w) -> {
                                actionTaken[0] = true;
                                adapter.notifyItemChanged(position);
                            })
                            .setPositiveButton("Delete", (d, w) -> {
                                actionTaken[0] = true;
                                if (getActivity() instanceof ProjectHost) {
                                    ProjectHost host = (ProjectHost) getActivity();
                                    Project project = host.getProject();
                                    if (project != null) {
                                        project.removeTaskById(task.getId());
                                        host.onProjectChanged();
                                        host.refreshVisibleFragments();
                                        applyFilters();
                                    }
                                }
                            })
                            .create();

                    dialog.setOnDismissListener(d -> {
                        if (!actionTaken[0]) {
                            adapter.notifyItemChanged(position);
                        }
                    });

                    dialog.show();
                }
            }
        };

        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(rvTasks);

        refresh();

        etSearchTasks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingText) {
                    return;
                }
                ProjectHost host = (ProjectHost) requireActivity();
                TaskFilterState state = host.getTaskFilterState();
                state.query = s != null ? s.toString().trim() : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvFilterStatus.setOnClickListener(v -> showStatusDialog());
        tvFilterPriority.setOnClickListener(v -> showPriorityDialog());
        tvFilterDate.setOnClickListener(v -> showDateDialog());
        tvFilterSort.setOnClickListener(v -> showSortDialog());

        tvResetFilters.setOnClickListener(v -> resetAllFilters());
        btnEmptyResetFilters.setOnClickListener(v -> resetAllFilters());

        btnAddTask.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.showTaskDialog(null);
        });

        btnEmptyAddTask.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.showTaskDialog(null);
        });
    }

    private void saveTaskOrderToProject() {
        if (getActivity() instanceof ProjectHost) {
            ProjectHost host = (ProjectHost) getActivity();
            Project project = host.getProject();
            if (project == null || project.getTasks() == null) {
                return;
            }

            List<TaskItem> adapterTasks = new ArrayList<>();
            for (TaskListItem item : adapter.getCurrentList()) {
                if (item.getType() == TaskListItem.TYPE_TASK && item.getTaskItem() != null) {
                    adapterTasks.add(item.getTaskItem());
                }
            }

            if (adapterTasks.isEmpty()) {
                return;
            }

            Set<String> adapterTaskIds = new HashSet<>();
            for (TaskItem t : adapterTasks) {
                adapterTaskIds.add(t.getId());
            }

            ArrayList<TaskItem> newProjectTasks = new ArrayList<>();
            int adapterIndex = 0;
            for (TaskItem originalTask : project.getTasks()) {
                if (adapterTaskIds.contains(originalTask.getId())) {
                    while (adapterIndex < adapterTasks.size()) {
                        TaskItem nextAdapterTask = adapterTasks.get(adapterIndex++);
                        newProjectTasks.add(nextAdapterTask);
                        break;
                    }
                } else {
                    newProjectTasks.add(originalTask);
                }
            }
            while (adapterIndex < adapterTasks.size()) {
                newProjectTasks.add(adapterTasks.get(adapterIndex++));
            }

            project.setTasks(newProjectTasks);
            host.onProjectChanged();
        }
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
            TaskFilterState state = host.getTaskFilterState();

            isUpdatingText = true;
            if (etSearchTasks != null) {
                etSearchTasks.setText(state.query);
            }
            isUpdatingText = false;

            updatePillLabels();
            applyFilters();
        }
    }

    public void applyFilters() {
        if (!isAdded()) {
            return;
        }
        if (getActivity() instanceof ProjectHost) {
            ProjectHost host = (ProjectHost) getActivity();
            Project project = host.getProject();
            TaskFilterState state = host.getTaskFilterState();

            shownTasks.clear();
            if (project != null && project.getTasks() != null) {
                String query = state.query != null ? state.query.toLowerCase(Locale.getDefault()) : "";

                for (TaskItem t : project.getTasks()) {
                    boolean matchesQuery = query.isEmpty() || (t.getDescription() != null && t.getDescription().toLowerCase(Locale.getDefault()).contains(query));
                    boolean matchesStatus = "All statuses".equals(state.status) || (t.getStatus() != null && t.getStatus().equals(state.status));
                    boolean matchesPriority = "All priorities".equals(state.priority) || (t.getPriority() != null && t.getPriority().equals(state.priority));
                    boolean matchesDate = true;
                    if ("Today".equals(state.date)) {
                        matchesDate = t.isDueToday();
                    } else if ("Overdue".equals(state.date)) {
                        matchesDate = t.isOverdue();
                    } else if ("No due date".equals(state.date)) {
                        matchesDate = !t.hasDueDate();
                    }

                    if (matchesQuery && matchesStatus && matchesPriority && matchesDate) {
                        shownTasks.add(t);
                    }
                }

                // Sorting
                shownTasks.sort((t1, t2) -> {
                    String sort = state.sort;
                    if ("Due date (earliest first)".equals(sort)) {
                        long d1 = t1.hasDueDate() ? t1.getDueDateMillis() : Long.MAX_VALUE;
                        long d2 = t2.hasDueDate() ? t2.getDueDateMillis() : Long.MAX_VALUE;
                        int cmp = Long.compare(d1, d2);
                        if (cmp != 0) return cmp;
                        return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                    } else if ("Due date (latest first)".equals(sort)) {
                        long d1 = t1.hasDueDate() ? t1.getDueDateMillis() : -1L;
                        long d2 = t2.hasDueDate() ? t2.getDueDateMillis() : -1L;
                        if (!t1.hasDueDate() && !t2.hasDueDate()) {
                            return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                        }
                        if (!t1.hasDueDate()) return 1;
                        if (!t2.hasDueDate()) return -1;
                        int cmp = Long.compare(d2, d1);
                        if (cmp != 0) return cmp;
                        return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                    } else if ("Priority (High to Low)".equals(sort)) {
                        int p1 = getPriorityRank(t1.getPriority());
                        int p2 = getPriorityRank(t2.getPriority());
                        int cmp = Integer.compare(p1, p2);
                        if (cmp != 0) return cmp;
                        return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                    } else { // "Newest first"
                        return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                    }
                });
            }

            List<TaskListItem> groupedItems = TaskGroupingHelper.groupTasks(shownTasks);
            if (adapter != null) {
                adapter.submitList(groupedItems);
            }

            int totalTasks = project != null ? project.getTotalTasks() : 0;
            int shownCount = shownTasks.size();

            if (tvShowing != null) {
                String showingText = String.format(Locale.getDefault(), "Showing %d of %d tasks", shownCount, totalTasks);
                SpannableString span = new SpannableString(showingText);
                String xStr = String.valueOf(shownCount);
                int start = showingText.indexOf(xStr);
                if (start >= 0) {
                    span.setSpan(new StyleSpan(Typeface.BOLD), start, start + xStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvShowing.setText(span);
            }

            // Empty states handling
            if (totalTasks == 0) {
                rvTasks.setVisibility(View.GONE);
                layoutEmptyNone.setVisibility(View.VISIBLE);
                layoutEmptyFiltered.setVisibility(View.GONE);
            } else if (shownCount == 0) {
                rvTasks.setVisibility(View.GONE);
                layoutEmptyNone.setVisibility(View.GONE);
                layoutEmptyFiltered.setVisibility(View.VISIBLE);
            } else {
                rvTasks.setVisibility(View.VISIBLE);
                layoutEmptyNone.setVisibility(View.GONE);
                layoutEmptyFiltered.setVisibility(View.GONE);
            }
        }
    }

    private int getPriorityRank(String priority) {
        if ("High".equals(priority)) return 0;
        if ("Normal".equals(priority)) return 1;
        if ("Low".equals(priority)) return 2;
        return 1;
    }

    private void updatePillLabels() {
        if (getActivity() instanceof ProjectHost) {
            ProjectHost host = (ProjectHost) getActivity();
            TaskFilterState state = host.getTaskFilterState();

            tvFilterStatus.setText("All statuses".equals(state.status) ? "Status: All" : "Status: " + state.status);
            tvFilterPriority.setText("All priorities".equals(state.priority) ? "Priority: All" : "Priority: " + state.priority);
            tvFilterDate.setText("All dates".equals(state.date) ? "Date: All" : "Date: " + state.date);
            tvFilterSort.setText("Due date (earliest first)".equals(state.sort) ? "Sort: Due date" : "Sort: " + state.sort);
        }
    }

    private void resetAllFilters() {
        ProjectHost host = (ProjectHost) getActivity();
        TaskFilterState state = host.getTaskFilterState();
        state.reset();

        isUpdatingText = true;
        if (etSearchTasks != null) {
            etSearchTasks.setText("");
        }
        isUpdatingText = false;

        updatePillLabels();
        applyFilters();
    }

    private void showStatusDialog() {
        ProjectHost host = (ProjectHost) getActivity();
        TaskFilterState state = host.getTaskFilterState();
        String[] options = {"All statuses", "To do", "In progress", "Done"};
        int selected = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(state.status)) {
                selected = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Filter Status")
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    state.status = options[which];
                    updatePillLabels();
                    applyFilters();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPriorityDialog() {
        ProjectHost host = (ProjectHost) getActivity();
        TaskFilterState state = host.getTaskFilterState();
        String[] options = {"All priorities", "High", "Normal", "Low"};
        int selected = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(state.priority)) {
                selected = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Filter Priority")
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    state.priority = options[which];
                    updatePillLabels();
                    applyFilters();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDateDialog() {
        ProjectHost host = (ProjectHost) getActivity();
        TaskFilterState state = host.getTaskFilterState();
        String[] options = {"All dates", "Today", "Overdue", "No due date"};
        int selected = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(state.date)) {
                selected = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Filter Date")
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    state.date = options[which];
                    updatePillLabels();
                    applyFilters();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSortDialog() {
        ProjectHost host = (ProjectHost) getActivity();
        TaskFilterState state = host.getTaskFilterState();
        String[] options = {"Due date (earliest first)", "Due date (latest first)", "Priority (High to Low)", "Newest first"};
        int selected = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(state.sort)) {
                selected = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Sort Tasks")
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    state.sort = options[which];
                    updatePillLabels();
                    applyFilters();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onTaskClicked(TaskItem task) {
        ProjectHost host = (ProjectHost) requireActivity();
        host.showTaskDialog(task);
    }

    @Override
    public void onTaskToggled(TaskItem task, boolean checked) {
        task.setStatus(checked ? TaskItem.STATUS_DONE : TaskItem.STATUS_TODO);
        ProjectHost host = (ProjectHost) requireActivity();
        host.onProjectChanged();
        if (rvTasks != null) {
            rvTasks.post(this::applyFilters);
        }
    }

    private void init(View view) {
        etSearchTasks = view.findViewById(R.id.et_search_tasks);
        tvFilterStatus = view.findViewById(R.id.tv_filter_status);
        tvFilterPriority = view.findViewById(R.id.tv_filter_priority);
        tvFilterDate = view.findViewById(R.id.tv_filter_date);
        tvFilterSort = view.findViewById(R.id.tv_filter_sort);
        tvShowing = view.findViewById(R.id.tv_showing);
        tvResetFilters = view.findViewById(R.id.tv_reset_filters);
        rvTasks = view.findViewById(R.id.rv_tasks);
        layoutEmptyNone = view.findViewById(R.id.layout_empty_none);
        layoutEmptyFiltered = view.findViewById(R.id.layout_empty_filtered);
        btnAddTask = view.findViewById(R.id.btn_add_task);
        btnEmptyAddTask = view.findViewById(R.id.btn_empty_add_task);
        btnEmptyResetFilters = view.findViewById(R.id.btn_empty_reset_filters);
    }
}
