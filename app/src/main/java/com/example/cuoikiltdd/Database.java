package com.example.cuoikiltdd;

import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 6;  // Đảm bảo đây là phiên bản mới nhất

    // Tên các bảng
    public static final String TABLE_NAME_NOTES = "Notes";
    public static final String TABLE_NAME_ACCOUNT = "Account";
    public static final String TABLE_NAME_DELETED_NOTES = "DeletedNotes"; // Bảng DeletedNotes

    // Các cột của bảng Notes
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_IS_DELETED = "is_deleted";  // Cột is_deleted
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String COLUMN_IS_PINNED = "is_pinned";

    // Cột của bảng DeletedNotes
    public static final String COLUMN_DELETED_AT = "deleted_at";

    // SQL để tạo bảng Notes
    private static final String TABLE_CREATE_NOTES =
            "CREATE TABLE " + TABLE_NAME_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_IS_DELETED + " INTEGER DEFAULT 0, " +  // Cột is_deleted
                    COLUMN_IS_PINNED + " INTEGER DEFAULT 0);";  // Thêm cột is_pinned

    // SQL để tạo bảng Account
    private static final String TABLE_CREATE_ACCOUNT =
            "CREATE TABLE " + TABLE_NAME_ACCOUNT + " (" +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWORD + " TEXT);";

    // SQL để tạo bảng DeletedNotes
    private static final String TABLE_CREATE_DELETED_NOTES =
            "CREATE TABLE " + TABLE_NAME_DELETED_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_DELETED_AT + " INTEGER);";  // Cột deleted_at

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_NOTES);    // Tạo bảng Notes
        db.execSQL(TABLE_CREATE_ACCOUNT);  // Tạo bảng Account
        db.execSQL(TABLE_CREATE_DELETED_NOTES);  // Tạo bảng DeletedNotes

        // Thêm dữ liệu mẫu vào bảng Notes (Có thể bỏ qua khi triển khai thật)
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, "Ghi chú 1");
        values.put(COLUMN_CONTENT, "Tối mai 8h30 phải đi làm bài tập");
        db.insert(TABLE_NAME_NOTES, null, values);

        values.put(COLUMN_TITLE, "Ghi chú 2");
        values.put(COLUMN_CONTENT, "Tôi phải mau cuốn sách vào sáng hôm sau");
        db.insert(TABLE_NAME_NOTES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            // Xóa bảng DeletedNotes nếu tồn tại (đảm bảo bảng được tạo lại)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DELETED_NOTES);
            // Thêm cột is_deleted vào bảng Notes nếu cần
            db.execSQL("ALTER TABLE " + TABLE_NAME_NOTES + " ADD COLUMN " + COLUMN_IS_PINNED + " INTEGER DEFAULT 0");
            // Tạo lại bảng DeletedNotes
            db.execSQL(TABLE_CREATE_DELETED_NOTES);
        }
    }

    // Phương thức lấy tài khoản theo username
    public Cursor getAccount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn bảng Account để lấy thông tin tài khoản theo username
        String selection = COLUMN_USERNAME + " = ?";  // Điều kiện tìm kiếm
        String[] selectionArgs = { username };  // Tham số điều kiện

        // Trả về con trỏ chứa kết quả truy vấn
        return db.query(TABLE_NAME_ACCOUNT, null, selection, selectionArgs, null, null, null);
    }

    // Phương thức lấy tất cả ghi chú đã xóa
    public Cursor getAllDeletedNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME_DELETED_NOTES, null, null, null, null, null, null);
    }

    // Phương thức tìm kiếm ghi chú theo tiêu đề
    public Cursor searchNotesByTitle(String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn bảng Notes, chỉ tìm các ghi chú có tiêu đề chứa từ khóa
        String selection = COLUMN_TITLE + " LIKE ?";  // Điều kiện tìm kiếm
        String[] selectionArgs = { "%" + query + "%" };  // Tìm các tiêu đề có chứa từ khóa

        return db.query(TABLE_NAME_NOTES, null, selection, selectionArgs, null, null, null);
    }


    // Phương thức xóa ghi chú (chuyển nó vào bảng DeletedNotes)
    public void deleteNoteById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME_NOTES, new String[]{COLUMN_TITLE, COLUMN_CONTENT},
                "_id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));

            // Chèn vào bảng DeletedNotes
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_CONTENT, content);
            values.put(COLUMN_DELETED_AT, System.currentTimeMillis());  // Lưu thời gian xóa
            db.insert(TABLE_NAME_DELETED_NOTES, null, values);

            // Xóa khỏi bảng Notes
            db.delete(TABLE_NAME_NOTES, "_id = ?", new String[]{String.valueOf(id)});
            cursor.close();
        }
    }

    // Khôi phục ghi chú từ bảng DeletedNotes về bảng Notes
    public void restoreNoteById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME_DELETED_NOTES, new String[]{COLUMN_TITLE, COLUMN_CONTENT},
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));

            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_CONTENT, content);
            db.insert(TABLE_NAME_NOTES, null, values);

            db.delete(TABLE_NAME_DELETED_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            cursor.close();
        }
    }

    // Xóa vĩnh viễn ghi chú khỏi bảng DeletedNotes
    public void permanentlyDeleteNoteById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_DELETED_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }


    // Phương thức thêm ghi chú mới
    public boolean addNote(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        long result = db.insert(TABLE_NAME_NOTES, null, values);
        db.close();
        return result != -1;
    }

    // Phương thức thêm tài khoản mới
    public boolean addAccount(String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_NAME_ACCOUNT, null, values);
        db.close();
        return result != -1;
    }

    // Phương thức lấy tất cả ghi chú
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = Database.COLUMN_IS_PINNED + " DESC, " + Database.COLUMN_ID + " DESC";  // Sắp xếp theo is_pinned và ID
        return db.query(TABLE_NAME_NOTES, null, null, null, null, null, null);
    }

    // Phương thức cập nhật ghi chú
    public boolean updateNote(long id, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        int rowsAffected = db.update(TABLE_NAME_NOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }
}
