package com.phuket.tour.audiorecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phuket.tour.audiorecorder.recorder.AudioConfigurationException;
import com.phuket.tour.audiorecorder.recorder.AudioRecordRecorderService;
import com.phuket.tour.audiorecorder.recorder.StartRecordingException;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecorderActivity extends AppCompatActivity {
    private TextView recorder_time_tip;
    private Button recorder_btn;
    private static final int DISPLAY_RECORDING_TIME_FLAG = 100000;
    private final int record = R.string.record;
    private final int stop = R.string.stop;

    private boolean isRecording = false;
    private AudioRecordRecorderService recorderService;
    private final String outputPath = "vocal.pcm";
    //"/mnt/sdcard/vocal.pcm";
    private Timer timer;
    private int recordingTimeInSecs = 0;
    private TimerTask displayRecordingTimeTask;
    private Handler mHandler;

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
    }

    private void findView() {
        recorder_time_tip = (TextView) findViewById(R.id.recorder_time_tip);
        recorder_btn = (Button) findViewById(R.id.recorder_btn);
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
                recordingTimeInSecs = 0;
                recorderService.stop();
                mHandler.sendEmptyMessage(DISPLAY_RECORDING_TIME_FLAG);
                displayRecordingTimeTask.cancel();
                timer.cancel();
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
    }
}