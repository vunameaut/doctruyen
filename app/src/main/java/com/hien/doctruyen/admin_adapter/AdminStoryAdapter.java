package com.hien.doctruyen.admin_adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hien.doctruyen.R;
import com.hien.doctruyen.detail.AdminStoryDetailActivity;
import com.hien.doctruyen.item.Story;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdminStoryAdapter extends RecyclerView.Adapter<AdminStoryAdapter.StoryViewHolder> implements Filterable {

    private List<Story> storyList;
    private List<Story> filteredStoryList;
    private Context context;

    public AdminStoryAdapter(List<Story> storyList, Context context) {
        this.storyList = storyList;
        this.filteredStoryList = new ArrayList<>(storyList);
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = filteredStoryList.get(position);
        holder.tvTitle.setText(story.getTitle());
        holder.tvAuthor.setText(story.getAuthor());

        List<String> genresList = story.getGenres();
        String genres = genresList != null && !genresList.isEmpty()
                ? String.join(", ", genresList)
                : "Không rõ thể loại";
        holder.tvGenres.setText(genres);

        // Bắt sự kiện click vào item và truyền dữ liệu đầy đủ sang AdminStoryDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminStoryDetailActivity.class);
            intent.putExtra("story_id", story.getId());
            intent.putExtra("story_title", story.getTitle());
            intent.putExtra("story_author", story.getAuthor());
            intent.putExtra("story_genre", genres);
            intent.putExtra("story_description", story.getDescription());
            intent.putExtra("story_image_url", story.getImageUrl());  // Truyền thêm URL của ảnh

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredStoryList.size();
    }

    public void updateList(List<Story> newList) {
        this.storyList = newList;
        this.filteredStoryList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return storyFilter;
    }

    private Filter storyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Story> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(storyList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Story story : storyList) {
                    if (story.getTitle().toLowerCase().contains(filterPattern) ||
                            story.getAuthor().toLowerCase().contains(filterPattern) ||
                            (story.getGenres() != null && String.join(", ", story.getGenres()).toLowerCase().contains(filterPattern))) {
                        filteredList.add(story);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredStoryList.clear();
            filteredStoryList.addAll((List<Story>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvGenres;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txt_story_title);
            tvAuthor = itemView.findViewById(R.id.txt_story_author);
            tvGenres = itemView.findViewById(R.id.txt_story_genres);
        }
    }
}
