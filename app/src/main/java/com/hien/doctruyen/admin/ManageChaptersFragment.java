package com.hien.doctruyen.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hien.doctruyen.R;
import com.hien.doctruyen.admin_adapter.AdminChapterAdapter;
import com.hien.doctruyen.item.Chapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.item.Story;

import java.util.ArrayList;
import java.util.List;

public class ManageChaptersFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminChapterAdapter chapterAdapter;
    private List<Object> storyOrChapterList;  // Dùng Object để có thể chứa cả Story và Chapter
    private SearchView searchView;

    private static final String TAG = "ManageChaptersFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_chapters, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_chapters);
        searchView = view.findViewById(R.id.search_view_chapters);

        storyOrChapterList = new ArrayList<>();
        chapterAdapter = new AdminChapterAdapter(storyOrChapterList, requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chapterAdapter);

        loadStoriesFromFirebase();  // Load danh sách truyện ban đầu
        setupSearchView();

        return view;
    }

    private void loadStoriesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories");

        dbRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyOrChapterList.clear();
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    try {
                        Story story = storySnapshot.getValue(Story.class);  // Tạo object Story
                        if (story != null) {
                            Log.d(TAG, "Title: " + story.getTitle());
                            storyOrChapterList.add(story);  // Thêm Story vào danh sách
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing story: " + e.getMessage());
                    }
                }
                chapterAdapter.updateList(storyOrChapterList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void setupSearchView() {
        searchView.setQueryHint("Tìm kiếm truyện");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                chapterAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chapterAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setIconifiedByDefault(false);
    }
}
