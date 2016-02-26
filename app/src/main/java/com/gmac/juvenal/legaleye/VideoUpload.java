package com.gmac.juvenal.legaleye;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VideoUpload extends AppCompatActivity {

    private String myFileName;
    private String responseString = "";

/**************** ADDED ********************/
    private ProgressBar progressBar;
    private TextView txtPercentage;
/*************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myFileName = extras.getString("fileName");
        }

        new StartVideoUpload().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /********* ADDED ****************/
        progressBar = (ProgressBar)findViewById(R.id.progressBarTry);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        /***********************************************************************/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public class StartVideoUpload extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }



        @Override
        protected String doInBackground(Void... params) {

            URL url;
            HttpURLConnection conn = null;
            DataOutputStream dos;
            DataInputStream inStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String myVideoPath = Environment.getExternalStorageDirectory() + "/QuickVid/" + myFileName;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String urlString = Config.FILE_UPLOAD_URL;


            try {
                FileInputStream fileInputStream = new FileInputStream(new File(myVideoPath));
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + myVideoPath + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams
                Log.e("Debug", "File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }

//            for(int i = 0; i <= 100; i++) {
                try {
                    for(int i = 0; i <= 100; i++) {
                        inStream = new DataInputStream(conn.getInputStream());
                        while ((responseString = inStream.readLine()) != null) {
                            Log.e("Debug", "Server Response " + responseString);
                        }
                        Thread.sleep(100);
                        publishProgress(i);
                    }

                } catch (IOException ioe) {
                    Log.e("Debug", "error: " + ioe.getMessage(), ioe);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                publishProgress(i);

//            }
            return responseString;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            // updating progress bar value
//            progressBar.setProgress(values[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(values[0]) + "%");


        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
        }

    }

}
