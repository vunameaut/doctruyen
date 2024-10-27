package com.hien.doctruyen.user;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;
import android.widget.SeekBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.provider.Settings;
import android.view.WindowManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ImageView ivPrevious, ivNext, ivMenu;
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
        ivPrevious = findViewById(R.id.iv_previous_chapter);
        ivNext = findViewById(R.id.iv_next_chapter);
        ivMenu = findViewById(R.id.iv_menu);

        // Các phần khởi tạo dữ liệu và sự kiện
        story = (Story) getIntent().getSerializableExtra("story");

        if (story == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu truyện.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentChapterIndex = getIntent().getIntExtra("chapterIndex", 0);
        tvStoryTitle.setText(story.getTitle());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("reading_progress")
                    .child(story.getId());
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu tiến trình đọc.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chapterList.addAll(story.getChapters().values());
        sortChapters();
        loadChapter(currentChapterIndex);
        updateChapterNavigation();

        // Xử lý sự kiện khi nhấn các ImageView chuyển chapter
        ivPrevious.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                loadChapter(currentChapterIndex);
                saveReadingProgress(currentChapterIndex, 0);
            }
        });

        ivNext.setOnClickListener(v -> {
            if (currentChapterIndex < chapterList.size() - 1) {
                currentChapterIndex++;
                loadChapter(currentChapterIndex);
                saveReadingProgress(currentChapterIndex, 0);
            }
        });

        ivMenu.setOnClickListener(v -> showMenu());
    }

    private void loadChapter(int index) {
        chapter = chapterList.get(index);
        tvChapterTitle.setText(chapter.getTitle());
        tvChapterContent.setText(chapter.getContent());
        updateChapterNavigation();
    }

    private void updateChapterNavigation() {
        ivPrevious.setVisibility(currentChapterIndex == 0 ? View.INVISIBLE : View.VISIBLE);
        ivNext.setVisibility(currentChapterIndex == chapterList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    private void sortChapters() {
        Collections.sort(chapterList, new Comparator<Chapter>() {
            @Override
            public int compare(Chapter c1, Chapter c2) {
                String title1 = c1.getTitle().replaceAll("[^0-9]", ""); // Lấy số chương từ tiêu đề
                String title2 = c2.getTitle().replaceAll("[^0-9]", ""); // Lấy số chương từ tiêu đề

                int chapterNum1 = title1.isEmpty() ? 0 : Integer.parseInt(title1);
                int chapterNum2 = title2.isEmpty() ? 0 : Integer.parseInt(title2);

                return Integer.compare(chapterNum1, chapterNum2);
            }
        });
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(this, ivMenu);
        popupMenu.getMenuInflater().inflate(R.menu.chapter_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_font_size) {
                changeFontSize();
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


    private void saveReadingProgress(int chapterIndex, int lastPosition) {
        if (userRef != null) {
            userRef.child("last_chapter_read").setValue(chapterIndex);
            userRef.child("last_position").setValue(lastPosition);
        } else {
            Log.e("ChapterDetailActivity", "userRef is null. Không thể lưu tiến trình đọc.");
        }
    }

    private void changeFontSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi kích cỡ chữ");

        final SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(50);
        seekBar.setProgress(16);

        builder.setView(seekBar);
        builder.setPositiveButton("OK", (dialog, which) -> tvChapterContent.setTextSize(seekBar.getProgress()));
        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    private void adjustBrightness() {
        try {
            if (Settings.System.canWrite(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Điều chỉnh độ sáng");

                final SeekBar seekBar = new SeekBar(this);
                seekBar.setMax(255);
                seekBar.setProgress(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));

                builder.setView(seekBar);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    int brightness = seekBar.getProgress();
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    layoutParams.screenBrightness = brightness / 255.0f;
                    getWindow().setAttributes(layoutParams);
                });
                builder.setNegativeButton("Hủy", null);
                builder.create().show();
            } else {
                Toast.makeText(this, "Bạn chưa cấp quyền chỉnh sửa cài đặt hệ thống.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        .setData(Uri.parse("package:" + getPackageName())));
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void changeBackgroundColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn màu nền");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            try {
                int color = Color.parseColor(input.getText().toString());
                tvChapterContent.setBackgroundColor(color);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Mã màu không hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    private void changeTextColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn màu chữ");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            try {
                int color = Color.parseColor(input.getText().toString());
                tvChapterContent.setTextColor(color);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Mã màu không hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }
}
