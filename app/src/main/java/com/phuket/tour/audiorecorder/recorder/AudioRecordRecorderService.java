package com.phuket.tour.audiorecorder.recorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.phuket.tour.audiorecorder.MyApplication;

public class AudioRecordRecorderService {

    private static final String TAG = "AudioRecordRecorderServiceImpl";

    public static final int WRITE_FILE_FAIL = 9208911;
    protected AudioRecord audioRecord;
    private Thread recordThread;

    private int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    public static int SAMPLE_RATE_IN_HZ = 44100;
    public final static int CHANNEL_CONFIGURATION = AudioFormat.CHANNEL_IN_MONO;
    private final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private int bufferSizeInBytes = 0;

    private boolean isRecording = false;
    private static final AudioRecordRecorderService instance = new AudioRecordRecorderService();

    protected AudioRecordRecorderService() {
    }

    public static AudioRecordRecorderService getInstance() {
        return instance;
    }

    public void initMetaData() throws AudioConfigurationException {
        if (null != audioRecord) {
            audioRecord.release();
        }
        try {
            // 首先利用我们标准的44_1K的录音频率初是录音器
            bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_CONFIGURATION, AUDIO_FORMAT);
            Log.e("audiorecorder3", String.valueOf(bufferSizeInBytes));
            if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE_IN_HZ, CHANNEL_CONFIGURATION, AUDIO_FORMAT,
                    bufferSizeInBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果初始化不成功的话,则降低为16K的采样率来初始化录音器
        if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            try {
                SAMPLE_RATE_IN_HZ = 16000;
                bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_CONFIGURATION,
                        AUDIO_FORMAT);
                Log.e("audiorecorder3", String.valueOf(bufferSizeInBytes));
                if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE_IN_HZ, CHANNEL_CONFIGURATION, AUDIO_FORMAT,
                        bufferSizeInBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new AudioConfigurationException();
        }
    }

    private FileOutputStream outputStream;
    private long wrote;
    private String outputFilePath;

    public void start(String filePath) throws StartRecordingException {
        if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            try {
                audioRecord.startRecording();
            } catch (Exception e) {
                throw new StartRecordingException();
            }
        } else {
            throw new StartRecordingException();
        }
        isRecording = true;
        recordThread = new Thread(new RecordThread(), "RecordThread");
        try {
            this.outputFilePath = filePath;
            recordThread.start();
        } catch (Exception e) {
            throw new StartRecordingException();
        }
    }

    public void stop() {
        try {
            if (audioRecord != null) {
                isRecording = false;
                try {
                    if (recordThread != null) {
                        recordThread.join();
                        recordThread = null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                releaseAudioRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseAudioRecord() {
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
            audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }

    class RecordThread implements Runnable {

        @Override
        public void run() {
            try {
                Log.e("audiorecorder", MyApplication.getContext().getCacheDir().getAbsolutePath());
                outputStream = MyApplication.getContext().openFileOutput(outputFilePath, Context.MODE_PRIVATE);
                byte[] audioSamples = new byte[bufferSizeInBytes];
                while (isRecording) {
                    int audioSampleSize = getAudioRecordBuffer(bufferSizeInBytes, audioSamples);
                    if (audioSampleSize > 0) {
                        outputStream.write(audioSamples);
                        wrote += audioSampleSize;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != outputStream) {
                    try {
                        outputStream.close();
                        Log.e("audiorecorder2", String.valueOf(wrote));
                        outputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected int getAudioRecordBuffer(int bufferSize, byte[] audioSamples) {
        if (audioRecord != null) {
            return audioRecord.read(audioSamples, 0, bufferSize);
        } else {
            return 0;
        }
    }

    public int getSampleRate() {
        return SAMPLE_RATE_IN_HZ;
    }

}