package com.hien.doctruyen.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hien.doctruyen.R;

public class ChapterListActivity extends AppCompatActivity {

    private TextView tvTitle, tvContent, tvNumberOfWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);  // Layout cho ChapterListActivity

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Ánh xạ các thành phần UI
        tvTitle = findViewById(R.id.txt_chapter_title);
        tvContent = findViewById(R.id.txt_chapter_content);
        tvNumberOfWords = findViewById(R.id.txt_chapter_number_of_words);

        // Nhận dữ liệu từ Intent
        String title = getIntent().getStringExtra("chapter_title");
        String content = getIntent().getStringExtra("chapter_content");
        int numberOfWords = getIntent().getIntExtra("chapter_number_of_words", 0);

        // Hiển thị dữ liệu
        tvTitle.setText(title);
        tvContent.setText(content);
        tvNumberOfWords.setText(numberOfWords + " từ");

        // Đặt tiêu đề thanh công cụ bằng tên chương
        getSupportActionBar().setTitle(title);
    }
}
