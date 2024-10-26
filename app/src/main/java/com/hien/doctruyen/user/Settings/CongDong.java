package com.hien.doctruyen.user.Settings;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hien.doctruyen.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CongDong extends AppCompatActivity {

    TextView tvTerms;
    ImageView btnBack;
    private static final String FILE_NAME = "tieu_chuan_cong_dong.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_congdong);

        btnBack = findViewById(R.id.ivBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        tvTerms = findViewById(R.id.tvTerms);
        String fileContent = readFromAssets();
        if (fileContent.isEmpty()) {
            Toast.makeText(this, "Không thể tải nội dung", Toast.LENGTH_SHORT).show();
        } else {
            tvTerms.setText(fileContent);
            tvTerms.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private String readFromAssets() {
        StringBuilder text = new StringBuilder();

        try (InputStream is = getAssets().open(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }
}
