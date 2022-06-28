package com.example.organizze.helper;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

public class DateCustom {

    @SuppressLint("SimpleDateFormat")
    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MM/yyyy");
        return simpleDateFormat.format(date);
    }
    public static String dataFormatadaString(String data){
        String retornoData[] = data.split("/");
        String mes = retornoData[1];
        String ano = retornoData[2];
        return mes+ano;
    }
}
