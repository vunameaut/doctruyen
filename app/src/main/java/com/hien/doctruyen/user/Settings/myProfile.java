package com.hien.doctruyen.user.Settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hien.doctruyen.R;
import com.hien.doctruyen.custom.CircleTransform;
import com.squareup.picasso.Picasso;

public class myProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    TextView viewUser;
    TextInputEditText editUser, editEmail, editPhone;
    LinearLayout linearLayout;
    Button btnCancel, btnSave;
    ImageView btnBack, btnAvatar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("users");

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    boolean isDataChanged = false;
    boolean isAvatarChanged = false;
    String avatarUrl, currentUser, currentEmail, currentPhone;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_myprofile);

        Mapping();
        linearLayout.setVisibility(View.GONE);
        GetData();
        WatchForChanges();

        btnSave.setOnClickListener(v -> {
            clearFocus();
            if (isDataChanged) {
                showConfirm();
            }
        });

        btnCancel.setOnClickListener(v -> {
            clearFocus();
            GetData();
        });

        btnBack.setOnClickListener(v -> {
            clearFocus();
            if (isDataChanged) {
                showConfirmBack();
            } else {
                finish();
            }
        });

        btnAvatar.setOnClickListener(v -> openGallery());
    }

    private void Mapping() {
        viewUser = findViewById(R.id.tv_user);
        editUser = findViewById(R.id.et_user);
        editEmail = findViewById(R.id.et_email);
        editPhone = findViewById(R.id.et_numPhone);
        linearLayout = findViewById(R.id.ll_btn);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        btnAvatar = findViewById(R.id.iv_avatar);
        btnBack = findViewById(R.id.ivBack);
    }



    private void GetData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        if (uid == null) {
            Toast.makeText(this, "Không thể xác thực người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                avatarUrl = snapshot.child("avatar").getValue(String.class);
                currentUser = snapshot.child("username").getValue(String.class);
                currentEmail = snapshot.child("email").getValue(String.class);
                currentPhone = snapshot.child("phone").getValue(String.class);

                viewUser.setText(currentUser);
                editUser.setText(currentUser);
                editEmail.setText(currentEmail);
                editPhone.setText(currentPhone);

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    setAvatarImage(Uri.parse(avatarUrl));
                }
            } else {
                Toast.makeText(this, "Lỗi khi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", null);

        if (uid == null) {
            Toast.makeText(this, "Không thể xác thực người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editUser.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(uid).child("username").setValue(editUser.getText().toString());
        dbRef.child(uid).child("email").setValue(editEmail.getText().toString());
        dbRef.child(uid).child("phone").setValue(editPhone.getText().toString());

        if (isAvatarChanged && imageUri != null) {
            uploadImageToFirebase(imageUri, uid);
        }
    }


    private void WatchForChanges() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasChanges = !editUser.getText().toString().equals(currentUser) ||
                        !editEmail.getText().toString().equals(currentEmail) ||
                        !editPhone.getText().toString().equals(currentPhone);

                if (hasChanges) {
                    isDataChanged = true;
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    isDataChanged = false;
                    linearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        editUser.addTextChangedListener(textWatcher);
        editEmail.addTextChangedListener(textWatcher);
        editPhone.addTextChangedListener(textWatcher);
    }

    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc chắn muốn lưu?");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            SetData();
            Toast.makeText(myProfile.this, "Đã lưu", Toast.LENGTH_SHORT).show();
            isDataChanged = false;
            linearLayout.setVisibility(View.GONE);
            GetData();
        });

        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showConfirmBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Có dư liệu chưa lưu. Bạn có muốn lưu?");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            SetData();
            Toast.makeText(myProfile.this, "Đã lưu", Toast.LENGTH_SHORT).show();
            finish();
        });

        builder.setNegativeButton("Không", (dialog, which) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearFocus() {
        editUser.clearFocus();
        editEmail.clearFocus();
        editPhone.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
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
            setAvatarImage(imageUri);
            isAvatarChanged = true;
            linearLayout.setVisibility(View.VISIBLE);
            isDataChanged = true;
        }
    }

    private void setAvatarImage(Uri uri) {
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_erro)
                .transform(new CircleTransform())
                .into(btnAvatar);
    }

    private void uploadImageToFirebase(Uri imageUri, String uid) {
        StorageReference avatarRef = storageRef.child("avatars/" + uid + "/avatar.jpg");

        avatarRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    dbRef.child(uid).child("avatar").setValue(uri.toString());
                    Toast.makeText(myProfile.this, "Đã tải lên hình ảnh", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(myProfile.this, "Lỗi khi tải lên hình ảnh", Toast.LENGTH_SHORT).show());
    }
}
