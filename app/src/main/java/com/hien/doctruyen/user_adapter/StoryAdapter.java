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
import com.hien.doctruyen.item.History;
import com.hien.doctruyen.user.ChapterDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private List<History> histories;
    private Context context;

    public StoryAdapter(List<History> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        History history = histories.get(position);

        holder.titleTextView.setText(history.getTitle());

        if (history.getCurrentChapter() != null) {
            holder.currentChapterTextView.setText("Chapter đang đọc: " + history.getCurrentChapter());
        } else {
            holder.currentChapterTextView.setText("Chapter đang đọc: N/A");
        }

        if (history.getLatestChapter() != null) {
            holder.latestChapterTextView.setText("Chapter mới nhất: " + history.getLatestChapter());
        } else {
            holder.latestChapterTextView.setText("Chapter mới nhất: N/A");
        }

        Picasso.get().load(history.getImageUrl()).into(holder.coverImageView);

        // Truyền dữ liệu qua Intent
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterDetailActivity.class);
            intent.putExtra("story", history.getStory());  // Truyền đối tượng Story
            intent.putExtra("chapterIndex", history.getCurrentChapter() != null ? history.getCurrentChapter().intValue() : 0);
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return histories.size();
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
