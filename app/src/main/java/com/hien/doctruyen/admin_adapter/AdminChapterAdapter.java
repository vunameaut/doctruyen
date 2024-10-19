package com.hien.doctruyen.admin_adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hien.doctruyen.R;
import com.hien.doctruyen.admin.ChapterListActivity;
import com.hien.doctruyen.admin.EditChapterActivity;
import com.hien.doctruyen.item.Chapter;
import com.hien.doctruyen.item.Story;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdminChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Object> storyOrChapterList;  // Chứa cả Story và Chapter
    private List<Object> filteredList;
    private Context context;
    private boolean isShowingChapters = false;  // Biến để kiểm tra trạng thái hiển thị

    public AdminChapterAdapter(List<Object> storyOrChapterList, Context context) {
        this.storyOrChapterList = storyOrChapterList;
        this.filteredList = new ArrayList<>(storyOrChapterList);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (filteredList.get(position) instanceof Story) {
            return 0;  // Story type
        } else {
            return 1;  // Chapter type
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {  // Nếu là Story
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_story, parent, false);  // Layout cho Story
            return new StoryViewHolder(view);
        } else {  // Nếu là Chapter
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chapter, parent, false);  // Layout cho Chapter
            return new ChapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StoryViewHolder) {
            Story story = (Story) filteredList.get(position);
            ((StoryViewHolder) holder).tvTitle.setText(story.getTitle());

            // Khi nhấn vào Story, mở ChapterListActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChapterListActivity.class);
                intent.putExtra("story_uid", story.getUid());  // Truyền storyId (UID)
                Log.d("AdminChapterAdapter", "Story UID: " + story.getUid());  // Log UID của truyện
                context.startActivity(intent);
            });

        }else if (holder instanceof ChapterViewHolder) {
            Chapter chapter = (Chapter) filteredList.get(position);
            ((ChapterViewHolder) holder).tvTitle.setText(chapter.getTitle());
            ((ChapterViewHolder) holder).tvAuthor.setText(chapter.getAuthor());
            ((ChapterViewHolder) holder).tvGenre.setText(chapter.getGenre());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditChapterActivity.class);
                intent.putExtra("chapter_id", chapter.getChapterId());
                intent.putExtra("story_id", chapter.getStoryId());  // Truyền cả storyId
                intent.putExtra("chapter_title", chapter.getTitle());
                intent.putExtra("chapter_content", chapter.getContent());
                intent.putExtra("chapter_author", chapter.getAuthor());
                intent.putExtra("chapter_genre", chapter.getGenre());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void updateList(List<Object> newList) {
        this.storyOrChapterList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Object> filtered = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(storyOrChapterList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Object item : storyOrChapterList) {
                        if (item instanceof Story) {
                            if (((Story) item).getTitle().toLowerCase().contains(filterPattern)) {
                                filtered.add(item);
                            }
                        } else if (item instanceof Chapter) {
                            if (((Chapter) item).getTitle().toLowerCase().contains(filterPattern)) {
                                filtered.add(item);
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<Object>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    private void loadChaptersFromFirebase(String storyId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories").child(storyId).child("chapters");

        dbRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chapter> chapterList = new ArrayList<>(); // Tạo danh sách mới cho các chương
                for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                    Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                    if (chapter != null) {
                        chapterList.add(chapter); // Thêm chương vào danh sách
                    }
                }
                storyOrChapterList.clear(); // Xóa danh sách cũ
                storyOrChapterList.addAll(chapterList); // Thêm tất cả chương vào danh sách
                isShowingChapters = true; // Đổi trạng thái sang hiển thị Chapter
                updateList(storyOrChapterList); // Cập nhật danh sách cho adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminChapterAdapter", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // ViewHolder cho Story
    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txt_story_title);
        }
    }

    // ViewHolder cho Chapter
    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvGenre;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txt_chapter_title);
            tvAuthor = itemView.findViewById(R.id.txt_chapter_author);
            tvGenre = itemView.findViewById(R.id.txt_chapter_genre);
        }
    }
}
