package com.example.lamond.ld_apnea;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
{
    //https://www.tutorialspoint.com/android/android_audio_capture.htm
    //AudioRecord audio;
    //int bufferSize;
    //int sampleRate = 8000;
    Boolean recording;

    private Button play, stop, record;
    String AudioSavePathInDevice = null;
    public static final int RequestPermissionCode = 1;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private MediaRecorder myAudioRecorder;
    private String outputFile;

    MediaRecorder mediaRecorder ;

    TextView tvNuevo;
    Button btnIngresar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNuevo = findViewById(R.id.tvNuevo);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvNuevo.setOnClickListener(NuevoUsuarioOnClickListener);
        btnIngresar.setOnClickListener(IngresarOnClickListener);

    }
    View.OnClickListener NuevoUsuarioOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            Intent intent  = new Intent(MainActivity.this ,Registro_Usuario.class);
            startActivity(intent);
        }
    };
    View.OnClickListener IngresarOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(MainActivity.this ,Menu_Principal.class);
            startActivity(intent);
        }
    };
    View.OnClickListener startRecOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {

           /* Thread recordThread = new Thread(new Runnable(){

                @Override
                public void run()
                {
                    recording = true;
                    Toast.makeText(getApplicationContext(), "startRecOnClickListener", Toast.LENGTH_LONG).show();
                    startRecord();
                }

            });

            recordThread.start();*/

            recording = true;

            //startRecord();
            startRecord2();
        }};

    View.OnClickListener stopRecOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            recording = false;

        }};

    View.OnClickListener playBackOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            playRecord();
        }

    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity)
    {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void MediaRecorderReady()
    {
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
    void startRecord2()
    {
        if(checkPermission())
        {

            AudioSavePathInDevice = Environment.getExternalStorageDirectory().toString() +"/AudioRecording.3gp";//Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
            Toast.makeText(getApplicationContext(), AudioSavePathInDevice, Toast.LENGTH_LONG).show();
            MediaRecorderReady();
            Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
            try
            {
                mediaRecorder.prepare();
                mediaRecorder.start();
                Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //buttonStart.setEnabled(false);
            //buttonStop.setEnabled(true);

            Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_LONG).show();
        }
        else
            {
                requestPermission();
        }
    }
    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void startRecord()
    {

        //File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
        //File file = new File("G:\\PruebaAudio.pcm");
        String pat = Environment.getExternalStorageDirectory().toString() +  "/test.pcm";
        Toast.makeText(getApplicationContext(), pat, Toast.LENGTH_LONG).show();
        File file = new File(pat);

        try
        {
            Toast.makeText(getApplicationContext(), "203", Toast.LENGTH_LONG).show();
            verifyStoragePermissions(this);
            if (!file.exists())
            {
                Toast.makeText(getApplicationContext(), "206", Toast.LENGTH_LONG).show();

                //file.mkdirs();
                file.createNewFile();
            }

            Toast.makeText(getApplicationContext(), "211", Toast.LENGTH_LONG).show();

            OutputStream outputStream = new FileOutputStream(file);
            Toast.makeText(getApplicationContext(), "213", Toast.LENGTH_LONG).show();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            Toast.makeText(getApplicationContext(), "215", Toast.LENGTH_LONG).show();
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
            Toast.makeText(getApplicationContext(), "217", Toast.LENGTH_LONG).show();
            int minBufferSize = AudioRecord.getMinBufferSize(11025, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];
            Toast.makeText(getApplicationContext(), "227", Toast.LENGTH_LONG).show();
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    11025,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);
            Toast.makeText(getApplicationContext(), "233", Toast.LENGTH_LONG).show();
            /*audioRecord.startRecording();
            Toast.makeText(getApplicationContext(), "Iniciando grabacion", Toast.LENGTH_LONG).show();

            while(recording)
            {
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numberOfShort; i++){
                    dataOutputStream.writeShort(audioData[i]);
                }
            }

            audioRecord.stop();
            Toast.makeText(getApplicationContext(), "Audio grabado exitosamente", Toast.LENGTH_LONG).show();
            dataOutputStream.close();*/

        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    void playRecord()
    {

        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
        verifyStoragePermissions(this);

        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        try
        {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    11025,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);
            Toast.makeText(getApplicationContext(), "Reproduciendo audio", Toast.LENGTH_LONG).show();

        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
