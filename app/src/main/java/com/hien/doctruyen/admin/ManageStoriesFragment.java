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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hien.doctruyen.R;
import com.hien.doctruyen.admin_adapter.AdminStoryAdapter;
import com.hien.doctruyen.item.Story;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ManageStoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminStoryAdapter storyAdapter;
    private List<Story> storyList;
    private FloatingActionButton btnAddStory;
    private SearchView searchView;

    private static final String TAG = "ManageStoriesFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_stories, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_stories);
        btnAddStory = view.findViewById(R.id.btn_add_story);
        searchView = view.findViewById(R.id.search_view_stories);

        storyList = new ArrayList<>();
        storyAdapter = new AdminStoryAdapter(storyList, requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(storyAdapter);

        loadStoriesFromFirebase();

        btnAddStory.setOnClickListener(v -> openAddStoryDialog());
        setupSearchView();

        return view;
    }

    private void loadStoriesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories");

        dbRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Story story = snapshot.getValue(Story.class);
                        if (story != null) {
                            // Ghi log các thuộc tính để kiểm tra
                            Log.d(TAG, "Title: " + story.getTitle());
                            Log.d(TAG, "Author: " + story.getAuthor());
                            Log.d(TAG, "Genres: " + story.getGenres());
                            storyList.add(story);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing story: " + e.getMessage());
                    }
                }
                storyAdapter.updateList(storyList);
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
                storyAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                storyAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setIconifiedByDefault(false);
    }

    private void openAddStoryDialog() {
        Intent intent = new Intent(getActivity(), add_truyen.class);
        startActivity(intent);
    }
}
