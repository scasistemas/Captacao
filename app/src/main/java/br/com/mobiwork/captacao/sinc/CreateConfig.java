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
public class CreateConfig extends AsyncTask<String, Void, Boolean> {

    private SQLiteDatabase db;
    private ProgressDialog dialog;
    private String erro;
    private DaoCreateDB daoDB;
    private DaoMotorista dm;
    private volatile boolean running = true;
    SincDadosSmb smb;
    Context ctx;
    Activity confirma = (Activity)ctx;
    public AsyncResponse delegate=null;
    private GoogleDrive gd;


    public CreateConfig(Context paramctx) {

        ctx=paramctx;
        daoDB= new DaoCreateDB(ctx);
        this.dialog = new ProgressDialog(paramctx);
        db = daoDB.getWritableDatabase();
        gd= new GoogleDrive(paramctx);
        erro="1";

    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Configuracoes");
        this.dialog.setMessage("Criando Config...");
        dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(4);

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
            dialog.setProgress(1);
        }catch (Exception e){
            erro="Erro ao Gerar Pedido";
        }
        try
        {
            dialog.setProgress(2);
            erro=gd.criarConfig(GoogleDrive.FOLDER);
            dialog.setProgress(3);
        }catch (Exception e){
             erro =e.getMessage();
        }
       finally
        {
            dialog.setProgress(4);
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

        if(paramBoolean.booleanValue()) {
            this.dialog.setMessage(erro);
            return;
        }else{
            this.dialog.setMessage("Criado Com Sucesso");
            return;
        }

    }


}
