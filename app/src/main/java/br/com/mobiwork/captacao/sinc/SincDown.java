package br.com.mobiwork.captacao.sinc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import java.io.IOException;

import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.model.Lancamentos;
import br.com.mobiwork.captacao.util.Alertas;
import br.com.mobiwork.captacao.util.Arquivo;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.EnviarRegistros;
import br.com.mobiwork.captacao.util.FolderConfig;
import br.com.mobiwork.captacao.util.GoogleDrive;
import br.com.mobiwork.captacao.util.SincDadosSmb;
import br.com.mobiwork.captacao.util.ToastManager;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class SincDown extends AsyncTask<String, Void, Boolean> {

    private SQLiteDatabase db,dbC;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private String loteEnvio;
    private String erro,logerro,status,caminhoteste;
    private DaoCreateDB daoDB;
    private DaoCreateDBC daoDBC;
    private DaoMotorista dm;
    Intent i ;
    ToastManager tm;
    SincDadosSmb smb;
    GoogleDrive gd;
    Context ctx;
    Alertas a;
    Activity confirma = (Activity)ctx;
    Motorista config;
    Arquivo arq ;
    Activity af;
    private Lancamentos lancamentos;


    public AsyncResponse delegate=null;



    public  SincDown(Context paramctx,Motorista paramconfig,String paramstatus,String paramteste) {
        ctx=paramctx;
        arq= new Arquivo(paramctx);
        daoDB= new DaoCreateDB(ctx);
        daoDBC = new DaoCreateDBC(ctx);
        db=daoDB.getWritableDatabase();
        dbC=daoDBC.getWritableDatabase();
        af= (Activity) ctx;
        config=paramconfig;
        confirma = (Activity)ctx;
        this.dialog = new ProgressDialog(paramctx);
        this.status=paramstatus;
        caminhoteste=paramteste;
        if(this.status.endsWith("drive")){
            gd = new GoogleDrive(ctx);
        }


        a= new Alertas(paramctx);
        erro="";

    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Sincronizacao");
        this.dialog.setMessage("Sincronizando Dados...");
        this.dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        this.dialog.setProgress(0);
        this.dialog.setMax(6);


        dialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //button click stuff here

                        dialog.dismiss();
                        try {
                            //         this.finalize();
                            SincDown.this.cancel(true);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }


                    }
                });

        this.dialog.show();

    }




    @Override
    protected Boolean doInBackground(String[] paramArrayOfString)
    {
        boolean versao=false;;
        this.erro="";
        String result="";


        try {
            if (!this.status.endsWith("OffLine")) {
                dialog.setProgress(1);

                if(!caminhoteste.equals("")){
                    config.setEndereco(caminhoteste);
                }//
                    try {

                        if (!isCancelled()) {

                            if(this.status.endsWith("wifi")){
                                dialog.setProgress(2);
                                smb= new SincDadosSmb(config,0);
                                result = smb.downloadFilePc(String.valueOf("captacaoDBC"), Integer.parseInt(config.getCodigo()) + ".db", 0);
                                dialog.setProgress(3);
                            }else{
                                dialog.setProgress(2);
                                result=gd.getDriveContents(GoogleDrive.FOLDEREXPORTADOS);
                                if(result.equals("1")){
                                    result="";
                                }
                                dialog.setProgress(3);
                                if(result.equalsIgnoreCase("")) {
                                    result = gd.downloadItemFromList(String.valueOf("captacaoDBC"), Integer.parseInt(config.getCodigo()) + ".db","sinc");
                                }

                            }
                            if (!result.equalsIgnoreCase("")) {
                                erro = result;
                            }
                        }
                    } catch (Exception e) {
                        erro = e.getMessage();
                    }
                }
                if((erro.equalsIgnoreCase(""))){
                    String pathExternalStorage = FolderConfig.getExternalStorageDirectory();
                    String thisFile = pathExternalStorage + "/" + Integer.parseInt(config.getCodigo()) + ".zip";
                    //recebEmail(thisFile);
                    java.io.File file = new java.io.File(thisFile);
                    if (!file.exists()) {
                        thisFile = FolderConfig.getExternalStorageDirectory() + "/" + Integer.parseInt(config.getCodigo()) + ".zip";
                        file = new java.io.File(thisFile);
                    }
                    thisFile = pathExternalStorage + "/";
                    String FilePath = file.getPath();
                    dialog.setProgress(4);
                    // this.arq.unzip(FilePath, thisFile);
                    thisFile = pathExternalStorage + "/" + "captacaoDBC" + Integer.parseInt(config.getCodigo()) + ".db";
                    java.io.File dbFile = new java.io.File(thisFile);
                    config= new DaoMotorista(ctx).consultar(this.dbC);
                    if (dbFile.exists()) {
                        dialog.setProgress(5);
                        String dbPackage = "br.com.mobiwork.captacao";
                        String dbName = "captacaoDBC";
                        String dbPath = Environment.getDataDirectory() + "/data/" + dbPackage + "/databases/";
                        java.io.File exportDir = new java.io.File(dbPath);
                        if (exportDir.exists()) {
                            exportDir.delete();
                        }
                        file = new java.io.File(exportDir, dbName);
                        try {
                            file.createNewFile();
                            this.arq.copyFile(dbFile, file);

                        } catch (IOException e) {
                            String t=e.getMessage();
                            erro = "Erro ao criar arquivo baixado";
                        }
                        //break;

                    }
                }

            handler.post(new Runnable() {
                public void run() {
                }
            });
        } finally {
            dialog.setProgress(6);
            if(erro.equals("")){
                return Boolean.valueOf(false);
            }else{
                return Boolean.valueOf(true);
            }

        }

    }

    @Override
    protected void onPostExecute(Boolean paramBoolean)
    {
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setText("OK");
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
        delegate.processFinish(erro);
        if(paramBoolean.booleanValue())
        {
            this.dialog.setMessage(erro);

            return  ;
        }else{
            this.dialog.setMessage("Atualizado com Sucesso");

            return ;

        }

    }


}
