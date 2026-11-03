package com.example.smd_assignment03;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FilesFragment extends Fragment {

    private static final String ARG_PROJECT_ID = "arg_project_id";
    private String projectId;

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
        TextView tv = new TextView(requireContext());
        tv.setText("Files");
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ProjectHost) {
            ProjectHost host = (ProjectHost) getActivity();
            host.getProject();
        }
    }
}
