package com.example.lamond.ld_apnea;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.lamond.ld_apnea.MainActivity.RequestPermissionCode;

public class Grabacion extends AppCompatActivity
{
    private Button stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private Boolean escuchado;
    private Timer producerTask, consumerTask;
    private Paquete paquete, paqueteAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabacion);

        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        stop.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        escuchado = false;
        paquete = new Paquete();
        paqueteAux = new Paquete();

        producerTask = new Timer();
        producerTask.scheduleAtFixedRate(new Producer(), 0, 500);
        consumerTask = new Timer();
        consumerTask.scheduleAtFixedRate(new Consumer(), 0, 500);
    }

    private void prepareNewAudio(){
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setAudioSamplingRate(44100);
        myAudioRecorder.setAudioEncodingBitRate(96000);
    }

    public void startRecord(View view){
        /*try {
            prepareNewAudio();
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }
        record.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Empezo Grabacion", Toast.LENGTH_SHORT).show();
        escuchado = true;*/

        new MakeNetworkCall().execute("http://192.168.0.11/test.php","Post");

    }

    public void stopRecord(View view){
        escuchado = false;
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        record.setEnabled(true);
        stop.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Termino Grabacion", Toast.LENGTH_SHORT).show();
    }

    private class Producer extends TimerTask
    {
        TextView sound = (TextView) findViewById(R.id.decibel);

        public Producer(){}

        public void run() {runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if(escuchado)
                {
                    int amplitude = myAudioRecorder.getMaxAmplitude();
                    String amplitudeTxt = String.valueOf(amplitude);
                    //double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                    sound.setText(amplitudeTxt);
                    if (!paquete.estaLleno())
                        paquete.add(amplitude);
                    //Thread.sleep(10);
                }
            }
        });
        }
    }

    private class Consumer extends TimerTask
    {
        public Consumer(){}

        public void run() {runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if (paquete.estaLleno()){
                    //copias la data de paquete a paquete auxiliar
                    paquete.copy(paqueteAux);
                    //trabajamos sobre paquete auxiliar

                    //emviamos los datos

                }
            }
        });
        }
    }

    InputStream ByPostMethod(String ServerURL) {

        InputStream DataInputStream = null;
        try {
            String PostParam = "username=jorge369";

            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection) url.openConnection();

            cc.setReadTimeout(5000);
            cc.setConnectTimeout(5000);
            cc.setRequestMethod("POST");
            cc.setDoInput(true);

            cc.connect();
            //Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.writeBytes(PostParam);
            //flushes data output stream.
            dos.flush();
            dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            //HttpURLConnection.HTTP_OK is equal to 200
            if(response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
                Log.i("coneccion","exitosaaaaa");
            }

        } catch (Exception e) {
            Log.e("errorrr", "Error in GetData", e);
        }
        return DataInputStream;
    }

    String ConvertStreamToString(InputStream stream) {

        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.e("error", "Error in ConvertStreamToString", e);
        } catch (Exception e) {
            Log.e("error", "Error in ConvertStreamToString", e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e("error", "Error in ConvertStreamToString", e);
            } catch (Exception e) {
                Log.e("error", "Error in ConvertStreamToString", e);
            }
        }
        return response.toString();
    }



    private class MakeNetworkCall extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg) {

            InputStream is = null;
            String URL = arg[0];
            String res = "";

            is = ByPostMethod(URL);

            if (is != null) {
                res = ConvertStreamToString(is);
            } else {
                res = "Something went wrong";
            }
            Log.i("datos devueltos",res);
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("postexecute", "Result: " + result);
        }
    }

}
