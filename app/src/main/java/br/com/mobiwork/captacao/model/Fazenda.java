package br.com.mobiwork.captacao.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class Fazenda {

    private int _id;
    private String codigo;
    private String fazenda;
    private String descri;
    private String sequencia;
    private String codlinha;




    public void setFazenda(Cursor c){
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.codigo = c.getString(c.getColumnIndex("codigo"));
        this.fazenda = c.getString(c.getColumnIndex("fazenda"));
        this.descri = c.getString(c.getColumnIndex("descri"));
        this.sequencia = c.getString(c.getColumnIndex("sequencia"));
        this.codlinha=c.getString(c.getColumnIndex("codlinha"));
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

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }
}
