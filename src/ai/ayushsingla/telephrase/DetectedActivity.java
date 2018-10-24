package ai.ayushsingla.telephrase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import ai.ayushsingla.telephrase.demo.R;

public class DetectedActivity extends Activity {

    public static boolean exist = false;
    private Button okBtn;
    private TextView doorbell, detected;
    private Vibrator vibrator = null;
    private Camera mCamera;
    private Camera.Parameters mParams;
    boolean on;
    long[] pattern = {0, 1000, 1000};
    int delay = 100; // in ms

    public void setMuseo() {
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/museo.otf");
        doorbell.setTypeface(typeFace);
        detected.setTypeface(typeFace);
        okBtn.setTypeface(typeFace);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        exist = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected);
        okBtn = (Button) findViewById(R.id.okBtn);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        doorbell = (TextView) this.findViewById(R.id.doorbell);
        detected = (TextView) this.findViewById(R.id.detected);
        setMuseo();
        setupFlashlight();
        flash.start();
        vibrator.vibrate(pattern, 0);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                vibrator.cancel();
                finish();
            }
        });
    }

    @Override
    public void finish() {
        exist = false;
        super.finish();
    }

    Thread flash = new Thread() {
        public void run() {
            try {
                while(true) {
                    toggleFlashLight();
                    sleep(delay);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public void turnOn() {
        if (mCamera != null) {
            // Turn on LED
            mParams = mCamera.getParameters();
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParams);
            on = true;
        }
    }

    public void setupFlashlight() {
        if (mCamera == null) {
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }
    }

    public void turnOff() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            if (mParams.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(mParams);
            }
        }
        on = false;
    }

    public void toggleFlashLight() {
        if (!on) {
            turnOn();
        } else {
            turnOff();
        }
    }
}
