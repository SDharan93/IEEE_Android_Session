package com.curesoft.memorybox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

import org.json.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String mPatientId;

    enum ListeningType  {
        AddMemory, RetrieveMemory
    }

    AppCompatButton mAddMemoryButton;
    AppCompatButton mRetrieveMemoryButton;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private ListeningType mListening = null;

    private AlertDialog mAddListeningDialog;
    private AlertDialog mRetrieveListeningDialog;

    private static final int REQUEST_AUDIO_RECORD_RESULT = 1;
    private DatabaseHelper dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAddMemoryButton = (AppCompatButton) findViewById(R.id.btn_add);
        mRetrieveMemoryButton= (AppCompatButton) findViewById(R.id.btn_retrieve);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        mAddMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddListeningDialog.show();
                mListening = ListeningType.AddMemory;
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        });

        mRetrieveMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRetrieveListeningDialog.show();
                mListening = ListeningType.RetrieveMemory;
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        });

        //setup();
        callVoice();
    }


    private void setup(){

        //Database portion of the application
        dbHandler = new DatabaseHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Speak the memory you want to SAVE")
                .setTitle("Listening...");
        mAddListeningDialog = builder.create();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Speak the memory you want to RETRIEVE")
                .setTitle("Listening...");
        mRetrieveListeningDialog = builder.create();
    }

    private void callVoice() {
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setup();
        }

        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this,
                            "Audio Recorder permission required to retrieve and store audio.", Toast.LENGTH_SHORT).show();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {android.Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_RECORD_RESULT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_AUDIO_RECORD_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setup();
            }

            else {
                Toast.makeText(this,
                        "Audio Recording permission has not been granted, cannot understand voice.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            //Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            int i = 0;
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do

            switch(mListening){
                case AddMemory:
                    if(mAddListeningDialog != null){
                        mAddListeningDialog.dismiss();
                        if(matches.size() != 0) {
                            addMemory(matches.get(0));
                        }else{
                            Toast.makeText(MainActivity.this, "We didn't catch what you said", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case RetrieveMemory:
                    if(mRetrieveListeningDialog != null){
                        mRetrieveListeningDialog.dismiss();
                    }
                    if(matches.size() != 0) {
                        retrieveMemory(matches.get(0));
                    }else{
                        Toast.makeText(MainActivity.this, "We didn't catch what you said", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    public void addMemory(final String spoken){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You said: " + spoken)
                .setTitle("Save?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] split = spoken.split(" ");
                        ArrayList<String> goodWords = new ArrayList<>();

                        //Adds key words to array while ignoring the common words.
                        for (String word : split) {
                            if (!ignoreWords.contains(word)) {
                                goodWords.add(word);
                            }
                        }

                        try {
                            dbHandler.insertData(spoken, goodWords);
                            Toast.makeText(MainActivity.this, "Added memory!", Toast.LENGTH_LONG).show();
                        } catch (SQLException e) {
                            Log.e(TAG, "Could not add memory");
                        }
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog results = builder.create();
        results.show();
    }

    public void retrieveMemory(final String spoken){
        String[] split = spoken.split(" ");
        Toast.makeText(MainActivity.this, "Retrieved memory!", Toast.LENGTH_LONG).show();

        Cursor cur = dbHandler.getData(split);
        String searchResults = "";
        //no match was found.
        if(cur.getCount() == 0) {
            //Log.d(TAG, "Did not get a hit on search.");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("For phrase: " + spoken)
                    .setTitle("No memory found")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog results = builder.create();
            results.show();
        } else {
            //Log.d(TAG, "got a hit on search, will start to parse.");
            //organize the string for user to read.
            int counter = 1;
            while(cur.moveToNext()) {
                searchResults += (counter) + ". " + cur.getString(0) + "\n";
                counter++;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(searchResults)
                    .setTitle("We found your memory")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog results = builder.create();
            results.show();
        }
    }

    List<String> ignoreWords = Arrays.asList(new String[]{"a", "i", "it", "am", "at", "on", "in", "to", "too", "very",
            "of", "from", "here", "even", "the", "but", "and", "is", "my",
            "them", "then", "this", "that", "than", "though", "so", "are"});
}
