package com.hien.doctruyen.detail;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.R;

public class AdminStoryDetailActivity extends AppCompatActivity {

    private EditText titleEditText, authorEditText, descriptionEditText;
    private Button saveButton, deleteButton;
    private DatabaseReference storyRef;
    private String storyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        // Ánh xạ các view từ layout
        titleEditText = findViewById(R.id.edit_text_title);
        authorEditText = findViewById(R.id.edit_text_author);
        descriptionEditText = findViewById(R.id.edit_text_description);
        saveButton = findViewById(R.id.button_save);
        deleteButton = findViewById(R.id.button_delete);

        // Nhận dữ liệu từ Intent
        storyId = getIntent().getStringExtra("story_id");

        // Kiểm tra xem storyId có null không
        if (storyId == null) {
            Log.e("AdminStoryDetail", "Story ID is null");
            finish();  // Đóng Activity nếu không có ID
            return;
        }

        String storyTitle = getIntent().getStringExtra("story_title");
        String storyAuthor = getIntent().getStringExtra("story_author");
        String storyDescription = getIntent().getStringExtra("story_description");

        // Hiển thị dữ liệu truyện lên các EditText
        titleEditText.setText(storyTitle);
        authorEditText.setText(storyAuthor);
        descriptionEditText.setText(storyDescription);

        // Khởi tạo tham chiếu Firebase
        storyRef = FirebaseDatabase.getInstance().getReference("stories").child(storyId);

        // Xử lý sự kiện nút lưu và nút xóa
        saveButton.setOnClickListener(v -> {
            String updatedTitle = titleEditText.getText().toString();
            String updatedAuthor = authorEditText.getText().toString();
            String updatedDescription = descriptionEditText.getText().toString();

            storyRef.child("title").setValue(updatedTitle);
            storyRef.child("author").setValue(updatedAuthor);
            storyRef.child("description").setValue(updatedDescription);
        });

        deleteButton.setOnClickListener(v -> {
            storyRef.removeValue();
            finish();
        });
    }

}
