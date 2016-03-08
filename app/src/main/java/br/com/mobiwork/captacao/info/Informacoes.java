package br.com.mobiwork.captacao.info;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.sinc.CreateConfig;
import br.com.mobiwork.captacao.sinc.SincDown;
import br.com.mobiwork.captacao.sinc.SincUp;
import br.com.mobiwork.captacao.util.Alertas;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.FolderConfig;
import br.com.mobiwork.captacao.util.GoogleDrive;

/**
 * Created by LuisGustavo on 29/05/2015.
 */
public class Informacoes extends Activity implements AsyncResponse {

    private EditText editExp,editUsui,editAnd,editAndSD,editEmp,result;
    private Motorista config;
    private Alertas a;
    private SincDown sinc;
    private SincUp sincup;
    private DaoMotorista dm;
    EditText userInput,login,senha;
    EditText edtxSit,edtxSizeDisp,edtxCaminho,edtxDriveId;
    SQLiteDatabase dbe;
    DaoCreateDBC dce;
    private TextView txCaminLoc;
    private ProgressDialog dialog;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.informacoes);
        iniEdit();
        inibloq();
      //  this.setFinishOnTouchOutside(false);
        dce = new DaoCreateDBC(this);
        dbe=dce.getWritableDatabase();
        a=new Alertas(this);
        dm= new DaoMotorista(this);
        config= new Motorista();
        config= new DaoMotorista(this).consultar(dbe);
        if(config.getLogin()==null||config.getLogin().equalsIgnoreCase("")){
            new DataXmlExporter(dbe, FolderConfig.getExternalStorageDirectory()).importData(this, "config", "motorista", "");
            config= new DaoMotorista(this).consultar(dbe);
        }

        preencherCampos();
    }


    public void preencherCampos(){

        editUsui.setText(String.valueOf(config.getCodigo())+" -"+config.getNome());
        editExp.setText(config.getEndereco());
        editAnd.setText(FolderConfig.getExternalStorageDirectory());
        editEmp.setText(config.getEmp() + " - 1.04");
        editAndSD.setText(FolderConfig.getExternalStorageDirectorySD(this));
        edtxDriveId.setText(config.getLogindrive());

    }

    public void infoAndSD(View v){
        infoAnd(0);
    }

    public void infoAndInt(View v){
        infoAnd(1);
    }


    public void infoAnd(int i){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(Informacoes.this);
        View promptsView = li.inflate(R.layout.alertinputinfo, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Informacoes.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        edtxSit = (EditText) promptsView
                .findViewById(R.id.edtxsit);
        edtxSizeDisp = (EditText) promptsView
                .findViewById(R.id.edtxsizedisp);
        edtxCaminho = (EditText) promptsView
                .findViewById(R.id.edtxcaminho);

        edtxSit.setEnabled(false);
        edtxSizeDisp.setEnabled(false);
        edtxCaminho.setEnabled(false);

        if(i==0) {

            if(!FolderConfig.getAvailableExternalMemorySize().equalsIgnoreCase("0")){
                edtxSit.setText("Disponivel");
            }else{
                edtxSit.setText("Indisponivel");
            }
            Informacoes.this.getExternalFilesDir(null);
            edtxSizeDisp.setText(FolderConfig.getAvailableExternalMemorySize());
            edtxCaminho.setText(FolderConfig.getExternalStorageDirectorySD(this));

        } else{
            if(!FolderConfig.getAvailableInternalMemorySize().equalsIgnoreCase("0")){
                edtxSit.setText("Disponivel");
            }else{
                edtxSit.setText("Indisponivel");
            }
            FolderConfig.getExternalStorageDirectory();
            FolderConfig.getExternalStorageDirectoryVs();
            edtxSizeDisp.setText(FolderConfig.getAvailableInternalMemorySize());
            edtxCaminho.setText(FolderConfig.getExternalStorageDirectory());
        }
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result

                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void habserv(View v){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(Informacoes.this);
        View promptsView = li.inflate(R.layout.alertinput, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Informacoes.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        login = (EditText) promptsView
                .findViewById(R.id.txtlogin);
        senha = (EditText) promptsView
                .findViewById(R.id.txtsenha);
       //userInput.setText("smb://10.10.10.10/sca_mercador/handped/importar/captacao/exportados");
        //login.setText("suporte");
        //senha.setText("Vk2906adm");
        userInput.setText(config.getEndereco());
        login.setText(config.getLogin());
        senha.setText(config.getSenha());
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    config.setLogin(login.getText().toString());
                                    config.setSenha(senha.getText().toString());
                                    ArrayList<String> checkedValue= new ArrayList<String>();
                                    checkedValue.add("teste");
                                    config.setLogin(login.getText().toString());
                                    config.setSenha(senha.getText().toString());
                                    config.setEndereco(userInput.getText().toString());
                                    sinc = new SincDown(Informacoes.this,config,"wifi",userInput.getText().toString() );
                                    sinc.delegate = Informacoes.this;
                                    sinc.execute(new String[0]);
                                } catch (Exception e) {
                                    a.AlertaSinc("Caminho invalido");
                                }
                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void inibloq(){
        editExp.setEnabled(false);
        editUsui.setEnabled(false);
        editAnd.setEnabled(false);
        editEmp.setEnabled(false);
        editAndSD.setEnabled(false);
        edtxDriveId.setEnabled(false);

    }


    public void iniEdit(){
        editExp=(EditText) findViewById(R.id.editExp);
        editUsui=(EditText) findViewById(R.id.editUsu);
        editAnd=(EditText) findViewById(R.id.editAnd);
        editEmp=(EditText) findViewById(R.id.editEmp);
        editAndSD=(EditText) findViewById(R.id.editAndSD);
        result = (EditText) findViewById(R.id.editTextDialogUserInput);
        txCaminLoc=(TextView)findViewById(R.id.txtCaminhoLoc);
        edtxDriveId=(EditText)findViewById(R.id.edtxdriveid);
    }


    public void voltar(View v){
        Informacoes.this.finish();
    }

    public void criarConfigs(View v){
        CreateConfig sincup = new CreateConfig(Informacoes.this);
        sincup.delegate = Informacoes.this;
        sincup.execute(new String[0]);

    }

    @Override
    public void processFinish(String output) {

        dm.update(dbe, userInput.getText().toString(),login.getText().toString(),senha.getText().toString());
        config = new DaoMotorista(Informacoes.this).consultar(dbe);
        config = new DaoMotorista(Informacoes.this).consultar(dbe);
        preencherCampos();
    }
}
