package com.example.speechtotext;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 10;
    private final int REQ_CODE_SPEECH_INPUT = 23;
    Button Pbutton;
    TextView textView;
    String text, timesysa;
    EditText et;
    TextToSpeech ttsobject;
    int result;
    SimpleDateFormat simpledateformat;
    Calendar calender;
    private int result1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = findViewById(R.id.editText);
        Pbutton = findViewById(R.id.push_button);
        textView = findViewById(R.id.textView);

        ttsobject = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    result = ttsobject.setLanguage(Locale.UK);

                } else {

                    Toast.makeText(getApplicationContext(),
                            "Feature not Supported in Your Device",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


        Pbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micclick();
            }
        });
    }

    private void micclick() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//perform and action for speech recognize
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);//supports multiple languages and maintain language free form
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());//using default system lang.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");//passing additional value and display additional message
        //putExtra is used to pass multiple parameter or multiple message in a single intent
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);//we pass object of intent and request code in startActivityForResult
            //startActivityForResult method is followed by onActivityResult method
        } catch (ActivityNotFoundException a) {

        }
    }

    //receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textView.setText(result.get(0));
                    et.setText(result.get(0));
                    //save speech to text file
                    writedatainfile(textView.getText().toString());
                }

                if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {

                    Toast.makeText(getApplicationContext(),
                            "Feature not Supported in Your Device",
                            Toast.LENGTH_SHORT).show();
                    et.getText();
                } else {

                    text = textView.getText().toString();
                    ttsobject.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                }


                break;
            }
        }
    }

    private void writedatainfile(String text) {
        calender = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        timesysa = simpledateformat.format(calender.getTime());
        timesysa = "ExternalData" + timesysa + ".txt";
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File myFile = new File(folder, timesysa); //Filename
        writeData(myFile, text);
    }

    private void writeData(File myFile, String text) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myFile);
            fileOutputStream.write(text.getBytes());
            Toast.makeText(this, "Done" + myFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
