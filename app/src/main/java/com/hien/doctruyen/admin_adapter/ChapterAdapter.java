package com.hien.doctruyen.admin_adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hien.doctruyen.R;
import com.hien.doctruyen.admin.EditChapterActivity;
import com.hien.doctruyen.item.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<Chapter> chapterList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Interface để lắng nghe sự kiện

    public ChapterAdapter(List<Chapter> chapterList, Context context) {
        this.chapterList = chapterList;
        this.context = context;
    }

    // Interface cho sự kiện nhấn vào một item
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Phương thức để thiết lập sự kiện nhấn
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvTitle.setText(chapter.getTitle());
        holder.tvAuthor.setText(chapter.getAuthor());
        holder.tvGenre.setText(chapter.getGenre());
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvGenre;

        public ChapterViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txt_chapter_title);
            tvAuthor = itemView.findViewById(R.id.txt_chapter_author);
            tvGenre = itemView.findViewById(R.id.txt_chapter_genre);

            // Thiết lập sự kiện nhấn vào item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position); // Gọi callback khi item được nhấn
                    }
                }
            });
        }
    }
}
