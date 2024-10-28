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

        btnDangXuat.setOnClickListener(v -> logoutUser()); // Use unified logout method
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

    // Unified logout method to clear user data and sign out
    private void logoutUser() {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clear all stored data
        editor.apply();

        FirebaseAuth.getInstance().signOut();

        Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // End current activity
    }


    // Check if permissions rationale should be shown
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleForCurrentPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            return shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}
