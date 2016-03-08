package br.com.mobiwork.captacao.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import br.com.mobiwork.captacao.R;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class BkpBancoDeDados extends Activity implements View.OnClickListener {



    public static Context mainContext;
    private ProgressDialog dialog;
    private Handler handler = new Handler();
    private String tBkp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bkpdb);
        this.tBkp = getIntent().getStringExtra("tBkp");
        Button button1Click = (Button) findViewById(R.id.btBackup);
        button1Click.setOnClickListener(this);
        button1Click = (Button) findViewById(R.id.btvoltar);
        button1Click.setOnClickListener(this);
        this.mainContext = this;


        if (this.tBkp.endsWith("Restaure"))
        {
            startActivityForResult(new Intent(this, ConfirmaRestaure.class), 2);
            return;
        } else if(this.tBkp.endsWith("Backuprapido")){

            exportToSd();
            BkpBancoDeDados.this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btBackup:
                exportToSd();
                break;
            case R.id.btvoltar:
                BkpBancoDeDados.this.finish();
                break;
        }
    }

    public void exportToSd()
    {
        if ((!"br.com.mobiwork.captacao".equals("")) && (!"captacaoDB".equals("")))
        {
            if (isExternalStorageAvailable()) {
                try{
                    new ExportDataBaseFileTask(this, "br.com.mobiwork.captacao", "captacaoDB",FolderConfig.getExternalStorageDirectoryBackupSeg()).execute(new String[0]);
                    showShortToast("Exportado com sucesso!");
                }catch(Exception e){
                    showShortToast("Erro ao efetuar Backup INTERNO");

                }

                try{
                    new ExportDataBaseFileTask(this, "br.com.mobiwork.captacao", "captacaoDB",System.getenv("SECONDARY_STORAGE") + "/Android/data/br.com.mobiwork.captacao/files/").execute(new String[0]);
                    showShortToast("Exportado com sucesso!");
                }catch(Exception e){
                    showShortToast("Erro ao efetuar Backup SDCARD");

                }
            }
        }
        else {
            showShortToast("Pasta de exportacao nao esta disponivel.");
            return;
        }

    }

    private boolean importToSd(int i)
    {
        Boolean result=false;
        if ((!"br.com.mobiwork.captacao".equals("")) && (!"captacaoDB".equals("")))
        {
            if (isExternalStorageAvailable()) {
                try{
                    new ImportDataBaseFileTask(this, "br.com.mobiwork.captacao", "captacaoDB",i).execute(new String[0]);
                    result=true;
                }catch (Exception e){
                }
            }
        }
        else {
            showShortToast("Pasta de exportacao nao esta disponivel.");

        }
        return result;
    }

    private void importToSdBkpEnvio()
    {
        if ((!"br.com.mobiwork.captacao".equals("")) && (!"captacaoDB".equals("")))
        {
            if (isExternalStorageAvailable()) {
                new ImportDataBaseFileTask(this, "br.com.mobiwork.captacao", "captacaoDB",0).execute(new String[0]);
            }
        }
        else {
            return;
        }
        showShortToast("External storage is not available, unable to export data.");
    }

    public static void showShortToast(String paramString)
    {
        Toast.makeText(BkpBancoDeDados.mainContext, paramString, 1).show();
    }

    public boolean isExternalStorageAvailable()
    {
        return Environment.getExternalStorageState().equals("mounted");
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if(paramIntent!=null) {
        Bundle params = paramIntent.getExtras();
            if (paramInt2 == -1) {
                if (params.getString("senhaRestaure").equals("123456")) {

                    new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_SDCARD_INT, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            restaure(paramAnonymousInt);
                        }
                    }).show().setCancelable(false);

                }
            }else{
                BkpBancoDeDados.this.finish();
            }
        }else{
            BkpBancoDeDados.this.finish();
        }

    }

    public void restaure(int i){
        if(i==2){
            BkpBancoDeDados.this.finish();
        }else{
            importToSd(i);
            BkpBancoDeDados.this.finish();
        }

    }
}

