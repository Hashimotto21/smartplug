package com.example.a2140252.smartplug;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ページ（タブ）のアイテム.
 */
public class PageItem implements Serializable{//Parcelable


    //ページ（タブ）名
    private String title;

    //タブID
    private String id;


    public String getTitle(){
        return  title;
    }
    public String getId(){
        return  id;
    }


    public PageItem(String title,String id) {
        this.title = title;
        this.id = id;
    }
    //-----------------------------------------

    }
