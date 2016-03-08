package br.com.mobiwork.captacao.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class ExportDataBaseFileTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = ExportDataBaseFileTask.class.getSimpleName();

    private final ProgressDialog dialog;
    private String dbPackage;
    private String dbName;
    private String dbPath;
    String caminho;

    public ExportDataBaseFileTask(Context context, String dbPackage, String dbName,String caminho) {
        dialog = new ProgressDialog(context);
        this.dbPackage = dbPackage;
        this.dbName = dbName;
        dbPath = Environment.getDataDirectory() + "/data/" + dbPackage + "/databases/";
        this.caminho=caminho;
    }

    // can use UI thread here
    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Exportando database...");
        this.dialog.show();
    }

    // automatically done on worker thread (separate from UI thread)
    @Override
    protected Boolean doInBackground(String[] paramArrayOfString)
    {
        File localFile1 = new File(this.dbPath + this.dbName);
        File localFile2 = new File(caminho);
        if (!localFile2.exists())
            localFile2.mkdirs();
        File localFile3 = new File(localFile2, this.dbName);
        try
        {
            localFile3.createNewFile();
            copyFile(localFile1, localFile3);
            Boolean localBoolean = Boolean.valueOf(true);
            return localBoolean;
        }
        catch (IOException localIOException)
        {
            Log.e(TAG, localIOException.getMessage(), localIOException);
        }
        return Boolean.valueOf(false);
    }

    // can use UI thread here
    @Override
    protected void onPostExecute(Boolean paramBoolean)
    {
        if (this.dialog.isShowing())
            this.dialog.dismiss();
        if (paramBoolean.booleanValue())
        {
            //    BkpBancoDeDados.showShortToast("Exportado com sucesso!");
            return;
        }
        //  BkpBancoDeDados.showShortToast("Export.failed");
    }

    void copyFile(File paramFile1, File paramFile2)
            throws IOException
    {
        FileChannel localFileChannel1 = new FileInputStream(paramFile1).getChannel();
        FileChannel localFileChannel2 = new FileOutputStream(paramFile2).getChannel();
        try
        {
            localFileChannel1.transferTo(0L, localFileChannel1.size(), localFileChannel2);
            return;
        }
        finally
        {
            if (localFileChannel1 != null)
                localFileChannel1.close();
            if (localFileChannel2 != null)
                localFileChannel2.close();
        }

    }

}