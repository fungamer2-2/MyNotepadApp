package com.myapp.notepadapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NotepadUtils {
    public static void saveContent(Context context, String content, String filename) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadContent(Context context, String filename) throws IOException {
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line).append('\n');
            line = reader.readLine();
        }
        return stringBuilder.toString();
    }

    public static String loadShortPreview(Context context, String filename, int limit) {
        char[] c = new char[limit];
        File f = new File(context.getFilesDir(),filename);
        long actualSize = f.length();
        FileInputStream fis;
        try {
            fis = context.openFileInput(filename);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to open file");
            return "";
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        int numRead;
        try {
            numRead = reader.read(c, 0, limit);
        } catch (IOException e) {
            return "";
        }
        c = Arrays.copyOf(c, numRead);
        String preview = String.valueOf(c);
        if (preview.length() < actualSize) {
            preview += "...";
        }
        return preview;

    }

    public static boolean fileExists(Context context, String filename) {
        return new File(context.getFilesDir(), filename).exists();
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    public static void deleteContent(Context context, String filename) {
        File f = new File(context.getFilesDir(), filename);
        f.delete();
    }

    public static String deduplicateNoteName(Context context, String noteName) {
        String saveName;

        int i = 0;
        while (true) {
            saveName = noteName;
            if (i > 0) {
                saveName += " (" + i + ")";
            }
            if (!fileExists(context, saveName)) {
                break;
            }
            i++;
            if (i > 256) { // Set a high, but reasonable limit to avoid infinite loops
                return null;
            }
        }

        return saveName;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    public static void renameContent(Context context, String oldName, String newName) {
        if (oldName.equals(newName)) return;
        File dir = context.getFilesDir();
        File oldPath = new File(dir, oldName);
        File newPath = new File(dir, newName);
        oldPath.renameTo(newPath);
    }

    public static ValidationResult validateNoteName(String name) {
        if (name.isBlank()) {
            return ValidationResult.failure("Note name cannot be blank");
        }
        if (name.contains("/")) {
            return ValidationResult.failure("Note name cannot contain '/'");
        }
        return ValidationResult.success();
    }


    public static class ValidationResult {
        boolean result;
        String message;

        public ValidationResult(boolean success, String message) {
            this.result = success;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        /** @noinspection BooleanMethodIsAlwaysInverted*/
        public boolean isSuccess() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }
}
