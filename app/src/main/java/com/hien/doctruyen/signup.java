package com.hien.doctruyen;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText, phoneEditText;
    private Button signUpButton;
    private TextView signUpRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        // Khởi tạo Firebase Auth và Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Liên kết các thành phần giao diện với mã Java
        usernameEditText = findViewById(R.id.input_username);
        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_pass);
        confirmPasswordEditText = findViewById(R.id.input_confirm_pass);
        phoneEditText = findViewById(R.id.input_phone);
        signUpButton = findViewById(R.id.sign_up_button);
        signUpRedirect = findViewById(R.id.already_have_account);

        // Xử lý khi người dùng nhấn nút đăng ký
        signUpButton.setOnClickListener(v -> registerUser());

        // Chuyển hướng về màn hình đăng nhập nếu người dùng đã có tài khoản
        signUpRedirect.setOnClickListener(v -> {
            finish();
        });
    }

    // Phương thức xử lý đăng ký người dùng mới
    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim(); // Lấy số điện thoại

        // Kiểm tra nếu thông tin chưa đầy đủ
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
            Toast.makeText(signup.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu mật khẩu không khớp
        if (!password.equals(confirmPassword)) {
            Toast.makeText(signup.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị hộp thoại đăng ký
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
        progressDialog.show();

        // Đăng ký người dùng mới với email và mật khẩu
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Đăng ký thành công, lưu thông tin người dùng vào Firebase Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Lưu thông tin người dùng vào Firebase với cấu trúc yêu cầu
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("uid", user.getUid());
                            userProfile.put("username", username);
                            userProfile.put("email", email);
                            userProfile.put("sdt", phone); // Thêm số điện thoại
                            userProfile.put("avatar", "url_to_avatar_image"); // Giả định avatar URL, có thể thay thế sau
                            userProfile.put("role", "user");
                            userProfile.put("favorites", new ArrayList<>()); // Danh sách truyện yêu thích rỗng
                            Map<String, Object> readingProgress = new HashMap<>();
                            userProfile.put("reading_progress", readingProgress); // Trạng thái đọc trống

                            mDatabase.child("users").child(user.getUid()).setValue(userProfile)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            // Gửi email xác thực cho người dùng
                                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                                if (verificationTask.isSuccessful()) {
                                                    Toast.makeText(signup.this, "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(signup.this, login.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(signup.this, "Không thể gửi email xác thực: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(signup.this, "Lưu thông tin người dùng thất bại: " + databaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Đăng ký thất bại
                        Toast.makeText(signup.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
