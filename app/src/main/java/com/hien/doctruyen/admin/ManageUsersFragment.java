package com.hien.doctruyen.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;  // Đảm bảo rằng bạn dùng androidx.fragment.app.Fragment
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hien.doctruyen.R;

public class ManageUsersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_users, container, false);
    }
}