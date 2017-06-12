package com.adamthorson.vocabbuilder;

import static com.adamthorson.vocabbuilder.WordDatabaseContract.*;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements MainActivity.WordListener{
    private static final String TAG = ReviewFragment.class.getSimpleName();
    private static final int[] LIST_TYPES = new int[]{WORD_LIST_DECK};
    // UI
    private View view;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ImageButton imageButtonCheck;
    private ImageButton imageButtonCancel;


    @Override
    public void onWordListenerRegistered(int[] listTypes){
        Log.d(TAG, "Registered with list types: " + Arrays.toString(listTypes));
        // Get first list
        ((MainActivity) getActivity()).updateWordListener(this);
    }


    @Override
    public void onWordListenerUpdate(ArrayList<Word> words, int listType) {
        ReviewRecyclerViewAdapter reviewRecyclerViewAdapter = (
                new ReviewRecyclerViewAdapter(words));
        recyclerView.setAdapter(reviewRecyclerViewAdapter);
        reviewRecyclerViewAdapter.notifyDataSetChanged();
    }


    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_review, container, false);

        setupRecyclerView();
        setupUI();

        return view;
    }


    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Attempting to register list types: " + Arrays.toString(LIST_TYPES));
        ((MainActivity) getActivity()).registerWordListener(this, LIST_TYPES);
    }


    @Override
    public void onDestroy(){
        ((MainActivity) getActivity()).unregisterWordListener(this);
        super.onDestroy();
    }


    private void setupRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_review_card);
        recyclerView.setHasFixedSize(true);

        ReviewRecyclerViewAdapter reviewRecyclerViewAdapter = (
                new ReviewRecyclerViewAdapter(new ArrayList<Word>()));
        recyclerView.setAdapter(reviewRecyclerViewAdapter);

        linearLayoutManager = new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    private void setupUI(){
        imageButtonCheck = (ImageButton) view.findViewById(R.id.image_button_check);
        imageButtonCancel = (ImageButton) view.findViewById(R.id.image_button_cancel);

        // Handle card view
        imageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){ handleCardView(true); }
        });
        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){ handleCardView(false); }
        });
    }


    private void handleCardView(boolean sansDifficulty){
        Toast.makeText(
                getActivity().getApplicationContext(),
                "Position " + linearLayoutManager.findFirstCompletelyVisibleItemPosition(),
                Toast.LENGTH_SHORT).show();
    }
}
