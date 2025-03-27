package com.example.cuoikiltdd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        editTextUsername = findViewById(R.id.editText2);
        editTextPassword = findViewById(R.id.editText3);
        Button loginButton = findViewById(R.id.login_button);
        Button btnDangKi = findViewById(R.id.btnDangKi);

        // Initialize database
        database = new Database(this);

        // Handle login button click
        loginButton.setOnClickListener(v -> checkLogin());

        // Navigate to registration screen on "Đăng Kí" button click
        btnDangKi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DangKi.class);
            startActivity(intent);
        });
    }

    // Method to check login
    private void checkLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui Lòng Nhập Tên Người Dùng Và Mật Khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query the account from the database
        Cursor cursor = database.getAccount(username);
        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndex(Database.COLUMN_PASSWORD));

            if (storedPassword.equals(password)) {
                // Lưu tên người dùng vào SharedPreferences
                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", username);  // Lưu tên người dùng
                editor.apply();

                // Login successful, navigate to TrangChu
                Intent intent = new Intent(MainActivity.this, TrangChu.class);
                startActivity(intent);
                finish();
            } else {
                // Incorrect password
                Toast.makeText(this, "Mật Khẩu Chưa Chính Xác", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Username not found
            Toast.makeText(this, "Không Tìm Thấy Tài Khoản.", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}
