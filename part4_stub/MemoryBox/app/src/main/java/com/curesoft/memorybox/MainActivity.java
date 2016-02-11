package com.curesoft.memorybox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //Things done on initial boot of the application
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Make buttons reference to buttons created in layout

        //Speech init stuff
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        //set what happens when you click the "add memory" button


        //set what happens when you click the "retrieve memory" button


        //Make sure to ask the user for permission (recording their voice);
        requestPermissions();
    }


    private void setup(){

        //Database portion of the application
        dbHandler = new DatabaseHelper(this);

        //Create dialog that will come up when the user hits the add memory button.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Create dialog that will come up when the user hits the retrieve memory button.
        builder = new AlertDialog.Builder(this);
    }

    //lets users know that they need permission with using the audio recording
    private void requestPermissions() {
        //if the user allows access
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setup();
        }
        //if user does not... why they no trust :(
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //show the user why you need their permission to record their voice.
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(this,
                            "Audio Recorder permission required to retrieve and store audio.", Toast.LENGTH_SHORT).show();
                }
            }

            //requests permission from Manifest.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {android.Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_RECORD_RESULT);
            }
        }
    }

    //callback from the result from requesting permissions.
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

    //class involved with speech recognition.
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
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do

            switch(mListening){
                case AddMemory:

                    //ADD CODE HERE

                    break;
                case RetrieveMemory:

                    //ADD CODE HERE

                    break;
            }
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    public void addMemory(final String spoken){
        //ADD CODE HERE...
    }

    public void retrieveMemory(final String spoken){
        //ADD CODE HERE...
    }

    //words we do not care about, add more to make better :)
    List<String> ignoreWords = Arrays.asList(new String[]{"a", "i", "it", "am", "at", "on", "in", "to", "too", "very",
            "of", "from", "here", "even", "the", "but", "and", "is", "my",
            "them", "then", "this", "that", "than", "though", "so", "are"});
}
