package com.hien.doctruyen.user.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.hien.doctruyen.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HoTro extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView btnBack, ivImage;
    EditText InputDescription;
    Button btnSendReport;
    LinearLayout btnChooseImage;

    String currentMail;
    Uri imageUri;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference taikhoanRef = database.getReference("users");
    DatabaseReference reportRef = database.getReference("reports");

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_hotro);

        Mapping();

        btnBack.setOnClickListener(v -> finish());

        btnChooseImage.setOnClickListener(v -> openGallery());

        btnSendReport.setOnClickListener(v -> {
            if (InputDescription.getText().toString().isEmpty()) {
                Toast.makeText(HoTro.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri == null) {
                Toast.makeText(HoTro.this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            sendReport();
            Toast.makeText(HoTro.this, "Báo cáo đã được gửi", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    protected void Mapping() {
        btnBack = findViewById(R.id.ivBack);
        InputDescription = findViewById(R.id.et_description);
        btnChooseImage = findViewById(R.id.ll_selectImage);
        btnSendReport = findViewById(R.id.btn_send_report);
        ivImage = findViewById(R.id.iv_image);
    }

    protected void sendReport() {
        // Lấy `uid` từ `SharedPreferences`
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        if (uid == null) {
            Toast.makeText(this, "Không thể xác thực người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy email người dùng từ Firebase
        taikhoanRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                currentMail = snapshot.child("email").getValue(String.class);

                String descrip = InputDescription.getText().toString();
                reportRef.child(uid).child("description").setValue(descrip);
                reportRef.child(uid).child("email").setValue(currentMail);

                if (imageUri != null) {
                    UploadImage(imageUri, uid);
                }
            } else {
                Toast.makeText(this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivImage.setImageURI(imageUri);
        }
    }

    private void UploadImage(Uri imageUri, String uid) {
        StorageReference storage = storageRef.child("reports/" + uid + "/" + System.currentTimeMillis() + ".jpg");

        storage.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storage.getDownloadUrl().addOnSuccessListener(uri -> {
                    reportRef.child(uid).child("imageUrl").setValue(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(HoTro.this, "Lỗi khi tải lên hình ảnh", Toast.LENGTH_SHORT).show());
    }
}
