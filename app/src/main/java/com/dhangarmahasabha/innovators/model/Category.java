package com.dhangarmahasabha.innovators.model;

/**
 * Created by AD on 09-May-16.
 */
public class Category {
    private int id;
    private int cat_id;
    private String cat_name;
    private int lang_status;

    public Category(){}

    public Category(int cat_id,String cat_name,int lang_status){
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.lang_status = lang_status;
    }

    public Category(int id,int cat_id,String cat_name,int lang_status){
        this.id = id;
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.lang_status = lang_status;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getCat_id(){
        return cat_id;
    }

    public void setCat_id(int cat_id){
        this.cat_id = cat_id;
    }

    public String getCat_name(){
        return cat_name;
    }

    public void setCat_name(String cat_name){
        this.cat_name = cat_name;
    }

    public int getLang_status(){
        return lang_status;
    }

    public void setLang_status(int lang_status){
        this.lang_status = lang_status;
    }
}
