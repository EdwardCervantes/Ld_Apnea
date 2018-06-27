package com.example.lamond.ld_apnea;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Historial extends AppCompatActivity
{
    Button btnBuscar;
    EditText etFechaDesde;
    EditText etFechaHasta;
    LineChart chart;
    List<Entry> entries;
    Spinner spinner;
    ArrayList<String> Lista_Nombres = new ArrayList<String>();
    ArrayAdapter<String> spinnerArrayAdapter;

    ArrayList<ID_Nombre> Id_Nombres = new ArrayList<ID_Nombre>();
    int[] frecuencias_obtenidas;
    int id_buscar = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        btnBuscar = findViewById(R.id.btnBuscar);
        etFechaDesde = findViewById(R.id.etFechaDesde);
        etFechaHasta = findViewById(R.id.etFechaHasta);
        btnBuscar.setOnClickListener(BuscarOnClickListener);
        etFechaDesde.setText("2018-06-25");
        etFechaHasta.setText("2018-06-26");
        chart = findViewById(R.id.chartHistorial);
        spinner = findViewById(R.id.sIds);

        chart.getDescription().setText("Frecuencia");
        entries = new ArrayList<>();
        entries.add(new Entry(1, 5));
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
        //chart.invalidate();


        spinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Lista_Nombres);
        spinner.setAdapter(spinnerArrayAdapter);
        //spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);


       /* String[] letra = {"A","B","C","D","E"};letra[4] = "j";
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));*/

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                id_buscar = 0;
                String selec = (String)adapterView.getItemAtPosition(pos);
                //Toast.makeText(adapterView.getContext(),(String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < Id_Nombres.size(); i++)
                {
                    ID_Nombre in= Id_Nombres.get(i);//Log.e("in.Nombre!!!!!",in.Nombre);
                    if(in.Nombre == selec)
                    {
                        //Toast.makeText(adapterView.getContext(), in.ID, Toast.LENGTH_SHORT).show();
                        id_buscar = Integer.parseInt(in.ID);
                        i=Id_Nombres.size();
                    }
                }
                if(id_buscar > 0)
                    new SendPostRequest2().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
    }
    View.OnClickListener BuscarOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            new SendPostRequest().execute();



        }
    };
    //https://medium.com/@lewisjkl/android-httpurlconnection-with-asynctask-tutorial-7ce5bf0245cd
    public class SendPostRequest extends AsyncTask<String, Void, String>
    {

        protected void onPreExecute()
        {
        }

        protected String doInBackground(String... arg0)
        {
            String result = "99";

            try
            {
                URL url = new URL("http://moviles.tueduca.online/api/rest_datos/daterange.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("inicio", etFechaDesde.getText());
                postDataParams.put("fin", etFechaHasta.getText());
                Log.e("postDataParams!!!!!",postDataParams.toString());

//                OutputStream os = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

//                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
//                os.write(postDataParams.getBytes());
//                os.flush();

                OutputStreamWriter ap_osw= new OutputStreamWriter(connection.getOutputStream());
                ap_osw.write(postDataParams.toString());
                ap_osw.flush();
                ap_osw.close();

//                writer.write(getPostDataString(postDataParams));
//
//                writer.flush();
//                writer.close();
//                os.close();

                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK)
                {
//                    BufferedReader in=new BufferedReader(new InputStreamReader( connection.getInputStream(),"UTF-8"));
//                    StringBuffer sb = new StringBuffer("");
//
//                    String line = "";
//                    while((line = in.readLine()) != null)
//                    {
//                        //sb.append(line);
//                        result+=line;
//                        //break;
//                    }
//
//                    in.close();

                    Log.e("Conectado!!!!!!!!!!!","Conectado !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    Lista_Nombres.clear();
                    //Id_Nombres.clear();

                    InputStream responseBody = connection.getInputStream();
//                    return inputStreamToString(responseBody);
                    Log.e("responseBody!!!!!!!!!!!","responseBody !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    Log.e("responseBodyReader!!!","responseBodyReader !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    Log.e("jsonReader!!!!!!!!!!!","jsonReader !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    jsonReader.beginObject();
                    Log.e("beginObject!!!!!!!!!!!","beginObject !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    while (jsonReader.hasNext())
                    {
                        String key = jsonReader.nextName();
                        Log.e("key!!!!!!!!!!!",key);
                        if (key.equals("datos"))
                        {
                           jsonReader.beginArray();

                            while(jsonReader.hasNext())
                            {
                                jsonReader.beginObject();
                                ID_Nombre id_n = new ID_Nombre();
                                while (jsonReader.hasNext())
                                {
                                    String propiedad = jsonReader.nextName();
                                    String valor = jsonReader.nextString();
                                    Log.e("propiedad!!!!!!!!!!!",propiedad);
                                    Log.e("valor!!!!!!!!!!!",valor);
                                    if(propiedad.contentEquals("id"))
                                    {
                                        //Lista_Nombres.add(valor);
                                        id_n.ID = valor;
                                        //Log.e("asd!!!!!!!!!!!",valor);
                                    }
                                    if(propiedad.contentEquals("name"))
                                    {
                                        Lista_Nombres.add(valor);
                                        id_n.Nombre = valor;
                                        //Log.e("asd!!!!!!!!!!!",valor);
                                    }
                                    /*if(propiedad.contentEquals("data"))
                                    {
                                        String[] frecuencias = valor.split(",");
                                        frecuencias_obtenidas = new int[frecuencias.length];
                                        for (int i = 0; i < frecuencias.length; i++)
                                        {
                                            frecuencias_obtenidas[i] = Integer.parseInt(frecuencias[i]);
                                        }
                                    }*/
                                    /*else
                                    {
                                        jsonReader.skipValue();
                                        Log.e("nameToRead!!!!!!!!!!!",nameToRead);
                                    }*/
                                }
                                Id_Nombres.add(id_n);
                                Log.e("break!!!!!!!!!!!",".");
                                //String tt = jsonReader.nextName();Log.e("tt!!!!!!!!!!!",tt);
                                jsonReader.endObject(); Log.e("endObject!!!!!!!!!!!",".");
                                //jsonReader.skipValue(); Log.e("skipValue!!!!!!!!!!!",".");
                            }
                            jsonReader.endArray();

//                            result = jsonReader.nextString();
//                            Log.e("*****nextString","nextString");
                            //break;
                        }
                        else
                        {
                            jsonReader.skipValue();
                        }
                    }
                    //spinnerArrayAdapter.add("3434");
                    spinnerArrayAdapter.notifyDataSetChanged();
                    Log.e("***************",result);
                    return result;
                }
                else
                {
                    Log.e("@@@@@@@@@@@@@@","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    return "Ok";
                }




                //return "Ok";
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                //Toast.makeText(Historial.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                return new String("MalformedURLException: " + e.getMessage());
            }
            catch (IOException e)
            {
                e.printStackTrace();
               // Toast.makeText(Historial.this, "IOException", Toast.LENGTH_SHORT).show();
                return new String("IOException: " + e.getMessage());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(Historial.this, "JSONException", Toast.LENGTH_SHORT).show();
                return new String("JSONException: " + e.getMessage());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                //Toast.makeText(Historial.this, "Exception", Toast.LENGTH_SHORT).show();
                return new String("Exception: " + e.getMessage());
            }
            //return result;
        }
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

          /*  try
            {
                JSONObject jsonObject = new JSONObject(s);
                Log.e("jsonObject!!!!!!!!!!!","jsonObject !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                JSONArray jsonArray = jsonObject.getJSONArray("datos");
            }
            catch (JSONException e)
            {
                Log.e("s!!!!!!!!",s);
                Log.e("JSONException!!!!!!!!","JSONException !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                e.printStackTrace();
            }*/
        }

    }

    public String getPostDataString(JSONObject params) throws Exception
    {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext())
        {
            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    private String inputStreamToString(InputStream is)
    {
        String rLine = "";
        StringBuilder answer = new StringBuilder();

        InputStreamReader isr = new InputStreamReader(is);

        BufferedReader rd = new BufferedReader(isr);

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer.toString();
    }
    public class ID_Nombre
    {
        public String ID;
        public String Nombre;
    }

    public class SendPostRequest2 extends AsyncTask<String, Void, String>
    {

        protected void onPreExecute()
        {
        }

        protected String doInBackground(String... arg0)
        {
            String result = "99";
            int x = 0;
            try
            {
                URL url = new URL("http://moviles.tueduca.online/api/rest_datos/getrecords.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("id", String.valueOf(id_buscar));
                Log.e("postDataParams!!!!!",postDataParams.toString());

//                OutputStream os = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

//                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
//                os.write(postDataParams.getBytes());
//                os.flush();

                OutputStreamWriter ap_osw= new OutputStreamWriter(connection.getOutputStream());
                ap_osw.write(postDataParams.toString());
                ap_osw.flush();
                ap_osw.close();

//                writer.write(getPostDataString(postDataParams));
//
//                writer.flush();
//                writer.close();
//                os.close();

                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK)
                {
//                    BufferedReader in=new BufferedReader(new InputStreamReader( connection.getInputStream(),"UTF-8"));
//                    StringBuffer sb = new StringBuffer("");
//
//                    String line = "";
//                    while((line = in.readLine()) != null)
//                    {
//                        //sb.append(line);
//                        result+=line;
//                        //break;
//                    }
//
//                    in.close();

                    Log.e("Conectado!!!!!!!!!!!","Conectado !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                    InputStream responseBody = connection.getInputStream();
//                    return inputStreamToString(responseBody);
                    Log.e("responseBody!!!!!!!!!!!","responseBody !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    Log.e("responseBodyReader!!!","responseBodyReader !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    Log.e("jsonReader!!!!!!!!!!!","jsonReader !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    jsonReader.beginObject();
                    Log.e("beginObject!!!!!!!!!!!","beginObject !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    while (jsonReader.hasNext())
                    {
                        String key = jsonReader.nextName();
                        Log.e("key!!!!!!!!!!!",key);
                        if (key.equals("datos"))
                        {
                            jsonReader.beginArray();

                            while(jsonReader.hasNext())
                            {
                                jsonReader.beginObject();
                                //ID_Nombre id_n = new ID_Nombre();
                                while (jsonReader.hasNext())
                                {
                                    String propiedad = jsonReader.nextName();
                                    String valor = jsonReader.nextString();
                                    Log.e("propiedad!!!!!!!!!!!",propiedad);
                                    Log.e("valor!!!!!!!!!!!",valor);
                                    /*if(propiedad.contentEquals("id"))
                                    {
                                        //Lista_Nombres.add(valor);
                                        id_n.ID = valor;
                                        //Log.e("asd!!!!!!!!!!!",valor);
                                    }
                                    if(propiedad.contentEquals("name"))
                                    {
                                        Lista_Nombres.add(valor);
                                        id_n.Nombre = valor;
                                        //Log.e("asd!!!!!!!!!!!",valor);
                                    }*/
                                    if(propiedad.contentEquals("data"))
                                    {
                                        String[] frecuencias = valor.split(",");
                                        frecuencias_obtenidas = new int[frecuencias.length];
                                        for (int i = 0; i < frecuencias.length; i++)
                                        {
                                            frecuencias_obtenidas[i] = Integer.parseInt(frecuencias[i]);
                                        }
                                    }
                                    /*else
                                    {
                                        jsonReader.skipValue();
                                        Log.e("nameToRead!!!!!!!!!!!",nameToRead);
                                    }*/
                                }
                                //Id_Nombres.add(id_n);
                                Log.e("break!!!!!!!!!!!",".");
                                //String tt = jsonReader.nextName();Log.e("tt!!!!!!!!!!!",tt);
                                jsonReader.endObject(); Log.e("endObject!!!!!!!!!!!",".");
                                //jsonReader.skipValue(); Log.e("skipValue!!!!!!!!!!!",".");
                            }
                            jsonReader.endArray();

//                            result = jsonReader.nextString();
//                            Log.e("*****nextString","nextString");
                            //break;
                        }
                        else
                        {
                            jsonReader.skipValue();
                        }
                    }
                    //spinnerArrayAdapter.add("3434");
                    //spinnerArrayAdapter.notifyDataSetChanged();
                    Log.e("buscado id",result);
                    return result;
                }
                else
                {
                    Log.e("@@@@@@@@@@@@@@","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    return "Ok";
                }




                //return "Ok";
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                //Toast.makeText(Historial.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                return new String("MalformedURLException: " + e.getMessage());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                // Toast.makeText(Historial.this, "IOException", Toast.LENGTH_SHORT).show();
                return new String("IOException: " + e.getMessage());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(Historial.this, "JSONException", Toast.LENGTH_SHORT).show();
                return new String("JSONException: " + e.getMessage());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                //Toast.makeText(Historial.this, "Exception", Toast.LENGTH_SHORT).show();
                return new String("Exception: " + e.getMessage());
            }
            //return result;
        }
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            chart.clear();
            entries = new ArrayList<>();
            for(int i = 0;i<frecuencias_obtenidas.length;i++)
            {
                entries.add(new Entry(i + 1, frecuencias_obtenidas[i]));
            }
            LineDataSet dataSet = new LineDataSet(entries, "Label");
            dataSet.setColor(Color.BLACK);
            dataSet.setDrawFilled(true);
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.setVisibleXRangeMaximum(15);
            //chart.moveViewToX(10);
            chart.moveViewToAnimated(4, 5000, YAxis.AxisDependency.RIGHT, 1000);
        }

    }
}
