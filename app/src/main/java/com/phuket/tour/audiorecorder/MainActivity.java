package com.phuket.tour.audiorecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button main_recorder_btn = (Button) findViewById(R.id.id_audio_recorder_btn);
        main_recorder_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AudioRecorderActivity.class);
            startActivity(intent);
        });
    }
}