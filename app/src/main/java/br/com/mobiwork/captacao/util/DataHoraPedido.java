package br.com.mobiwork.captacao.util;

import java.util.Calendar;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class DataHoraPedido {
    private long ano;
    private long mes;
    private long dia;
    private long hora;
    private long minuto;
    private long segundo;
    private long milesimos;
    private long am_pm;

    private DataHoraPedido(int diasAd) {
        final Calendar c = Calendar.getInstance();
        if (diasAd > 0){
            c.add(Calendar.DAY_OF_MONTH, diasAd);
        }
        this.ano = c.get(Calendar.YEAR);
        this.mes = c.get(Calendar.MONTH) +1;
        this.dia = c.get(Calendar.DAY_OF_MONTH);
        this.hora = c.get(Calendar.HOUR_OF_DAY);
        this.am_pm = c.get(Calendar.AM_PM);
        this.minuto = c.get(Calendar.MINUTE);
        this.segundo = c.get(Calendar.SECOND);
        this.milesimos = c.get(Calendar.MILLISECOND);
    }

    public DataHoraPedido(int ano, int mes, int dia, int diasAd) {
        final Calendar c = Calendar.getInstance();
        if (diasAd > 0){
            c.add(Calendar.DAY_OF_MONTH, diasAd);
        }  else {
            mes--;
            c.set(Calendar.YEAR,ano);
            c.set(Calendar.MONTH,mes);
            c.set(Calendar.DAY_OF_MONTH,dia);
        }
        this.ano = c.get(Calendar.YEAR);
        this.mes = c.get(Calendar.MONTH) +1;
        this.dia = c.get(Calendar.DAY_OF_MONTH);
        this.hora = c.get(Calendar.HOUR_OF_DAY);
        this.am_pm = c.get(Calendar.AM_PM);
        this.minuto = c.get(Calendar.MINUTE);
        this.segundo = c.get(Calendar.SECOND);
        this.milesimos = c.get(Calendar.MILLISECOND);
    }

    public static DataHoraPedido criarDataHoraPedido() {
        return new DataHoraPedido(0);
    }

    public static DataHoraPedido criarDataHoraPedido(int diasAd) {
        return new DataHoraPedido(diasAd);
    }

    public static DataHoraPedido SetDataHora(Long ano,Long mes,Long dia, int diasAd) {
        return new DataHoraPedido(ano.intValue(),mes.intValue(),dia.intValue(),diasAd);
    }

    public long getAno() {
        return ano;
    }

    public void setAno(long ano) {
        this.ano = ano;
    }

    public long getMes() {
        return mes;
    }

    public void setMes(long mes) {
        this.mes = mes;
    }

    public long getDia() {
        return dia;
    }

    public void setDia(long dia) {
        this.dia = dia;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public long getMinuto() {
        return minuto;
    }

    public void setMinuto(long minuto) {
        this.minuto = minuto;
    }

    public long getSegundo() {
        return segundo;
    }

    public void setSegundo(long segundo) {
        this.segundo = segundo;
    }

    public long getMilesimos() {
        return milesimos;
    }

    public void setMilesimos(long milesimos) {
        this.milesimos = milesimos;
    }

    public long getAm_pm() {
        return am_pm;
    }

    public void setAm_pm(long am_pm) {
        this.am_pm = am_pm;
    }
}

