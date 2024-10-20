package com.hien.doctruyen.user_adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hien.doctruyen.R;
import com.hien.doctruyen.user.StoryDetailActivity;
import com.hien.doctruyen.item.Story;
import com.squareup.picasso.Picasso;

import java.util.List;

public class userStoryAdapter extends RecyclerView.Adapter<userStoryAdapter.StoryViewHolder> {

    private List<Story> storyList;
    private Context context;

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
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_erro)
                .into(holder.ivCover);

        // Xử lý sự kiện nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StoryDetailActivity.class);
            intent.putExtra("story", story);  // Truyền dữ liệu truyện qua Intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public void updateList(List<Story> filteredList) {
        storyList = filteredList;
        notifyDataSetChanged();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvGenres;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_story_cover);  // sửa thành id mới
            tvTitle = itemView.findViewById(R.id.tv_story_title);
            tvAuthor = itemView.findViewById(R.id.tv_story_author);
            tvGenres = itemView.findViewById(R.id.tv_story_genres);
        }
    }
}
