package com.adamthorson.vocabbuilder;

import static com.adamthorson.vocabbuilder.WordConstants.*;
import static com.adamthorson.vocabbuilder.WordDatabaseContract.*;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WordActivity extends AppCompatActivity {
    private static final String TAG = WordActivity.class.getSimpleName();
    // UI
    private EditText editTextDefinition;
    private EditText editTextUsage;
    //
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        handleIncomingIntent();
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            handleOutgoingIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleIncomingIntent(){
        Intent intent = getIntent();
        word = WordHelpers.capitalizeFirst(intent.getStringExtra(REQUEST_STRING_WORD));
        setupUI();
    }

    private void handleOutgoingIntent() {
        WordDatabaseContract.Word word = getWord();
        Intent intent = new Intent();
        intent.putExtra(REQUEST_OBJ_WORD, word);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_word);
        setSupportActionBar(toolbar);
        //
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        getSupportActionBar().setTitle("New Word");
    }

    private void setupUI(){
        ((TextView) findViewById(R.id.text_view_word)).setText(word);
        editTextDefinition = (EditText) findViewById(R.id.edit_text_definition);
        editTextUsage = (EditText) findViewById(R.id.edit_text_usage);
    }

    private WordDatabaseContract.Word getWord(){
        return new Word(
                word, "definition", "usage", 0, 0, 0, System.currentTimeMillis()
        );
    }
}
