package com.myapp.notepadapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    NotepadListAdapter adapter;

    /** @noinspection deprecation*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new NotepadListAdapter(this, R.layout.list_row);
        ListView listView = findViewById(R.id.user_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String filename = adapter.getItem(position);
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra("is_new", false);
            intent.putExtra("note_name", filename);
            startActivityForResult(intent, 1);

        });
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** @noinspection deprecation*/
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        if (item.getItemId() == R.id.add_button) {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra("is_new", true);
            intent.putExtra("note_name", getResources().getString(R.string.note_default_name));
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                adapter.updateFileList();
                adapter.notifyDataSetChanged();
            }
        }
    }
}