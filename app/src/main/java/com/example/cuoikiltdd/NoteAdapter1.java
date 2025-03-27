package com.example.cuoikiltdd;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;

public class NoteAdapter1 extends RecyclerView.Adapter<NoteAdapter1.NoteViewHolder> {
    private Context context;
    private Cursor cursor;
    private Database database;

    // Constructor
    public NoteAdapter1(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.database = new Database(context);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout for each note item
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        // Get data from cursor
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        long id = cursor.getLong(cursor.getColumnIndex("_id"));

        holder.titleTextView.setText(title);
        holder.itemView.setTag(id);  // Set the ID for the item

        // Show note detail when clicking on the item
        holder.itemView.setOnClickListener(v -> showNoteDetail(title, content));

        // Show "..." menu when clicking on the "More" button
        holder.moreOptions.setOnClickListener(v -> showDeletedOptionsDialog(id, title, content));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Show note detail in a dialog
    private void showNoteDetail(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Show options dialog for deleted notes (Restore and Permanently Delete)
    private void showDeletedOptionsDialog(long id, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tùy Chọn")
                .setItems(new String[]{"Khôi phục", "Xóa vĩnh viễn"}, (dialog, which) -> {
                    if (which == 0) {  // Restore note
                        database.restoreNoteById(id);  // Restore note from DeletedNotes to main Notes table
                        swapCursor(database.getAllDeletedNotes());  // Update the list of deleted notes
                    } else if (which == 1) {  // Permanently delete note
                        database.permanentlyDeleteNoteById(id);  // Permanently delete from database
                        swapCursor(database.getAllDeletedNotes());  // Update the list of deleted notes
                    }
                });
        builder.create().show();
    }

    // Method to update the cursor when data changes
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();  // Notify the adapter that the data has changed
        }
    }

    // Ensure to close the cursor when it's no longer in use
    public void closeCursor() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    // ViewHolder class to represent a note item
    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView moreOptions;  // ImageView for the "..." button

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            moreOptions = itemView.findViewById(R.id.note_more_options);  // Connect to the "..." button
        }
    }
}
