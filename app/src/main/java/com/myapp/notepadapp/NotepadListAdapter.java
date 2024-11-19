package com.myapp.notepadapp;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class NotepadListAdapter extends ArrayAdapter<String> {

    public NotepadListAdapter(Context context, int resource) {
        super(context, resource, new ArrayList<>(Arrays.asList(context.fileList())));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        String filename = getItem(position);
        String contentPreview = NotepadUtils.loadShortPreview(getContext(), filename, 32);

        TextView textView1 = currentItemView.findViewById(R.id.note_name);
        textView1.setText(filename);

        TextView textView2 = currentItemView.findViewById(R.id.note_content_preview);
        textView2.setText(contentPreview);

        TextView renameNote = currentItemView.findViewById(R.id.rename_note_button);
        TextView deleteNote = currentItemView.findViewById(R.id.delete_note_button);

        renameNote.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Rename Note");

            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(filename);
            builder.setView(input);

            builder.setPositiveButton("Rename", (dialog, which) -> {
                String newName = input.getText().toString().strip();
                NotepadUtils.ValidationResult result = NotepadUtils.validateNoteName(newName);
                if (!result.isSuccess()) {
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                newName = NotepadUtils.deduplicateNoteName(getContext(), newName);
                if (newName == null) {
                    return;
                }
                NotepadUtils.renameContent(getContext(), filename, newName);
                this.updateFileList();
                this.notifyDataSetChanged();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();

        });
        deleteNote.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Note");
            builder.setMessage(String.format(
                    getContext().getResources().getString(R.string.delete_confirm_msg),
                    filename
            ));
            builder.setPositiveButton("Yes", (dialog, i) -> {
                NotepadUtils.deleteContent(getContext(), filename);
                this.updateFileList();
                this.notifyDataSetChanged();
            });
            builder.setNegativeButton("No", (dialog, i) -> dialog.cancel());
            builder.show();
        });

        return currentItemView;
    }

    public void updateFileList() {
        this.clear();
        this.addAll(getContext().fileList());
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
