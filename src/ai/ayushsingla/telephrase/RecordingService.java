package ai.ayushsingla.telephrase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import ai.ayushsingla.telephrase.audio.AudioDataSaver;
import ai.ayushsingla.telephrase.audio.RecordingThread;
import ai.ayushsingla.telephrase.demo.R;

import static android.content.ContentValues.TAG;

public class RecordingService extends Service {

    public static int NOTIFICATION_ID = 121;
    private RecordingThread recordingThread = null;
    private Vibrator vibrator = null;
    private Intent intent, addintent;
    private NotificationManager notificationManager;
    public static boolean on = false;
    public static SpeechResults speechResults = null;
    public Context getCtx() {
        return this;
    }

    @Override
    public void onCreate() {
        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        on = true;
        intent = new Intent(this, DetectedActivity.class);
        addintent = new Intent(this, AddActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startRecording();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Notification nameCalledNotification (Context context, Class<?> home) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Someone just called out your name!")
                        .setContentText("We recommend that you take a look around.");


        Intent resultIntent = new Intent(context, home);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(home);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Intent buttonIntent = new Intent(this, DismissButtonReceiver.class);
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, buttonIntent,0);

        mBuilder.setContentIntent(resultPendingIntent)
                .addAction(R.mipmap.ic_launcher, "Dismiss", dismissPendingIntent)
                .setAutoCancel(true);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    private void startRecording() {
        recordingThread.startRecording();
        Log.i(TAG, "Recording started.");
    }

    private void stopRecording() {
        recordingThread.stopRecording();
        Log.i(TAG, "Recording stopped.");
    }

    @SuppressLint("HandlerLeak")
    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    if((int) msg.obj == 1) {
                        notificationManager.notify(NOTIFICATION_ID, nameCalledNotification(getCtx(), Demo.class));
                        vibrator.vibrate(1000);
                    } else {
                        if(!DetectedActivity.exist)
                            startActivity(intent);
                    }
                    Log.i(TAG, msg.toString());
                    break;
                case MSG_RECORD_START:
                    Toast.makeText(getCtx(), "fail", Toast.LENGTH_SHORT);
                    break;
                case MSG_INFO:
                    speechResults = (SpeechResults) msg.obj;
                    startActivity(addintent);
                    break;
                case MSG_VAD_SPEECH:
                    Log.i(TAG, String.valueOf(msg.obj));
                    break;
                case MSG_VAD_NOSPEECH:
                    Log.i(TAG, String.valueOf(msg.obj));
                    break;
                case MSG_ERROR:
                    Log.e(TAG, msg.toString());
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        stopRecording();
        on = false;
        super.onDestroy();
    }
}
