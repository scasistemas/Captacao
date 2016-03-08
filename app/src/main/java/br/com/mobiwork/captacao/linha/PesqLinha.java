package br.com.mobiwork.captacao.linha;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoLinha;
import br.com.mobiwork.captacao.dao.DaoMateria;

/**
 * Created by LuisGustavo on 02/07/2015.
 */
public class PesqLinha extends Activity {

    private DaoCreateDB daoDB;
    private DaoCreateDBC daoDBC;
    private SQLiteDatabase db,dbc;
    private ListView list;
    private DaoLinha dl;
    private EditText searchText;
    private Cursor curlinha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pesqlinha);
        daoDB = new DaoCreateDB(this);
        db =  daoDB.getWritableDatabase();
        daoDBC = new DaoCreateDBC(this);
        dbc =  daoDBC.getWritableDatabase();
        dl = new DaoLinha(this);
        list=((ListView)findViewById(R.id.list));
        searchText = (EditText) findViewById (R.id.searchText);



        searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                popularLista();
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        popularLista();

    }


    public void popularLista() {
        String whereFiltro="";
        if (searchText.getText() != null && !searchText.getText().toString().equalsIgnoreCase("")) {

            whereFiltro = "where  lindescri like '"+searchText.getText().toString()+"%'";

        }

        curlinha=dl.pesquisarLinha(dbc,whereFiltro);
         list.setAdapter(new SimpleCursorAdapter(this, R.layout.list_linha, curlinha, new String[]{"lincodigo", "lindescri"},
                 new int[]{R.id.lincodigo, R.id.lindecri}));



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecionar(curlinha.getString(curlinha.getColumnIndex("lincodigo")), curlinha.getString(curlinha.getColumnIndex("lindescri")));

            }
        });


    }

    public void limpa(View v){
        searchText.setText("");
    }
    public void selecionar(String codigo,String descricao){
        Intent result = new Intent(this, PesqLinha.this.getClass());
        result.putExtra("lincodigo",  codigo);
        result.putExtra("lindescri",  descricao);
        setResult(RESULT_OK, result);
        PesqLinha.this.finish();
    }


    @Override
    protected void onPause(){
        super.onPause();
        Intent result = new Intent(this, PesqLinha.this.getClass());
        result.putExtra("lincodigo",  "");
        result.putExtra("lindescri", "");
        setResult(RESULT_OK, result);

    }



}
