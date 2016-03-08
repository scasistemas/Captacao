package br.com.mobiwork.captacao.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class Linha {
    private int _id;
    private String lincodigo;
    private String linnome;




    public void setLinha(Cursor c){
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.lincodigo = c.getString(c.getColumnIndex("lincodigo"));
        this.linnome = c.getString(c.getColumnIndex("linnome"));
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getLinnome() {
        return linnome;
    }

    public void setLinnome(String linnome) {
        this.linnome = linnome;
    }

    public String getLincodigo() {
        return lincodigo;
    }

    public void setLincodigo(String lincodigo) {
        this.lincodigo = lincodigo;
    }
}
