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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;
import com.example.smd_assignment03.models.Project;

import java.util.ArrayList;
import java.util.Locale;

public class FilesFragment extends Fragment implements AttachmentAdapter.AttachmentActionListener {

    private static final String ARG_PROJECT_ID = "arg_project_id";
    private String projectId;

    EditText etSearchFiles;
    TextView tvChipAll;
    TextView tvChipImages;
    TextView tvChipText;
    TextView tvShowing;
    RecyclerView rvFiles;
    LinearLayout layoutEmptyNone;
    LinearLayout layoutEmptyFiltered;
    Button btnAddAttachment;
    Button btnEmptyAdd;
    Button btnEmptyReset;

    AttachmentAdapter adapter;
    ArrayList<AttachmentLink> shownLinks = new ArrayList<>();
    boolean isUpdatingText = false;

    public FilesFragment() {
    }

    public static FilesFragment newInstance(String projectId) {
        FilesFragment fragment = new FilesFragment();
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
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        adapter = new AttachmentAdapter(this);
        rvFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFiles.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return makeMovementFlags(0, 0);
                }
                return makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                AttachmentLink link = adapter.getItemAt(position);
                if (link == null) {
                    adapter.notifyItemChanged(position);
                    return;
                }

                if (direction == ItemTouchHelper.RIGHT) {
                    showDeleteAttachmentDialog(link, position);
                } else {
                    adapter.notifyItemChanged(position);
                }
            }
        };

        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(rvFiles);

        refresh();

        etSearchFiles.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingText) {
                    return;
                }
                ProjectHost host = (ProjectHost) requireActivity();
                FileFilterState state = host.getFileFilterState();
                state.query = s != null ? s.toString().trim() : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvChipAll.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.getFileFilterState().type = "All";
            updateChipStyles();
            applyFilters();
        });

        tvChipImages.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.getFileFilterState().type = "Images";
            updateChipStyles();
            applyFilters();
        });

        tvChipText.setOnClickListener(v -> {
            ProjectHost host = (ProjectHost) requireActivity();
            host.getFileFilterState().type = "Text";
            updateChipStyles();
            applyFilters();
        });

        btnAddAttachment.setOnClickListener(v -> showAddAttachmentDialog());
        btnEmptyAdd.setOnClickListener(v -> showAddAttachmentDialog());
        btnEmptyReset.setOnClickListener(v -> resetFilters());
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
            FileFilterState state = host.getFileFilterState();

            isUpdatingText = true;
            if (etSearchFiles != null) {
                etSearchFiles.setText(state.query);
            }
            isUpdatingText = false;

            updateChipStyles();
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
            FileFilterState state = host.getFileFilterState();

            shownLinks.clear();
            if (project != null && project.getAttachments() != null) {
                String query = state.query != null ? state.query.toLowerCase(Locale.getDefault()) : "";

                for (AttachmentLink link : project.getAttachments()) {
                    CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
                    if (item == null) {
                        continue;
                    }

                    boolean matchesQuery = query.isEmpty() || item.getDisplayName().toLowerCase(Locale.getDefault()).contains(query);
                    boolean matchesType = true;
                    if ("Images".equals(state.type)) {
                        matchesType = "IMAGE".equals(item.getType());
                    } else if ("Text".equals(state.type)) {
                        matchesType = "TEXT".equals(item.getType());
                    }

                    if (matchesQuery && matchesType) {
                        shownLinks.add(link);
                    }
                }
            }

            if (adapter != null) {
                adapter.submitList(new ArrayList<>(shownLinks));
            }

            int totalFiles = project != null ? project.getAttachmentCount() : 0;
            int shownCount = shownLinks.size();

            if (tvShowing != null) {
                String showingText = String.format(Locale.getDefault(), "Showing %d of %d attachments", shownCount, totalFiles);
                SpannableString span = new SpannableString(showingText);
                String xStr = String.valueOf(shownCount);
                int start = showingText.indexOf(xStr);
                if (start >= 0) {
                    span.setSpan(new StyleSpan(Typeface.BOLD), start, start + xStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvShowing.setText(span);
            }

            if (project != null) {
                tvChipAll.setText(String.format(Locale.getDefault(), "All (%d)", project.getAttachmentCount()));
                tvChipImages.setText(String.format(Locale.getDefault(), "Images (%d)", project.getImageCount()));
                tvChipText.setText(String.format(Locale.getDefault(), "Text (%d)", project.getTextCount()));
            }

            if (totalFiles == 0) {
                rvFiles.setVisibility(View.GONE);
                layoutEmptyNone.setVisibility(View.VISIBLE);
                layoutEmptyFiltered.setVisibility(View.GONE);
            } else if (shownCount == 0) {
                rvFiles.setVisibility(View.GONE);
                layoutEmptyNone.setVisibility(View.GONE);
                layoutEmptyFiltered.setVisibility(View.VISIBLE);
            } else {
                rvFiles.setVisibility(View.VISIBLE);
                layoutEmptyNone.setVisibility(View.GONE);
                layoutEmptyFiltered.setVisibility(View.GONE);
            }
        }
    }

    private void updateChipStyles() {
        if (getActivity() instanceof ProjectHost) {
            ProjectHost host = (ProjectHost) getActivity();
            FileFilterState state = host.getFileFilterState();

            String type = state.type != null ? state.type : "All";

            setChipSelected(tvChipAll, "All".equals(type));
            setChipSelected(tvChipImages, "Images".equals(type));
            setChipSelected(tvChipText, "Text".equals(type));
        }
    }

    private void setChipSelected(TextView chip, boolean selected) {
        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip_unselected);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
        }
    }

    private void resetFilters() {
        ProjectHost host = (ProjectHost) getActivity();
        FileFilterState state = host.getFileFilterState();
        state.reset();

        isUpdatingText = true;
        if (etSearchFiles != null) {
            etSearchFiles.setText("");
        }
        isUpdatingText = false;

        updateChipStyles();
        applyFilters();
    }

    private void showAddAttachmentDialog() {
        ProjectHost host = (ProjectHost) getActivity();
        if (host == null) {
            return;
        }
        Project project = host.getProject();
        if (project == null) {
            return;
        }

        ArrayList<CatalogItem> unlinked = new ArrayList<>();
        for (CatalogItem item : AttachmentCatalog.getItems()) {
            if (!project.isCatalogItemLinked(item.getId())) {
                unlinked.add(item);
            }
        }

        if (unlinked.isEmpty()) {
            Toast.makeText(requireContext(), "All catalog files are already linked", Toast.LENGTH_SHORT).show();
            return;
        }

        CatalogPickerDialog.show(requireContext(), unlinked, item -> {
            project.getAttachments().add(new AttachmentLink(item.getId(), item.getResId()));
            host.onProjectChanged();
            refresh();
            host.refreshVisibleFragments();
        });
    }

    @Override
    public void onPreview(AttachmentLink link) {
        AttachmentPreviewDialog.show(requireContext(), link);
    }

    @Override
    public void onDelete(AttachmentLink link) {
        showDeleteAttachmentDialog(link, RecyclerView.NO_POSITION);
    }

    private void showDeleteAttachmentDialog(AttachmentLink link, int position) {
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
        String displayName = item != null ? item.getDisplayName() : "";

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_attachment, null);
        TextView tvMessage = dialogView.findViewById(R.id.tv_delete_message);
        EditText etConfirm = dialogView.findViewById(R.id.et_confirm_name);

        tvMessage.setText(String.format(Locale.getDefault(), "Type %s to confirm", displayName));
        etConfirm.setHint(displayName);

        final boolean[] actionTaken = {false};

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Delete attachment")
                .setView(dialogView)
                .setNegativeButton("Cancel", (d, w) -> {
                    actionTaken[0] = true;
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.notifyItemChanged(position);
                    }
                })
                .setPositiveButton("Delete", (d, w) -> {
                    actionTaken[0] = true;
                    String typed = etConfirm.getText() != null ? etConfirm.getText().toString().trim() : "";
                    if (typed.equals(displayName)) {
                        ProjectHost host = (ProjectHost) getActivity();
                        if (host != null) {
                            Project project = host.getProject();
                            if (project != null) {
                                project.removeAttachmentById(link.getId());
                                host.onProjectChanged();
                                refresh();
                                host.refreshVisibleFragments();
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Wrong attachment name", Toast.LENGTH_SHORT).show();
                        if (position != RecyclerView.NO_POSITION) {
                            adapter.notifyItemChanged(position);
                        }
                    }
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setEnabled(false);

            etConfirm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String typed = s != null ? s.toString().trim() : "";
                    positiveBtn.setEnabled(typed.equals(displayName));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        });

        dialog.setOnDismissListener(d -> {
            if (!actionTaken[0] && position != RecyclerView.NO_POSITION) {
                adapter.notifyItemChanged(position);
            }
        });

        dialog.show();
    }

    private void init(View view) {
        etSearchFiles = view.findViewById(R.id.et_search_files);
        tvChipAll = view.findViewById(R.id.tv_chip_all);
        tvChipImages = view.findViewById(R.id.tv_chip_images);
        tvChipText = view.findViewById(R.id.tv_chip_text);
        tvShowing = view.findViewById(R.id.tv_showing);
        rvFiles = view.findViewById(R.id.rv_files);
        layoutEmptyNone = view.findViewById(R.id.layout_empty_none);
        layoutEmptyFiltered = view.findViewById(R.id.layout_empty_filtered);
        btnAddAttachment = view.findViewById(R.id.btn_add_attachment);
        btnEmptyAdd = view.findViewById(R.id.btn_empty_add);
        btnEmptyReset = view.findViewById(R.id.btn_empty_reset);
    }
}
