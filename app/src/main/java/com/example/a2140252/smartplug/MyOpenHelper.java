package com.example.a2140252.smartplug;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 2140306 on 2017/01/17.
 */
public class MyOpenHelper extends SQLiteOpenHelper {
    /* データベース名 */
    private static final String DB_NAME = "smartplugDB";
    /* データベースのバージョン */
    private static final int DB_VER = 1;

    /**
     * @param context
     */
    public MyOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String configs = "create table configs(" +
                "_id integer primary key autoincrement, " +
                "plug_id text not null unique, " + "power_notice integer, " +
                "power_alert integer, power_auto integer, " +
                "temperature_notice integer, " + "temperature_alert integer, " +
                "temperature_auto integer, " + "accident_notice integer," +
                "accident_alert integer, " + "accident_auto integer" + ");";
        String plugs = "create table plugs(" +
                "_id integer primary key autoincrement, " +
                "id text not null unique, " + "name text, " +
                "user_id text, " + "switch_flg integer, " +
                "power_error integer, " + "temperature_error integer, " +
                "accident_error integer" + ");";
        String power = "create table power(" +
                "_id integer primary key autoincrement, " +
                "plug_id text not null, " + "date text, " +
                "power integer, " + "unique(plug_id, date)" + ");";
        String timers = "create table timers(" +
                "_id integer primary key autoincrement, " +
                "plug_id text not null, " + "ontime text, " +
                "offtime text, " + "time_flg integer" + ");";
        String users = "create table users (" +
                "_id integer primary key autoincrement, " +
                "id text not null unique, " + "password text, " +
                "token text" + ");";
        String usetime = "create table usetime(" +
                "_id integer primary key autoincrement, " +
                "plug_id text, " + "date text, " +
                "usetime integer, " + "unique(plug_id, date)" + ");";

        String[] sql = {configs,  plugs, power, timers, users, usetime};
        for(int i = 0; i < sql.length; i++) {
            sqLiteDatabase.execSQL(sql[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
