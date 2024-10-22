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
import com.hien.doctruyen.item.History;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private Context context;
    private List<History> histories;

    public StoryAdapter(Context context, List<History> histories) {
        this.context = context;
        this.histories = histories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);  // Ensure correct layout
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        History history = histories.get(position);

        // Gán dữ liệu cho các TextView và ImageView
        holder.titleTextView.setText(history.getTitle());
        holder.currentChapterTextView.setText("Chap đang đọc: " + history.getCurrentChapter());
        holder.latestChapterTextView.setText("Chap mới nhất: " + history.getLatestChapter());

        // Sử dụng Picasso để tải ảnh bìa truyện
        Picasso.get().load(history.getImageUrl()).into(holder.coverImageView);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, currentChapterTextView, latestChapterTextView;
        ImageView coverImageView;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liên kết các view với ID trong layout
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            currentChapterTextView = itemView.findViewById(R.id.textViewCurrentChapter);
            latestChapterTextView = itemView.findViewById(R.id.textViewLatestChapter);
            coverImageView = itemView.findViewById(R.id.imageViewCover);
        }
    }
}
