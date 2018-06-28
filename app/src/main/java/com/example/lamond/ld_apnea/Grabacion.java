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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Grabacion extends AppCompatActivity
{
    private Button stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private Boolean escuchado;
    private Timer producerTask, consumerTask;
    private Paquete paquete;

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
        paquete.setNewId();
        try {
            prepareNewAudio();
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            Log.e("IllegalStateException","preparar y comenzar audio");
        } catch (IOException ioe) {
            Log.e("IOException","preparar y comenzar audio");
        }
        record.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Empezo Grabacion", Toast.LENGTH_SHORT).show();
        escuchado = true;
    }

    public void stopRecord(View view){
        escuchado = false;
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        record.setEnabled(true);
        stop.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Termino Grabacion", Toast.LENGTH_SHORT).show();
        paquete.llenar();
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

                    sound.setText(amplitudeTxt);
                    if (!paquete.estaLleno())
                        paquete.add(amplitude);
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
                    //obtenemos la data a enviar
                    String dataToSend = paquete.getJson();
                    paquete.vaciar();
                    Log.i("data a enviar",dataToSend);
                    //emviamos los datos
                    new MakeNetworkCall().execute("http://moviles.tueduca.online/api/rest_datos/getall.json",dataToSend);
                }
            }
        });
        }
    }

    InputStream ByPostMethod(String ServerURL, String dataJson) {

        InputStream DataInputStream = null;
        try {
            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection) url.openConnection();
            cc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            cc.setReadTimeout(5000);
            cc.setConnectTimeout(5000);
            cc.setRequestMethod("POST");
            cc.setDoInput(true);

            cc.connect();
            //Writing data (bytes) to the data output stream
            //DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            //dos.writeBytes(dataJson);

            java.io.OutputStreamWriter ap_osw= new java.io.OutputStreamWriter(cc.getOutputStream());
                ap_osw.write(dataJson);
                ap_osw.flush();
                ap_osw.close();



            //flushes data output stream.
            //dos.flush();
            //dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();

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
            Log.e("IOException", "Error in ConvertStreamToString", e);
        } catch (Exception e) {
            Log.e("Exception", "Error in ConvertStreamToString", e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e("IOException", "Error in Close stream", e);
            } catch (Exception e) {
                Log.e("Exception", "Error in Close stream", e);
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
            String dataToSend = arg[1];
            String res = "";
            Log.i("datos para: ",dataToSend);
            is = ByPostMethod(URL, dataToSend);

            if (is != null) {
                res = ConvertStreamToString(is);
            } else {
                res = "Something went wrong";
            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("postexecute", "Result: " + result);
        }
    }

}
