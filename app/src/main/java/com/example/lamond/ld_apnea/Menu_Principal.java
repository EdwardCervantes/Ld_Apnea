package com.example.lamond.ld_apnea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu_Principal extends AppCompatActivity
{
    Button btnIniciar;
    Button btnHistorial;
    Button btnDiagnostico;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__principal);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnDiagnostico = findViewById(R.id.btnDiagnostico);
        btnIniciar.setOnClickListener(IniciarOnClickListener);
        btnHistorial.setOnClickListener(HistorialOnClickListener);
        btnDiagnostico.setOnClickListener(DiagnosticoOnClickListener);
    }
    View.OnClickListener IniciarOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(Menu_Principal.this ,Grabacion.class);
            startActivity(intent);
        }
    };
    View.OnClickListener HistorialOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(Menu_Principal.this ,Historial.class);
            startActivity(intent);
        }
    };
    View.OnClickListener DiagnosticoOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    };
}
