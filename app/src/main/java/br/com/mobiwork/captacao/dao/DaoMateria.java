package br.com.mobiwork.captacao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.SimpleCursorAdapter;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class DaoMateria extends DaoCreateDBC {

    private Cursor cursor;
    private Context ctx;


    public DaoMateria(Context context) {
        super(context);
        ctx=context;
    }

    public Cursor popularComboOperacao(SQLiteDatabase db){

       try {
           return db.rawQuery("SELECT _id,codigo, nome FROM materia ", null);
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }

    }

    public String pesqMateria(SQLiteDatabase db,String nomemateria) {

        try {
            Cursor c = db.rawQuery("SELECT codigo FROM materia where trim(nome)='" + nomemateria.trim() + "'", null);
            if (c.moveToNext()) {
                return c.getString(c.getColumnIndex("codigo"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";

    }

    public String pesqNomeMateria(SQLiteDatabase db,String codigo) {

        try {
            Cursor c = db.rawQuery("SELECT nome FROM materia where trim(codigo)='" + codigo.trim() + "'", null);
            if (c.moveToNext()) {
                return c.getString(c.getColumnIndex("nome"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";

    }

    public String pesqMateriaFirst(SQLiteDatabase db) {

        try {
            Cursor c = db.rawQuery("SELECT codigo FROM materia order by codigo asc", null);
            if (c.moveToNext()) {
                return c.getString(c.getColumnIndex("codigo"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";

    }



}
