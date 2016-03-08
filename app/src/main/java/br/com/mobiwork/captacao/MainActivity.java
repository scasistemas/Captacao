package br.com.mobiwork.captacao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.mobiwork.captacao.atualizar.ListAtu;
import br.com.mobiwork.captacao.coleta.Coleta;
import br.com.mobiwork.captacao.coleta.ColetaEnv;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.impBluetooth.Bluetooth_main;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.info.Informacoes;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.sinc.SincDown;
import br.com.mobiwork.captacao.util.BkpBancoDeDados;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.FolderConfig;


public class MainActivity extends Activity implements AsyncResponse {


    private SincDown sinc;
    public int selecionarsincMenuPricipalID;
    private Date data;
    private  SimpleDateFormat fmtdata,fmtdata2;
    private Motorista config;
    private SQLiteDatabase dbC;
    private DaoCreateDBC daoDBC;
    public Button btopecoes;
    public ImageButton imgfiltmain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createConfig();
        btopecoes=(Button)findViewById(R.id.btopcoes);
        imgfiltmain=(ImageButton)findViewById(R.id.imgfiltmain);
    }

    public void createConfig(){
        FolderConfig.getExternalStorageDirectory();
        FolderConfig.getExternalStorageDirectoryVs();
        FolderConfig.getExternalStorageDirectoryBackupSeg();
        FolderConfig.getExternalStorageDirectoryBackupSegDiario();
        MainActivity.this.getExternalFilesDir(null);
    }


    public void ircoleta(View v) {
        data = new Date();
        fmtdata= new SimpleDateFormat("dd/MM/yyyy");
        fmtdata2= new SimpleDateFormat("yyyy-MM-dd");
        Intent ix = new Intent(this,Coleta.class);
        ix.putExtra("data", fmtdata.format(data));
        ix.putExtra("data2", fmtdata2.format(data));
        this.startActivityForResult(ix, 1);

    }

    public void opcoesDoMenu(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_principal, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                           selecionarMenu(i);
                    }
                }).show();
    }

    public void btopcoes(View v){
        if(btopecoes.getVisibility()==View.VISIBLE) {
            btopecoes.setVisibility(View.GONE);
            imgfiltmain.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_collapse2));
        }else{
            btopecoes.setVisibility(View.VISIBLE);
            imgfiltmain.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_expand2));
        }
    }

    protected void selecionarsincMenuPricipal(int paramInt1) {

        daoDBC = new DaoCreateDBC(this);
        dbC=daoDBC.getWritableDatabase();
        config= new DaoMotorista(this).consultar(this.dbC);
        if(config==null ){
            new DataXmlExporter(this.dbC, FolderConfig.getExternalStorageDirectory()).importData(this, "config", "config", "");
            config= new DaoMotorista(this).consultar(this.dbC);
        }
        switch (paramInt1) {
            case 0:
                sinc = new SincDown(MainActivity.this, config, "wifi", "");
                sinc.delegate = MainActivity.this;
                sinc.execute(new String[0]);
                break;

            case 1:
                sinc = new SincDown(MainActivity.this, config, "drive", "");
                sinc.delegate = MainActivity.this;
                sinc.execute(new String[0]);
                break;


            case 2:
                sinc = new SincDown(MainActivity.this, config, "OffLine", "");
                sinc.delegate = MainActivity.this;
                sinc.execute(new String[0]);
                break;

        }
    }

    protected void selecionarAtu(int paramInt1) {
        Intent ix = null;
        switch (paramInt1) {

            case 0:
                ix = new Intent(this, ListAtu.class);
                ix.putExtra("conn","wifi");
                this.startActivityForResult(ix, 0);
                break;

            case 1:
                ix = new Intent(this, ListAtu.class);
                ix.putExtra("conn","drive");
                this.startActivityForResult(ix, 0);
                break;

        }

    }


    private void sincMenuPricipal()
    {
        new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_WIFI_OFFLINE, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                MainActivity.this.selecionarsincMenuPricipalID = paramAnonymousInt;
                selecionarsincMenuPricipal(selecionarsincMenuPricipalID);

            }
        }).show();

    }
    private void atuSistema()
    {
        new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_GD_DRIVE, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                MainActivity.this.selecionarsincMenuPricipalID = paramAnonymousInt;
                selecionarAtu(selecionarsincMenuPricipalID);

            }
        }).show();

    }



    private void runBkp()
    {
        new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_BKP_REST, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                MainActivity.this.selecionarsincMenuPricipalID = paramAnonymousInt;
                selecionarBkp(selecionarsincMenuPricipalID);

            }
        }).show();

    }

    protected void selecionarBkp(int paramInt1) {
        Intent ix = new Intent(this,BkpBancoDeDados.class);
        switch (paramInt1) {

            case 0:
                ix = new Intent(this,BkpBancoDeDados.class);
                ix.putExtra("tBkp", "Backuprapido");
                this.startActivityForResult(ix,0);
                break;


            case 1:
                ix.putExtra("tBkp", "Restaure");
                this.startActivity(ix);
                break;

        }
    }


    protected void selecionarMenu(int i) {
        switch (i) {
            case 0:
                Intent j = new Intent(MainActivity.this, ColetaEnv.class);
                MainActivity.this.startActivityForResult(j, 0);
                break;
            case 1:
                sincMenuPricipal();

                break;
            case 2:
                if (!FolderConfig.verConfig()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Aplicativo nao configurado.Deseja criar as configuracoes?")
                            .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    if (i == 0) {
                                        FolderConfig.getExternalStorageDirectory();
                                        FolderConfig.getExternalStorageDirectoryVs();
                                        FolderConfig.getExternalStorageDirectoryBackupSeg();
                                        FolderConfig.getExternalStorageDirectoryBackupSegDiario();
                                        MainActivity.this.getExternalFilesDir(null);
                                        Intent ix = new Intent(MainActivity.this, Informacoes.class);
                                        MainActivity.this.startActivityForResult(ix, 2);
                                    }
                                }
                            }).show();
                    break;
                }else {
                   Intent ix = new Intent(MainActivity.this, Informacoes.class);
                   MainActivity.this.startActivityForResult(ix, 2);
               }
                break;
            case 3:
                Intent ix = new Intent(MainActivity.this, Bluetooth_main.class);
                startActivity(ix);
                break;
            case 4:
                atuSistema();
                break;
            case 5:
                runBkp();
                break;
            case 6:


                new AlertDialog.Builder(this)
                        .setTitle("Deseja Sair do Pedido sem Salvar?")
                        .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                if (i == 0) {
                                    MainActivity.this.finish();
                                }
                            }
                        }).show();
                break;

        }
    }

    @Override
    public void processFinish(String output) {

    }
}
