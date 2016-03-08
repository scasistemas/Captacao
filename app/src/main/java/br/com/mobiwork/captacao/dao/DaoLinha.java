package br.com.mobiwork.captacao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class DaoLinha extends DaoCreateDBC  {



    private Cursor cursor;
    private Context ctx;


    public DaoLinha(Context context) {
        super(context);
        ctx=context;
    }

    public Cursor pesquisarLinha(SQLiteDatabase db,String filtro){

        try {
            return db.rawQuery("SELECT * FROM linha "+filtro+"", null);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public String pesqCodLinha(SQLiteDatabase db,String nomelinha) {

        try {
            Cursor c = db.rawQuery("SELECT lincodigo FROM linha where trim(lindescri)='" + nomelinha.trim() + "'", null);
            if (c.moveToNext()) {
                return c.getString(c.getColumnIndex("lincodigo"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";

    }


}







