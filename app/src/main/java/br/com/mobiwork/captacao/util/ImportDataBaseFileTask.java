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

public class ImportDataBaseFileTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = ImportDataBaseFileTask.class.getSimpleName();

    private final ProgressDialog dialog;
    private String dbPackage;
    private String dbName;
    private String dbPath;
    private int caminho=0;

    public ImportDataBaseFileTask(Context context, String dbPackage, String dbName,int paramcaminho) {
        dialog = new ProgressDialog(context);
        this.dbPackage = dbPackage;
        this.dbName = dbName;
        dbPath = Environment.getDataDirectory() + "/data/" + dbPackage + "/databases/";
        this.caminho=paramcaminho;

    }

    // can use UI thread here
    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Exporting database...");
        this.dialog.show();
    }

    // automatically done on worker thread (separate from UI thread)
    @Override
    protected Boolean doInBackground(final String... args) {
        File dbFile;
        if(caminho==0) {
             dbFile = new File(FolderConfig.getExternalStorageDirectoryBackupSeg() + "/" + dbName);
        }else{
            dbFile = new File( System.getenv("SECONDARY_STORAGE") + "/Android/data/br.com.mobiwork.captacao/files/" + dbName);
        }
        // path on sd by convention
        File exportDir = new File(dbPath);
        if (!exportDir.exists()) {
            // boolean result =
            exportDir.mkdirs();
            // Log.i(TAG, "create directory " + (result ? "succesful" :
            // "failed"));
        }

        File file = new File(exportDir, dbName);

        try {
            file.createNewFile();
            this.copyFile(dbFile, file);
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    // can use UI thread here
    @Override
    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (success) {
            BkpBancoDeDados.showShortToast("Restaurado com Sucesso!");
        } else {
            BkpBancoDeDados.showShortToast("Falha na Restauracao!");
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