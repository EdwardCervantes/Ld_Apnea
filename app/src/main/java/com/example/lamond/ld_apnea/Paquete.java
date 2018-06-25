package com.example.lamond.ld_apnea;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class Paquete {
    public final static int tamPaquete = 10;
    private int data[];
    private boolean lleno = false;
    private int index = 0;
    private final static DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    private final static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static Semaphore mutex = new Semaphore(1);

    public Paquete() {
        data = new int[tamPaquete];
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

        String json = "{";
        json += "\"hora\":\"" + hourFormat.format(date) + "\",";
        json += "\"fecha\":\"" + dateFormat.format(date) + "\",";
        json += "\"data\":[" + Integer.toString(data[0]);
        for (int i=1; i<tamPaquete; i++){
            json += "," + Integer.toString(data[i]);
        }
        json += "]}";
        return json;
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
}
