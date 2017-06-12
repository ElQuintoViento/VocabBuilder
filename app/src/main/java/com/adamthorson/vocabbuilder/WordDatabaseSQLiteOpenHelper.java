package com.adamthorson.vocabbuilder;

import static com.adamthorson.vocabbuilder.WordDatabaseContract.*;

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
    public long addWord(
            String word, String definition, String usage, int step, int listType, long epoch){
        SQLiteDatabase db = singletonInstance.getWritableDatabase();
        // Map values to columns
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordEntry.COLUMN_NAME_WORD, word);
        contentValues.put(WordEntry.COLUMN_NAME_DEFINITION, definition);
        contentValues.put(WordEntry.COLUMN_NAME_USAGE, usage);
        contentValues.put(WordEntry.COLUMN_NAME_STEP, step);
        contentValues.put(WordEntry.COLUMN_NAME_LIST, listType);
        contentValues.put(WordEntry.COLUMN_NAME_EPOCH, epoch);
        // Returns row's id
        return db.insert(WordDatabaseContract.WordEntry.TABLE_NAME, null, contentValues);

    }

    public long addWord(String word, String definition, String usage){
        return addWord(word, definition, usage, 0, WORD_LIST_PRE_QUEUE, System.currentTimeMillis());
    }


    public long addWord(Word word){
        return addWord(
                word.getWord(), word.getDefinition(), word.getUsage(),
                word.getStep(), word.getListType(), System.currentTimeMillis());
    }


    // Read
    public ArrayList<Word> getAllWords(){
        return getWords(null);
    }

    public ArrayList<Word> getPrequeueWords(){ return getWordList(WORD_LIST_PRE_QUEUE); }

    public ArrayList<Word> getActiveWords(){ return getWordList(WORD_LIST_ACTIVE); }

    public ArrayList<Word> getMaintenanceWords(){ return getWordList(WORD_LIST_MAINTENANCE); }

    public ArrayList<Word> getWordList(int listType){
        // Pseudo word list type
        if(listType == WORD_LIST_DECK){ return getDeckWords(); }
        // DB word list types
        else {
            HashMap<String, String> whereArgs = new HashMap<String, String>();
            whereArgs.put(WordEntry.COLUMN_NAME_LIST, Integer.toBinaryString(listType));
            return getWords(whereArgs);
        }
    }

    private ArrayList<Word> getWords(HashMap<String, String> whereArgs){
        Cursor cursor = getWordCursor(whereArgs);
        return getWordsFromCursor(cursor);
    }


    private ArrayList<Word> getDeckWords(){
        // This is INCORRECT
        return getAllWords();
    }


    private ArrayList<Word> getWordsFromCursor(Cursor cursor){
        ArrayList<Word> words = new ArrayList<Word>();

        Word word;
        while(cursor.moveToNext()){
            word = new Word(
                    cursor.getString(cursor.getColumnIndex(WordEntry.COLUMN_NAME_WORD)),
                    cursor.getString(cursor.getColumnIndex(WordEntry.COLUMN_NAME_DEFINITION)),
                    cursor.getString(cursor.getColumnIndex(WordEntry.COLUMN_NAME_USAGE)),
                    cursor.getInt(cursor.getColumnIndex(WordEntry._ID)),
                    cursor.getInt(cursor.getColumnIndex(WordEntry.COLUMN_NAME_STEP)),
                    cursor.getInt(cursor.getColumnIndex(WordEntry.COLUMN_NAME_LIST)),
                    cursor.getLong(cursor.getColumnIndex(WordEntry.COLUMN_NAME_EPOCH))
            );
            words.add(word);
        }

        return words;
    }


    private Cursor getWordCursor(HashMap<String, String> whereArgs){
        SQLiteDatabase db = singletonInstance.getReadableDatabase();

        // Projection specifies returned table columns
        String[] projection = {
                WordEntry._ID,
                WordEntry.COLUMN_NAME_WORD,
                WordEntry.COLUMN_NAME_DEFINITION,
                WordEntry.COLUMN_NAME_USAGE,
                WordEntry.COLUMN_NAME_STEP,
                WordEntry.COLUMN_NAME_LIST,
                WordEntry.COLUMN_NAME_EPOCH,
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
        String sortOrder = WordEntry.COLUMN_NAME_WORD + " DESC";

        return db.query(
                WordEntry.TABLE_NAME,
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
        String selection = String.format("%s LIKE ?", WordEntry.COLUMN_NAME_WORD);
        String[] selectionArg = new String[]{word};
        return db.delete(WordEntry.TABLE_NAME, selection, selectionArg);
    }
}
