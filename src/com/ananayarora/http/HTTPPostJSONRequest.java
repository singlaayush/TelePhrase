package com.ananayarora.http;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTPPostJSONRequest extends AsyncTask <String, Void, String>
{
    private static String recPath = Environment.getExternalStorageDirectory() + "/snowboy";

    @Override
    public String doInBackground(String... url)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try
        {
            return downloadUrl(url[0],url[1],url[2]);
        } catch (IOException e) {
            Log.e("URL Error: ", e.toString());
            return "Error. URL maybe invalid";
        }
    }

    public String downloadUrl(String myurl, String query, String PMDLFileName) throws IOException {
        InputStream is = null;
        int len = 500;

        try {

            URL url = new URL(myurl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","*/*");
            conn.setRequestMethod("POST");
            conn.setReadTimeout(100000);

            Writer writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(query);
            writer.flush();
            writer.close();

            is = conn.getInputStream();

            File outputFile = new File(recPath, PMDLFileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
            }

            fos.close();
            is.close();

            return "done";

        } finally {
            if (is != null) {
                is.close();
            }
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
