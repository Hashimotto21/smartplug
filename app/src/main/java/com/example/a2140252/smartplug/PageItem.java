package com.example.a2140252.smartplug;

import java.io.Serializable;

/**
 * ページ（タブ）のアイテム.
 */
public class PageItem implements Serializable {//Parcelable

    //ページ（タブ）名
    private String title;

    //タブID
    private int id;

    ////11/07 16:19追加
    public String getTitle(){
        return  title;
    }
    public int getId(){
        return  id;
    }


    public PageItem(String title, int id) {
       this.title = title;
        this.id = id;
    }
    //-----------------------------------------

    }
