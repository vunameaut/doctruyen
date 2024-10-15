package com.hien.doctruyen.user;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.hien.doctruyen.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView ivFullImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ivFullImage = findViewById(R.id.iv_full_image);

        // Lấy URL ảnh được truyền từ Intent
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Sử dụng Picasso để tải và hiển thị ảnh từ URL
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(ivFullImage);
        }
    }
}
