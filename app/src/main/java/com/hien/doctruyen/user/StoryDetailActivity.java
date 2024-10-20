package com.hien.doctruyen.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private DatabaseReference commentRef, chaptersRef;
    private Button btnComment;
    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail2);

        // Ánh xạ ImageView back
        ivBack = findViewById(R.id.iv_back);

        // Xử lý khi nhấn vào nút back
        ivBack.setOnClickListener(v -> {
            onBackPressed();  // Quay lại Activity trước đó
        });

        // Tiếp tục ánh xạ và xử lý các thành phần khác...
        ivCover = findViewById(R.id.iv_story_cover);
        tvTitle = findViewById(R.id.tv_story_title);
        tvAuthor = findViewById(R.id.tv_story_author);
        tvGenres = findViewById(R.id.tv_story_genres);
        tvDescription = findViewById(R.id.tv_story_description);
        rvChapters = findViewById(R.id.recycler_view_chapters);
        rvComments = findViewById(R.id.recycler_view_comments);
        btnComment = findViewById(R.id.btn_post_comment);
        etComment = findViewById(R.id.et_comment);


        // Lấy thông tin từ Intent
        Intent intent = getIntent();
        Story story = (Story) intent.getSerializableExtra("story");

        if (story != null) {
            // Hiển thị thông tin truyện
            tvTitle.setText(story.getTitle());
            tvAuthor.setText(story.getAuthor());
            tvGenres.setText(String.join(", ", story.getGenres()));
            tvDescription.setText(story.getDescription());

            // Hiển thị ảnh bìa
            Picasso.get().load(story.getImageUrl()).into(ivCover);

            // Khởi tạo danh sách bình luận
            commentList = new ArrayList<>();
            commentAdapter = new CommentAdapter(commentList, this);
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(commentAdapter);

            // Khởi tạo danh sách chapter
            chapterList = new ArrayList<>();
            chapterAdapter = new ChapterAdapter(chapterList, this);
            rvChapters.setLayoutManager(new LinearLayoutManager(this));
            rvChapters.setAdapter(chapterAdapter);

            // Lấy dữ liệu chapter từ Firebase
            chaptersRef = FirebaseDatabase.getInstance().getReference("stories").child(story.getId()).child("chapters");
            loadChapters();

            // Lấy bình luận từ Firebase
            commentRef = FirebaseDatabase.getInstance().getReference("comments").child(story.getId());
            loadComments();

            // Xử lý khi nhấn nút đăng bình luận
            btnComment.setOnClickListener(view -> {
                String commentText = etComment.getText().toString();
                if (!commentText.isEmpty()) {
                    postComment(commentText);
                    etComment.setText("");
                }
            });
        }
    }

    private void loadChapters() {
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
        String userId = comment.getUserId();  // Giả sử bạn có userId trong Comment

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


    private void postComment(String content) {
        String commentId = commentRef.push().getKey();
        Comment comment = new Comment("username_placeholder", content, "avatar_url_placeholder");  // Thay thế bằng dữ liệu thực
        if (commentId != null) {
            commentRef.child(commentId).setValue(comment);
        }
    }
}
