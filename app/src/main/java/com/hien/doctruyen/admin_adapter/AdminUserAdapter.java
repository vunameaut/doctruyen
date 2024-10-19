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
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public AdminUserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
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
                .load(user.getAvatar())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_erro)
                .into(holder.avatarImage);

        holder.deleteImage.setOnClickListener(v -> listener.onDeleteUser(user)); // Xóa
        holder.blockImage.setOnClickListener(v -> listener.onBlockUser(user)); // Khóa
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserActionListener {
        void onDeleteUser(User user);
        void onBlockUser(User user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView emailText;
        ImageView avatarImage;
        ImageView deleteImage; // Nút xóa
        ImageView blockImage; // Nút khóa/mở khóa

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            emailText = itemView.findViewById(R.id.email_text);
            avatarImage = itemView.findViewById(R.id.avatar_image);
            deleteImage = itemView.findViewById(R.id.delete_image); // Nút xóa
            blockImage = itemView.findViewById(R.id.block_image); // Nút khóa/mở khóa
        }
    }
}
