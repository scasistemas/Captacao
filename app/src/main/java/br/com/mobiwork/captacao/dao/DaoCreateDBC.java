package br.com.mobiwork.captacao.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.mobiwork.captacao.R;

/**
 * Created by LuisGustavo on 01/06/2015.
 */
public class DaoCreateDBC extends SQLiteOpenHelper{
private static final String NOME_BD = "captacaoDBC";
private static final int VERSAO_BD = 2;
private static final String LOG_TAG = "captacaoDBC";
private Context contexto;

        public DaoCreateDBC(Context context) {
            super(context, NOME_BD, null, VERSAO_BD);
            this.contexto = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.beginTransaction();

            try
            {
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_fazenda).split("\n"));
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_linha).split("\n"));
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_materia).split("\n"));
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_motorista).split("\n"));
                ExecutarComandosSQL(db, contexto.getString(R.string.tabela_produtor).split("\n"));

                db.setTransactionSuccessful();
            }
            catch (SQLException e)
            {
                Log.e("Erro ao criar as tabelas e testar os dados", e.toString());
            }
            finally
            {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            String teste;
            //atualizaDBSQL(db,oldVersion,newVersion);
        }

        private void ExecutarComandosSQL(SQLiteDatabase db, String[] sql)
        {
            for( String s : sql )
                if (s.trim().length()>0)
                    db.execSQL(s);
        }

        public void criarTabelaProdutoInfo(SQLiteDatabase db)
        {
            //  ExecutarComandosSQL(db, contexto.getString(R.string.tabela_produtoInfo).split("\n"));
        }


        private void atualizaDBSQL(SQLiteDatabase db,int oldVsDB, int newVsDB)
        {

            Log.w(LOG_TAG, "Atualizando a base de dados da versao " + oldVsDB + " para " + newVsDB + ", que destruira todos os dados antigos");

            db.beginTransaction();

            try
            {
                if (oldVsDB<2&&newVsDB>=2) {

                }

            }
            catch (SQLException e)
            {
                Log.e("Erro ao atualizar as tabelas e testar os dados", e.toString());
                throw e;
            }
            finally
            {
                db.endTransaction();
            }
        }

        public long insert(SQLiteDatabase db,String table, String nullColumnHack, ContentValues values) {
            return db.insert(table, nullColumnHack, values);
        }

        public static int update(SQLiteDatabase db,String table, ContentValues values, String whereClause, String[] whereArgs){
            return db.update(table, values, whereClause, whereArgs);
        }

        public static int delete(SQLiteDatabase db,String table, String whereClause, String[] whereArgs){
            return db.delete(table, whereClause, whereArgs);
        }
}