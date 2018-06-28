package com.example.lamond.ld_apnea;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Paquete {
    private final static int tamPaquete = 10;
    private int data[];
    private boolean lleno = false;
    private int index = 0;
    private int id = 0;
    private Random random;
    private final static DateFormat hourFormat = new SimpleDateFormat("hh:mm:ss");
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
    private static Semaphore mutex = new Semaphore(1);

    public Paquete() {
        data = new int[tamPaquete];
        for (int i=0; i<tamPaquete;i++)
            data[i]=-1;
        random = new Random();
    }

    public void add(int item){
        try {
            if (!lleno){
                data[index] = item;
                index++;
                if (index == tamPaquete){
                    mutex.acquire();
                    lleno = true;
                    mutex.release();
                }
            }
        }
        catch (InterruptedException ex){
            mutex.release();
            ex.printStackTrace();
        }
    }

    public boolean estaLleno(){
        return lleno;
    }

    public void vaciar(){
        index = 0;
        for (int i=0; i<tamPaquete; i++)
            data[i] = -1;
        try {
            mutex.acquire();
            lleno = false;
            mutex.release();
        }
        catch (InterruptedException ex){
            mutex.release();
            ex.printStackTrace();
        }
    }

    public String getJson(){
        Date date = new Date();
        JSONObject PostParam = new JSONObject();
        String dataArray = Arrays.toString(data);
        dataArray = dataArray.substring(1,dataArray.length()-1);

        try {
            PostParam.put("id",String.valueOf(id));
            PostParam.put("hora",hourFormat.format(date));
            //PostParam.put("fecha",dateFormat.format(date));
            PostParam.put("fecha","2018-06-20");
            PostParam.put("data",dataArray);
        }
        catch(JSONException e){
            Log.e("JsonExcepcion","error al crear json format");
        }

        return PostParam.toString();
    }

    public void setData(int i, int value){
        data[index] = value;
    }

    public void llenar(){
        try {
            mutex.acquire();
            lleno = true;
            index = tamPaquete;
            mutex.release();
        }
        catch (InterruptedException ex){
            mutex.release();
            ex.printStackTrace();
        }
    }

    public void copy(Paquete p){
        for (int i=0; i < tamPaquete; i++){
            p.setData(i, this.data[i]);
            this.data[i] = 0;
        }
        p.llenar();
        try {
            mutex.acquire();
            this.index = 0;
            this.lleno = false;
            mutex.release();
        }
        catch (InterruptedException ex){
            mutex.release();
            ex.printStackTrace();
        }
    }

    public void setNewId(){
        id = random.nextInt(10000)+1;
    }
}
