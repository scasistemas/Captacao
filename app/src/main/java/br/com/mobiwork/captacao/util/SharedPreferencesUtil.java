package br.com.mobiwork.captacao.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by LuisGustavo on 02/11/2015.
 */
public class SharedPreferencesUtil {

    public static String BLUETOOTH="BLUETOOTH";
    private Context ctx ;
    android.content.SharedPreferences sharedPreferences ;

    public SharedPreferencesUtil(Context ctx){
        this.ctx=ctx;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setImpBT(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BLUETOOTH,status);
        editor.commit();
    }

    public boolean getImpBT(){
       SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
       return sharedPreferences.getBoolean(BLUETOOTH, false);
    }


}
