package br.com.mobiwork.captacao.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;


/**
 * Created by LuisGustavo on 18/05/2015.
 */
public class BaseAdapterFiltroLista extends BaseAdapter {

    private Context context;
    private Cursor c,c2;
    private SQLiteDatabase db;
    DateFormat formatter;
    Date d ;
    private int _id;
    private String _id2;
    ArrayList<Integer> p= new ArrayList<Integer>();
    ArrayList<String> p2= new ArrayList<String>();
    String sit,filtro;
    ListView le ;
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    ArrayList<String> checkedValue;

    public BaseAdapterFiltroLista(Context context, List<HashMap<String, String>> fillMaps2, ListView l, String filtror){
        this.context = context;
        //this.c = rotas;
        this.fillMaps=fillMaps2;
        DaoCreateDBC dao = new DaoCreateDBC(context);
        db =  dao.getWritableDatabase();
        formatter= new SimpleDateFormat("dd/MM/yyyy");
        d = new Date();
        le=l;
        this.filtro=filtror;
        checkedValue = new ArrayList<String>();

    }

    public ArrayList<String> getCheckedValue() {
        return checkedValue;
    }

    public void setCheckedValue(String value) {
        this.checkedValue.add(value);
    }
    public void reomevValue(String value){
        this.checkedValue.remove(value);
    }

    @Override
    public int getCount() {
        return fillMaps.size();
    }

    @Override
    public Object getItem(int position) {
        if(filtro.equalsIgnoreCase("restaure")){
            return position;
        }else{
            return this.p.get(position);

        }
    }


    @Override
    public long getItemId(int position) {
        if(filtro.equalsIgnoreCase("restaure")){
            return position;
        }else{
            return this.p.get(position);

        }
    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        View layout;
        Activity aa = (Activity)context;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_atu, null);
        }
        else{
            convertView = convertView;
        }

        boolean r=false;

        if(filtro.equalsIgnoreCase("restaure")){
            if(p.isEmpty()){
                this._id2=fillMaps.get(position).get("_id");
                this.p2.add(position,_id2);

            }else{
                if(position==p.size()){
                    this._id2=fillMaps.get(position).get("_id");
                    this.p2.add(position,_id2);
                    ;
                }
            }

        }
        final TextView modelo = (TextView) convertView.findViewById(R.id.nomeCarga);
        modelo.setText(fillMaps.get(position).get("_id"));

        TextView data = (TextView) convertView.findViewById(R.id.data);
        data.setText(fillMaps.get(position).get("data"));
        TextView hora = (TextView) convertView.findViewById(R.id.hora);
        hora.setText(fillMaps.get(position).get("hora"));




        return convertView;
    }

}
