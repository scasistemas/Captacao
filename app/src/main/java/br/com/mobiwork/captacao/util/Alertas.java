package br.com.mobiwork.captacao.util;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import br.com.mobiwork.captacao.R;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class Alertas {

    private Context ctx;
    protected static final String ZXING_MARKET = "market://search?q=pname:cn.menue.barcodescanner";
    protected static final String ZXING_DIRECT = "https://zxing.googlecode.com/files/BarcodeScanner3.1.apk";

    public Alertas(Context context){
        ctx=context;
    }

    public void Alerta( String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(mensagem);
        builder.setNegativeButton("OK", null);
        AlertDialog d = builder.create();
        d.show();

    }
    public double AlertaYN(String mensagem, String texto){
        final double[] resp = new double[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(texto);
        builder.setTitle(mensagem);
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resp[0] =1;
                    }
                });

        builder.setNegativeButton("Nao",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resp[0] =0;
                    }
                });

        AlertDialog d = builder.create();
        d.show();


        return resp[0];
    }
    public void AlertaSinc(String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(mensagem);
        builder.setNegativeButton("OK", null);
        AlertDialog d = builder.create();
        d.show();

    }
    public void msg(String titulo,String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(mensagem);
        builder.setTitle(titulo);
        builder.setNegativeButton("OK", null);
        AlertDialog d = builder.create();
        d.show();

    }

    public int AlertaEsc(String [] titulo,String mensagem,String param){
        final int[] arrayOfInt = { 0 };
        String[] arrayOfString = new String[15];

        for(int i=0;i<titulo.length;i++){
            arrayOfString[i]=titulo[i];
        }
        final AlertDialog.Builder localBuilder = new AlertDialog.Builder(ctx);
        localBuilder.setTitle(mensagem).setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
                arrayOfInt[0] = paramAnonymous2Int;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
                paramAnonymous2DialogInterface.cancel();


            }
        });
        localBuilder.create().show();
        return arrayOfInt[0];
    }
    public void baixarBarcode() {
        new AlertDialog.Builder(ctx)
                .setTitle("Instalar o scanner codigo de barras?")
                .setMessage("Deseja baixar o aplicativo para o escaneamento?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Instalar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_MARKET));
                                try {
                                    ctx.startActivity(intent);
                                } catch (ActivityNotFoundException e) { // Se nao tiver o Play Store
                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_DIRECT));
                                    ctx.startActivity(intent);
                                }
                            }
                        })
                .setNegativeButton("Cancelar", null).show();

    }

    public int alertaExcluirPedido(String msg) {
        final int[] resp = {0};
        new AlertDialog.Builder(ctx)
                .setTitle(msg)
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            resp[0] =i;

                        }
                    }
                }).show();
        return resp[0];
    }


}