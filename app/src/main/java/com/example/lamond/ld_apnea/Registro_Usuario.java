package com.example.lamond.ld_apnea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Registro_Usuario extends AppCompatActivity
{
    Button btnRegistrar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro__usuario);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(RegistrarOnClickListener);
    }
    View.OnClickListener RegistrarOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    };
}
