package com.adamthorson.vocabbuilder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * Created by tor on 5/31/17.
 */
public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<WordDatabaseContract.WordViewHolder> {
    private static final String TAG = ReviewRecyclerViewAdapter.class.getSimpleName();
    //
    private ArrayList<WordDatabaseContract.Word> words;


    public ReviewRecyclerViewAdapter(ArrayList<WordDatabaseContract.Word> words){
        this.words = words;
    }

    @Override
    public WordDatabaseContract.WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_review, parent, false);
        WordDatabaseContract.WordViewHolder wordViewHolder = (
                new WordDatabaseContract.WordViewHolder(view));
        return wordViewHolder;
    }

    @Override
    public void onBindViewHolder(WordDatabaseContract.WordViewHolder holder, int position) {
        WordDatabaseContract.Word word = words.get(position);
        holder.textViewWord.setText(word.getWord());
        holder.textViewDefinition.setText(word.getDefinition());
        holder.textViewUsage.setText(word.getUsage());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
