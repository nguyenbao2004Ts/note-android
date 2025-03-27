package com.example.cuoikiltdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.Intent;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private Cursor cursor;
    private Database database;

    public NoteAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.database = new Database(context);
    }
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        // Lấy dữ liệu từ cursor
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
        int isPinned = cursor.getInt(cursor.getColumnIndex(Database.COLUMN_IS_DELETED)); // Lấy trạng thái "ghim"

        holder.titleTextView.setText(title);
        holder.itemView.setTag(id);

        // Cập nhật biểu tượng sao
        setStarIcon(holder.pinIcon, isPinned == 1);  // Nếu isPinned = 1, sao đầy (ghim)

        // Xử lý nhấn vào sao để ghim hoặc hủy ghim
        holder.pinIcon.setOnClickListener(v -> {
            if (isPinned == 1) {
                unpinNote(id);  // Nếu ghi chú đã ghim, hủy ghim
            } else {
                pinNote(id);  // Nếu ghi chú chưa ghim, thực hiện ghim
            }
        });

        // Hiển thị chi tiết ghi chú khi nhấp chuột trái
        holder.itemView.setOnClickListener(v -> showNoteDetail(title, content));

        // Hiển thị menu "..." khi nhấp vào nút "More"
        holder.moreOptions.setOnClickListener(v -> showOptionsDialog(id, title, content));

    }

    private void setStarIcon(ImageView starIcon, boolean isPinned) {
        if (isPinned) {
            starIcon.setImageResource(R.drawable.ic_star_filled); // Sao đầy
        } else {
            starIcon.setImageResource(R.drawable.ic_star_outline); // Sao trống
        }
    }

    // Phương thức ghim ghi chú lên đầu
    private void pinNote(long id) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Database.COLUMN_IS_DELETED, 1);  // Đánh dấu ghi chú là ghim
        db.update(Database.TABLE_NAME_NOTES, values, Database.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        swapCursor(database.getAllNotes());  // Cập nhật lại danh sách ghi chú
    }

    // Phương thức hủy ghim ghi chú
    private void unpinNote(long id) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Database.COLUMN_IS_DELETED, 0);  // Hủy đánh dấu ghi chú là ghim
        db.update(Database.TABLE_NAME_NOTES, values, Database.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        swapCursor(database.getAllNotes());  // Cập nhật lại danh sách ghi chú
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private void showNoteDetail(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showOptionsDialog(long id, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tùy Chọn")
                .setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {  // Sửa ghi chú
                        Intent editIntent = new Intent(context, Sua.class);
                        editIntent.putExtra("note_id", id);
                        editIntent.putExtra("note_title", title);
                        editIntent.putExtra("note_content", content);
                        context.startActivity(editIntent);
                    } else if (which == 1) {  // Xóa ghi chú và chuyển sang bảng DeletedNotes
                        database.deleteNoteById(id);  // Xóa ghi chú và chuyển vào DeletedNotes
                        swapCursor(database.getAllNotes());  // Cập nhật danh sách
                    }
                });
        builder.create().show();
    }


    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }


    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView pinIcon;  // Khai báo pinIcon
        ImageView moreOptions;  // Thêm ImageView cho nút "..."

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            pinIcon = itemView.findViewById(R.id.note_pin);
            moreOptions = itemView.findViewById(R.id.note_more_options);  // Kết nối với nút "..."
        }
    }
}
