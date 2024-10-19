package com.hien.doctruyen.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Chapter;

import androidx.appcompat.widget.Toolbar;

public class AddChapterActivity extends AppCompatActivity {

    private EditText edtChapterTitle, edtChapterContent;
    private Button btnAddChapter;
    private String storyUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Ánh xạ view
        edtChapterTitle = findViewById(R.id.edt_chapter_title);
        edtChapterContent = findViewById(R.id.edt_chapter_content);
        btnAddChapter = findViewById(R.id.btn_add_chapter);

        // Nhận storyUid từ Intent
        storyUid = getIntent().getStringExtra("story_uid");

        // Xử lý sự kiện nhấn nút thêm chương
        btnAddChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtChapterTitle.getText().toString().trim();
                String content = edtChapterContent.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(AddChapterActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    addChapterToFirebase(title, content);
                }
            }
        });
    }

    private void addChapterToFirebase(String title, String content) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories").child(storyUid).child("chapters");

        // Tạo uid mới cho chương
        String chapterId = dbRef.push().getKey();

        // Tạo đối tượng Chapter
        Chapter chapter = new Chapter(title, content, chapterId, storyUid);

        // Lưu chương vào Firebase
        if (chapterId != null) {
            dbRef.child(chapterId).setValue(chapter)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddChapterActivity.this, "Thêm chương thành công", Toast.LENGTH_SHORT).show();
                        finish();  // Đóng Activity sau khi thêm thành công
                    })
                    .addOnFailureListener(e -> Log.e("AddChapterActivity", "Error adding chapter", e));
        }
    }
}
