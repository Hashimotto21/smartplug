package com.example.a2140252.smartplug;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a2140252.smartplug.dataPackage.PlugData;
import com.example.a2140252.smartplug.dataPackage.UserData;
import com.github.mikephil.charting.charts.CombinedChart;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;


public class HomeActivity extends AppCompatActivity {//AppCompatActivity FragmentActivity

    private static final int ADDMORE_CODE = 1;
    private static final int DELETE_CODE = 2;


    CustomFragmentPagerAdapter adapter;

    ArrayList<PageItem> list;

    ViewPager pager;

    //UserData userData;
    //SwitchFlagList switchFlagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        //ViewPager
        pager = (ViewPager) findViewById(R.id.pager);

        // PagerTitleStrip のカスタマイズ   ----
        PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.strip);
        strip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        strip.setTextColor(0xFF303F9F);
        strip.setTextSpacing(50);
        strip.setNonPrimaryAlpha(0.3f);
        strip.setDrawFullUnderline(true);//下線表示
        strip.setTabIndicatorColor(0x303F9F);//インディケーターの色
        //-----------------

        list = adapter.getList();//ArrayList生成
        //---------------------------------

        //userData=(UserData) this.getApplication();

        //adapterにplugを追加
//        for(int i = 0; i <  userData.getPlugData().length; i++) {
//            PlugData plugData = userData.getPlugData(i);
//            PageItem plug = new PageItem( plugData.getPlugName(), plugData.getPlugId());//("タイトル","id")
//            adapter.addItem(plug);
//        }

        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            SharedPreferences preferences = getSharedPreferences("smartplug", Context.MODE_PRIVATE);
            String user_id = preferences.getString("user_id", "");
            cursor = database.query("plugs", new String[]{"id, name"}, "user_id=?", new String[]{user_id}, null, null, null, null);
            if(cursor.moveToFirst()) {
                do {
                    int plug_idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");

                    String plug_id = cursor.getString(plug_idIndex);
                    String name = cursor.getString(nameIndex);

                    PageItem plug = new PageItem(name, plug_id);
                    adapter.addItem(plug);
                } while (cursor.moveToNext());
            }
        } finally {
            database.close();
            cursor.close();
        }

        pager.setAdapter(adapter);
        //タブ切り替えた時　 Logを残すためのメソッド -------------------------------
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            //findViewById(R.id.pager).addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (ViewPager.SCROLL_STATE_IDLE == state) {//ViewPagerの位置が落ち着いた状態が0(= CROLL_STATE_IDLE)

                    Log.d("ViewPager", "現在選択中のタブ" + ( pager.getCurrentItem() + 1) + "つ目 -------");

                }
            }
        });

    }//onCreate()

    //HomeActivityのインスタンスが既に存在している場合
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {//startActivityForResult(intent,1);の１か？

            case (ADDMORE_CODE)://ADDMORE_CODE = 1  PlugAddMoreActivity()から戻ってきたら
                if (resultCode == RESULT_OK) {
                    //「コンセントを追加する」ボタンを押して戻ってきたときの処理
                    Intent intent = getIntent();
                    String id = intent.getStringExtra("id");
                    String name = intent.getStringExtra("name");
                    PageItem pageItem = new PageItem(name, id);
                    adapter.addItem(pageItem);
                    //adapter.addItem((PageItem) data.getSerializableExtra("newPlug"));
                    pager.setAdapter(adapter);

//                    int length = userData.getPlugData().length;
//                    PlugData[] newPlugData = new PlugData[length + 1];
//                    for(int i = 0; i < length; i++) {
//                        newPlugData[i] = userData.getPlugData(i);
//                    }
//                    userData.setPlugData(newPlugData);
//                    userData.getPlugData(length - 1).setPlugId(id);
//                    userData.getPlugData(length - 1).setPlugName(name);

                    //SQLite
                    String plug_id = data.getStringExtra("plug_id");
                    MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    try {
                        //テーブルの追加処理

                        ContentValues values = new ContentValues();
                        values.put("id", plug_id);
                        values.put("name", data.getStringExtra("name"));
                        database.insert("plugs", "0", values);

                        values.clear();
                        values.put("plug_id", plug_id);
                        values.put("time_flg", 0);
                        database.insert("timers", null, values);

                        String[] configs = {"power_notice", "power_alert", "power_auto",
                                "temperature_notice", "temperature_alert", "temperature_auto",
                                "accident_notice", "accident_alert", "accident_auto"};
                        values.clear();
                        values.put("plug_id", plug_id);
                        for(int i = 0; i < configs.length; i++) {
                            values.put(configs[i], 1);
                        }
                        database.insert("configs", null, values);
                    } finally {
                        database.close();
                    }

                    //serverにpowerTableのデータを取りに行く？


                    Log.d("onActivityResult", "タブ追加後★RESULT_OK");
                }
                break;
            case(DELETE_CODE)://DELETE_CODE = 2  PlugDeleteActivity()から戻ってきたら
                if (resultCode == RESULT_OK) {
                    //「コンセントを削除する」ボタンを押して戻ってきたときの処理
                    int d  =Integer.parseInt(data.getSerializableExtra("delete").toString());

                    adapter.Delete(d);//ArrayList から削除

                    adapter.destroyAllItem(pager);//削除
                    Log.d("onActivityResult", "adapter.destroyAllItem(pager);　後");
                    adapter.notifyDataSetChanged();
                    pager.setAdapter(adapter);

//                    int index = userData.findPlugData(data.getStringExtra("plug_id"));
//                    //plugDataを削除
//                    userData.setPlugData(null, index);;
//                    //PlugData配列を整理
//                    PlugData[] newPlugData = new PlugData[userData.getPlugData().length - 1];
//                    int j = 0;
//                    for(int i = 0; i < userData.getPlugData().length; i++) {
//                        if(userData.getPlugData(i) != null) {
//                            newPlugData[j] = userData.getPlugData(i);
//                            j++;
//                        }
//                    }
//                    userData.setPlugData(newPlugData);

                    //SQLite
                    String plug_id = data.getStringExtra("plug_id");
                    MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    try {
                        String[] whereArgs = new String[]{plug_id};
                        database.delete("plugs", "id=?", whereArgs);
                        database.delete("timers", "plug_id=?", whereArgs);
                        database.delete("configs", "plug_id=?", whereArgs);
                        database.delete("power", "plug_id=?", whereArgs);
                    } finally {
                        database.close();
                    }

                    Log.d("onActivityResult", "タブ削除後★RESULT_OK");
                }
                break;
            default:
                break;
        }
    }

    //ハンバーガーメニュー
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_user:
                intent = new Intent(this, UserConfigActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                intent = new Intent(this, PlugAddMoreActivity.class);
                list= adapter.getList();
                intent.putExtra("list",list);//ArrayListを渡す

                startActivityForResult(intent, ADDMORE_CODE);//起動先からデータを返してもらえる
                return true;
            case R.id.menu_del:
                intent = new Intent(this, PlugDeleteActivity.class);
                list= adapter.getList();
                intent.putExtra("list",list);//ArrayListを渡す

                startActivityForResult(intent,DELETE_CODE);
                return true;
            case R.id.menu_logout:
                intent = new Intent(this, LogoutActivity.class);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return false;
    }

}
