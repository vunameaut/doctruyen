package com.hien.doctruyen.user;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.hien.doctruyen.R;
import com.hien.doctruyen.user_adapter.ViewPagerAdapter;
import com.hien.doctruyen.user_adapter.userStoryAdapter;
import com.hien.doctruyen.item.Story;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private userStoryAdapter storyAdapter;
    private List<Story> storyList;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private List<String> imageUrls;
    private Handler sliderHandler;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter cho RecyclerView
        storyList = new ArrayList<>();
        storyAdapter = new userStoryAdapter(storyList, getContext());  // Truyền context cho adapter
        recyclerView.setAdapter(storyAdapter);

        // Khởi tạo ViewPager2 cho slider
        viewPager2 = view.findViewById(R.id.viewpager_highlight);
        imageUrls = new ArrayList<>();
        viewPagerAdapter = new ViewPagerAdapter(imageUrls, getContext());
        viewPager2.setAdapter(viewPagerAdapter);

        sliderHandler = new Handler(Looper.getMainLooper());
        sliderHandler.postDelayed(sliderRunnable, 3000);  // Tự động chuyển slide

        // Load dữ liệu từ Firebase
        loadStoriesFromFirebase();
        loadImagesFromFirebaseStorage();

        // Thiết lập SearchView cho tìm kiếm
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStories(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchStories(newText);
                return false;
            }
        });

        return view;
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager2.getCurrentItem();
            int totalItems = viewPagerAdapter.getItemCount();

            if (currentItem < totalItems - 1) {
                viewPager2.setCurrentItem(currentItem + 1);
            } else {
                viewPager2.setCurrentItem(0);
            }
            sliderHandler.postDelayed(this, 3000);  // Tiếp tục sau 3 giây
        }
    };

    private void loadImagesFromFirebaseStorage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("highlight_images");

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.add(uri.toString());
                            viewPagerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error loading images: " + e.getMessage());
            }
        });
    }

    private void loadStoriesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("stories");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    if (story != null) {
                        storyList.add(story);
                        Log.d(TAG, "Loaded: " + story.getTitle());
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void searchStories(String query) {
        List<Story> filteredList = new ArrayList<>();
        for (Story story : storyList) {
            if (story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(story);
            }
        }
        storyAdapter.updateList(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);  // Ngăn memory leak
    }
}
