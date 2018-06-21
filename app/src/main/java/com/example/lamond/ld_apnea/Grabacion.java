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
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio, buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    MediaPlayer mediaPlayer ;

    Thread thread;
     AudioRecord audio;
     int bufferSize;
     double lastLevel = 0;
    double blow_value = 0;
    int sampleRate = 8000;
    int SAMPLE_DELAY = 75;
    ImageView img;
    LineChart chart;
    List<Entry> entries = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabacion);



        buttonStart =  findViewById(R.id.buttonStart);
        buttonStop =  findViewById(R.id.buttonStop);
        buttonPlayLastRecordAudio =  findViewById(R.id.buttonPlayLastRecordAudio);
        buttonStopPlayingRecording = findViewById(R.id.buttonStopPlayingRecording);
        //img = (ImageView)findViewById(R.id.logo);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        Toast.makeText(Grabacion.this, "bufferSize " + bufferSize, Toast.LENGTH_LONG).show();

        chart = findViewById(R.id.chart);
        chart.getDescription().setText("Frecuencia");


        /*entries.add(new Entry(1, 5));
        entries.add(new Entry(2, 4));
        entries.add(new Entry(3, 6));
        entries.add(new Entry(4, 2));
        entries.add(new Entry(5, 4));
        entries.add(new Entry(6, 9));
        entries.add(new Entry(7, 1));
        entries.add(new Entry(8, 2));
        entries.add(new Entry(9, 5));
        entries.add(new Entry(10, 6));
        entries.add(new Entry(11, 3));
        entries.add(new Entry(12, 7));
        entries.add(new Entry(13, 4));
        entries.add(new Entry(14, 8));
        entries.add(new Entry(15, 5));
        entries.add(new Entry(16, 9));
        entries.add(new Entry(17, 2));
        entries.add(new Entry(18, 5));
        entries.add(new Entry(19, 4));
        entries.add(new Entry(20, 7));
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.BLACK);
        dataSet.setDrawFilled(true);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setVisibleXRangeMaximum(15);
        //chart.moveViewToX(10);
        chart.moveViewToAnimated(4, 5000, YAxis.AxisDependency.RIGHT, 1000);
        //chart.invalidate();*/



        buttonStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if(checkPermission()) {

                    AudioSavePathInDevice =  Environment.getExternalStorageDirectory().toString() +"/AudioRecording.3gp";

                    MediaRecorderReady();

                    try
                    {
                        escuchar = true;
                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new RecorderTask(mediaRecorder), 0, 500);

                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                    //IdentificarVoz();
                    //isBlowing();
                    //DetectarFrecuencia();
                    Toast.makeText(Grabacion.this, "Recording started", Toast.LENGTH_LONG).show();
                }
                else
                {
                    requestPermission();
                }

            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                //thread.interrupt();
                //thread = null;
                escuchar = false;
                Toast.makeText(Grabacion.this, "Recording Completed", Toast.LENGTH_LONG).show();
            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException
            {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try
                {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                mediaPlayer.start();


                Toast.makeText(Grabacion.this, "Recording Playing", Toast.LENGTH_LONG).show();
            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
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
    void readAudioBuffer()
    {
        try
        {
            Toast.makeText(Grabacion.this, "bufferReadResult 0: ", Toast.LENGTH_LONG).show();
            short[] buffer = new short[bufferSize];
            int bufferReadResult = 1;
            if (audio != null)
            {

                bufferReadResult = audio.read(buffer, 0, bufferSize);
                Toast.makeText(Grabacion.this, "bufferReadResult: " + bufferReadResult, Toast.LENGTH_LONG).show();
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++)
                {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));
            }
            else
            {
                Toast.makeText(Grabacion.this, "audio nulo" , Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void IdentificarVoz()
    {
        thread = new Thread(new Runnable()
        {
            public void run()
            {
                while(thread != null && !thread.isInterrupted())
                {
                    //Toast.makeText(Grabacion.this, "thread: ", Toast.LENGTH_LONG).show();
                    /*try
                    {
                        Thread.sleep(SAMPLE_DELAY);
                    }
                    catch(InterruptedException ie)
                    {
                        ie.printStackTrace();
                    }*/
                    //readAudioBuffer();

                    /*runOnUiThread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            Toast.makeText(Grabacion.this, "LastLevel: " + lastLevel, Toast.LENGTH_LONG).show();
                            if(lastLevel > 0 && lastLevel <= 50)
                            {
                                img.setImageResource(R.drawable.canon);
                            }else
                            if(lastLevel > 50 && lastLevel <= 100)
                            {
                                img.setImageResource(R.drawable.sleeping);
                            }else
                            if(lastLevel > 100 && lastLevel <= 170)
                            {
                                img.setImageResource(R.drawable.canon);
                            }
                            if(lastLevel > 170)
                            {
                                img.setImageResource(R.drawable.sleeping);
                            }
                        }
                    });*/
                }
            }
        });
        thread.start();
    }
    boolean escuchar;
    public void DetectarFrecuencia()
    {
        int BASE = 600;
        escuchar = true;
        while(escuchar)
        {
            int ratio = mediaRecorder.getMaxAmplitude();// / BASE;
            Toast.makeText(Grabacion.this, "ratio: " + ratio, Toast.LENGTH_LONG).show();
            /*int db = 0;// DB
            if (ratio > 1)
                db = (int) (20 * Math.log10(ratio));
            System.out.println("Decibel value: "+db+"     "+Math.log10(ratio));
            switch (db / 4)
            {
                case 0:
                    img.setImageBitmap(null);
                    break;
                case 1:
                    img.setImageResource(R.drawable.canon);
                    break;
                case 2:
                    img.setImageResource(R.drawable.sleeping);
                    break;
                case 3:
                    img.setImageResource(R.drawable.canon);
                    break;
            }*/
        }
    }
    public boolean isBlowing()
    {
        boolean recorder=true;

        int minSize = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);


        short[] buffer = new short[minSize];

        ar.startRecording();
        while(recorder)
        {

            ar.read(buffer, 0, minSize);
            for (short s : buffer)
            {
                if (Math.abs(s) > 27000)
                {
                    blow_value=Math.abs(s);
                    System.out.println("Blow Value="+blow_value);
                    ar.stop();
                    recorder=false;

                    return true;

                }

            }
        }
        return false;

    }
    private class RecorderTask extends TimerTask
    {
        TextView sound = (TextView) findViewById(R.id.decibel);
        private MediaRecorder recorder;

        int x = 0;

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
                        double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                        sound.setText("" + amplitude);

                        x += 1;
//                        entries.add(new Entry(x, amplitude));
//                        LineDataSet dataSet = new LineDataSet(entries, "Label");
//                        dataSet.setColor(Color.BLACK);
//                        dataSet.setDrawFilled(true);
//                        //dataSet.addEntry(new Entry(x, amplitude));
//
//                        LineData lineData = new LineData(dataSet);
//
//                        lineData.notifyDataChanged();
//                        chart.notifyDataSetChanged();
//
//                        chart.setData(lineData);
//                        chart.invalidate();
                        if(x == 20)
                        {
                            //chart.moveViewToX(20);
                            //XAxis xAxis = chart.getXAxis();xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        }
                    }
                    else
                    {
                        //chart.moveViewToAnimated(20, 5000, YAxis.AxisDependency.RIGHT, 1000);
                        //chart.invalidate();
                    }
                }
            });
        }
    }
}
