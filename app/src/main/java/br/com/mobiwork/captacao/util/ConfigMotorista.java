package br.com.mobiwork.captacao.util;

/**
 * Created by LuisGustavo on 09/06/2015.
 */
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;


import java.io.File;

import br.com.mobiwork.captacao.model.Motorista;

public final class ConfigMotorista {
    public static Motorista config;
    private static SQLiteDatabase db;

    public static Motorista getConfig(SQLiteDatabase db){
        if (config == null){
            config = new Motorista();
            Cursor c = db.rawQuery("SELECT * FROM motorista tb " +
                    " WHERE tb._id = ?",  new String[]{"1"});
            if (c.moveToFirst()) {
                config.setConfigMot(c);
            }
        }

        return config;
    }


    public static void setConfig(Motorista config) {
        ConfigMotorista.config = config;
    }



}
