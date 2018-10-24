package ai.ayushsingla.telephrase;
import java.io.File;
import android.os.Environment;

public class Constants {
    public static final String ASSETS_RES_DIR = "snowboy";
    public static final String DEFAULT_WORK_SPACE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/snowboy/";
    public static final String ALEXA_UMDL = "Alexa.umdl";
    public static final String NAME_UMDL = "trainedModel.pmdl";
    public static final String BELL_UMDL = "fire.pmdl";
    public static final String ACTIVE_RES = "common.res";
    public static final String SAVE_AUDIO = Constants.DEFAULT_WORK_SPACE + File.separatorChar + "recording.pcm";
    public static final int SAMPLE_RATE = 16000;
    public static final String WATSON_USERNAME = "";
    public static final String WATSON_PASSWORD = "";
}
