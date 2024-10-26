package com.hien.doctruyen.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hien.doctruyen.R;
import com.hien.doctruyen.login;
import com.hien.doctruyen.user.Settings.CongDong;
import com.hien.doctruyen.user.Settings.DieuKhoan;
import com.hien.doctruyen.user.Settings.GioiThieu;
import com.hien.doctruyen.user.Settings.HoTro;
import com.hien.doctruyen.user.Settings.TKvaBM;
import com.hien.doctruyen.user.Settings.ThongBao;

public class SettingsActivity extends AppCompatActivity {

    ImageView btnBack;
    Button btnThongBao, btnTaiKhoanBaoMat, btnNgonNgu, btnHoTro,
            btnCongDong, btnDieuKhoan, btnGioiThieu, btnDangXuat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ các thành phần giao diện
        Mapping();

        // Đăng ký sự kiện nhấn
        LoadActivity();

        btnDangXuat.setOnClickListener(v -> logout()); // Sử dụng hàm logout
    }

    // Ánh xạ các thành phần giao diện
    private void Mapping() {
        btnBack = findViewById(R.id.ivBack);
        btnThongBao = findViewById(R.id.btnThongBao);
        btnTaiKhoanBaoMat = findViewById(R.id.btnTkBm);
        btnHoTro = findViewById(R.id.btnHoTro);
        btnCongDong = findViewById(R.id.btnCongDong);
        btnDieuKhoan = findViewById(R.id.btnDieuKhoan);
        btnGioiThieu = findViewById(R.id.btnGioiThieu);
        btnDangXuat = findViewById(R.id.btnDangXuat);
    }

    private void LoadActivity() {
        btnBack.setOnClickListener(view -> finish());
        btnThongBao.setOnClickListener(view -> startActivity(new Intent(this, ThongBao.class)));
        btnTaiKhoanBaoMat.setOnClickListener(view -> startActivity(new Intent(this, TKvaBM.class)));
        btnHoTro.setOnClickListener(view -> startActivity(new Intent(this, HoTro.class)));
        btnCongDong.setOnClickListener(view -> startActivity(new Intent(this, CongDong.class)));
        btnDieuKhoan.setOnClickListener(view -> startActivity(new Intent(this, DieuKhoan.class)));
        btnGioiThieu.setOnClickListener(view -> startActivity(new Intent(this, GioiThieu.class)));
    }

    // Hàm đăng xuất người dùng và xóa thông tin đăng nhập
    private void logout() {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clear all stored data
        editor.apply();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Kết thúc Activity hiện tại
    }

    // Hàm chỉnh sửa hồ sơ người dùng
    private void editUserProfile() {
        Toast.makeText(this, "Chức năng chỉnh sửa hồ sơ đang được phát triển.", Toast.LENGTH_SHORT).show();
    }


}
