package com.example.ducks.bitmap;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    MediaPlayer mediaPlayer;
    Boolean isPlayed = null;
    Boolean last = false;
    Boolean last0 = false;
    int i = 0;
    File path;
    String List[];

    private float[] rotationMatrix; //матрица поворота

    private float[] accelerometer;  //данные с акселерометра
    private float[] geomagnetism;   //данные геомагнитного датчика
    private SensorManager sensorManager; //менеджер сенсоров

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        rotationMatrix = new float[16];
        accelerometer = new float[3];
        geomagnetism = new float[3];

        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.play);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlayed == null) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!path.exists()) {
                        TextView textView = new TextView(MainActivity.this);
                        textView.setText(R.string.smth);
                        return;
                    }
                    List = path.list();
                    if (List == null) {
                        TextView textView = findViewById(R.id.txt);
                        textView.setText(R.string.smth);
                        return;
                    }
                    TextView textView = findViewById(R.id.txt);
                    textView.setText(List[0]);
                    newThread newThread = new newThread();
                    newThread.execute();
                }
            }
        });

//        ScrollView scrollView = findViewById(R.id.sv);
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        scrollView.addView(linearLayout);
//        for(String s : list){
//            Button button = new Button(this);
//            button.setText(s);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            button.setLayoutParams(lp);
//            button.setBackgroundColor(Color.TRANSPARENT);
//            linearLayout.addView(button);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlayed != null && !isPlayed) {
            mediaPlayer.start();
            isPlayed = true;
        }
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlayed != null && isPlayed
        ) {
            mediaPlayer.pause();
            isPlayed = false;
        }
        sensorManager.unregisterListener(this);
    }

    private void loadSensorData(SensorEvent event) {
        final int type = event.sensor.getType(); //определяем тип датчика
        if (type == Sensor.TYPE_ACCELEROMETER) { //если акселерометр
            accelerometer = event.values.clone();
        }

        if (type == Sensor.TYPE_MAGNETIC_FIELD) { //если геомагнитный датчик
            geomagnetism = event.values.clone();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        loadSensorData(event);
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer, geomagnetism); //получаем матрицу поворота
        if ((Math.round(Math.toDegrees(accelerometer[1]))) > 600 && isPlayed != null) {
            last = true;
        } else if ((Math.round(Math.toDegrees(accelerometer[1]))) < 500 && last) {
            if (isPlayed) {
                mediaPlayer.pause();
                isPlayed = false;
            } else {
                mediaPlayer.start();
                isPlayed = true;
            }
            last = false;
        }

        if ((Math.round(Math.toDegrees(accelerometer[0]))) > 200 && isPlayed != null) {
            last0 = true;
        } else if ((Math.round(Math.toDegrees(accelerometer[0]))) < -200 && last0) {
            if(isPlayed) {
                mediaPlayer.stop();
            }
            i++;
            newThread newThread = new newThread();
            newThread.execute();
            isPlayed = true;
            last0 = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class newThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(path.getAbsolutePath() + List[i]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    i++;
                    newThread newThread = new newThread();
                    newThread.execute();
                }
            });
            isPlayed = true;
            return null;
        }
    }
}
