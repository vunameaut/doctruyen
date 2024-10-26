package com.hien.doctruyen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.admin.AdminActivity;
import com.hien.doctruyen.user.main;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";

    private DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener authStateListener; // Khai báo lắng nghe trạng thái xác thực

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth và Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Liên kết các thành phần giao diện với mã Java
        emailEditText = findViewById(R.id.input_username);
        passwordEditText = findViewById(R.id.input_pass);
        loginButton = findViewById(R.id.sign_in_button);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        TextView forgotPassword = findViewById(R.id.forgot_password);

        // Khởi tạo SharedPreferences để lưu thông tin đăng nhập
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        forgotPassword.setOnClickListener(v -> {
            // Redirect to ForgotPasswordActivity
            Intent intent = new Intent(login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Kiểm tra trạng thái đăng nhập trước đó
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Người dùng đã đăng nhập, tự động chuyển đến trang chính
            autoLogin(currentUser.getUid());
        } else if (sharedPreferences.getBoolean("remember", false)) {
            // Nếu không có người dùng nhưng đã chọn "Nhớ tôi", tự động đăng nhập
            String uid = sharedPreferences.getString("uid", null);
            if (uid != null) {
                autoLogin(uid);
            }
        }

        // Xử lý hiển thị mật khẩu
        ImageButton togglePasswordVisibility = findViewById(R.id.toggle_password_visibility);
        togglePasswordVisibility.setOnClickListener(v -> {
            if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Chuyển hướng tới trang đăng ký
        TextView signUpRedirect = findViewById(R.id.sign_up_redirect);
        signUpRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });

        // Sự kiện khi nhấn nút đăng nhập
        loginButton.setOnClickListener(v -> loginUser());

        // Khởi tạo lắng nghe trạng thái xác thực
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // Người dùng đã bị đăng xuất
                Toast.makeText(login.this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                // Chuyển hướng về màn hình đăng nhập hoặc xử lý lại đăng nhập
            }
        };

    }

    private void autoLogin(String uid) {
        // Kiểm tra thông tin người dùng trong Firebase Realtime Database
        mDatabase.child("users").child(uid).get().addOnCompleteListener(databaseTask -> {
            if (databaseTask.isSuccessful()) {
                DataSnapshot snapshot = databaseTask.getResult();
                if (snapshot.exists()) {
                    // Lấy thông tin vai trò của người dùng
                    String role = snapshot.child("role").getValue(String.class);
                    Boolean isActive = snapshot.child("isActive").getValue(Boolean.class);

                    if (isActive == null || isActive) {
                        // Chuyển đến màn hình tương ứng
                        Intent intent;
                        if ("admin".equals(role)) {
                            Toast.makeText(login.this, "Đăng nhập với quyền quản trị!", Toast.LENGTH_SHORT).show();
                            intent = new Intent(login.this, AdminActivity.class);
                        } else {
                            Toast.makeText(login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            intent = new Intent(login.this, main.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        // Nếu tài khoản bị khóa hoặc không hoạt động
                        Toast.makeText(login.this, "Tài khoản của bạn đã bị vô hiệu hóa.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(login.this, "Lỗi khi truy vấn dữ liệu người dùng: " + databaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức xử lý đăng nhập người dùng
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Kiểm tra nếu thông tin người dùng chưa đầy đủ
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(login.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị hộp thoại tải trong quá trình đăng nhập
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();

        // Sử dụng Firebase để đăng nhập với email và mật khẩu
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Tắt hộp thoại tải sau khi hoàn tất đăng nhập
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Kiểm tra thông tin người dùng trong Firebase Realtime Database
                            mDatabase.child("users").child(user.getUid()).get().addOnCompleteListener(databaseTask -> {
                                if (databaseTask.isSuccessful()) {
                                    DataSnapshot snapshot = databaseTask.getResult();
                                    if (snapshot.exists()) {
                                        // Lấy thông tin vai trò của người dùng
                                        String role = snapshot.child("role").getValue(String.class);
                                        Boolean isActive = snapshot.child("isActive").getValue(Boolean.class);

                                        if (isActive == null || isActive) {
                                            // Chuyển đến màn hình tương ứng
                                            Intent intent;
                                            if ("admin".equals(role)) {
                                                Toast.makeText(login.this, "Đăng nhập với quyền quản trị!", Toast.LENGTH_SHORT).show();
                                                intent = new Intent(login.this, AdminActivity.class);
                                            } else {
                                                Toast.makeText(login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                                intent = new Intent(login.this, main.class);
                                            }
                                            startActivity(intent);
                                            finish();

                                            // Lưu thông tin đăng nhập
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("uid", user.getUid());  // Lưu UID
                                            editor.putBoolean("remember", rememberMeCheckBox.isChecked());  // Lưu trạng thái "Nhớ tôi"
                                            editor.apply();
                                        } else {
                                            // Nếu tài khoản bị khóa hoặc không hoạt động
                                            Toast.makeText(login.this, "Tài khoản của bạn đã bị vô hiệu hóa.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut(); // Đăng xuất nếu tài khoản không hoạt động
                                        }
                                    } else {
                                        Toast.makeText(login.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(login.this, "Lỗi khi truy vấn dữ liệu người dùng: " + databaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    } else {
                        // Đăng nhập thất bại
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng nhập thất bại";
                        Toast.makeText(login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener); // Thêm lắng nghe trạng thái xác thực
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener); // Xóa lắng nghe trạng thái xác thực
        }
    }
}
