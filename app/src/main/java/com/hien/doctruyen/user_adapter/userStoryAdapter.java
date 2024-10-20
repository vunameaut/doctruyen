package com.hien.doctruyen.user_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Story;
import com.squareup.picasso.Picasso;

import java.util.List;

public class userStoryAdapter extends RecyclerView.Adapter<userStoryAdapter.StoryViewHolder> {

    private List<Story> storyList;
    private Context context;  // Cần để dùng Picasso

    // Constructor
    public userStoryAdapter(List<Story> storyList, Context context) {
        this.storyList = storyList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userstory, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);

        // Gán dữ liệu cho các thành phần giao diện
        holder.tvTitle.setText(story.getTitle());
        holder.tvAuthor.setText("Tác giả: " + story.getAuthor());
        holder.tvGenres.setText("Thể loại: " + String.join(", ", story.getGenres()));

        // Dùng Picasso để tải ảnh bìa từ URL
        Picasso.get()
                .load(story.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)  // Ảnh mặc định khi chưa tải xong
                .error(R.drawable.ic_erro)  // Ảnh lỗi nếu không tải được
                .into(holder.ivCover);
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    // Cập nhật danh sách khi tìm kiếm
    public void updateList(List<Story> filteredList) {
        storyList = filteredList;
        notifyDataSetChanged();
    }

    // ViewHolder để giữ các thành phần giao diện của mỗi item
    public static class StoryViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvGenres;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_story_cover);
            tvTitle = itemView.findViewById(R.id.tv_story_title);
            tvAuthor = itemView.findViewById(R.id.tv_story_author);
            tvGenres = itemView.findViewById(R.id.tv_story_genres);
        }
    }
}
