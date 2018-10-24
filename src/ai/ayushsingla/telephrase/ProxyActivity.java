package ai.ayushsingla.telephrase;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ai.ayushsingla.telephrase.demo.R;

public class ProxyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
        TextView textView = findViewById(R.id.ad);
        EditText editText = findViewById(R.id.eit);
        Button addBtn = findViewById(R.id.addtn);
        Button cncBtn = findViewById(R.id.cnctn);
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/museo.otf");
        addBtn.setTypeface(typeFace);
        cncBtn.setTypeface(typeFace);
        editText.setTypeface(typeFace);
        textView.setTypeface(typeFace);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}