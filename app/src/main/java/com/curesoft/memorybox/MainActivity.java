package com.curesoft.memorybox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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

        setup();
    }


    private void setup(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mPatientId = sharedPref.getString(getString(R.string.patient_id), "");

        JSONObject obj = new JSONObject();
        try {
            obj.put("name", "somename");
            obj.put("email", "someemail");
        }catch(JSONException e){

        }

        if(mPatientId.equals("")){
            MemoryBoxRestClient.post(this, "api/patient/", obj, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        mPatientId = response.getString("patientId");
                    }catch (JSONException e){
                        Log.e(TAG, "Couldn't call patient api");
                    }

                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.patient_id), mPatientId);
                    editor.commit();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Could not create patient: " + errorResponse);
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Speak the memory you want to SAVE")
                .setTitle("Listening...");
        mAddListeningDialog = builder.create();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Speak the memory you want to RETRIEVE")
                .setTitle("Listening...");
        mRetrieveListeningDialog = builder.create();
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
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
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

                        for(String word : split){
                            if(!ignoreWords.contains(word)){
                                goodWords.add(word);
                            }
                        }

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("keywords", new JSONArray(goodWords));
                            obj.put("message", spoken);
                        }catch (JSONException e){
                            Log.e(TAG, "Error adding memory", e);
                        }

                        MemoryBoxRestClient.post(MainActivity.this, "api/memory/" + mPatientId, obj, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(MainActivity.this, "Added memory!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e(TAG, "Could not add memory: " + errorResponse);

                            }
                        });
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
        RequestParams params = new RequestParams();

        for(String word : split){
            if(!ignoreWords.contains(word)){
                params.put("searchkeys", word);
            }
        }

        MemoryBoxRestClient.get("api/memory/" + mPatientId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(MainActivity.this, "Retrieved memory!", Toast.LENGTH_LONG).show();

                if(response.has("memories")){
                    JSONArray jArray =  null;
                    try{
                        jArray = response.getJSONArray("memories");
                    } catch (JSONException e){
                        Log.e(TAG, "Memories response is empty: ", e);
                        return;
                    }
                    String memory = "";
                    if (jArray != null) {
                        for (int i=0;i<jArray.length();i++){
                            try {
                                memory += (i+1) + ". " + jArray.get(i).toString() + "\n";
                            } catch (JSONException e) {
                                Log.e(TAG, "couldn't parse memories: ", e);
                                return;
                            }
                        }
                    }

                    if(!memory.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(memory)
                                .setTitle("We found your memory")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog results = builder.create();
                        results.show();
                    }else{

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
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Could not retrieve memory: " + errorResponse);
            }
        });
    }

    List<String> ignoreWords = Arrays.asList(new String[]{"a", "i", "it", "am", "at", "on", "in", "to", "too", "very",
            "of", "from", "here", "even", "the", "but", "and", "is", "my",
            "them", "then", "this", "that", "than", "though", "so", "are"});
}
