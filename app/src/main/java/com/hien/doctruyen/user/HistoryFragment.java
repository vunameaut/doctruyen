package com.hien.doctruyen.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hien.doctruyen.R;
import com.hien.doctruyen.item.History;
import com.hien.doctruyen.user_adapter.StoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<History> historyList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Firebase Auth và Database Reference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();  // Lấy UID người dùng hiện tại
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(uid).child("reading_progress");

            // Lấy dữ liệu lịch sử từ Firebase Realtime Database
            loadReadingHistory();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), historyList);
        recyclerView.setAdapter(storyAdapter);

        return view;
    }

    private void loadReadingHistory() {
        // Truy vấn dữ liệu lịch sử đọc
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String storyId = snapshot.getKey();
                    Long lastChapterRead = snapshot.child("last_chapter_read").getValue(Long.class);

                    // Truy vấn chi tiết truyện từ node stories
                    FirebaseDatabase.getInstance().getReference("stories").child(storyId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot storySnapshot) {
                                    if (storySnapshot.exists()) {
                                        String title = storySnapshot.child("title").getValue(String.class);
                                        String imageUrl = storySnapshot.child("imageUrl").getValue(String.class);
                                        Long latestChapter = storySnapshot.child("chapters").getChildrenCount();

                                        // Tạo đối tượng History và thêm vào danh sách
                                        History history = new History(storyId, title, "", "", new ArrayList<>(), imageUrl, null, lastChapterRead, latestChapter);
                                        historyList.add(history);
                                        storyAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý lỗi truy vấn
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi truy vấn
            }
        });
    }
}
