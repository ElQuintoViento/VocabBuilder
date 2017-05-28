package com.adamthorson.vocabbuilder;

import android.app.Activity;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
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

        setupNavigationDrawer();
        setupToolbar();
        setupDatabase();
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
            addWord();
            return true;
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set left drawer menu icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Hide app label
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // UI toolbar elements
        editTextSearchToolbar = (EditText) toolbar.findViewById(R.id.edit_text_toolbar_search);
    }

    private void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment f = null;
                int itemId = menuItem.getItemId();

                if (itemId == R.id.action_drawer_main) {
                    f = new MainFragment();
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
        });

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

    private void addWord(){
        String word = editTextSearchToolbar.getText().toString();
        // Skip if missing text
        if(word.length() < 1){ return; }
        // Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
        // Clear text
        editTextSearchToolbar.setText("");
    }
}
