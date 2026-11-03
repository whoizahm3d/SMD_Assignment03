package com.example.smd_assignment03;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProjectPagerAdapter extends FragmentStateAdapter {

    private String projectId;

    public ProjectPagerAdapter(@NonNull FragmentActivity fragmentActivity, String projectId) {
        super(fragmentActivity);
        this.projectId = projectId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
        {
            return OverviewFragment.newInstance(projectId);
        }
        else if (position == 1)
        {
            return TasksFragment.newInstance(projectId);
        }
        else if (position == 2)
        {
            return FilesFragment.newInstance(projectId);
        }
        else
        {
            return DiscussionFragment.newInstance(projectId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
