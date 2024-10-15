package com.hien.doctruyen.user;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hien.doctruyen.R;
import com.hien.doctruyen.user_adapter.userStoryAdapter;
import com.hien.doctruyen.item.Story;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private userStoryAdapter storyAdapter;
    private List<Story> storyList;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storyList = new ArrayList<>();
        storyAdapter = new userStoryAdapter(storyList);
        recyclerView.setAdapter(storyAdapter);

        // Load stories from Firebase
        loadStoriesFromFirebase();

        // Setup search view
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStories(query); // Perform search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchStories(newText); // Filter as text changes
                return false;
            }
        });

        return view;
    }

    private void loadStoriesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    if (story != null) {
                        storyList.add(story);
                        Log.d(TAG, "Title: " + story.getTitle());
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void searchStories(String query) {
        List<Story> filteredList = new ArrayList<>();
        for (Story story : storyList) {
            if (story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(story);
            }
        }
        storyAdapter.updateList(filteredList);
    }
}
