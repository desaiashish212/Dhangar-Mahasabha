package com.dhangarmahasabha.innovators.model;

/**
 * Created by AD on 1/3/2016.
 */
public class News {
    private int id;
    private int nid;
    private String title;
    private String news;
    private String time;
    private String status;
    private String date;
    private String path;
    private Byte[] image;

    public News() {
    }

    public News(int nid, String title, String news, String time, String date, String status, String path, Byte[] image) {
        this.nid = nid;
        this.title = title;
        this.news = news;
        this.time = time;
        this.date = date;
        this.status = status;
        this.path = path;
        this.image = image;
    }

    public News(int id, int nid, String title, String news, String time, String date, String status, String path, Byte[] image) {
        this.id = id;
        this.nid = nid;
        this.title = title;
        this.news = news;
        this.time = time;
        this.date = date;
        this.status = status;
        this.path = path;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public String getnews() {
        return news;
    }

    public void setnews(String news) {
        this.news = news;
    }

    public String gettime() {
        return time;
    }

    public void settime(String time) {
        this.time = time;
    }

    public String getStatus(){ return status;}

    public void setStatus(String status){this.status=status;}

    public String getdate(){ return date;}

    public void setdate(String date){this.date=date;}

    public Byte[] getimage(){ return image;}

    public void setimage(Byte[] image){this.image=image;}

    public String getpath(){ return path;}

    public void setpath(String path){this.path=path;}

}
