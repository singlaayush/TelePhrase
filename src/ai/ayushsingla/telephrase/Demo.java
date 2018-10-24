package ai.ayushsingla.telephrase;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import ai.ayushsingla.telephrase.demo.R;


public class Demo extends Activity {

    private Button openTrain, btnService;
    private Intent intent, serviceIntent;

    public void checkAllPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }
    public void checkAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);
        }
    }
    public void checkStoragePermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 6);
        }
    }
    public void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
        }
    }
    public void checkPhonePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 8);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAllPermissions();
        checkAudioPermissions();
        checkCameraPermissions();
        checkPhonePermissions();
        checkStoragePermissions();
        intent = new Intent(this, TrainActivity.class);
        serviceIntent = new Intent(this, RecordingService.class);
        setContentView(R.layout.main);

        AppResCopy.copyResFromAssetsToSD(this);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(RecordingService.NOTIFICATION_ID);

        openTrain = (Button) this.findViewById(R.id.btnTrain);
        openTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        Button btnTest = findViewById(R.id.btnTest);
        btnService = (Button) this.findViewById(R.id.btnService);
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/museo.otf");
        openTrain.setTypeface(typeFace);
        btnService.setTypeface(typeFace);
        if(RecordingService.on){
            btnService.setText("Stop Detection");
        } else {
            btnService.setText("Start Detection");
        }
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnService.getText().equals("Start Detection")){
                    startService(serviceIntent);
                    btnService.setText("Stop Detection");
                } else {
                    stopService(serviceIntent);
                    btnService.setText("Start Detection");
                }
            }
        });
        btnTest.setBackgroundColor(Color.TRANSPARENT);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenti = new Intent(getCtx(), ProxyActivity.class);
                startActivity(intenti);
            }
        });
    }
    public Context getCtx() {
        return this;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
