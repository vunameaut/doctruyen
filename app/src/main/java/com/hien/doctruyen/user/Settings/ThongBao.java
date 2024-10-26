package com.hien.doctruyen.user.Settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.hien.doctruyen.R;

public class ThongBao extends AppCompatActivity {

    private static final String CHANNEL_ID = "thong_bao";
    private static final String PREFS_NAME = "ThongBaoPrefs";
    private static final String KEY_APP_SWITCH = "app_switch";
    private static final String KEY_MAIL_SWITCH = "mail_switch";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    ImageView btnBack;
    SwitchCompat swcApp, swcMail;
    SharedPreferences sharedPreferences;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_thongbao);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }

        btnBack = findViewById(R.id.ivBack);
        swcApp = findViewById(R.id.swApp);
        swcMail = findViewById(R.id.swEmail);

        btnBack.setOnClickListener(v -> onBackPressed());

        saveSwitchState();

        createNotificationChannel();

        swcApp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_APP_SWITCH, isChecked).apply();

            if (isChecked) {
                Toast.makeText(this, "Bật thông báo", Toast.LENGTH_SHORT).show();

                // Kiểm tra quyền trước khi gửi thông báo
                if (hasNotificationPermission()) {
                    sendNotification();
                } else {
                    requestNotificationPermission();
                }
            } else {
                Toast.makeText(this, "Tắt thông báo", Toast.LENGTH_SHORT).show();
            }
        });

        swcMail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_MAIL_SWITCH, isChecked).apply();

            if (isChecked) {
                Toast.makeText(this, "Bật thông báo về mail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tắt thông báo về mail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
        }
    }

    private boolean hasNotificationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền thông báo đã được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền thông báo bị từ chối. Đi đến cài đặt để bật.", Toast.LENGTH_LONG).show();
                openAppSettings();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ThongBao";
            String description = "ThongBao";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("My notification")
                .setContentText("Đã bật thông báo")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    protected void saveSwitchState() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isAppSwitchOn = sharedPreferences.getBoolean(KEY_APP_SWITCH, false);
        boolean isMailSwitchOn = sharedPreferences.getBoolean(KEY_MAIL_SWITCH, false);

        swcApp.setChecked(isAppSwitchOn);
        swcMail.setChecked(isMailSwitchOn);
    }
}
