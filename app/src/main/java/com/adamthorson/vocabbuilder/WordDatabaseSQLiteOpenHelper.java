package com.adamthorson.vocabbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tor on 5/27/17.
 */
public class WordDatabaseSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = WordDatabaseSQLiteOpenHelper.class.getSimpleName();
    //
    private static WordDatabaseSQLiteOpenHelper singletonInstance;


    public static synchronized WordDatabaseSQLiteOpenHelper getSingletonInstance(Context context){
        if(singletonInstance == null){
            singletonInstance = new WordDatabaseSQLiteOpenHelper(context);
        }
        return singletonInstance;
    }


    private WordDatabaseSQLiteOpenHelper(Context context){
        super(context, WordDatabaseContract.DATABASE_NAME, null, WordDatabaseContract.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WordDatabaseContract.WordEntry.SQL_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(WordDatabaseContract.WordEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    // Create
    public long addWord(String word, String definition){
        SQLiteDatabase db = singletonInstance.getWritableDatabase();
        // Map values to columns
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordDatabaseContract.WordEntry.COLUMN_NAME_WORD, word);
        contentValues.put(WordDatabaseContract.WordEntry.COLUMN_NAME_DEFINITION, definition);
        // Returns row's id
        return db.insert(WordDatabaseContract.WordEntry.TABLE_NAME, null, contentValues);
    }


    // Read
    public ArrayList<WordDatabaseContract.Word> getAllWords(){
        return getWords(null);
    }

    public ArrayList<WordDatabaseContract.Word> getPrequeueWords(){
        return getWordList(WordDatabaseContract.WORD_LIST_PRE_QUEUE);
    }

    public ArrayList<WordDatabaseContract.Word> getActiveWords(){
        return getWordList(WordDatabaseContract.WORD_LIST_ACTIVE);
    }

    public ArrayList<WordDatabaseContract.Word> getMaintenanceWords(){
        return getWordList(WordDatabaseContract.WORD_LIST_MAINTENANCE);
    }

    private ArrayList<WordDatabaseContract.Word> getWordList(int listType){
        HashMap<String, String> whereArgs = new HashMap<String, String>();
        whereArgs.put(
                WordDatabaseContract.WordEntry.COLUMN_NAME_LIST, Integer.toBinaryString(listType));
        return getWords(whereArgs);
    }

    private ArrayList<WordDatabaseContract.Word> getWords(HashMap<String, String> whereArgs){
        ArrayList<WordDatabaseContract.Word> words = new ArrayList<WordDatabaseContract.Word>();

        Cursor cursor = getWordCursor(whereArgs);

        WordDatabaseContract.Word word;
        while(cursor.moveToNext()){
            word = new WordDatabaseContract.Word(
                    cursor.getString(
                            cursor.getColumnIndex(WordDatabaseContract.WordEntry.COLUMN_NAME_WORD)),
                    cursor.getString(
                            cursor.getColumnIndex(WordDatabaseContract.WordEntry.COLUMN_NAME_DEFINITION)),
                    cursor.getString(
                            cursor.getColumnIndex(WordDatabaseContract.WordEntry.COLUMN_NAME_USAGE)),
                    cursor.getInt(
                            cursor.getColumnIndex(WordDatabaseContract.WordEntry._ID)),
                    cursor.getInt(
                            cursor.getColumnIndex(WordDatabaseContract.WordEntry.COLUMN_NAME_STEP)),
                    cursor.getInt(
                            cursor.getColumnIndex(WordDatabaseContract.WordEntry.COLUMN_NAME_LIST))
            );
            words.add(word);
        }

        return words;
    }

    private Cursor getWordCursor(HashMap<String, String> whereArgs){
        SQLiteDatabase db = singletonInstance.getReadableDatabase();

        // Projection specifies returned table columns
        String[] projection = {
                WordDatabaseContract.WordEntry._ID,
                WordDatabaseContract.WordEntry.COLUMN_NAME_WORD,
                WordDatabaseContract.WordEntry.COLUMN_NAME_DEFINITION,
                WordDatabaseContract.WordEntry.COLUMN_NAME_USAGE,
                WordDatabaseContract.WordEntry.COLUMN_NAME_STEP,
                WordDatabaseContract.WordEntry.COLUMN_NAME_LIST
        };

        // Filter by selection args
        String selection = null;
        ArrayList<String> selectionArgsList = null;
        String[] selectionArgs = null;
        if((whereArgs != null) && !whereArgs.isEmpty()){
            selection = "";
            selectionArgsList = new ArrayList<String>();
            Iterator iterator = whereArgs.entrySet().iterator();

            // Iterate over selection columns with args
            while(iterator.hasNext()){
                // Intersection linking
                if(selection.length() > 0){
                    selection += " AND ";
                }
                Map.Entry<String, String> pair = (
                        (Map.Entry<String, String>) iterator.next());
                selection += String.format("%s = ?", pair.getKey());
                selectionArgsList.add(pair.getValue());
            }

            selectionArgs = (String[]) selectionArgsList.toArray();
        }

        // Sorting rule
        String sortOrder = WordDatabaseContract.WordEntry.COLUMN_NAME_WORD + " DESC";

        return db.query(
                WordDatabaseContract.WordEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,           // Disable group rows
                null,           // Disable filter by row groups
                sortOrder
        );
    }


    // Update


    // Delete
    public int removeWord(String word){
        SQLiteDatabase db = singletonInstance.getWritableDatabase();
        String selection = String.format(
                "%s LIKE ?", WordDatabaseContract.WordEntry.COLUMN_NAME_WORD);
        String[] selectionArg = new String[]{word};
        return db.delete(WordDatabaseContract.WordEntry.TABLE_NAME, selection, selectionArg);
    }
}
