package com.hien.doctruyen.user_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Hiển thị tên người dùng và nội dung bình luận
        holder.tvCommentUsername.setText(comment.getUsername());
        holder.tvCommentContent.setText(comment.getContent());

        // Tải ảnh đại diện của người dùng
        Picasso.get().load(comment.getAvatarUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_erro)
                .into(holder.ivAvatar);

        // Hiển thị thời gian bình luận (dùng TimeAgo để hiển thị "x giờ trước", "x ngày trước")
        String timeAgo = TimeAgo.using(comment.getTimestamp());
        holder.tvCommentTime.setText(timeAgo);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView tvCommentUsername, tvCommentContent, tvCommentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvCommentUsername = itemView.findViewById(R.id.tv_comment_username);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            tvCommentTime = itemView.findViewById(R.id.tv_comment_time);  // Thêm phần hiển thị thời gian
        }
    }
}
