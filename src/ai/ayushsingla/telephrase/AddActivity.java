package ai.ayushsingla.telephrase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import ai.ayushsingla.telephrase.demo.R;

public class AddActivity extends Activity {

    Button addBtn;
    Button cncBtn;
    TextView add;
    EditText edit;
    SpeechResults speechResults = RecordingService.speechResults;

    public void setMuseo() {
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/museo.otf");
        add.setTypeface(typeFace);
        edit.setTypeface(typeFace);
        addBtn.setTypeface(typeFace);
        cncBtn.setTypeface(typeFace);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        addBtn = this.findViewById(R.id.addBtn);
        cncBtn = this.findViewById(R.id.cncBtn);
        edit = this.findViewById(R.id.edit);
        add = this.findViewById(R.id.add);
        setMuseo();
        String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
        edit.setText(text);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                String text = edit.getText().toString();
                speechResults.getResults().get(0).getAlternatives().get(0).setTranscript(text);
                //send this to web
                /*String endpoint = "";
                new HTTPPostJSONRequestTwo() {

                    @Override
                    public void onPostExecute(String result) {
                        if(result != "done") {
                            //makeToast
                        } else {
                            finish();
                        }
                    }

                }.execute(endpoint, speechResults.toString());*/
                Log.e("add todo", speechResults.getResults().get(0).getAlternatives().get(0).getTranscript());
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
