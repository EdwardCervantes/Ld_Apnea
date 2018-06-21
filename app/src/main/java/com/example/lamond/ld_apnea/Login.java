package com.example.lamond.ld_apnea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity
{
    TextView tvNuevo;
    Button btnIngresar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
            Intent intent  = new Intent(Login.this ,Registro_Usuario.class);
            startActivity(intent);
        }
    };
    View.OnClickListener IngresarOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            Intent intent  = new Intent(Login.this ,Menu_Principal.class);
            startActivity(intent);
        }
    };
}
