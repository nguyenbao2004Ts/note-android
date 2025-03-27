package com.example.cuoikiltdd;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "user_prefs";  // Tên file SharedPreferences
    private static final String KEY_USERNAME = "username";  // Key cho tên người dùng
    private static final String KEY_PASSWORD = "password";  // Key cho mật khẩu

    // Phương thức lưu thông tin đăng nhập
    public static void saveUserLogin(Context context, String username, String password) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();  // Lưu thông tin vào SharedPreferences
    }

    // Phương thức lấy tên người dùng đã lưu
    public static String getUsername(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USERNAME, null);  // Trả về null nếu không tìm thấy
    }

    // Phương thức lấy mật khẩu đã lưu
    public static String getPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_PASSWORD, null);  // Trả về null nếu không tìm thấy
    }

    // Phương thức xóa thông tin đăng nhập khi đăng xuất
    public static void clearUserLogin(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Xóa tất cả dữ liệu
        editor.apply();
    }
}