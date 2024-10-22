package com.hien.doctruyen.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private DatabaseReference dbRef;
    private StorageReference storageRef;

    // ProgressDialog cho vòng tròn loading
    private ProgressDialog progressDialog;

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

        // Tham chiếu tới Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("stories");
        storageRef = FirebaseStorage.getInstance().getReference("cover_images");

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang thêm truyện...");
        progressDialog.setCancelable(false);

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
        String title = editTitle.getText().toString().trim();
        String author = editAuthor.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        List<String> selectedGenres = new ArrayList<>();
        for (int i = 0; i < linearGenres.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) linearGenres.getChildAt(i);
            if (checkBox.isChecked()) {
                selectedGenres.add(checkBox.getText().toString());
            }
        }

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(description) || imageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        String storyId = dbRef.push().getKey();
        StorageReference fileRef = storageRef.child(storyId + ".jpg");

        // Hiển thị ProgressDialog khi bắt đầu lưu
        progressDialog.show();

        fileRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Map<String, Chapter> chapters = new HashMap<>();

                    Story newStory = new Story(storyId, title, author, description, selectedGenres, imageUrl, chapters, storyId);

                    dbRef.child(storyId).setValue(newStory).addOnCompleteListener(dbTask -> {
                        progressDialog.dismiss();
                        if (dbTask.isSuccessful()) {
                            Toast.makeText(this, "Thêm truyện thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi lưu truyện!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Lỗi khi tải ảnh!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
