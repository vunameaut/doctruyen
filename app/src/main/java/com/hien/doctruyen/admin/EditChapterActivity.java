package com.hien.doctruyen.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Chapter;

import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

public class EditChapterActivity extends AppCompatActivity {

    private EditText edtTitle, edtContent;
    private Button btnSave, btnDelete;
    private String chapterId, storyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        // Thiết lập Toolbar và nút quay lại
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Ánh xạ các EditText và Button
        edtTitle = findViewById(R.id.edt_chapter_title);
        edtContent = findViewById(R.id.edt_chapter_content);
        btnSave = findViewById(R.id.btn_save_chapter);
        btnDelete = findViewById(R.id.btn_delete_chapter);

        // Lấy dữ liệu từ Intent
        chapterId = getIntent().getStringExtra("chapter_id");
        storyId = getIntent().getStringExtra("story_id");
        edtTitle.setText(getIntent().getStringExtra("chapter_title"));
        edtContent.setText(getIntent().getStringExtra("chapter_content"));

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> updateChapter());

        // Xử lý nút Xóa
        btnDelete.setOnClickListener(v -> deleteChapter());
    }

    // Xử lý nút back trong Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Quay lại khi nhấn nút back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateChapter() {
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();

        if (chapterId == null || storyId == null) {
            Toast.makeText(EditChapterActivity.this, "ID chương hoặc ID truyện không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("stories")
                .child(storyId)
                .child("chapters")
                .child(chapterId);

        Chapter updatedChapter = new Chapter(title, content, chapterId, storyId);

        dbRef.setValue(updatedChapter)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditChapterActivity.this, "Cập nhật chương thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e("EditChapterActivity", "Error updating chapter", e));
    }

    private void deleteChapter() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("stories")
                .child(storyId)
                .child("chapters")
                .child(chapterId);

        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditChapterActivity.this, "Xóa chương thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e("EditChapterActivity", "Error deleting chapter", e));
    }
}
