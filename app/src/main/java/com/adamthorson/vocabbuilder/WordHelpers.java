package com.adamthorson.vocabbuilder;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by tor on 5/29/17.
 */
public final class WordHelpers {
    private static final String TAG = WordHelpers.class.getSimpleName();

    // Prevent instantiation
    private WordHelpers(){};

    // UI
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    // Non UI
    public static String capitalizeFirst(String str){
        if((str == null) || (str.length() < 2)){ return ""; }
        return (
                str.substring(0, 1).toUpperCase() +
                str.substring(1).toLowerCase()
        );
    }
}
