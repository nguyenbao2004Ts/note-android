package com.example.cuoikiltdd;

import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DaXoaGanDay extends AppCompatActivity {

    private Database database;
    private NoteAdapter1 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_da_xoa_gan_day);

        database = new Database(this);
        RecyclerView recyclerView = findViewById(R.id.note_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the list of recently deleted notes from the database
        Cursor cursor = database.getAllDeletedNotes();
        adapter = new NoteAdapter1(this, cursor);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.closeCursor();  // Close cursor using NoteAdapter's method
        }
    }

    // Method to go back to the Home screen
    public void backToHome(View view) {
        Intent intent = new Intent(this, TrangChu.class);
        startActivity(intent);
    }
}
