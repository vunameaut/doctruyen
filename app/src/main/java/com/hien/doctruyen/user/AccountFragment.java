package com.hien.doctruyen.user;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hien.doctruyen.R;
import com.hien.doctruyen.custom.CircleTransform;
import com.hien.doctruyen.login;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountFragment extends Fragment {

    private TextView tvUsername, tvEmail, tvPhone;
    private ImageView ivProfile, ivSettings; // Thêm ivSettings
    private Button btnLogout, btnEditProfile;
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các view từ layout
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        ivProfile = view.findViewById(R.id.iv_profile);
        ivSettings = view.findViewById(R.id.iv_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        // Khởi tạo Image Picker Launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            uploadImageToFirebase(imageUri);
                        }
                    } else {
                        Toast.makeText(getContext(), "Không chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        // Thiết lập sự kiện cho ảnh đại diện
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageOptions();
            }
        });

        // Thiết lập sự kiện cho biểu tượng răng cưa
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();  // Mở SettingsActivity
            }
        });



        // Thiết lập sự kiện cho nút đăng xuất
        btnLogout.setOnClickListener(v -> logout());

        // Thiết lập sự kiện cho nút sửa thông tin cá nhân
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserProfile();
            }
        });

        // Hiển thị thông tin người dùng
        displayUserInfo();

        return view;
    }

    /**
     * Hiển thị các tùy chọn khi người dùng nhấn vào ảnh đại diện.
     */
    private void showImageOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn hành động");
        builder.setItems(new CharSequence[]{"Chọn ảnh từ thư viện", "Tải ảnh về", "Xem ảnh"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Mở gallery để chọn ảnh
                    openImagePicker();
                } else if (which == 1) {
                    // Tải ảnh về bộ nhớ thiết bị
                    checkPermissionAndDownload();
                } else if (which == 2) {
                    // Xem ảnh
                    viewProfileImage();
                }
            }
        });
        builder.show();
    }

    /**
     * Mở trình chọn ảnh từ thư viện.
     */
    private void openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Sử dụng quyền mới cho Android 13 trở lên
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                launchImagePicker();
            }
        } else {
            // Sử dụng quyền cũ cho các phiên bản trước
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                launchImagePicker();
            }
        }
    }

    /**
     * Khởi động Intent để chọn ảnh.
     */
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Tải ảnh được chọn lên Firebase Storage và cập nhật URL ảnh trong hồ sơ người dùng.
     * @param imageUri URI của ảnh được chọn
     */
    private void uploadImageToFirebase(Uri imageUri) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                .child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Cập nhật URL ảnh trong hồ sơ người dùng Firebase
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Ảnh đại diện đã được cập nhật!", Toast.LENGTH_SHORT).show();
                                        Picasso.get().load(uri).transform(new CircleTransform()).into(ivProfile);  // Hiển thị ảnh đại diện mới với hình tròn
                                    }
                                });
                            }
                        }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Kiểm tra quyền và tải ảnh về nếu được cấp quyền.
     */
    private void checkPermissionAndDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Đối với Android 10 trở lên, không cần WRITE_EXTERNAL_STORAGE khi sử dụng getExternalFilesDir
            downloadProfileImage();
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                downloadProfileImage();
            }
        }
    }

    /**
     * Tải ảnh đại diện từ Firebase và lưu vào bộ nhớ.
     */
    private void downloadProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            String imageUrl = currentUser.getPhotoUrl().toString();
            Picasso.get()
                    .load(imageUrl)
                    .transform(new CircleTransform()) // Áp dụng transformation để hình tròn
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            // Lưu bitmap vào bộ nhớ
                            saveImageToStorage(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(getContext(), "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolder) {
                            // Có thể thêm hành động khi chuẩn bị tải ảnh
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Không có ảnh đại diện", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lưu bitmap vào bộ nhớ.
     * @param bitmap Bitmap ảnh cần lưu
     */
    private void saveImageToStorage(Bitmap bitmap) {
        String fileName = "profile.jpg";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            Toast.makeText(getContext(), "Không thể truy cập thư mục lưu trữ", Toast.LENGTH_SHORT).show();
            return;
        }
        File imageFile = new File(storageDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(getContext(), "Ảnh đã được lưu tại: " + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lưu ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hiển thị thông tin người dùng trong giao diện.
     */
    private void displayUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber(); // Lấy số điện thoại nếu có

            tvUsername.setText("Name: " + (displayName != null ? displayName : "Không có tên"));
            tvEmail.setText("Email: " + (email != null ? email : "Không có email"));
            tvPhone.setText("SĐT: " + (phone != null ? phone : "Không có số điện thoại"));

            if (user.getPhotoUrl() != null) {
                Picasso.get()
                        .load(user.getPhotoUrl())
                        .transform(new CircleTransform()) // Áp dụng transformation để hình tròn
                        .into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.default_profile); // Đặt ảnh mặc định nếu không có
            }
        } else {
            tvUsername.setText("Người dùng chưa đăng nhập");
            tvEmail.setText("");
            tvPhone.setText("");
            ivProfile.setImageResource(R.drawable.default_profile); // Đặt ảnh mặc định nếu không có
        }
    }

    /**
     * Xem ảnh đại diện trong một Activity khác.
     */
    private void viewProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            String imageUrl = currentUser.getPhotoUrl().toString();
            Intent intent = new Intent(getActivity(), ImageViewActivity.class);
            intent.putExtra("imageUrl", imageUrl);  // Truyền URL ảnh qua Intent
            startActivity(intent);  // Khởi động ImageViewActivity
        } else {
            Toast.makeText(getContext(), "Không có ảnh đại diện", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Đăng xuất người dùng và chuyển hướng về màn hình đăng nhập.
     */
    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        // Chuyển hướng người dùng về màn hình đăng nhập hoặc trang chủ
        Intent intent = new Intent(getActivity(), login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Chỉnh sửa hồ sơ người dùng.
     */
    private void editUserProfile() {
        // Triển khai chức năng chỉnh sửa hồ sơ người dùng
        // Ví dụ: Mở một Activity hoặc Dialog để người dùng nhập thông tin mới
        // Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        // startActivity(intent);
        Toast.makeText(getContext(), "Chức năng chỉnh sửa hồ sơ đang được phát triển.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Kiểm tra xem có nên hiển thị lời nhắc xin quyền cho quyền hiện tại hay không.
     * @return true nếu nên hiển thị lời nhắc, false nếu không
     */
    private boolean shouldShowRequestPermissionRationaleForCurrentPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            return shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    /**
     * Mở màn hình cài đặt ứng dụng để người dùng có thể cấp quyền thủ công.
     */
    private void openSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void logout() {
        SharedPreferences preferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clear all stored data
        editor.apply();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getActivity(), login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();  // Kết thúc Activity chứa Fragment
    }
}
