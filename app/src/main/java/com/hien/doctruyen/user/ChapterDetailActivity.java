package com.hien.doctruyen.user;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Chapter;
import com.hien.doctruyen.item.Story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChapterDetailActivity extends AppCompatActivity {
    private TextView tvChapterTitle, tvChapterContent, tvStoryTitle;
    private Button btnPrevious, btnNext;
    private ImageView ivMenu;
    private DatabaseReference userRef;
    private String userId;
    private Story story;
    private Chapter chapter;
    private List<Chapter> chapterList = new ArrayList<>();
    private int currentChapterIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);

        // Ánh xạ các view
        tvChapterTitle = findViewById(R.id.tv_chapter_title);
        tvChapterContent = findViewById(R.id.tv_chapter_content);
        tvStoryTitle = findViewById(R.id.tv_story_title);
        btnPrevious = findViewById(R.id.btn_previous_chapter);
        btnNext = findViewById(R.id.btn_next_chapter);
        ivMenu = findViewById(R.id.iv_menu);

        // Lấy dữ liệu từ Intent
        story = (Story) getIntent().getSerializableExtra("story");
        currentChapterIndex = getIntent().getIntExtra("chapterIndex", 0);  // Lấy chỉ số chương từ Intent

        // Lấy các chương từ Firebase và lưu vào chapterList
        if (story != null) {
            tvStoryTitle.setText(story.getTitle());
            if (story.getChapters() != null) {
                chapterList.addAll(story.getChapters().values());  // Lưu danh sách chương từ Firebase vào chapterList
                sortChapters();  // Sắp xếp danh sách chương
                loadChapter(currentChapterIndex);  // Tải chương theo chỉ số nhận được từ Intent
            }
        }

        // Điều chỉnh hiển thị các nút chuyển chương
        updateChapterNavigation();

        // Xử lý chuyển chương trước
        btnPrevious.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                loadChapter(currentChapterIndex);
            }
        });

        // Xử lý chuyển chương tiếp theo
        btnNext.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) {
                currentChapterIndex++;
                loadChapter(currentChapterIndex);
            }
        });

        // Hiển thị menu tùy chọn khi ấn vào nút menu
        ivMenu.setOnClickListener(v -> showMenu());
    }


    private void loadChapter(int index) {
        chapter = chapterList.get(index);
        tvChapterTitle.setText(chapter.getTitle());
        tvChapterContent.setText(chapter.getContent());
        updateChapterNavigation();
    }

    // Cập nhật trạng thái hiển thị của các nút "<--" và "-->"
    private void updateChapterNavigation() {
        btnPrevious.setVisibility(currentChapterIndex == 0 ? View.INVISIBLE : View.VISIBLE);
        btnNext.setVisibility(currentChapterIndex == chapterList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    // Sắp xếp các chương theo thứ tự tự nhiên dựa trên tên chương
    private void sortChapters() {
        Collections.sort(chapterList, new Comparator<Chapter>() {
            @Override
            public int compare(Chapter c1, Chapter c2) {
                String title1 = c1.getTitle().replaceAll("[^0-9]", ""); // Lấy số chương từ tiêu đề
                String title2 = c2.getTitle().replaceAll("[^0-9]", ""); // Lấy số chương từ tiêu đề

                // Nếu không tìm thấy số chương, mặc định là 0
                int chapterNum1 = title1.isEmpty() ? 0 : Integer.parseInt(title1);
                int chapterNum2 = title2.isEmpty() ? 0 : Integer.parseInt(title2);

                return Integer.compare(chapterNum1, chapterNum2);
            }
        });
    }

    // Hiển thị menu tùy chọn khi nhấn vào icon
    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(this, ivMenu);
        popupMenu.getMenuInflater().inflate(R.menu.chapter_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_font_size) {
                changeFontSize();
                return true;
            } else if (itemId == R.id.menu_eye_protection) {
                enableEyeProtectionMode();
                return true;
            } else if (itemId == R.id.menu_brightness) {
                adjustBrightness();
                return true;
            } else if (itemId == R.id.menu_background_color) {
                changeBackgroundColor();
                return true;
            } else if (itemId == R.id.menu_text_color) {
                changeTextColor();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void changeFontSize() {
        // Thực hiện thay đổi kích cỡ chữ
    }

    private void enableEyeProtectionMode() {
        // Thực hiện bật chế độ bảo vệ mắt
    }

    private void adjustBrightness() {
        // Thực hiện điều chỉnh độ sáng màn hình
    }

    private void changeBackgroundColor() {
        // Thực hiện thay đổi màu nền
    }

    private void changeTextColor() {
        // Thực hiện thay đổi màu chữ
    }
}
