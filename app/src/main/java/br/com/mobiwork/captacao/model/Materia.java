package br.com.mobiwork.captacao.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class Materia {
    private int _id;
    private String codigo;
    private String nome;


    public void setMateria(Cursor c){
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.codigo = c.getString(c.getColumnIndex("codigo"));
        this.nome = c.getString(c.getColumnIndex("nome"));
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
