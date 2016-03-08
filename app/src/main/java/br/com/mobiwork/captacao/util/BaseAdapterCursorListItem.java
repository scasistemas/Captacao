package br.com.mobiwork.captacao.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoLancamentos;
import br.com.mobiwork.captacao.dao.DaoMateria;
import br.com.mobiwork.captacao.dao.DaoProdutor;
import br.com.mobiwork.captacao.model.Motorista;

/**
 * Created by LuisGustavo on 05/06/2015.
 */
public class BaseAdapterCursorListItem extends BaseAdapter {

    private Context context;
    private Cursor c;
    private int _id;
    ArrayList<Integer> p= new ArrayList<Integer>();
    String dataatualf;
    ListView le ;
    private SQLiteDatabase db,dbC;
    private DaoMateria dm;
    private DaoProdutor dp;


    public BaseAdapterCursorListItem(Context context, Cursor itens, ListView l, SQLiteDatabase db,SQLiteDatabase dbC, String dataatualf){
        this.context = context;
        this.c = itens;
        le=l;
        this.db=db;
        this.dbC=dbC;
        this.dataatualf=dataatualf;
        dm= new DaoMateria(context);
        dp = new DaoProdutor(context);


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
            convertView = inflater.inflate(R.layout.list_lanc_item, null);
        }
        else{
            convertView = convertView;
        }
        boolean r=false;
        this.c.moveToPosition(position);
        this._id=Integer.parseInt(c.getString(c.getColumnIndex("_id")));
        if(p.isEmpty()){
            this.p.add(position, _id);
        }else{
            if(position==p.size()){
                this.p.add(position,_id);
            }
        }



        TextView materia = (TextView) convertView.findViewById(R.id.lancmateria);
        materia.setText(dm.pesqNomeMateria(dbC,c.getString(c.getColumnIndex("materia"))));

        TextView volume = (TextView) convertView.findViewById(R.id.lancqtd);
        volume.setText("V: "+ c.getString(c.getColumnIndex("volume")));

        return convertView;
    }
}
