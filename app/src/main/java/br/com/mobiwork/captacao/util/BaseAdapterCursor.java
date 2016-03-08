package br.com.mobiwork.captacao.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoFazenda;
import br.com.mobiwork.captacao.dao.DaoLancamentos;
import br.com.mobiwork.captacao.model.Motorista;

/**
 * Created by LuisGustavo on 05/06/2015.
 */
public class BaseAdapterCursor  extends BaseAdapter {

    private Context context;
    private Cursor c;
    DateFormat formatter;
    Date d ;
    private int _id;
    ArrayList<Integer> p= new ArrayList<Integer>();
    String dataatualf;
    ListView le ;
    private Motorista config;
    private DaoLancamentos dl;
    private SQLiteDatabase db,dbc;
    private boolean header;
    private DaoFazenda df;


    public BaseAdapterCursor(Context context, Cursor itens,ListView l,Motorista config ,DaoLancamentos dl,SQLiteDatabase db,SQLiteDatabase dbc,String dataatualf){
        this.context = context;
        this.c = itens;
        df = new DaoFazenda(context);
        formatter= new SimpleDateFormat("dd/MM/yyyy");
        d = new Date();
        le=l;
        this.config=config;
        this.dl=dl;
        this.db=db;
        this.dbc=dbc;
        this.dataatualf=dataatualf;
        header=true;


    }


    @Override
    public int getCount() {
        return c.getCount();
    }

    @Override
    public Object getItem(int position) {
        return this.p.get(position);
    }

    @Override
    public long getItemId(int position) {

        return this.p.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_coleta_rel, null);
        }
        else{
            convertView = convertView;
        }
        boolean r=false;
        this.c.moveToPosition(position);
        this._id=Integer.parseInt(c.getString(c.getColumnIndex("fazenda._id")));

        if(p.isEmpty()){
            this.p.add(position, _id);
        }else{
            if(position==p.size()){
                this.p.add(position,_id);
            }
        }

        TextView codprodh=(TextView)convertView.findViewById(R.id.codprodh);
        LinearLayout linome=(LinearLayout)convertView.findViewById(R.id.linome);
        if(position>0){
            codprodh.setVisibility(View.GONE);
            linome.setVisibility(View.GONE);
        }else{
            codprodh.setVisibility(View.VISIBLE);
            linome.setVisibility(View.VISIBLE);
        }


        TextView codigo = (TextView) convertView.findViewById(R.id.codprod);
        codigo.setText(c.getString(c.getColumnIndex("codigo")));

        TextView nome = (TextView) convertView.findViewById(R.id.txtnome);
        nome.setText(c.getString(c.getColumnIndex("nome")));
        if(position==20){
            String teste="";
        }
        TextView volume = (TextView) convertView.findViewById(R.id.txtvolume);

        Cursor c=df.pesquisarFazenda(dbc, p.get(position));
        if (c.moveToFirst()) {
            Cursor proclan =dl.procSumVol(db, c.getString(c.getColumnIndex("codigo")),c.getString(c.getColumnIndex("fazenda")),dataatualf);
            if(proclan.moveToFirst()){;
                volume.setText(String.valueOf( proclan.getDouble(proclan.getColumnIndex("volume"))));
            }else{
                volume.setText("0.0");
            }
        }
//        volume.setText(c.getString(c.getColumnIndex("volume")));
        TextView fazenda = (TextView) convertView.findViewById(R.id.txtfazenda);
        fazenda.setText(c.getString(c.getColumnIndex("descri")));





        return convertView;
    }
}
