package br.com.mobiwork.captacao.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 27/10/2015.
 */
public class Configgd {

    private int _id;
    private String folderpai;
    private String folderexp;
    private String foldercol;
    private String foldervs;


    public void Configgd(Cursor c){
        this._id=c.getInt(c.getColumnIndex("_id"));
        this.folderpai = c.getString(c.getColumnIndex("folderpai"));
        this.folderexp = c.getString(c.getColumnIndex("folderexp"));
        this.foldercol = c.getString(c.getColumnIndex("foldercol"));
        this.foldervs = c.getString(c.getColumnIndex("foldervs"));
    }

    public String getFoldervs() {
        return foldervs;
    }

    public void setFoldervs(String foldervs) {
        this.foldervs = foldervs;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFolderpai() {
        return folderpai;
    }

    public void setFolderpai(String folderpai) {
        this.folderpai = folderpai;
    }

    public String getFolderexp() {
        return folderexp;
    }

    public void setFolderexp(String folderexp) {
        this.folderexp = folderexp;
    }

    public String getFoldercol() {
        return foldercol;
    }

    public void setFoldercol(String foldercol) {
        this.foldercol = foldercol;
    }
}
