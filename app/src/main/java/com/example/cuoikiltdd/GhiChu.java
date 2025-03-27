package com.example.cuoikiltdd;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GhiChu extends AppCompatActivity {

    private Database database;
    private NoteAdapter adapter;
    private EditText searchText;  // Thêm biến để lưu trữ EditText tìm kiếm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghi_chu);

        database = new Database(this);
        RecyclerView recyclerView = findViewById(R.id.note_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách tất cả ghi chú
        Cursor cursor = database.getAllNotes();
        adapter = new NoteAdapter(this, cursor);
        recyclerView.setAdapter(adapter);

        // Kết nối với EditText tìm kiếm
        searchText = findViewById(R.id.search_text);

        // Lắng nghe sự kiện thay đổi văn bản trong EditText
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Khi văn bản thay đổi, thực hiện tìm kiếm
                String query = charSequence.toString();
                searchNotes(query);  // Tìm kiếm ghi chú theo từ khóa
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Nút Thêm ghi chú
        Button btnThem = findViewById(R.id.btnThem);
        btnThem.setOnClickListener(v -> {
            Intent intent = new Intent(GhiChu.this, them.class);
            startActivity(intent);
        });
    }

    // Phương thức tìm kiếm ghi chú trong cơ sở dữ liệu
    private void searchNotes(String query) {
        // Truy vấn cơ sở dữ liệu tìm ghi chú theo tiêu đề chứa từ khóa
        Cursor cursor = database.searchNotesByTitle(query);
        adapter.swapCursor(cursor);  // Cập nhật adapter với kết quả tìm kiếm
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(database.getAllNotes());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();  // Đóng database khi Activity bị hủy
    }

    // Phương thức trở về màn hình Trang Chủ
    public void backToHome(View view) {
        Intent intent = new Intent(this, TrangChu.class);
        startActivity(intent);
    }
}
