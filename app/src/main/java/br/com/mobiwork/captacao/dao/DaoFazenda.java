package br.com.mobiwork.captacao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class DaoFazenda extends DaoCreateDBC {

    private Cursor cursor;
    private Context ctx;

    public DaoFazenda(Context context) {
        super(context);
        ctx = context;
    }

    public Cursor pesquisarFazenda(SQLiteDatabase db, int _id) {

        try {
            return db.rawQuery("SELECT * FROM fazenda where _id=" + _id + "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String pesquisarFazendaNome(SQLiteDatabase db, int _id) {

        try {
           cursor=db.rawQuery("SELECT * FROM fazenda where _id=" + _id + "", null);
            if(cursor.moveToFirst()){
                return cursor.getString(cursor.getColumnIndex("descri"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }
}
