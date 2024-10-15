package com.hien.doctruyen.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar; // Thêm import cho ProgressBar
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Story;
import com.hien.doctruyen.item.Chapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_truyen extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTitle, editAuthor, editDescription;
    private ImageView imageCover;
    private LinearLayout linearGenres;
    private Button btnSelectImage;
    private ImageButton btnBack;
    private androidx.appcompat.widget.AppCompatButton btnSaveStory;
    private Uri imageUri;
    private ProgressBar progressBar; // Thêm ProgressBar

    private DatabaseReference dbRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truyen);

        // Ánh xạ các view
        editTitle = findViewById(R.id.edit_title);
        editAuthor = findViewById(R.id.edit_author);
        editDescription = findViewById(R.id.edit_description);
        imageCover = findViewById(R.id.image_cover);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnBack = findViewById(R.id.btn_back);
        btnSaveStory = findViewById(R.id.btn_save_story);
        linearGenres = findViewById(R.id.linear_genres);
        progressBar = findViewById(R.id.progressBar); // Khởi tạo ProgressBar

        // Tham chiếu tới Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("stories");
        storageRef = FirebaseStorage.getInstance().getReference("cover_images");

        // Sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        // Sự kiện lưu truyện
        btnSaveStory.setOnClickListener(v -> saveStory());

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageCover.setImageURI(imageUri);
        }
    }

    private void saveStory() {
        try {
            String title = editTitle.getText().toString().trim();
            String author = editAuthor.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            // Lấy danh sách thể loại đã chọn từ các CheckBox
            List<String> selectedGenres = new ArrayList<>();
            for (int i = 0; i < linearGenres.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) linearGenres.getChildAt(i);
                if (checkBox.isChecked()) {
                    selectedGenres.add(checkBox.getText().toString());
                }
            }

            // Kiểm tra thông tin không được trống
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(description) || imageUri == null) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
                Log.e("SaveStory", "Thiếu thông tin hoặc ảnh không được chọn.");
                return;
            }

            String storyId = dbRef.push().getKey();
            StorageReference fileRef = storageRef.child(storyId + ".jpg");

            progressBar.setVisibility(android.view.View.VISIBLE); // Hiện ProgressBar

            fileRef.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        Map<String, Chapter> chapters = new HashMap<>();

                        Story newStory = new Story(
                                storyId,
                                title,
                                author,
                                description,
                                selectedGenres,
                                imageUrl,
                                chapters,
                                storyId
                        );

                        dbRef.child(storyId).setValue(newStory).addOnCompleteListener(dbTask -> {
                            progressBar.setVisibility(android.view.View.GONE); // Ẩn ProgressBar
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(this, "Thêm truyện thành công", Toast.LENGTH_SHORT).show();
                                Log.d("SaveStory", "Truyện được lưu thành công với ID: " + storyId);
                                finish();
                            } else {
                                String errorMsg = dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown error";
                                Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                                Log.e("SaveStory", "Lỗi khi lưu truyện: " + errorMsg);
                            }
                        });
                    });
                } else {
                    progressBar.setVisibility(android.view.View.GONE); // Ẩn ProgressBar
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(this, "Lỗi tải ảnh: " + errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("SaveStory", "Lỗi khi tải ảnh: " + errorMsg);
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE); // Ẩn ProgressBar
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("SaveStory", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
