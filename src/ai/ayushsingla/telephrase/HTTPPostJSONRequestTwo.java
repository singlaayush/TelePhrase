package ai.ayushsingla.telephrase;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class HTTPPostJSONRequestTwo extends AsyncTask <String, Void, String>
{
    private static String recPath = Environment.getExternalStorageDirectory() + "/snowboy";

    @Override
    public String doInBackground(String... url)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try
        {
            return downloadUrl(url[0],url[1]);
        } catch (IOException e) {
            Log.e("URL Error: ", e.toString());
            return "Error. URL maybe invalid";
        }
    }

    public String downloadUrl(String myurl, String query) throws IOException {
        int len = 500;

        try {

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("POST");

            query = query.replaceAll("\n", "\\n");

            Writer writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(query);
            writer.flush();
            writer.close();
            conn.disconnect();
/*

            is = conn.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }

            is.close();
*/

            return "done";

        }
        catch(MalformedURLException error) {
            Log.e("Err", error.getStackTrace().toString());
            return "error";
        }
        catch(SocketTimeoutException error) {
            Log.e("Err", error.getStackTrace().toString());
            return "error";
        }
        catch (IOException error) {
            Log.e("Err", error.getStackTrace().toString());
            return "error";
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
