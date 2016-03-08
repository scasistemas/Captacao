package br.com.mobiwork.captacao.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 01/06/2015.
 */
public class Lancamentos {
    private int _id;
    private String codigo;
    private String fazenda;
    private String materia;
    private String data;
    private String hora;
    private String linha;
    private double volume;
    private double temperatura;
    private int ntanque;


    public Lancamentos(){
        _id=0;
        codigo="";
        fazenda="";
        materia="";
        data="";
        hora="";
        linha="";
        volume=0;
        temperatura=0;
        ntanque=0;
    }

    public void setLancamentos(Cursor c){
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.codigo = c.getString(c.getColumnIndex("codigo"));
        this.fazenda = c.getString(c.getColumnIndex("fazenda"));
        this.materia = c.getString(c.getColumnIndex("materia"));
        this.data = c.getString(c.getColumnIndex("data"));
        this.hora = c.getString(c.getColumnIndex("hora"));
        this.volume = c.getDouble(c.getColumnIndex("volume"));
        this.linha=c.getString(c.getColumnIndex("linha"));
        this.temperatura = c.getInt(c.getColumnIndex("temperatura"));
        this.ntanque=c.getInt(c.getColumnIndex("ntanque"));

    }

    public int getNtanque() {
        return ntanque;
    }

    public void setNtanque(int ntanque) {
        this.ntanque = ntanque;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFazenda() {
        return fazenda;
    }

    public void setFazenda(String fazenda) {
        this.fazenda = fazenda;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
}
