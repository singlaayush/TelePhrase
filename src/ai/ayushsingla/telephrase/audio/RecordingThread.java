package ai.ayushsingla.telephrase.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ai.ayushsingla.telephrase.Constants;
import ai.ayushsingla.telephrase.MsgEnum;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import ai.ayushsingla.telephrase.SnowboyDetect;

public class RecordingThread {
    static { System.loadLibrary("snowboy-detect-android"); }

    private static final String TAG = RecordingThread.class.getSimpleName();

    private SpeechToText service = null;
    private RecognizeOptions options = null;
    private BaseRecognizeCallback callback = null;
    private boolean shouldRecord = false;
    private int shouldRecordCount = 0;
    private static final String ACTIVE_RES = Constants.ACTIVE_RES;
    private static final String NAME_UMDL = Constants.NAME_UMDL;
    private static final String BELL_UMDL = Constants.BELL_UMDL;
    private static final String ALEXA_UMDL = Constants.ALEXA_UMDL;
    private static final String WATSON_USERNAME = Constants.WATSON_USERNAME;
    private static final String WATSON_PASSWORD = Constants.WATSON_PASSWORD;
    //private static final String INTERCOM_UMDL = Constants.INTERCOM_UMDL;
    //private static final String SMOKE_UMDL = Constants.SMOKE_UMDL;
    //private static final String LANDLINE_UMDL = Constants.LANDLINE_UMDL;

    private boolean shouldContinue;
    private AudioDataReceivedListener listener = null;
    private Handler handler = null;
    private Thread thread;
    
    private static String strEnvWorkSpace = Constants.DEFAULT_WORK_SPACE;
    private String activeModel = strEnvWorkSpace + NAME_UMDL + "," + strEnvWorkSpace + BELL_UMDL;
    private String alexaModel = strEnvWorkSpace + ALEXA_UMDL + "," + strEnvWorkSpace + BELL_UMDL;
    private String commonRes = strEnvWorkSpace + ACTIVE_RES;
    private SnowboyDetect detector;

    private void initCallback() {
        callback = new BaseRecognizeCallback() {
            @Override
            public void onTranscription(SpeechResults speechResults) {
                boolean bool = speechResults.getResults().get(0).getKeywordsResult().isEmpty();
                if(!bool) {
                    Log.e("Recognised:", speechResults.toString());
                    //Open Activity
                    sendMessage(MsgEnum.MSG_INFO, speechResults);
                } else {
                    Log.e("Not Recognised:", speechResults.getResults().get(0).getAlternatives().get(0).getTranscript());
                    sendMessage(MsgEnum.MSG_RECORD_START, "");
                }
            }

            @Override
            public void onDisconnected() {
                Log.e("Demo: ", "DISCONNECTED");
            }
        };
    }

    public RecordingThread(Handler handler, AudioDataReceivedListener listener) {
        service = new SpeechToText();
        service.setUsernameAndPassword(WATSON_USERNAME, WATSON_PASSWORD);
        options = new RecognizeOptions.Builder()
                .model("en-US_BroadbandModel").contentType("audio/l16;rate=16000;channels=1")
                .interimResults(false).maxAlternatives(0)
                .keywords(new String[]{"project", "work", "activity", "task", "job", "remember", "contract", "deadline", "submit", "projects"})
                .keywordsThreshold(0.6).build();

        initCallback();
        this.handler = handler;
        this.listener = listener;
        File nameModel = new File(strEnvWorkSpace + NAME_UMDL);
        if(nameModel.exists()) {
            detector = new SnowboyDetect(commonRes, activeModel);
        } else {
            detector = new SnowboyDetect(commonRes, alexaModel);
        }
        detector.SetSensitivity("0.48,0.47"); //DO NOT MODIFY UNLESS SPECIFIED BY ME
        detector.ApplyFrontend(true);
    }

    private void sendMessage(MsgEnum what, Object obj){
        if (null != handler) {
            Message msg = handler.obtainMessage(what.ordinal(), obj);
            handler.sendMessage(msg);
        }
    }

    public void startRecording() {
        if (thread != null)
            return;

        shouldContinue = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        thread.start();
    }

    public void stopRecording() {
        if (thread == null)
            return;

        shouldContinue = false;
        thread = null;
    }

    private void record() {
        Log.v(TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // Buffer size in bytes: for 0.1 second of audio
        int bufferSize = (int)(Constants.SAMPLE_RATE * 0.1 * 2);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = Constants.SAMPLE_RATE * 2;
        }

        byte[] audioBuffer = new byte[bufferSize];
        AudioRecord record = new AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            Constants.SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();
        if (null != listener) {
            listener.start();
        }
        Log.v(TAG, "Start recording");

        long shortsRead = 0;
        detector.Reset();
        while (shouldContinue) {
            record.read(audioBuffer, 0, audioBuffer.length);

            if (null != listener && shouldRecord) {
                listener.onAudioDataReceived(audioBuffer, audioBuffer.length);
                shouldRecordCount++;
                if(shouldRecordCount > 50) {
                    shouldRecord = false;
                    listener.stop();
                    shouldRecordCount = 0;
                    try {
                        service.recognizeUsingWebSocket
                                (new FileInputStream(Constants.SAVE_AUDIO), options, callback);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Converts to short array.
            short[] audioData = new short[audioBuffer.length / 2];
            ByteBuffer.wrap(audioBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData);

            shortsRead += audioData.length;

            // Snowboy hotword detection.
            int result = detector.RunDetection(audioData, audioData.length);

            if (result == -2) {
                if(shouldRecordCount > 50) {
                    shouldRecord = false;
                    listener.stop();
                    shouldRecordCount = 0;
                    try {
                        service.recognizeUsingWebSocket
                                (new FileInputStream(Constants.SAVE_AUDIO), options, callback);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                sendMessage(MsgEnum.MSG_VAD_NOSPEECH, null);
            } else if (result == -1) {
                sendMessage(MsgEnum.MSG_ERROR, "Unknown Detection Error");
            } else if (result == 0) {
                sendMessage(MsgEnum.MSG_VAD_SPEECH, null);
            } else if (result > 0) {
                sendMessage(MsgEnum.MSG_ACTIVE, result);
                if(result == 1) {
                    if (null != listener) {
                        listener.start();
                    }
                }
                shouldRecord = true;
                Log.i("Snowboy: ", "Hotword " + Integer.toString(result) + " detected!");
            }
        }

        record.stop();
        record.release();


        if (null != listener) {
            listener.stop();
        }

        Log.v(TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }
}
