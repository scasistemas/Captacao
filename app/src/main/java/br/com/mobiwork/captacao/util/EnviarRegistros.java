package br.com.mobiwork.captacao.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.captacao.coleta.Coleta;
import br.com.mobiwork.captacao.dao.DaoCreateDBEnvio;
import br.com.mobiwork.captacao.dao.DaoLancamentos;
import br.com.mobiwork.captacao.model.Lancamentos;

/**
 * Created by LuisGustavo on 01/06/2015.
 */
public class EnviarRegistros extends Activity {
    private static final String TAG = EnviarRegistros.class.getSimpleName();
    private Cursor cursor, c,c2,c3;
    private Context context;


    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    public ContentValues ContentValues(Cursor c, String loteEnvio) {
        ContentValues result = new ContentValues();
        for (int i = 0; i < c.getColumnCount(); i++) {
            if (c.getString(i) == null){
                    result.put(c.getColumnName(i), "");
            } else {
                    result.put(c.getColumnName(i), c.getString(i));
            }
        }
        return result;
    }

    public String gerarLoteDePedidos(Context ctx, SQLiteDatabase db, String codmot,String dataIni,String dataFim)  {
        context=ctx;

        DataHoraPedido dataHoraMS = DataHoraPedido.criarDataHoraPedido();

        DaoLancamentos dc = new DaoLancamentos(context);
        if(dataIni.equalsIgnoreCase("")&&dataFim.equalsIgnoreCase("")){
            c = dc.procLancGeral(db);
            dataIni="Geral";
        }else {
            c = dc.procLancPer(db, dataIni, dataFim);
        }

        String loteEnvio = codmot + "_" + String.valueOf(dataHoraMS.getDia()) + "_" +
                String.valueOf(dataHoraMS.getMes()) + "_" +
                String.valueOf(dataHoraMS.getAno()) + " " +
                String.valueOf(dataHoraMS.getHora()) + "." +
                String.valueOf(dataHoraMS.getMinuto()) + "." +
                String.valueOf(dataHoraMS.getSegundo()) + "." +
                String.valueOf(dataHoraMS.getAm_pm())+"--"+dataIni+" - "+dataFim;
        ArrayList<ContentValues> lancamentos = new ArrayList<ContentValues>();
        if (c.moveToFirst()) {

            do {
                lancamentos.add(ContentValues(c, loteEnvio));

            } while (c.moveToNext());
        }


        DaoCreateDBEnvio dao = new DaoCreateDBEnvio(ctx);
        db =  dao.getWritableDatabase();
        db.execSQL("DELETE FROM lancamentos");

        for (ContentValues value : lancamentos){
            dao.insert(db,"lancamentos", "", value);
        }
        enviarLoteDePedidos(loteEnvio,"pedidos");
        return loteEnvio;

    }

    public String gerarBackup(Context ctx, SQLiteDatabase db)  {
        context=ctx;
        SimpleDateFormat fmtdata2= new SimpleDateFormat("yyyy-MM-dd");
        Date data = new Date();
        String dataIni=fmtdata2.format(data);
        String dataFim=dataIni;
        DaoLancamentos dc = new DaoLancamentos(context);
        c = dc.procLancPer(db, dataIni, dataFim);
        String loteEnvio = "Backup--"+dataIni;
        ArrayList<ContentValues> lancamentos = new ArrayList<ContentValues>();
        if (c.moveToFirst()) {

            do {
                lancamentos.add(ContentValues(c, loteEnvio));

            } while (c.moveToNext());
        }
        DaoCreateDBEnvio dao = new DaoCreateDBEnvio(ctx);
        db =  dao.getWritableDatabase();
        db.execSQL("DELETE FROM lancamentos");

        for (ContentValues value : lancamentos){
            dao.insert(db,"lancamentos", "", value);
        }
        enviarLoteDePedidos(loteEnvio,"backup");
        return loteEnvio;

    }




    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enviarLoteDePedidos( String nome,String paramBackup)  { //SQLiteDatabase db,
        String dbPackage = "br.com.mobiwork.captacao";
        String dbName = "captacaoDBEnvio";
        String dbPath = Environment.getDataDirectory() + "/data/" + dbPackage + "/databases/";
        File dbFile=null;
        File dbFile2=null;
        File exportDir = new File(dbPath);
        if (exportDir.exists()){
            File file = new File(exportDir, dbName);
            try {
                file.createNewFile();
                String thisFile="";
                if(paramBackup.equalsIgnoreCase("backup")) {
                    thisFile = FolderConfig.getExternalStorageDirectoryBackupSegDiario() + "/" + nome  + ".db";
                }else{
                    thisFile = FolderConfig.getExternalStorageDirectory() + "/" + nome  + ".db";
                }
                dbFile = new File(thisFile);
                String thisFile2="";
                if(System.getenv("SECONDARY_STORAGE")!=null){
                    thisFile2 = System.getenv("SECONDARY_STORAGE") + "/Android/data/br.com.mobiwork.captacao/files/" + nome + ".db";
                }else{

                    thisFile2 = FolderConfig.getExternalStorageDirectoryCopia() + "/" + nome  + ".db";
                }
                dbFile2 = new File(thisFile2);
                this.copyFile(file,dbFile);
                this.copyFile(dbFile,dbFile2);

            } catch (IOException e) {
                if(!dbFile.exists()){
                    showToast("Erro na Grav. Arquivo ");
                }
                if(!dbFile2.exists()){
                    if(System.getenv("SECONDARY_STORAGE")!=null) {
                        showToast("Erro na Grav. no SD CARD");
                    }
                }
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

}
