package com.example.cuoikiltdd;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DangKi extends AppCompatActivity {

    private EditText emailInput, usernameInput, passwordInput, confirmPasswordInput;
    private Button signInButton, cancelButton;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);

        emailInput = findViewById(R.id.editText);
        usernameInput = findViewById(R.id.editText2);
        passwordInput = findViewById(R.id.editText3);
        confirmPasswordInput = findViewById(R.id.editText4);
        signInButton = findViewById(R.id.btnLogin);
        cancelButton = findViewById(R.id.btnCancel);

        database = new Database(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccount();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về MainActivity
            }
        });
    }

    private void registerAccount() {
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Email không hợp lệ");
            return;
        }

        if (!password.matches("\\d{8,}")) {
            passwordInput.setError("Mật khẩu phải có từ 8 ký tự trở lên");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        if (database.addAccount(email, username, password)) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Tên người dùng đã tồn tại!", Toast.LENGTH_SHORT).show();
        }
    }
}
