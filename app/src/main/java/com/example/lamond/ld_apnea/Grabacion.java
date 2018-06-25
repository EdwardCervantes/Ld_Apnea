package com.example.lamond.ld_apnea;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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

import java.io.IOException;
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
        try {
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


}
