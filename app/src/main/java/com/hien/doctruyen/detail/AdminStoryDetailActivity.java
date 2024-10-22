package com.hien.doctruyen.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog; // Thêm import cho ProgressDialog
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hien.doctruyen.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdminStoryDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText, authorEditText, genreEditText, descriptionEditText;
    private Button saveButton, deleteButton;
    private ImageView avatarImageView;
    private ImageButton btnBack;

    private DatabaseReference storyRef;
    private StorageReference storageRef;
    private String storyId;
    private Uri avatarUri;

    // Biến lưu trữ dữ liệu ban đầu
    private String initialTitle, initialAuthor, initialGenre, initialDescription;

    // Thêm ProgressDialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCancelable(false); // Không cho phép hủy khi đang load

        // Ánh xạ các view từ layout
        titleEditText = findViewById(R.id.edit_text_title);
        authorEditText = findViewById(R.id.edit_text_author);
        genreEditText = findViewById(R.id.edit_text_genre);
        descriptionEditText = findViewById(R.id.edit_text_description);
        avatarImageView = findViewById(R.id.avatar_image_view);
        btnBack = findViewById(R.id.btn_back);
        saveButton = findViewById(R.id.button_save);
        deleteButton = findViewById(R.id.button_delete);

        // Khởi tạo tham chiếu Firebase
        storyRef = FirebaseDatabase.getInstance().getReference("stories");
        storageRef = FirebaseStorage.getInstance().getReference("avatars");

        // Nhận dữ liệu từ Intent
        storyId = getIntent().getStringExtra("story_id");
        if (storyId == null) {
            Log.e("AdminStoryDetail", "Story ID is null");
            finish();
            return;
        }

        // Lấy thông tin câu chuyện từ Intent
        initialTitle = getIntent().getStringExtra("story_title");
        initialAuthor = getIntent().getStringExtra("story_author");
        initialGenre = getIntent().getStringExtra("story_genre");
        initialDescription = getIntent().getStringExtra("story_description");
        String storyImageUrl = getIntent().getStringExtra("story_image_url");

        // Hiển thị dữ liệu trên các trường
        titleEditText.setText(initialTitle);
        authorEditText.setText(initialAuthor);
        genreEditText.setText(initialGenre);
        descriptionEditText.setText(initialDescription);

        // Hiển thị ảnh bìa (avatar) bằng Picasso
        if (storyImageUrl != null && !storyImageUrl.isEmpty()) {
            Picasso.get().load(storyImageUrl).into(avatarImageView);
        }

        // Chọn ảnh mới từ bộ sưu tập
        avatarImageView.setOnClickListener(v -> openFileChooser());

        // Xử lý sự kiện lưu
        saveButton.setOnClickListener(v -> saveStoryDetails());

        // Xử lý sự kiện xóa
        deleteButton.setOnClickListener(v -> {
            storyRef.child(storyId).removeValue();
            finish();
        });

        // Xử lý sự kiện quay lại với hộp thoại xác nhận
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (isStoryModified()) {
            // Hiển thị hộp thoại xác nhận nếu có thay đổi
            showConfirmDialog();
        } else {
            super.onBackPressed();
        }
    }

    // Mở bộ sưu tập ảnh
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh bìa"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            avatarImageView.setImageURI(avatarUri);
        }
    }

    // Kiểm tra xem dữ liệu có thay đổi không
    private boolean isStoryModified() {
        return !titleEditText.getText().toString().equals(initialTitle) ||
                !authorEditText.getText().toString().equals(initialAuthor) ||
                !genreEditText.getText().toString().equals(initialGenre) ||
                !descriptionEditText.getText().toString().equals(initialDescription) ||
                avatarUri != null;
    }

    // Hiển thị hộp thoại xác nhận thoát
    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát mà không lưu?")
                .setMessage("Bạn có muốn lưu các thay đổi trước khi thoát?")
                .setPositiveButton("Lưu và thoát", (dialog, which) -> {
                    saveStoryDetails();
                    finish();
                })
                .setNegativeButton("Không lưu", (dialog, which) -> finish())
                .setNeutralButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Lưu thông tin truyện vào Firebase
    private void saveStoryDetails() {
        // Hiển thị vòng tròn xoay
        progressDialog.show();

        String updatedTitle = titleEditText.getText().toString();
        String updatedAuthor = authorEditText.getText().toString();
        String updatedGenre = genreEditText.getText().toString();
        String updatedDescription = descriptionEditText.getText().toString();

        // Cập nhật dữ liệu vào Firebase Database
        Map<String, Object> storyUpdates = new HashMap<>();
        storyUpdates.put("title", updatedTitle);
        storyUpdates.put("author", updatedAuthor);
        storyUpdates.put("genres", updatedGenre);
        storyUpdates.put("description", updatedDescription);

        if (avatarUri != null) {
            StorageReference fileRef = storageRef.child(storyId + ".jpg");
            fileRef.putFile(avatarUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    storyUpdates.put("imageUrl", uri.toString());
                    storyRef.child(storyId).updateChildren(storyUpdates)
                            .addOnCompleteListener(task -> onSaveComplete(task.isSuccessful()));
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi khi lấy URL ảnh!", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Lỗi khi tải ảnh lên!", Toast.LENGTH_SHORT).show();
            });
        } else {
            storyRef.child(storyId).updateChildren(storyUpdates)
                    .addOnCompleteListener(task -> onSaveComplete(task.isSuccessful()));
        }
    }

    // Hàm xử lý sau khi lưu thành công hoặc thất bại
    private void onSaveComplete(boolean success) {
        progressDialog.dismiss();
        if (success) {
            Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity sau khi lưu thành công
        } else {
            Toast.makeText(this, "Lỗi khi lưu!", Toast.LENGTH_SHORT).show();
        }
    }
}
