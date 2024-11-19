package com.myapp.notepadapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

public class EditNoteActivity extends AppCompatActivity {
    String noteName;
    boolean isNewNote;
    EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);

        noteText = findViewById(R.id.notepadEditText);
        Intent intent = getIntent();

        noteName = intent.getStringExtra("note_name");
        isNewNote = intent.getBooleanExtra("is_new", false);

        Objects.requireNonNull(getSupportActionBar()).setTitle(noteName);

        if (!isNewNote) {
            try {
                noteText.setText(NotepadUtils.loadContent(this, noteName));
            } catch (IOException e) {
                Toast.makeText(this, "Error loading note", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** @noinspection ExtractMethodRecommender*/
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        if (item.getItemId() == R.id.save_button) {
            if (isNewNote) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save Note");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(noteName);
                builder.setView(input);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().strip();
                    NotepadUtils.ValidationResult result = NotepadUtils.validateNoteName(name);
                    if (!result.isSuccess()) {
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    name = NotepadUtils.deduplicateNoteName(this, name);
                    if (name == null) {
                        Toast.makeText(this, "Too many notes with this name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    noteName = name;
                    String noteContent = noteText.getText().toString();
                    NotepadUtils.saveContent(this, noteContent, noteName);
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

                    Intent intent = getIntent();
                    intent.putExtra("event", "saved");
                    setResult(RESULT_OK, intent);
                    finish();

                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialogInterface -> {
                    Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setFocusable(true);
                    button.setFocusableInTouchMode(true);
                    button.requestFocus();
                });
                dialog.show();
            } else {
                String noteContent = noteText.getText().toString();
                NotepadUtils.saveContent(EditNoteActivity.this, noteContent, noteName);
                Toast.makeText(EditNoteActivity.this, "Saved!", Toast.LENGTH_SHORT).show();

                Intent intent = getIntent();
                intent.putExtra("event", "saved");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}