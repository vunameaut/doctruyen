package com.hien.doctruyen.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hien.doctruyen.R;

public class main extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        navigationView = findViewById(R.id.bottom_navigation);

        // Set initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.body_container, new HomeFragment())
                    .commit();
        }
        navigationView.setSelectedItemId(R.id.nav_home);

        // Handle bottom navigation item selection
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.nav_history) {
                    fragment = new HistoryFragment();
                } else if (itemId == R.id.nav_account) {
                    fragment = new AccountFragment();
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.body_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }

                return true;
            }
        });

    }
}
