package com.hien.doctruyen.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseError;
import com.hien.doctruyen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hien.doctruyen.admin_adapter.AdminUserAdapter;
import com.hien.doctruyen.item.User;

import java.util.ArrayList;
import java.util.List;
import android.widget.SearchView;
import android.widget.Toast;

public class ManageUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private List<User> userList;
    private List<User> filteredList; // Danh sách đã lọc
    private SearchView searchView; // Thanh tìm kiếm

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        filteredList = new ArrayList<>(); // Khởi tạo danh sách đã lọc
        adapter = new AdminUserAdapter(filteredList, new AdminUserAdapter.OnUserActionListener() {
            @Override
            public void onDeleteUser(User user) {
                deleteUserFromFirebase(user);
            }

            @Override
            public void onBlockUser(User user) {
                blockUser(user);
            }
        });
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.search_view); // Tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });

        loadUsersFromFirebase();

        return view;
    }

    private void loadUsersFromFirebase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getUid() != null && !user.getUid().isEmpty() && "user".equals(user.getRole())) {
                        userList.add(user);
                    }
                }
                filteredList.clear(); // Xóa danh sách đã lọc cũ
                filteredList.addAll(userList); // Thêm tất cả người dùng vào danh sách đã lọc
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterUsers(String query) {
        filteredList.clear();
        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.notifyDataSetChanged(); // Cập nhật danh sách hiển thị
    }

    private void deleteUserFromFirebase(User user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        usersRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
            loadUsersFromFirebase(); // Tải lại danh sách người dùng
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi xóa người dùng", Toast.LENGTH_SHORT).show();
        });
    }

    private void blockUser(User user) {
        if (user == null || user.getUid() == null || user.getUid().isEmpty()) {
            Toast.makeText(getContext(), "Không thể khóa người dùng này (UID không hợp lệ)", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu UID không hợp lệ
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.child("blocked").setValue(!user.isBlocked()).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), user.isBlocked() ? "Đã mở khóa người dùng" : "Đã khóa người dùng", Toast.LENGTH_SHORT).show();
            loadUsersFromFirebase(); // Tải lại danh sách người dùng
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái người dùng", Toast.LENGTH_SHORT).show();
        });
    }

}
