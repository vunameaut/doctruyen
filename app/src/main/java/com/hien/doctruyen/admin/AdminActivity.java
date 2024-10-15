package com.hien.doctruyen.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hien.doctruyen.R;
import com.hien.doctruyen.admin_adapter.AdminPagerAdapter;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hien.doctruyen.login;

public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdminPagerAdapter adminPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        // Thiết lập TabLayout và ViewPager
        tabLayout = findViewById(R.id.admin_tab_layout);
        viewPager = findViewById(R.id.admin_view_pager);

        // Adapter cho ViewPager
        adminPagerAdapter = new AdminPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adminPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();  // Gọi phương thức đăng xuất
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Xóa tất cả dữ liệu đã lưu
        editor.apply();

        FirebaseAuth.getInstance().signOut();  // Đăng xuất khỏi Firebase

        Intent intent = new Intent(AdminActivity.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Kết thúc Activity hiện tại
    }
}
