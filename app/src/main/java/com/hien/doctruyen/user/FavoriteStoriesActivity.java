package com.hien.doctruyen.user;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Chapter;
import com.hien.doctruyen.item.FavoriteStory;
import com.hien.doctruyen.item.Story;
import com.hien.doctruyen.user_adapter.FavoriteStoryAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteStoriesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteStoryAdapter favoriteStoryAdapter;
    private List<FavoriteStory> favoriteStoryList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_stories);  // Cập nhật file layout nếu cần

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerViewFavoriteStories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteStoryList = new ArrayList<>();
        favoriteStoryAdapter = new FavoriteStoryAdapter(favoriteStoryList, this);
        recyclerView.setAdapter(favoriteStoryAdapter);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid).child("favorites");
            loadFavoriteStories();
        }
    }

    private void loadFavoriteStories() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteStoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String storyId = snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference("stories").child(storyId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot storySnapshot) {
                                    if (storySnapshot.exists()) {
                                        String title = storySnapshot.child("title").getValue(String.class);
                                        String author = storySnapshot.child("author").getValue(String.class);
                                        String description = storySnapshot.child("description").getValue(String.class);
                                        String imageUrl = storySnapshot.child("imageUrl").getValue(String.class);
                                        String uid = storySnapshot.child("uid").getValue(String.class);

                                        List<String> genres = new ArrayList<>();
                                        Object genresData = storySnapshot.child("genres").getValue();
                                        if (genresData instanceof List) {
                                            genres = (List<String>) genresData;
                                        } else if (genresData instanceof String) {
                                            genres.add((String) genresData);
                                        }

                                        Map<String, Chapter> chapters = new HashMap<>();
                                        for (DataSnapshot chapterSnapshot : storySnapshot.child("chapters").getChildren()) {
                                            Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                                            chapters.put(chapterSnapshot.getKey(), chapter);
                                        }

                                        Story story = new Story(storyId, title, author, description, genres, imageUrl, chapters, uid);
                                        FavoriteStory favoriteStory = new FavoriteStory(storyId, title, imageUrl, 0L, (long) chapters.size(), story);

                                        favoriteStoryList.add(favoriteStory);
                                        favoriteStoryAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý lỗi truy vấn
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi truy vấn
            }
        });
    }
}
