package br.com.mobiwork.captacao.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.mobiwork.captacao.model.Motorista;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class DaoMotorista extends DaoCreateDBC {

    private Cursor cursor;
    private Context ctx;


    public DaoMotorista(Context context) {
        super(context);
        ctx=context;
    }


    public Motorista consultar(SQLiteDatabase db) {
        Motorista config = new Motorista();
        cursor = db.rawQuery("SELECT * FROM motorista tb ",null);

        int count =cursor.getCount();
        if (cursor.moveToFirst()) {
            config.setConfigMot(cursor);
        }
        return config;
    }

    public Cursor consultarExist(SQLiteDatabase db) {
        Motorista config = new Motorista();
        cursor = db.rawQuery("SELECT * FROM motorista",  null);
        return cursor;
    }

    public void update (SQLiteDatabase db, String endereco,String login,String senha){
        try{
            String sqlup=("UPDATE motorista SET endereco = '"+endereco+"' , login='"+login+"' , senha='"+senha+"'  WHERE _id >=0");
            db.execSQL(sqlup);
        }catch(Exception e){
            String erro= e.getMessage();
        }
    }

    public String inserir(SQLiteDatabase db,ContentValues values){
       String erro="";
        String sqlup="";
        try {
             if(consultarExist(db).moveToFirst()){
                  sqlup=("UPDATE motorista SET codigo = "+values.get("codigo")+", nome='"+values.get("nome")+"'" +
                          ", endereco='"+values.get("endereco")+"', login='"+values.get("login")+"', senha='"+values.get("senha")+"', emp='"+values.get("emp")+"', logindrive='"+values.get("logingd")+"'" +
                         " WHERE _id >=0");
             }else{
                  sqlup = ("INSERT INTO motorista (_id,codigo,nome,endereco,login,senha,emp,logindrive) values " +
                         "(" + 1 + ",'" + values.get("codigo") + "','" + values.get("nome") + "','" + values.get("endereco") + "','" + values.get("login") + "'" +
                         ",'" + values.get("senha") + "','" + values.get("emp") + "','" + values.get("logingd") + "')");
             }
            db.execSQL(sqlup);
        }catch (Exception e){
            erro=e.getMessage();
        }
        return erro;
    }


}
