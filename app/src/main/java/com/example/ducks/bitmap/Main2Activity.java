/*mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(txt);
            } catch (IOException e) {
                txt = getString(R.string.smth);
                return null;
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                txt = getString(R.string.smth);
                return null;
            }
            mediaPlayer.start();
            //isPlayed = true;
            txt = getString(R.string.ready);*/
package com.example.ducks.bitmap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Main2Activity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String txt;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et = findViewById(R.id.et);
                txt = et.getText().toString();
                Toast.makeText(Main2Activity.this, R.string.wait, Toast.LENGTH_LONG).show();
                new newThread().execute();
                et.setText(txt);
            }
        });
    }

    class newThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(txt);
                URLConnection yc = url.openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                yc.getInputStream()));
                int input;


                FileOutputStream fOut = openFileOutput("smth.mp3", Context.MODE_PRIVATE);
                PrintWriter writer = new PrintWriter(fOut);

                while ((input = in.read()) != -1) {
                    writer.printf("%x", input);
                }
                in.close();
                writer.close();
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource("d:" + File.separator + "smth.mp3");
            } catch (IOException e) {
                txt = getString(R.string.smth);
                return null;
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                txt = getString(R.string.smth);
                return null;
            }
            mediaPlayer.start();
            return null;
        }
    }
}
