package com.hien.doctruyen.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.User;
import com.squareup.picasso.Picasso;
import com.hien.doctruyen.item.Chapter;
import com.hien.doctruyen.item.Comment;
import com.hien.doctruyen.item.Story;
import com.hien.doctruyen.user_adapter.ChapterAdapter;
import com.hien.doctruyen.user_adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoryDetailActivity extends AppCompatActivity {

    private ImageView ivBack, ivCover;
    private TextView tvTitle, tvAuthor, tvGenres, tvDescription;
    private RecyclerView rvChapters, rvComments;
    private CommentAdapter commentAdapter;
    private ChapterAdapter chapterAdapter;
    private List<Comment> commentList;
    private List<Chapter> chapterList;
    private DatabaseReference commentRef, chaptersRef, userFavoritesRef, readingProgressRef;
    private Button btnComment;
    private EditText etComment;
    private ImageButton btnFavorite;

    private Story story;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail2);

        // Ánh xạ các thành phần giao diện
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_story_cover);
        tvTitle = findViewById(R.id.tv_story_title);
        tvAuthor = findViewById(R.id.tv_story_author);
        tvGenres = findViewById(R.id.tv_story_genres);
        tvDescription = findViewById(R.id.tv_story_description);
        rvChapters = findViewById(R.id.recycler_view_chapters);
        rvComments = findViewById(R.id.recycler_view_comments);
        btnComment = findViewById(R.id.btn_post_comment);
        etComment = findViewById(R.id.et_comment);
        btnFavorite = findViewById(R.id.btn_favorite);

        // Lấy thông tin story từ Intent và gán vào biến instance story
        Intent intent = getIntent();
        story = (Story) intent.getSerializableExtra("story");

        if (story != null) {
            // Hiển thị thông tin truyện
            tvTitle.setText(story.getTitle());
            tvAuthor.setText(story.getAuthor());
            tvGenres.setText(String.join(", ", story.getGenres()));
            tvDescription.setText(story.getDescription());
            Picasso.get().load(story.getImageUrl()).into(ivCover);

            // Lấy reference đến mục yêu thích của người dùng
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userFavoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
            readingProgressRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reading_progress").child(story.getId());

            checkIfFavorite();

            // Xử lý khi người dùng nhấn nút yêu thích
            btnFavorite.setOnClickListener(v -> {
                if (isFavorite) {
                    removeFavorite();
                } else {
                    addFavorite();
                }
            });

            // Khởi tạo danh sách chapter và comment
            commentList = new ArrayList<>();
            chapterList = new ArrayList<>();
            commentAdapter = new CommentAdapter(commentList, this);
            chapterAdapter = new ChapterAdapter(chapterList, this);
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(commentAdapter);
            rvChapters.setLayoutManager(new LinearLayoutManager(this));
            rvChapters.setAdapter(chapterAdapter);

            // Lấy dữ liệu từ Firebase
            chaptersRef = FirebaseDatabase.getInstance().getReference("stories").child(story.getId()).child("chapters");
            commentRef = FirebaseDatabase.getInstance().getReference("comments").child(story.getId());

            // Truy vấn chapter đang đọc
            loadReadingProgress();
            loadComments();

            // Xử lý khi nhấn nút đăng bình luận
            btnComment.setOnClickListener(view -> {
                String commentText = etComment.getText().toString();
                if (!commentText.isEmpty()) {
                    postComment(commentText);
                    etComment.setText("");
                }
            });

            // Xử lý nút back
            ivBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void addFavorite() {
        userFavoritesRef.child(story.getId()).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.ic_favorite_checked);  // Icon đậm
            }
        });
    }

    private void removeFavorite() {
        userFavoritesRef.child(story.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.ic_favorite_unchecked);  // Icon viền
            }
        });
    }

    private void checkIfFavorite() {
        userFavoritesRef.child(story.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavorite = snapshot.exists();
                if (isFavorite) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_checked);  // Icon đậm
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_unchecked);  // Icon viền
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StoryDetailActivity", "Error checking favorite status: " + error.getMessage());
            }
        });
    }

    private void postComment(String content) {
        // Lấy userId từ phiên người dùng hiện tại (đăng nhập)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tạo đối tượng Comment với userId và storyId từ biến story
        String commentId = commentRef.push().getKey();
        Comment comment = new Comment(commentId, story.getId(), userId, content, System.currentTimeMillis());
        if (commentId != null) {
            commentRef.child(commentId).setValue(comment);
        }
    }

    private void loadReadingProgress() {
        readingProgressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long lastChapterRead = dataSnapshot.child("last_chapter_read").getValue(Long.class);
                if (lastChapterRead != null) {
                    openLastReadChapter(lastChapterRead.intValue());
                } else {
                    loadChapterList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StoryDetailActivity", "Error loading reading progress: " + databaseError.getMessage());
                loadChapterList();
            }
        });
    }

    private void openLastReadChapter(int lastChapterIndex) {
        if (lastChapterIndex >= 0 && lastChapterIndex < chapterList.size()) {
            Chapter lastReadChapter = chapterList.get(lastChapterIndex);

            Intent intent = new Intent(StoryDetailActivity.this, ChapterDetailActivity.class);
            intent.putExtra("chapter", lastReadChapter);
            intent.putExtra("story", story);
            intent.putExtra("chapterIndex", lastChapterIndex);  // Truyền chỉ số của chương qua Intent
            startActivity(intent);
        } else {
            loadChapterList();  // Nếu không tìm thấy chapter đang đọc, load danh sách chapter
        }
    }

    private void loadChapterList() {
        chaptersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = snapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter);
                    }
                }
                chapterAdapter.notifyDataSetChanged();

                // Xử lý khi người dùng nhấn vào chapter
                chapterAdapter.setOnItemClickListener((chapter, position) -> {
                    Intent intent = new Intent(StoryDetailActivity.this, ChapterDetailActivity.class);
                    intent.putExtra("chapter", chapter);
                    intent.putExtra("story", story);
                    intent.putExtra("chapterIndex", position);
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StoryDetailActivity", "Error loading chapters: " + databaseError.getMessage());
            }
        });
    }

    private void loadComments() {
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        loadCommentUserData(comment);
                        commentList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StoryDetailActivity", "Error loading comments: " + databaseError.getMessage());
            }
        });
    }

    private void loadCommentUserData(Comment comment) {
        String userId = comment.getUserId();

        if (userId != null && !userId.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        comment.setUsername(user.getUsername());
                        comment.setAvatarUrl(user.getAvatar());
                        commentAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("StoryDetailActivity", "Error loading user data: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("StoryDetailActivity", "User ID is null or empty");
        }
    }
}
