package com.example.cuoikiltdd;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

public class TrangChu extends AppCompatActivity {

    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu);

        usernameTextView = findViewById(R.id.username_text);
        // Lấy username từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = preferences.getString("username", "User"); // Mặc định là "User" nếu không có username

        usernameTextView.setText(username);
        // Xử lý hệ thống thanh trạng thái và thanh điều hướng
        findViewById(R.id.main).setPadding(0, 50, 0, 0); // Thêm một chút padding trên cùng nếu cần

        // Xử lý hệ thống thanh trạng thái và thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Phương thức mở màn hình Ghi chú
    public void openGhiChu(View view) {
        Intent intent = new Intent(this, GhiChu.class);
        startActivity(intent);
    }

    // Phương thức đăng xuất và quay lại màn hình đăng nhập
    public void backToHome(View view) {
        // Hiển thị hộp thoại xác nhận trước khi đăng xuất
        showLogoutConfirmationDialog();
    }

    // Phương thức mở màn hình Đã Xóa Gần Đây
    public void openDaXoaGanDay(View view) {
        Intent intent = new Intent(this, DaXoaGanDay.class);
        startActivity(intent);
    }

    // Hiển thị hộp thoại xác nhận đăng xuất
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setCancelable(false)  // Không thể hủy bỏ bằng cách bấm ra ngoài
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu người dùng đồng ý, thực hiện đăng xuất
                        logout();
                    }
                })
                .setNegativeButton("Hủy", null)  // Nếu bấm "Hủy", đóng hộp thoại
                .show();
    }

    // Phương thức thực hiện đăng xuất
    private void logout() {
        // Xóa thông tin đăng nhập khỏi SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Xóa tất cả các giá trị lưu trữ
        editor.apply();

        // Quay lại màn hình đăng nhập (MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đảm bảo clear các activity trước đó
        startActivity(intent);
        finish(); // Kết thúc activity hiện tại
    }
}
