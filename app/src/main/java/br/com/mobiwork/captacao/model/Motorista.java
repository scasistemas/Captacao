package br.com.mobiwork.captacao.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class Motorista {

    private int _id;
    private String codigo;
    private String nome;
    private String endereco;
    private String login;
    private String senha;
    private String emp;
    private String logindrive;

    public Motorista(){
        this._id=0;
        this.codigo="";
        this.nome="";
        this.endereco="";
        this.login="";
        this.senha="";
        this.emp="";
        logindrive="";
    }

    public void setConfigMot(Cursor c){
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.codigo = c.getString(c.getColumnIndex("codigo"));
        this.nome = c.getString(c.getColumnIndex("nome"));
        this.endereco = c.getString(c.getColumnIndex("endereco"));
        this.login = c.getString(c.getColumnIndex("login"));
        this.senha = c.getString(c.getColumnIndex("senha"));
        this.emp = c.getString(c.getColumnIndex("emp"));
        this.logindrive=c.getString(c.getColumnIndex("logindrive"));
    }

    public String getLogindrive() {
        return logindrive;
    }

    public void setLogindrive(String logindrive) {
        this.logindrive = logindrive;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
