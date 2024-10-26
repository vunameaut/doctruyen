package com.hien.doctruyen.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.R;
import com.hien.doctruyen.admin_adapter.ChapterAdapter;
import com.hien.doctruyen.custom.SpaceItemDecoration;
import com.hien.doctruyen.item.Chapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;

public class ChapterListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChapters;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private ImageView imgAddChapter;  // ImageView để thêm chương mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewChapters = findViewById(R.id.recycler_view_chapters);
        recyclerViewChapters.setLayoutManager(new LinearLayoutManager(this));

        // Thêm khoảng cách giữa các item
        int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        recyclerViewChapters.addItemDecoration(new SpaceItemDecoration(spacing));

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(chapterList, this);
        recyclerViewChapters.setAdapter(chapterAdapter);

        String storyUid = getIntent().getStringExtra("story_uid");
        Log.d("ChapterListActivity", "Story UID: " + storyUid);

        loadChaptersFromFirebase(storyUid);

        chapterAdapter.setOnItemClickListener((position) -> {
            Chapter selectedChapter = chapterList.get(position);
            Intent intent = new Intent(ChapterListActivity.this, EditChapterActivity.class);
            intent.putExtra("chapter_id", selectedChapter.getUid());
            intent.putExtra("story_id", storyUid);
            intent.putExtra("chapter_title", selectedChapter.getTitle());
            intent.putExtra("chapter_content", selectedChapter.getContent());
            startActivity(intent);
        });

        imgAddChapter = findViewById(R.id.img_add_chapter);
        imgAddChapter.setOnClickListener(v -> {
            Intent intent = new Intent(ChapterListActivity.this, AddChapterActivity.class);
            intent.putExtra("story_uid", storyUid);
            startActivity(intent);
        });
    }


    private void loadChaptersFromFirebase(String storyUid) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories").child(storyUid).child("chapters");

        dbRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear();
                for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapter.setUid(chapterSnapshot.getKey());
                        chapterList.add(chapter);
                    }
                }
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChapterListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
