package com.phuket.tour.audiorecorder;

import com.phuket.tour.studio.LameMp3Encoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phuket.tour.audiorecorder.recorder.AudioConfigurationException;
import com.phuket.tour.audiorecorder.recorder.AudioRecordRecorderService;
import com.phuket.tour.audiorecorder.recorder.StartRecordingException;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecorderActivity extends AppCompatActivity {
    private final String TAG = "AudioRecorderActivity";
    private TextView recorder_time_tip;
    private Button recorder_btn;
    private Button mp3_btn;
    private static final int DISPLAY_RECORDING_TIME_FLAG = 100000;
    private final int record = R.string.record;
    private final int stop = R.string.stop;
    private final int to_mp3_sucess = R.string.to_mp3_sucess;

    private boolean isRecording = false;
    private AudioRecordRecorderService recorderService;
    private final String outputPath = "vocal.pcm";
    private final String mp3OutputPath = "vocal.mp3";
    //"/mnt/sdcard/vocal.pcm";
    private Timer timer;
    private int recordingTimeInSecs = 0;
    private TimerTask displayRecordingTimeTask;
    private Handler mHandler;

    static {
        System.loadLibrary("audioencoder");
    }

    final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        findView();
        initView();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == DISPLAY_RECORDING_TIME_FLAG) {
                    int minutes = recordingTimeInSecs / 60;
                    int seconds = recordingTimeInSecs % 60;
                    String timeTip = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                    recorder_time_tip.setText(timeTip);
                }
            }
        };
        bindListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AudioRecorderActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 这里是用户授予或拒绝权限后回调的地方
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限开启成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限开启失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findView() {
        recorder_time_tip = (TextView) findViewById(R.id.recorder_time_tip);
        recorder_btn = (Button) findViewById(R.id.recorder_btn);
        mp3_btn = (Button) findViewById(R.id.mp3_btn);
    }

    private void initView() {
        String timeTip = "00:00";
        recorder_time_tip.setText(timeTip);
    }

    private void bindListener() {
        recorder_btn.setOnClickListener(v -> {
            if (isRecording) {
                isRecording = false;
                recorder_btn.setText(getString(record));
                displayRecordingTimeTask.cancel();
                timer.cancel();
                recordingTimeInSecs = 0;
                recorderService.stop();
                mHandler.sendEmptyMessage(DISPLAY_RECORDING_TIME_FLAG);
            } else {
                isRecording = true;
                recorder_btn.setText(getString(stop));
                //启动AudioRecorder来录音
                recorderService = AudioRecordRecorderService.getInstance();
                try {
                    recorderService.initMetaData();
                    recorderService.start(outputPath);
                    //启动一个定时器来监测时间
                    recordingTimeInSecs = 0;
                    timer = new Timer();
                    displayRecordingTimeTask = new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(DISPLAY_RECORDING_TIME_FLAG);
                            recordingTimeInSecs++;
                        }
                    };
                    timer.schedule(displayRecordingTimeTask, 0, 1000);
                } catch (AudioConfigurationException | StartRecordingException e) {
                    Toast.makeText(AudioRecorderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        mp3_btn.setOnClickListener(v -> {
            if (isRecording) {
                return;
            }
            LameMp3Encoder encoder = new LameMp3Encoder();
            String pcmPath = getExternalFilesDir(null).getAbsolutePath() + File.separator + outputPath;
            int audioChannels;
            if (AudioRecordRecorderService.CHANNEL_CONFIGURATION == AudioFormat.CHANNEL_IN_MONO) {
                audioChannels = 1;
            } else if (AudioRecordRecorderService.CHANNEL_CONFIGURATION == AudioFormat.CHANNEL_IN_STEREO) {
                audioChannels = 2;
            } else {
                Log.e(TAG, "Only support mono and stereo pcm...");
                return;
            }
            int bitRate = 128 * 1024;
            int sampleRate = 44100;
            String mp3Path = getExternalFilesDir(null).getAbsolutePath() + File.separator + mp3OutputPath;
            Log.i(TAG, "input pcm path is " + pcmPath);
            Log.i(TAG, "output mp3 path is " + mp3Path);
            int ret = encoder.init(pcmPath, audioChannels, bitRate, sampleRate, mp3Path);
            if(ret >= 0) {
                Log.i(TAG, "Start Encode Mp3 Success");
                encoder.encode();
                encoder.destroy();
                Log.i(TAG, "Encode Mp3 Success");
            } else {
                Log.i(TAG, "Encoder Initialized Failed...");
            }
            recorder_time_tip.setText(getString(to_mp3_sucess));
        });
    }
}