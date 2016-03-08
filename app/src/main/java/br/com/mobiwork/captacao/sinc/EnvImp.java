package br.com.mobiwork.captacao.sinc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

import java.io.File;
import java.io.IOException;

import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.model.Lancamentos;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.util.ArquivoTxt;

import br.com.mobiwork.captacao.util.utilBluetooth;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.FolderConfig;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class EnvImp extends AsyncTask<String, Void, Boolean> {

   private SQLiteDatabase db,dbC;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private Motorista config;
    private DaoCreateDB daoDB;
    private DaoCreateDBC daoDBC;
    private Context ctx;
    private Activity confirma = (Activity)ctx;
    public AsyncResponse delegate=null;
    private volatile boolean running = true;
    private String erro;
    boolean pag_test;
    private Lancamentos l;



    public EnvImp(Context paramctx,Motorista m,boolean pag_teste,Lancamentos l) {

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
        this.pag_test=pag_teste;
        this.l=l;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Impressão");
        this.dialog.setMessage("Processando impressão...");
        dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(5);

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
        utilBluetooth b = new utilBluetooth(ctx);
        try{
            ArquivoTxt at;
            String nome_arquivo;
            if(pag_test){
                nome_arquivo="paginha_teste.txt";
            }else{
               nome_arquivo=l.getData()+"__"+l.getCodigo()+"-"+l.getMateria()+".txt";
            }
            at = new ArquivoTxt(ctx, nome_arquivo,config);
            File arqtemp=at.createFileTxt();
            at.defineImp(arqtemp,l,dbC,pag_test);
            dialog.setProgress(1);
            changeMessage("Procurando Impressora");
           if(!b.findBT().equals(utilBluetooth.BTINDISPONIVEL)) {
                dialog.setProgress(2);
                changeMessage("Abrindo Conexão....");
                if (!b.BTopen(confirma)) {
                    dialog.setProgress(3);
                    changeMessage("Enviando Impressão....");
                  //  b.sendData(arqtemp);
                    dialog.setProgress(4);
                } else {
                    changeMessage("Impossivel abri conexão");
                }
            }else{
                changeMessage("Bluetooth não disponivel");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            changeMessage(e.getMessage());
            return Boolean.valueOf(false);
        } catch (IOException ex) {
            ex.printStackTrace();
            changeMessage(ex.getMessage());
            return Boolean.valueOf(false);
        } catch (Exception e){
            changeMessage(e.getMessage());
            return Boolean.valueOf(false);
        } finally {
            try {
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Boolean.valueOf(true);
    }


    @Override
    protected void onCancelled() {
        running = false;
    }

    public void changeMessage(final String msg){
        confirma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage(msg);
            }
        });

    }

    @Override
    protected void onPostExecute(Boolean paramBoolean)
    {
        this.dialog.setProgress(5);
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setText("OK");
        dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
        delegate.processFinish(erro);
        if(!paramBoolean.booleanValue()) {
            return;
        }else{
            this.dialog.setMessage("Enviado com Sucesso");
            return;
        }
    }

}
