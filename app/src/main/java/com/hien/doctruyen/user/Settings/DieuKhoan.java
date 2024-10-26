package com.hien.doctruyen.user.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hien.doctruyen.R;
import com.hien.doctruyen.login;
import com.hien.doctruyen.user.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DieuKhoan extends AppCompatActivity {

    private CheckBox cbAgree;
    private Button btnContinue;
    private TextView tvTerms;
    private ImageView btnBack;
    private static final String FILE_NAME = "dieu_khoan.txt";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dieukhoan);


        btnBack = findViewById(R.id.ivBack);
        tvTerms = findViewById(R.id.tvTerms);

        // Đọc và hiển thị nội dung điều khoản từ file assets
        tvTerms.setText(readFromAssets());
        tvTerms.setTextColor(getResources().getColor(R.color.black));

        // Xử lý sự kiện nhấn nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());


    }


    // Phương thức đọc nội dung file điều khoản từ thư mục assets
    private String readFromAssets() {
        StringBuilder text = new StringBuilder();
        try (InputStream is = getAssets().open(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("DieuKhoan", "Error reading terms file", e);
        }
        return text.toString();
    }
}
