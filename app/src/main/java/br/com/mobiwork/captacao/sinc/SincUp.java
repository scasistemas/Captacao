package br.com.mobiwork.captacao.sinc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoLancamentos;
import br.com.mobiwork.captacao.dao.DaoMateria;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.model.Lancamentos;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.util.Alertas;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.EnviarRegistros;
import br.com.mobiwork.captacao.util.FolderConfig;
import br.com.mobiwork.captacao.util.GoogleDrive;
import br.com.mobiwork.captacao.util.SincDadosSmb;
import br.com.mobiwork.captacao.util.ToastManager;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class SincUp extends AsyncTask<String, Void, Boolean> {

    private SQLiteDatabase db,dbC;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private String loteEnvio,erro,logerro,caminteste,dataini,datafim;
    private Motorista config;
    private DaoCreateDB daoDB;
    private DaoCreateDBC daoDBC;
    private DaoLancamentos dl;
    private DaoMotorista dm;
    private volatile boolean running = true;
    Intent i ;
    ToastManager tm;
    SincDadosSmb smb;
    Context ctx;
    Lancamentos lan;
    Alertas a;
    EnviarRegistros envReg;
    Activity confirma = (Activity)ctx;
    public AsyncResponse delegate=null;
    private String paramup;
    private GoogleDrive gd;


    public  SincUp(Context paramctx,Motorista m,String dataini,String datafim,String paramup) {

        ctx=paramctx;
        daoDB= new DaoCreateDB(ctx);
        daoDBC = new DaoCreateDBC(ctx);
        confirma = (Activity)ctx;
        this.dialog = new ProgressDialog(paramctx);
        db = daoDB.getWritableDatabase();
        dbC=daoDBC.getWritableDatabase();
        config= m;
        if(config==null){
            new DataXmlExporter(this.dbC, FolderConfig.getExternalStorageDirectory()).importData(ctx, "config", "config", "");
            config= new DaoMotorista(ctx).consultar(this.dbC);
        }
        this.dataini=dataini;
        this.datafim=datafim;
        this.paramup=paramup;
        if(paramup.equalsIgnoreCase("drive")){
            gd = new GoogleDrive(ctx);
        }
        a= new Alertas(paramctx);
        dl = new DaoLancamentos(ctx);
        dm = new DaoMotorista(ctx);
        envReg = new EnviarRegistros();
        erro="1";

    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("COLETA");
        this.dialog.setMessage("Processando Coleta...");
        dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(2);

        dialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //button click stuff here
                        try {
                            cancel(true);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }

                        if(ctx.getClass().getSimpleName().equalsIgnoreCase("ConfirmaPedido")){
                            confirma.finish();
                        }
                        //   dialog.dismiss();
                    }
                });

        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(String[] paramArrayOfString)
    {
        try{
            loteEnvio = envReg.gerarLoteDePedidos(ctx,db,config.getCodigo(),dataini,datafim);
            dialog.setProgress(1);
        }catch (Exception e){
            erro="Erro ao Gerar Pedido";
            logerro=e.getMessage();
        }
        try
        {
            smb = new SincDadosSmb(config,1);

                if(paramup.equalsIgnoreCase("WIFI")){
                    if(!isCancelled()){
                        erro = smb.sendFileFromPeerToSdcard(this.loteEnvio, ".db");
                    }
                }else{
                   // gd.getDriveContents();
                    erro=gd.saveFileToDrive(this.loteEnvio+".db");
                }


        }catch (Exception e){
            String erro =e.getMessage();

        }

       finally
        {
            dialog.setProgress(2);
            if(erro.equals("1")){
                return Boolean.valueOf(false);
            }else{
                return Boolean.valueOf(true);
            }

        }

    }


    @Override
    protected void onCancelled() {

        running = false;
    }


    @Override
    protected void onPostExecute(Boolean paramBoolean)
    {
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setText("OK");
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
        delegate.processFinish(erro);
        if(paramBoolean.booleanValue()) {
            this.dialog.setMessage(erro);
            return;
        }else{
            this.dialog.setMessage("Enviado com Sucesso");
            return;
        }

    }


}
