package com.example.cuoikiltdd;

import android.os.Bundle;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Sua extends AppCompatActivity {

    private EditText editTextTitle, editTextContent;
    private long noteId;  // Biến để lưu id của ghi chú
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        noteId = intent.getLongExtra("note_id", -1);  // Lấy note_id từ Intent
        String noteTitle = intent.getStringExtra("note_title");
        String noteContent = intent.getStringExtra("note_content");

        // Ánh xạ EditText và nút "Sửa"
        editTextTitle = findViewById(R.id.editTextText);
        editTextContent = findViewById(R.id.editTextText2);
        Button buttonUpdate = findViewById(R.id.button);

        // Hiển thị dữ liệu ghi chú vào EditText
        editTextTitle.setText(noteTitle);
        editTextContent.setText(noteContent);

        // Khởi tạo Database
        database = new Database(this);

        // Sự kiện khi nhấn nút "Sửa"
        buttonUpdate.setOnClickListener(v -> {
            String newTitle = editTextTitle.getText().toString();
            String newContent = editTextContent.getText().toString();

            if (database.updateNote(noteId, newTitle, newContent)) {
                Toast.makeText(Sua.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                // Kích hoạt làm mới danh sách ghi chú trong activity chính
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(Sua.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm quay lại khi nhấn vào nút quay lại
    public void backToGhiChu(View view) {
        finish();
    }
}
