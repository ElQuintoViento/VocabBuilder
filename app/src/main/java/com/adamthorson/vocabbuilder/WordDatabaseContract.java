package com.adamthorson.vocabbuilder;

import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by tor on 5/27/17.
 */
public final class WordDatabaseContract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "vocabBuilderWordDatabase.db";
    // Other
    public static final int WORD_LIST_DECK = -1;        // Pseudo word list
    public static final int WORD_LIST_PRE_QUEUE = 0;
    public static final int WORD_LIST_ACTIVE = 1;
    public static final int WORD_LIST_MAINTENANCE = 2;


    // Prevent from unnecessary instantiation
    private WordDatabaseContract(){};


    // Word table
    // Contract entry definition
    public static class WordEntry implements BaseColumns{
        // Table and column names
        public static final String TABLE_NAME = "word";
        public static final String COLUMN_NAME_DEFINITION = "definition";
        public static final String COLUMN_NAME_EPOCH = "epoch";
        public static final String COLUMN_NAME_LIST = "list";
        public static final String COLUMN_NAME_STEP = "step";
        public static final String COLUMN_NAME_USAGE = "usage";
        public static final String COLUMN_NAME_WORD = "word";


        public static final String SQL_CREATE_TABLE = (
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_DEFINITION + " TEXT NOT NULL," +
                        COLUMN_NAME_EPOCH + " INTEGER NOT NULL," +
                        COLUMN_NAME_USAGE + " TEXT NOT NULL," +
                        COLUMN_NAME_LIST + " INTEGER NOT NULL DEFAULT " + WORD_LIST_PRE_QUEUE + "," +
                        COLUMN_NAME_STEP + " INTEGER NOT NULL DEFAULT -1," +
                        COLUMN_NAME_WORD + " TEXT NOT NULL" +
                        ")"
                );

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // Object definition
    public static class Word implements Serializable{
        private String definition;
        private String usage;
        private String word;
        private int id;
        private int step = 0;
        private int listType = WORD_LIST_PRE_QUEUE;
        private long epoch;


        public Word(String word, String definition, String usage, int id, int step, int listType, long epoch){
            this.word = word;
            this.definition = definition;
            this.usage = usage;
            this.id = id;
            this.step = step;
            this.listType = listType;
            this.epoch = epoch;
        }


        public String getDefinition(){ return definition; }

        public String getUsage(){ return usage; }

        public String getWord(){ return word; }

        public int getId(){ return id; }

        public int getListType(){ return listType; }

        public int getStep(){ return step; }

        public long getEpoch(){ return epoch; }

        public boolean isInPrequeueList(){ return (listType == WORD_LIST_PRE_QUEUE); }

        public boolean isInActiveList(){ return (listType == WORD_LIST_ACTIVE); }

        public boolean isInMaintenanceList(){ return (listType == WORD_LIST_MAINTENANCE); }
    }

    // RecyclerView.ViewHolder definition
    public static class WordViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewWord;
        public TextView textViewDefinition;
        public TextView textViewUsage;

        public WordViewHolder(View view){
            super(view);
            textViewWord = (TextView) view.findViewById(R.id.text_view_word);
            textViewDefinition = (TextView) view.findViewById(R.id.text_view_definition);
            textViewUsage = (TextView) view.findViewById(R.id.text_view_usage);
        }
    }
}
