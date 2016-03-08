package br.com.mobiwork.captacao.coleta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Calendar;

import br.com.mobiwork.captacao.R;


import br.com.mobiwork.captacao.atualizar.ListAtu;
import br.com.mobiwork.captacao.calendar.MyCalendarActivity;
import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoLancamentos;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.info.Informacoes;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.sinc.SincUp;
import br.com.mobiwork.captacao.util.Alertas;
import br.com.mobiwork.captacao.util.BkpBancoDeDados;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.FolderConfig;


/**
 * Created by LuisGustavo on 10/06/2015.
 */
public class ColetaEnv extends Activity implements AsyncResponse {

    private EditText datain,datafim;
    private String dataInGlobal="",datafimGlobal="";
    private SincUp sincup;
    private Motorista config;
    private DaoCreateDBC daoDBC;
    private DaoCreateDB daoDB;
    private SQLiteDatabase dbC,db;
    private ListView listlanc;
    private ListAdapter adapter;
    private DaoLancamentos dl ;
    private Cursor lancam;
    private TextView text_date;
    private DatePicker date_picker;
    private Button button;

    private int year;
    private int month;
    private int day;
    int datasel=0;

    static final int DATE_DIALOG_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exportalanc);
        datain= (EditText)findViewById(R.id.datain);
        datafim=(EditText)findViewById(R.id.datafim);
        listlanc=(ListView)findViewById(R.id.listlanc);
        daoDB = new DaoCreateDB(this);
        daoDBC = new DaoCreateDBC(this);
        dbC=daoDBC.getWritableDatabase();
        db=daoDB.getWritableDatabase();
        dl = new DaoLancamentos(this);
        config= new Motorista();
        config= new DaoMotorista(this).consultar(dbC);
        if(config.getLogin()==null||config.getLogin().equalsIgnoreCase("")){
            new DataXmlExporter(dbC, FolderConfig.getExternalStorageDirectory()).importData(this, "config", "motorista", "");
            config= new DaoMotorista(this).consultar(dbC);
        }
        datain.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasel=0;
                if(Integer.valueOf(android.os.Build.VERSION.SDK)>11){
                    startActivityForResult(new Intent(ColetaEnv.this, MyCalendarActivity.class), 131);
                }else {
                    showDialog(DATE_DIALOG_ID);
                }
            }
        });
        datafim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasel=1;
                int a =Integer.valueOf(android.os.Build.VERSION.SDK);
                if(Integer.valueOf(android.os.Build.VERSION.SDK)>11){
                    startActivityForResult(new Intent(ColetaEnv.this, MyCalendarActivity.class), 132);
                }else {
                    showDialog(DATE_DIALOG_ID);
                }
            }
        });

        popular();

        listlanc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String d = lancam.getString(lancam.getColumnIndex("data"));
                String de = lancam.getString(lancam.getColumnIndex("dataform"));
                irColeta(lancam.getString(lancam.getColumnIndex("data")), lancam.getString(lancam.getColumnIndex("dataform")));
            }
        });

        setCurrentDate();





    }

    public void irColeta(String data,String dataform){
        Intent ix = new Intent(this,Coleta.class);
        ix.putExtra("data",dataform);
        ix.putExtra("data2",data);
        this.startActivityForResult(ix, 100);
    }

    public void enviar(View v){

        if(dataInGlobal.equalsIgnoreCase("")&&!datafimGlobal.equalsIgnoreCase("")||!dataInGlobal.equalsIgnoreCase("")&&datafimGlobal.equalsIgnoreCase("")){
            Alertas a = new Alertas(this);
            a.AlertaSinc("Por favor preencha as duas Datas");
        }else {
            new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_WIFI_OFFLINE, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    String param="";
                    if (paramAnonymousInt == 0) {
                        param="WIFI";
                    } else if (paramAnonymousInt == 1) {
                        param="drive";
                    } else if (paramAnonymousInt==2){
                        param="offline";
                    }
                    sincup = new SincUp(ColetaEnv.this, config, dataInGlobal, datafimGlobal,param);
                    sincup.delegate = ColetaEnv.this;
                    sincup.execute(new String[0]);

                }
            }).show();


        }



    }

    public void opcoesDoMenu(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_env, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarMenu(i);
                    }
                }).show();
    }

    protected void selecionarMenu(int i) {
        switch (i) {
            case 0:
               ColetaEnv.this.finish();
                break;
        }
    }


    public void popularLista (View v){
     popular();

   }
    public void popular(){
        lancam=dl.procLancData(db,dataInGlobal,datafimGlobal);
        if(dataInGlobal.equals("")&&datafimGlobal.equals("")){
            lancam=dl.procLancDataGeral(db);
        }
        adapter = new SimpleCursorAdapter(this,R.layout.listdtalancam,lancam,new String[] {"dataform","ncoletas"},
                new int[] {R.id.txtdata, R.id.txtqtd});
        listlanc.setAdapter(adapter);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==131){
            if(data!=null){
                dataInGlobal=data.getExtras().getString("data1");
                datain.setText(data.getExtras().getString("data2"));
            }

        }
        if(requestCode==132){
            if(data!=null) {
                datafimGlobal = data.getExtras().getString("data1");
                datafim.setText(data.getExtras().getString("data2"));
            }
        }
        popular();

    }

    public void voltar(View v){
        ColetaEnv.this.finish();
    }

    @Override
    public void processFinish(String output) {

    }



    // display current date both on the text view and the Date Picker
    public void setCurrentDate() {

        date_picker = (DatePicker) findViewById(R.id.date_picker);

        final Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // set current date into Date Picker
        date_picker.init(year, month, day, null);

    }


    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,day);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into Text View
            if(datasel==0) {
                dataInGlobal= year+"-"+String.format("%02d",month+1)+"-"+String.format("%02d",day);
                datain.setText(new StringBuilder().append(String.format("%02d", day))
                        .append("/").append(String.format("%02d", month + 1)).append("/").append(year).append(" "));
            }else{
                datafimGlobal= year+"-"+String.format("%02d",month+1)+"-"+String.format("%02d", day);
                datafim.setText(new StringBuilder().append(String.format("%02d", day))
                        .append("/").append(String.format("%02d", month + 1)).append("/").append(year).append(" "));
            }

            // set selected date into Date Picker

            date_picker.init(year, month, day, null);

        }
    };


}
