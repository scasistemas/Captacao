package br.com.mobiwork.captacao.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.captacao.model.Configgd;
import br.com.mobiwork.captacao.model.Lancamentos;

/**
 * Created by LuisGustavo on 27/10/2015.
 */
public class DaoConfiggd extends DaoCreateDB  {


    public DaoConfiggd(Context context) {
        super(context);
    }


    public Cursor selconfig(SQLiteDatabase db){
        Cursor cursor=null;
        try{
            cursor= db.rawQuery("SELECT * FROM configgd where  _id="+1   , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public long instValores(SQLiteDatabase db,Configgd c){

        ContentValues values = new ContentValues();
        values.put("folderpai", c.getFolderpai());
        values.put("folderexp", c.getFolderexp());
        values.put("foldercol",c.getFoldercol());
        values.put("foldervs",c.getFoldercol());
        return  db.insert("configgd", "", values);
    }

    public void altValores( SQLiteDatabase db,Configgd c){
        try{
            String sqlup = ("UPDATE configgd SET  folderpai ='" + c.getFolderpai() + "', folderexp='"+c.getFolderexp()+"'," +
                    " foldercol='"+c.getFoldercol() +", foldervs='"+c.getFoldervs()+"' WHERE codigo="+1);
            db.execSQL(sqlup);

        }catch (Exception e){
            String ef=e.getMessage();

        }finally {


        }
    }
}
