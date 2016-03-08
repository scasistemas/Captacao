package br.com.mobiwork.captacao.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.captacao.model.Lancamentos;

/**
 * Created by LuisGustavo on 01/06/2015.
 */
public class DaoLancamentos extends DaoCreateDB {

    SimpleDateFormat fmtdata;
    SimpleDateFormat fmthora;
    DaoLinha dl ;
    Date data;
    public DaoLancamentos(Context context) {
        super(context);
        data = new Date();
        dl = new DaoLinha(context);
        fmtdata= new SimpleDateFormat("yyyy-MM-dd");
        fmthora = new SimpleDateFormat("HH:mm:ss");
    }

    public Cursor procLancPer(SQLiteDatabase db,String dataIni,String dataFim){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT * FROM lancamentos WHERE  data between '"+dataIni+"'  and '"+dataFim+"' "   , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }


    public Cursor procLanc(SQLiteDatabase db,String codigo,String data,String materia){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT * FROM lancamentos WHERE codigo='"+codigo+"' and data='"+data+"' and materia='"+materia+"'"    , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor procLancId(SQLiteDatabase db,int id){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT * FROM lancamentos WHERE _id="+id , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor procLancF(SQLiteDatabase db,String codigo,String data,String fazenda){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT * FROM lancamentos WHERE codigo='"+codigo+"' and data='"+data+"' and fazenda='"+fazenda+"'"    , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor procSumVol(SQLiteDatabase db,String codigo,String fazenda,String data){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT sum(volume) as volume FROM lancamentos WHERE codigo='"+codigo+"' and data='"+data+"'"    , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Double procSumVolTotal(SQLiteDatabase db,String data){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT sum(volume) as volume FROM lancamentos WHERE data='"+data+"'"    , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        if(cursor.moveToFirst()){
            return cursor.getDouble(cursor.getColumnIndex("volume"));
        }

        return 0.0;
    }

    public Double procSumVolTotalLinha(SQLiteDatabase db,SQLiteDatabase dbC,String linha,String data){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT sum(volume) as volume FROM lancamentos WHERE data='"+data+"' and linha='"+dl.pesqCodLinha(dbC, linha)+"'"    , null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        if(cursor.moveToFirst()){
            return cursor.getDouble(cursor.getColumnIndex("volume"));
        }

        return 0.0;
    }

    public Cursor procLancGeral(SQLiteDatabase db){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("SELECT * FROM lancamentos ", null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor procLancData(SQLiteDatabase db,String datain,String datafim){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("select _id,data,strftime('%d/%m/%Y',data) as dataform,count(*) as ncoletas from lancamentos group by (data) having data >='"+datain+"' and data<='"+datafim+"'  order by data desc",null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }

    public Cursor procLancDataGeral(SQLiteDatabase db){
        Cursor cursor=null;

        try{
            cursor= db.rawQuery("select _id,data,strftime('%d/%m/%Y',data) as dataform,count(*) as ncoletas from lancamentos group by (data) order by data desc",null);
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return cursor;
    }



    public void insAtu(Lancamentos l ,SQLiteDatabase db,String data,String materia,boolean edit,String materiaAtu){
        if(!procLanc(db,l.getCodigo(),data,materia).moveToFirst()&&edit==false){
            instValores(l,db,data);
        }else{
            altValores(l,db,materiaAtu);
        }
    }

    public void instValores(Lancamentos l,SQLiteDatabase db,String data2){

        try {
            ContentValues values = new ContentValues();
            values.put("codigo", l.getCodigo());
            values.put("fazenda", l.getFazenda());
            values.put("materia", l.getMateria());
            values.put("linha", l.getLinha());
            values.put("data", data2);
            values.put("hora",fmthora.format(data));
            values.put("volume",l.getVolume());
            values.put("temperatura", l.getTemperatura());
            values.put("ntanque",l.getNtanque());
            long i=db.insert("lancamentos", "", values);



        }catch (Exception e){

        }finally {

        }
    }
    public void altValores(Lancamentos l, SQLiteDatabase db,String materiaAtu){


            try{

                String sqlup = ("UPDATE lancamentos SET  codigo ='" + l.getCodigo() + "', fazenda='"+l.getFazenda()+"'," +
                        " materia='"+l.getMateria()+"', data='"+l.getData()+"', hora='"+l.getHora()+"', linha='"+l.getLinha()+"', "+
                        "volume ='"+l.getVolume()+"', temperatura='"+l.getTemperatura()+"', ntanque="+l.getNtanque()+" WHERE codigo='"+l.getCodigo()+"' and data='"+l.getData()+"' and materia='"+materiaAtu+"'");
                db.execSQL(sqlup);

            }catch (Exception e){
                String ef=e.getMessage();

            }finally {


            }
    }

    public void deletar(SQLiteDatabase db,int id){


        try{
            String sqlup = ("delete from lancamentos WHERE _id="+id);
            db.execSQL(sqlup);

        }catch (Exception e){
            String ef=e.getMessage();

        }finally {


        }
    }


    public ArrayList<String> seleMateria(SQLiteDatabase db,String codigo,String data,String fazenda){
        Cursor cursor=null;
        ArrayList<String> materias= new ArrayList<String>();
        try{
            cursor= db.rawQuery("SELECT materia FROM lancamentos WHERE codigo='"+codigo+"' and data='"+data+"' and fazenda='"+fazenda+"'"    , null);
            if (cursor.moveToFirst()) {
                do {
                    materias.add(cursor.getString(cursor.getColumnIndex("materia")));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            String erro = e.getMessage();
        }
        return materias;
    }

}
