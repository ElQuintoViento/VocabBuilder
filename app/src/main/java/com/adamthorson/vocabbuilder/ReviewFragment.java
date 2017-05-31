package com.adamthorson.vocabbuilder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {


    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        ArrayList<WordDatabaseContract.Word> words = new ArrayList<>();
        words.add(new WordDatabaseContract.Word("test 0", "definition", "usage", 0, 0, 1));
        words.add(new WordDatabaseContract.Word("test 1", "definition", "usage", 0, 0, 1));
        words.add(new WordDatabaseContract.Word("test 2", "definition", "usage", 0, 0, 1));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_review_card);
        recyclerView.setHasFixedSize(true);
        ReviewRecyclerViewAdapter reviewRecyclerViewAdapter = (
                new ReviewRecyclerViewAdapter(words));
        recyclerView.setAdapter(reviewRecyclerViewAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }


}
