package com.adamthorson.vocabbuilder;

import static com.adamthorson.vocabbuilder.WordConstants.*;
import static com.adamthorson.vocabbuilder.WordDatabaseContract.*;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    // Interface & Associated object
    public interface WordListener{
        public void onWordListenerRegistered(int[] listTypes);
        public void onWordListenerUpdate(ArrayList<Word> words, int listType);
    }
    private class WordListenerObject{
        private WordDatabaseSQLiteOpenHelper wordDatabaseSQLiteOpenHelper;
        private WordListener listener;
        private int[] listTypes;

        public WordListenerObject(
                WordDatabaseSQLiteOpenHelper wordDatabaseSQLiteOpenHelper, WordListener listener, int[] listTypes){
            this.wordDatabaseSQLiteOpenHelper = wordDatabaseSQLiteOpenHelper;
            this.listener = listener;
            this.listTypes = listTypes;
        }

        public WordListener getListener(){ return listener; }

        public void notifyDatasetChanged(){
            for(int listType : listTypes){
                listener.onWordListenerUpdate(
                        wordDatabaseSQLiteOpenHelper.getWordList(listType), listType
                );
            }
        }
    }
    // Class constants
    private static final String TAG = MainActivity.class.getSimpleName();
    // WordListeners
    private ArrayList<WordListenerObject> wordListenerObjects;
    // Database
    public WordDatabaseSQLiteOpenHelper wordDatabaseSQLiteOpenHelper;
    // UI
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView listViewDrawer;
    private Toolbar toolbar;
    private EditText editTextSearchToolbar;
    // Other
    private String[] drawerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Keyboard slides over Activity's layout without affecting layout dimensions
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setupNavigationDrawer();
        setupToolbar();
        setupDatabase();

        wordListenerObjects = new ArrayList<WordListenerObject>();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if(id == R.id.action_add_word){
            createNewWordIntent();
            return true;
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode == REQUEST_CODE_WORD){
            if(resultCode == Activity.RESULT_OK){ addWord(intent); }
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set left drawer menu icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Hide app label
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // UI toolbar_main elements
        editTextSearchToolbar = (EditText) toolbar.findViewById(R.id.edit_text_toolbar_search);
    }


    private void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return setFragment(menuItem.getItemId());
            }
        });
        setFragment(R.id.action_drawer_main);

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };
        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);
    }


    private void setupDatabase(){
        wordDatabaseSQLiteOpenHelper = WordDatabaseSQLiteOpenHelper.getSingletonInstance(
                getApplicationContext());
    }


    private boolean setFragment(int itemId){
        Fragment f = null;

        if (itemId == R.id.action_drawer_main) {
            f = new ReviewFragment();
        } else if (itemId == R.id.action_drawer_settings) {
            f = new SettingsFragment();
        }

        if (f != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, f);
            transaction.commit();
            drawerLayout.closeDrawers();
            return true;
        }

        return false;
    }


    private synchronized WordListenerObject getWordListenerObject(WordListener listener){
        WordListenerObject wordListenerObject = null;
        for(WordListenerObject listenerObject : wordListenerObjects){
            if(listenerObject.getListener().equals(listener)){
                wordListenerObject = listenerObject;
                break;
            }
        }
        return wordListenerObject;
    }


    public synchronized void registerWordListener(WordListener listener, int[] listTypes){
        wordListenerObjects.add(
                new WordListenerObject(
                        wordDatabaseSQLiteOpenHelper, listener, listTypes
                )
        );
        listener.onWordListenerRegistered(listTypes);
    }


    public synchronized void unregisterWordListener(WordListener listener){
        WordListenerObject wordListenerObject = getWordListenerObject(listener);
        // Unregister if exists
        if(wordListenerObject != null){ wordListenerObjects.remove(wordListenerObject); }
    }


    private synchronized void updateWordListeners(){
        for(WordListenerObject listenerObject : wordListenerObjects){
            listenerObject.notifyDatasetChanged();
        };
    }


    public synchronized void updateWordListener(WordListener listener){
        WordListenerObject wordListenerObject = getWordListenerObject(listener);
        wordListenerObject.notifyDatasetChanged();
    }


    private void createNewWordIntent(){
        String word = editTextSearchToolbar.getText().toString();
        // Skip if missing text
        if(word.length() < 1){ return; }
        // Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
        // Clear text
        editTextSearchToolbar.setText("");
        // Clear keyboard
        View view = getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // Make intent
        Intent intent = new Intent(this, WordActivity.class);
        intent.putExtra(REQUEST_STRING_WORD, word);
        startActivityForResult(intent, REQUEST_CODE_WORD);
    }


    // Handle onActivityResult
    private void addWord(Intent intent){
        Word word = ((Word)intent.getSerializableExtra(REQUEST_OBJ_WORD));
        wordDatabaseSQLiteOpenHelper.addWord(word);
        updateWordListeners();
    }
}
