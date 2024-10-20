package com.hien.doctruyen.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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


        btnDangXuat.setOnClickListener(v -> {
            // Xóa thông tin uid đã lưu trong SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("uid"); // Xóa uid
            editor.apply(); // Áp dụng thay đổi

            // Đăng xuất FirebaseAuth nếu sử dụng
            FirebaseAuth.getInstance().signOut(); // Đăng xuất FirebaseAuth

            // Tạo intent chuyển về màn hình Login và xóa tất cả các Activity khác
            Intent intent = new Intent(this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa hết các Activity trước đó
            startActivity(intent);
            finish(); // Đóng màn hình hiện tại
        });




    }

    // Ánh xạ các thành phần giao diện
    private void Mapping() {
        btnBack =  findViewById(R.id.ivBack);
        btnThongBao = findViewById(R.id.btnThongBao);
        btnTaiKhoanBaoMat = findViewById(R.id.btnTkBm);
        btnHoTro = findViewById(R.id.btnHoTro);
        btnCongDong = findViewById(R.id.btnCongDong);
        btnDieuKhoan = findViewById(R.id.btnDieuKhoan);
        btnGioiThieu = findViewById(R.id.btnGioiThieu);
        btnDangXuat = findViewById(R.id.btnDangXuat);
    }

    private void LoadActivity() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
        btnThongBao.setOnClickListener(view -> {
            Intent intent = new Intent(this, ThongBao.class);
            startActivity(intent);
        });
        btnTaiKhoanBaoMat.setOnClickListener(view -> {
            Intent intent = new Intent(this, TKvaBM.class);
            startActivity(intent);
        });

        btnHoTro.setOnClickListener(view -> {
            Intent intent = new Intent(this, HoTro.class);
            startActivity(intent);
        });
        btnCongDong.setOnClickListener(view -> {
            Intent intent = new Intent(this, CongDong.class);
            startActivity(intent);
        });
        btnDieuKhoan.setOnClickListener(view -> {
            Intent intent = new Intent(this, DieuKhoan.class);
            startActivity(intent);
        });
        btnGioiThieu.setOnClickListener(view -> {
            Intent intent = new Intent(this, GioiThieu.class);
            startActivity(intent);
        });
    }
}