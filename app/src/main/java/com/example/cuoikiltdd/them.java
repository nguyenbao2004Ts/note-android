package com.example.cuoikiltdd;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Toast;

public class them extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them);

        // Ánh xạ các EditText và tạo đối tượng Database
        titleEditText = findViewById(R.id.editTextText);
        contentEditText = findViewById(R.id.editTextText2);
        database = new Database(this);
    }

    // Phương thức để lưu ghi chú vào database
    public void addNote(View view) {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            // Nếu tiêu đề hoặc nội dung trống thì hiển thị thông báo lỗi
            Toast.makeText(this, "Vui lòng nhập tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
        } else {
            // Lưu ghi chú vào database
            boolean isInserted = database.addNote(title, content);

            if (isInserted) {
                // Nếu lưu thành công, thông báo và quay về màn hình Ghi Chú
                Toast.makeText(this, "Ghi chú đã được thêm", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, GhiChu.class);
                startActivity(intent);
            } else {
                // Nếu có lỗi trong quá trình lưu
                Toast.makeText(this, "Lỗi khi thêm ghi chú", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Phương thức trở về màn hình Ghi Chú
    public void backToGhiChu(View view) {
        Intent intent = new Intent(this, GhiChu.class);
        startActivity(intent);
    }
}