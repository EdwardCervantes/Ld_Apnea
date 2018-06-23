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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.lamond.ld_apnea.MainActivity.RequestPermissionCode;

public class Grabacion extends AppCompatActivity
{
    Button buttonStart;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;

    AudioRecord audio;
    int bufferSize;
    int sampleRate = 8000;
    boolean escuchar = false;
    LineChart chart;
    List<Entry> entries = new ArrayList<>();
    Paquete paquete;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabacion);

        buttonStart =  findViewById(R.id.buttonStart);
        chart = findViewById(R.id.chart);
        chart.getDescription().setText("Frecuencia");

        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        paquete = new Paquete();
    }

    public void IniciarDetener(View view)
    {
        //el boton inicia la grabacion
        if (!escuchar)
        {
            if(checkPermission())
            {
                buttonStart.setText(R.string.detener_grabacion);
                escuchar = !escuchar;
                AudioSavePathInDevice =  Environment.getExternalStorageDirectory().toString() +"/AudioRecording.3gp";
                MediaRecorderReady();
                try
                {
                    timer = new Timer();
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    timer.scheduleAtFixedRate(new RecorderTask(mediaRecorder), 0, 500);
                }
                catch (IllegalStateException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                Toast.makeText(Grabacion.this, "Empezo Grabacion", Toast.LENGTH_SHORT).show();
            }
            else
            {
                requestPermission();
            }
        }
        //el boton detiene la grabacion
        else{
            mediaRecorder.stop();

            timer.cancel();
            timer.purge();
            escuchar = !escuchar;
            Toast.makeText(this, "Grabacion Detenida", Toast.LENGTH_SHORT).show();
            buttonStart.setText(R.string.iniciar_grabacion);
        }
    }

    public void MediaRecorderReady()
    {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(96000);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(Grabacion.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    //productor
    private class RecorderTask extends TimerTask
    {
        TextView sound = (TextView) findViewById(R.id.decibel);
        private MediaRecorder recorder;

        public RecorderTask(MediaRecorder recorder)
        {
            this.recorder = recorder;
        }

        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(escuchar)
                    {
                        int amplitude = recorder.getMaxAmplitude();
                        String amplitudeTxt = String.valueOf(amplitude);
                        //double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                        sound.setText(amplitudeTxt);
                        paquete.add(amplitude);
                    }
                }
            });
        }
    }

    //consumidor
    private class ConeccionServidor extends TimerTask
    {

        public ConeccionServidor()
        {

        }

        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (paquete.estaLleno()){
                        String json = paquete.getJson();
                        paquete.vaciar();

                        //enviar paquete al servidor
                        try {



                            //handle response here...

                        }catch (Exception ex) {

                            //handle exception here

                        } finally {
                            //Deprecated
                            //httpClient.getConnectionManager().shutdown();
                        }
                    }
                }
            });
        }
    }
}
