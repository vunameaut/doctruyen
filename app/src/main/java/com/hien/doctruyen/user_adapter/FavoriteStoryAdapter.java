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
import com.hien.doctruyen.item.FavoriteStory;
import com.hien.doctruyen.user.ChapterDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteStoryAdapter extends RecyclerView.Adapter<FavoriteStoryAdapter.StoryViewHolder> {
    private List<FavoriteStory> favoriteStories; // Đổi từ History sang FavoriteStory
    private Context context;

    public FavoriteStoryAdapter(List<FavoriteStory> favoriteStories, Context context) { // Đổi từ History sang FavoriteStory
        this.favoriteStories = favoriteStories;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        FavoriteStory favoriteStory = favoriteStories.get(position);

        holder.titleTextView.setText(favoriteStory.getTitle());

        // Hiển thị chương hiện tại cho người dùng, cộng thêm 1
        if (favoriteStory.getCurrentChapter() != null) {
            holder.currentChapterTextView.setText("Chapter đang đọc: " + (favoriteStory.getCurrentChapter() + 1));
        } else {
            holder.currentChapterTextView.setText("Chapter đang đọc: N/A");
        }

        if (favoriteStory.getLatestChapter() != null) {
            holder.latestChapterTextView.setText("Chapter mới nhất: " + favoriteStory.getLatestChapter());
        } else {
            holder.latestChapterTextView.setText("Chapter mới nhất: N/A");
        }

        Picasso.get().load(favoriteStory.getImageUrl()).into(holder.coverImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterDetailActivity.class);
            intent.putExtra("story", favoriteStory.getStory());

            // Đảm bảo `chapterIndex` luôn là 0 hoặc cao hơn
            int chapterIndex = favoriteStory.getCurrentChapter() != null ? Math.max(favoriteStory.getCurrentChapter().intValue() - 1, 0) : 0;
            intent.putExtra("chapterIndex", chapterIndex);

            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return favoriteStories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView, currentChapterTextView, latestChapterTextView;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            coverImageView = itemView.findViewById(R.id.imageViewCover);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            currentChapterTextView = itemView.findViewById(R.id.textViewCurrentChapter);
            latestChapterTextView = itemView.findViewById(R.id.textViewLatestChapter);
        }
    }
}
