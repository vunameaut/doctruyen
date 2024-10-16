package com.hien.doctruyen.admin_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.User;
import com.squareup.picasso.Picasso; // Thêm import cho Picasso

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<User> userList;

    public AdminUserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameText.setText(user.getUsername());
        holder.emailText.setText(user.getEmail());

        // Sử dụng Picasso để tải hình ảnh avatar từ Firebase vào ImageView
        Picasso.get()
                .load(user.getAvatar()) // Đảm bảo rằng phương thức getAvatar() trả về URL hình ảnh
                .placeholder(R.drawable.ic_placeholder) // Hình ảnh placeholder trong khi tải
                .error(R.drawable.ic_erro) // Hình ảnh hiển thị khi có lỗi tải
                .into(holder.avatarImage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView emailText;
        ImageView avatarImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            emailText = itemView.findViewById(R.id.email_text);
            avatarImage = itemView.findViewById(R.id.avatar_image);
        }
    }
}
