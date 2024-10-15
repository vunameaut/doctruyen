
package com.hien.doctruyen.user_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hien.doctruyen.R;
import com.hien.doctruyen.item.Story;

import java.util.List;

public class userStoryAdapter extends RecyclerView.Adapter<userStoryAdapter.StoryViewHolder> {

    private List<Story> storyList;

    public userStoryAdapter(List<Story> storyList) {
        this.storyList = storyList;
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
        holder.tvTitle.setText(story.getTitle());
        holder.tvAuthor.setText(story.getAuthor());
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

        TextView tvTitle, tvAuthor;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_story_title);
            tvAuthor = itemView.findViewById(R.id.tv_story_author);
        }
    }
}

