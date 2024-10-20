package com.hien.doctruyen.user_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Chapter;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<Chapter> chapterList;
    private Context context;

    public ChapterAdapter(List<Chapter> chapterList, Context context) {
        this.chapterList = chapterList;
        this.context = context;  // Đảm bảo context được khởi tạo
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng context từ parent thay vì context truyền vào
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvTitle.setText(chapter.getTitle());  // Hiển thị chỉ tiêu đề chương
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_chapter_title);  // Ánh xạ đến TextView tiêu đề
        }
    }
}
