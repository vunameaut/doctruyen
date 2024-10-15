package com.hien.doctruyen.admin_adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hien.doctruyen.admin.ManageChaptersFragment;
import com.hien.doctruyen.admin.ManageStoriesFragment;
import com.hien.doctruyen.admin.ManageUsersFragment;

public class AdminPagerAdapter extends FragmentPagerAdapter {

    public AdminPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ManageStoriesFragment();
            case 1:
                return new ManageChaptersFragment();
            case 2:
                return new ManageUsersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Quản lý truyện";
            case 1:
                return "Quản lý chương";
            case 2:
                return "Quản lý tài khoản";
            default:
                return null;
        }
    }
}
