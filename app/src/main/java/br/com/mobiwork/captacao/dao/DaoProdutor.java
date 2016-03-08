package br.com.mobiwork.captacao.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class DaoProdutor extends DaoCreateDBC {

    private DaoLinha dl;
    private DaoMateria dm;
    public DaoProdutor(Context context) {
        super(context);
        dl= new DaoLinha(context);
        dm=new DaoMateria(context);
    }

    public Cursor pesqprodutor(SQLiteDatabase db,String linha,boolean seq){
        Cursor cursor=null;
        String where="";
        String orderby="";

        if(!linha.equals("")){
            where="where fazenda.codlinha='"+dl.pesqCodLinha(db, linha)+"'";
        }
        if(seq){
            orderby=" order by  fazenda.sequencia asc";
        }else{
            orderby=" order by  produtor.nome asc";
        }

        try{
            cursor= db.rawQuery("SELECT produtor._id,produtor.codigo,nome,codlinha,fazenda._id,fazenda.descri,fazenda.codlinha as fazenda FROM fazenda inner join produtor on fazenda.codigo=produtor.codigo    "+where+orderby, null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor pesqprdoId(SQLiteDatabase db,String _id){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT * from produtor WHERE _id='"+_id+"' "   , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor pesqprdoCodigo(SQLiteDatabase db,String codigo){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT produtor._id,produtor.codigo,nome,codlinha,fazenda.descri,fazenda.codlinha as fazenda FROM produtor inner join fazenda on produtor.codigo=fazenda.codigo WHERE produtor.codigo='"+codigo+"' "   , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public String pesqprdoCod(SQLiteDatabase db,String codigo){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT nome from produtor WHERE codigo='"+codigo+"' "   , null);
            if(cursor.moveToFirst()){
                return cursor.getString(cursor.getColumnIndex("nome"));
            }
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return "";
    }
}
