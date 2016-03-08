package br.com.mobiwork.captacao.coleta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoFazenda;
import br.com.mobiwork.captacao.dao.DaoLancamentos;
import br.com.mobiwork.captacao.dao.DaoMateria;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.dao.DaoProdutor;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.info.Informacoes;
import br.com.mobiwork.captacao.linha.PesqLinha;
import br.com.mobiwork.captacao.model.Lancamentos;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.sinc.EnvImp;
import br.com.mobiwork.captacao.sinc.SincDown;
import br.com.mobiwork.captacao.sinc.SincUp;
import br.com.mobiwork.captacao.util.Alertas;
import br.com.mobiwork.captacao.util.BaseAdapterCursor;
import br.com.mobiwork.captacao.util.BaseAdapterCursorListItem;
import br.com.mobiwork.captacao.util.DataXmlExporter;
import br.com.mobiwork.captacao.util.EnviarRegistros;
import br.com.mobiwork.captacao.util.FolderConfig;
import br.com.mobiwork.captacao.util.SharedPreferencesUtil;

/**
 * Created by LuisGustavo on 05/06/2015.
 */
public class Coleta extends Activity implements AsyncResponse {

    private DaoCreateDB daoDB;
    private DaoCreateDBC daoDBC;
    private DaoMateria dmat;
    private SQLiteDatabase db,dbC;
    private Spinner spmateria;
    private Cursor cbmateria;
    private EditText edtxlinha;
    private DaoProdutor dp;
    private ListView listprod;
    private   Cursor produtor;
    private EditText edtxvolume,edtxtemp,edtxntanque;
    private TextView txtdescri,txtdtacol,txthora,txtpesoporlinha,txtpesogeral;
    String materiaid;
    private Motorista config;
    private Date data;
    private  SimpleDateFormat fmtdata;
    String dataAtual,dataAtualf;
    private SincUp sincup;
    private ImageView imgpesq,imgmais,imgfilt,imgclear;
    private LinearLayout lidata;
    private BaseAdapterCursor bac;
    private BaseAdapterCursorListItem baci;
    private CheckBox seq;
    private ListView llanc;
    private ListAdapter adapter;
    private DaoFazenda df;
    private InputMethodManager imm;
    private DaoLancamentos dl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coleta);

        daoDB = new DaoCreateDB(this);
        daoDBC = new DaoCreateDBC(this);
        dmat = new DaoMateria(this);
        df= new DaoFazenda(this);

        db = daoDB.getReadableDatabase();
        dbC = daoDBC.getWritableDatabase();
        edtxlinha=(EditText) findViewById(R.id.edtxlinha);
        txtdtacol=(TextView)findViewById(R.id.txtdtacol);
        listprod=(ListView)findViewById(R.id.list);
        imgpesq=(ImageView)findViewById(R.id.imgpesq);
        imgmais=(ImageView)findViewById(R.id.imgmais);
        imgfilt=(ImageView)findViewById(R.id.imgfilt);
        imgclear=(ImageView)findViewById(R.id.imgclear);
        lidata=(LinearLayout)findViewById(R.id.lidata);
        txtpesoporlinha=(TextView)findViewById(R.id.txtpesolinha);
        txtpesogeral=(TextView)findViewById(R.id.txtpesototal);
        seq=(CheckBox)findViewById(R.id.ckbseq);
        dp = new DaoProdutor(this);
        dataAtual=getIntent().getStringExtra("data");
        dataAtualf=getIntent().getStringExtra("data2");
        txtdtacol.setText("Data da Coleta: " + dataAtual);
        dl = new DaoLancamentos(this);

        edtxlinha.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(edtxlinha.getText().toString().equalsIgnoreCase("")){
                    popularlista();
                }
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        config= new Motorista();
        config= new DaoMotorista(this).consultar(dbC);
        if(config.getLogin()==null||config.getLogin().equalsIgnoreCase("")){
            new DataXmlExporter(dbC, FolderConfig.getExternalStorageDirectory()).importData(this, "config", "motorista", "");
            config= new DaoMotorista(this).consultar(dbC);
        }
        cliclist();
        clicseq();
        popularlista();
        hidenKeyboard();
    }

    public void hidenKeyboard(){

        this.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        edtxlinha.setInputType(InputType.TYPE_NULL);
        edtxlinha.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                edtxlinha.setInputType(InputType.TYPE_CLASS_TEXT);
                edtxlinha.onTouchEvent(event); // call native handler
                return true; // consume touch even
            }
        });

        if(imm != null) {

            this.imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
            if (this.imm != null)
            {
                this.imm.toggleSoftInput(0, 0);
                this.imm.showSoftInput(this.edtxlinha, 0);
            }
        }

    }

    public void clicseq(){
        seq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                           @Override
                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                               popularlista();
                                           }
                                       }
        );
    }

    public void cliclist(){
        listprod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int pos, long id) {
                // TODO Auto-generated method stub
                if (produtor != null) {
                    int cod = Integer.parseInt(String.valueOf(id));
                    Cursor c = df.pesquisarFazenda(dbC, cod);
                    if (c.moveToFirst()) {
                        listlanc(c.getString(c.getColumnIndex("codigo")), c.getString(c.getColumnIndex("fazenda")),c.getString(c.getColumnIndex("_id")));
                    }

                }

            }
        });
    }

    public void sumirPesq(View v){
        if(edtxlinha.getVisibility()==View.VISIBLE){
            edtxlinha.setVisibility(View.GONE);
            imgpesq.setVisibility(View.GONE);
            imgmais.setVisibility(View.GONE);
            imgclear.setVisibility(View.GONE);
            lidata.setVisibility(View.GONE);
            imgfilt.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_expand));
        }else{
            edtxlinha.setVisibility(View.VISIBLE);
            imgpesq.setVisibility(View.VISIBLE);
            imgmais.setVisibility(View.VISIBLE);
            lidata.setVisibility(View.VISIBLE);
            imgclear.setVisibility(View.VISIBLE);
            imgfilt.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_collapse));

        }

    }

    public void opcoesDoMenu(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_lancam, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                       selecionarMenu(i);
                    }
                }).show();
    }
    public void clear(View v){
        edtxlinha.setText("");
    }

    public void selecionarMenu(int i){
        switch (i) {
            case 0:

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
                     sincup = new SincUp(Coleta.this, config, dataAtualf, dataAtualf,param);
                     sincup.delegate = Coleta.this;
                     sincup.execute(new String[0]);

                 }
             }).show();
                break;
            case 1:
                edtxlinha.setText("");
                break;
        }

    }

   public void  popularlista(){
       String linha=null;
       if(edtxlinha.getText().toString()==null){
           linha="";
       }else{
           linha=edtxlinha.getText().toString();
       }
        produtor=dp.pesqprodutor(dbC,linha,seq.isChecked());
       if(produtor!=null) {
      /* listprod.setAdapter(new SimpleCursorAdapter(this, R.layout.list_coleta_rel, produtor, new String[]{"codigo", "nome", "descri"},
               new int[]{R.id.codprod, R.id.txtnome, R.id.txtfazenda}));*/
           DaoLancamentos dl = new DaoLancamentos(getBaseContext());
           bac = new BaseAdapterCursor(this, produtor, listprod, config, dl, db,dbC, dataAtualf);
           listprod.setAdapter(bac);
       }
       preencherpeso();

   }

    public void preencherpeso(){
        txtpesogeral.setText("Vol. Geral: "+String.valueOf(dl.procSumVolTotal(db,dataAtualf)));
        txtpesoporlinha.setText("Vol. desta Linha: " + String.valueOf(dl.procSumVolTotalLinha(db, dbC, edtxlinha.getText().toString(), dataAtualf)));
    }

    public int popularComboMateria(String cod,String fazenda){
        DaoLancamentos dl = new DaoLancamentos(this);
        ArrayList<String> materias = new ArrayList<String>();
        materias=dl.seleMateria(db,cod,dataAtualf,fazenda);
        cbmateria=dmat.popularComboOperacao(dbC);

        ArrayList materia = new ArrayList();
        ArrayList materiacod= new ArrayList();
        if(cbmateria.moveToFirst()){
            do{
                if(!verifqcombo(materias, cbmateria.getString(cbmateria.getColumnIndex("codigo")))){
                    materia.add(cbmateria.getString(cbmateria.getColumnIndex("nome")));
                    materiacod.add(cbmateria.getString(cbmateria.getColumnIndex("codigo")));
                }
            }while (cbmateria.moveToNext());
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, materia);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spmateria.setAdapter(spinnerArrayAdapter);


       if (materiaid != null) {
            if (materia != null) {

                for (int x=0;x<materia.size();x++) {
                    if (materiaid.equalsIgnoreCase(materiacod.get(x).toString())) {
                        spmateria.setSelection(x);
                    }
                }
            }
        }
        return materia.size();
    }

    public boolean verifqcombo(ArrayList<String> materias,String materia){
        for (int j = 0; j < materias.size(); j++){
            String materiaprov=materias.get(j).toString();
            if(materia.equalsIgnoreCase(materias.get(j).toString())){
                if(!materia.equalsIgnoreCase(materiaid)){
                    return true;
                }
             }
        }
        return false;
    }

    public void pesqlinha(View v){

        this.startActivityForResult(new Intent(this, PesqLinha.class), 101);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Incluir Item
        if(requestCode == 101 ) {
            if(data!=null) {
                edtxlinha.setText(data.getExtras().getString("lindescri"));
            }
        }
        popularlista();
    }



    public void insvolume(final String cod, final String materia, final String fazenda, final String fazenda_id){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(Coleta.this);
        View promptsView = li.inflate(R.layout.inseredados, null);
        boolean edit=false;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Do some stuff
            Rect displayRectangle = new Rect();
            Window window = getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            promptsView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
            promptsView.setMinimumHeight((int) (displayRectangle.height() * 0.9f));
        }


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Coleta.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        txthora = (TextView) promptsView
                .findViewById(R.id.txthora);
        txtdescri = (TextView) promptsView
                .findViewById(R.id.txtdescri);
        edtxvolume = (EditText) promptsView
                .findViewById(R.id.edtxvolume);
        edtxtemp = (EditText) promptsView
                .findViewById(R.id.edtxTemp);
        edtxntanque = (EditText) promptsView
                .findViewById(R.id.edtxNTanque);

        spmateria = (Spinner) promptsView.findViewById(R.id.spmateria);



            DaoLancamentos dl = new DaoLancamentos(getBaseContext());
            final Lancamentos l = new Lancamentos();
            final Cursor proclan = dl.procLanc(db, cod, dataAtualf, materia);
            if (proclan.moveToFirst()) {
                l.setLancamentos(proclan);
                edtxvolume.setText(String.valueOf(l.getVolume()));
                edtxtemp.setText(String.valueOf(l.getTemperatura()));
                edtxntanque.setText(String.valueOf(l.getNtanque()));
                txthora.setText("Hora da Coleta: "+l.getHora());
                materiaid = l.getMateria();
                edit=true;
            }

        txtdescri.setText(cod + "- " + dp.pesqprdoCod(dbC, cod));

        final DaoMateria dm = new DaoMateria(getBaseContext());
        if(popularComboMateria(cod,fazenda)>0) {

           // materiaid = dm.pesqMateriaFirst(dbC);
            // set dialog message
            final boolean finalEdit = edit;
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    try {
                                        DaoProdutor dp = new DaoProdutor(getBaseContext());
                                        DaoLancamentos dl = new DaoLancamentos(getBaseContext());

                                        if (!proclan.moveToFirst()) {
                                            Cursor c = dp.pesqprdoCodigo(dbC, cod);
                                            if (c.moveToFirst()) {
                                                l.set_id(c.getInt(c.getColumnIndex("produtor._id")));
                                                l.setCodigo(c.getString(c.getColumnIndex("codigo")));
                                                l.setMateria(dm.pesqMateria(dbC, spmateria.getSelectedItem().toString()));
                                                l.setLinha(c.getString(c.getColumnIndex("fazenda.codlinha")));
                                            }
                                        }
                                        if (!edtxvolume.getText().toString().equals("")) {
                                            l.setVolume(Double.parseDouble(edtxvolume.getText().toString()));
                                        }
                                        if (!edtxtemp.getText().toString().equals("")) {
                                            l.setTemperatura(Double.parseDouble(edtxtemp.getText().toString()));
                                        }
                                        if (!edtxntanque.getText().toString().equals("")) {
                                            l.setNtanque(Integer.parseInt(edtxntanque.getText().toString()));
                                        }
                                        l.setFazenda(fazenda);
                                        l.setMateria(dm.pesqMateria(dbC, spmateria.getSelectedItem().toString()));
                                        dl.insAtu(l, db, dataAtualf, l.getMateria(), finalEdit, materia);
                                        l.setData(dataAtualf);
                                        EnviarRegistros envReg = new EnviarRegistros();
                                        envReg.gerarBackup(Coleta.this, db);
                                        preencherpeso();
                                        bac.notifyDataSetChanged();
                                        if(baci!=null){
                                            baci.notifyDataSetChanged();
                                        }
                                        l.setFazenda(fazenda_id);
                                        SharedPreferencesUtil sdu = new SharedPreferencesUtil(getBaseContext());
                                        if(sdu.getImpBT()) {
                                            imprimir(l);
                                        }
                                }

                                catch(
                                Exception e
                                )

                                {
                                    e.printStackTrace();
                                }
                            }
        }
                    ).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }

            );

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }else{
            Alertas a= new Alertas(Coleta.this);
            a.AlertaSinc("Ja foram coletados todas as materias");
        }
        materiaid="";
    }

    void imprimir(final Lancamentos l){
        AlertDialog.Builder builder = new AlertDialog.Builder(Coleta.this);
        builder.setMessage("Impressão");
        builder.setTitle("Deseja Imprimir ?");
        builder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EnvImp ei = new EnvImp(Coleta.this, config, false, l);
                        ei.delegate = Coleta.this;
                        ei.execute(new String[0]);
                    }
                });

        builder.setNegativeButton("Nao",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog d = builder.create();
        d.show();
    }



    public void listlanc(final String cod, final String fazenda, final String fazenda_id){
        // get prompts.xml view

        final DaoLancamentos dl = new DaoLancamentos(this);
        final Cursor proclanc=dl.procLancF(db, cod, dataAtualf,fazenda);
        final AlertDialog alertDialog ;
        if(proclanc.getCount()<1 ){
            insvolume(cod,"novo",fazenda,fazenda_id);
        }else {
            LayoutInflater li = LayoutInflater.from(Coleta.this);
            final View promptsView = li.inflate(R.layout.list_lanc, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Coleta.this);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);
            // set dialog message
            llanc = (ListView) promptsView
                    .findViewById(R.id.listlanc);


            llanc.setAdapter(adapter);
            baci= new BaseAdapterCursorListItem(this, proclanc, llanc,db,dbC, dataAtualf);
            llanc.setAdapter(baci);



            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Novo",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    try {
                                        insvolume(cod,"novo",fazenda,fazenda_id);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }

            );

            // create alert dialog
            alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

            llanc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    // TODO Auto-generated method stub
                    int cod = Integer.parseInt(String.valueOf(id));
                    deletar(cod, dl);
                    alertDialog.cancel();
                    return true;
                }
            });

            llanc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int pos, long id) {
                    // TODO Auto-generated method stub
                    if (proclanc != null) {
                        int cod = Integer.parseInt(String.valueOf(id));
                        Cursor c = dl.procLancId(db, cod);
                        if (c.moveToFirst()) {
                            insvolume(c.getString(c.getColumnIndex("codigo")), c.getString(c.getColumnIndex("materia")),c.getString(c.getColumnIndex("fazenda")),fazenda_id);
                            alertDialog.cancel();
                        }
                    }
                    alertDialog.cancel();

                }
            });


        }

    }

    public void icon(){

        final String[] items = {
                getString(R.string.op_menu_sinc_drive),
                getString(R.string.op_menu_sinc_drive),
                getString(R.string.op_menu_sinc_drive)
        };

// dialog list icons: some examples here
        final int[] icons = {
                android.R.drawable.ic_menu_edit,
                android.R.drawable.ic_menu_send,
                android.R.drawable.ic_menu_delete
        };


        ListAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.list_row_op, items) {

            ViewHolder holder;

            class ViewHolder {
                ImageView icon;
                TextView title;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    convertView = inflater.inflate(
                            R.layout.list_item, null);

                    holder = new ViewHolder();
                    holder.icon = (ImageView) convertView
                            .findViewById(R.id.icon);
                    holder.title = (TextView) convertView
                            .findViewById(R.id.title);
                    convertView.setTag(holder);
                } else {
                    // view already defined, retrieve view holder
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.title.setText(items[position]);

                holder.icon.setImageResource(icons[position]);
                return convertView;
            }
        };

// ----------

        AlertDialog.Builder builder = new AlertDialog.Builder(Coleta.this);

        builder.setTitle("title");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // ---
            }

        });

        builder.create();
        builder.show();
    }

    private void deletar(final int  id, final DaoLancamentos dl) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_deletar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarDeletar(i,id,dl);
                    }
                }).show();

    }

    private void selecionarDeletar(int i, final int id, final DaoLancamentos dl){
        if(i==0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.op_menu_principal)
                    .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            if (i == 0) {
                                dl.deletar(db,id);
                                bac.notifyDataSetChanged();


                            }
                        }
                    }).show();
        }

    }


    @Override
    public void processFinish(String output) {

    }
}
