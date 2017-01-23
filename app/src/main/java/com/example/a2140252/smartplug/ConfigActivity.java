package com.example.a2140252.smartplug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/10/21.
 */
public class ConfigActivity extends AppCompatActivity {//設定ボタンを押したときPreferenceActivity  AppCompatActivity
    CustomFragmentPagerAdapter adapter;
    ViewPager pager;

    static int no;//Tabの位置

    static Intent intent;
    static String PlugId;
    //plugのconfigデータ 変更前
    static boolean[] configs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);

        //ToolBarを表示する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
      
        intent = getIntent();
        no =Integer.parseInt(intent.getSerializableExtra("no").toString());//Tabの位置　ArrayListから取り出すときの添え字になる

        //configItem =(ConfigItem) this.getApplication();
        //userData = (UserData) this.getApplication();
        PlugId = intent.getStringExtra("plug_id");
        //plugData = userData.getPlugData(userData.findPlugData(PlugId));

        //bList = Arrays.copyOf(plugData.getConfigArray(),plugData.getConfigArray().length); //変更前の値を保存

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new PrefsFragment()).commit();
      //  Log.d("Config","-------------- "+configItem.cList.get(0)+ "===List title "+configItem.cList.get(no)[0]);

        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] columns = {"power_notice", "power_alert", "power_auto",
                    "temperature_notice", "temperature_alert", "temperature_auto",
                    "accident_notice", "accident_alert", "accident_auto"};
            int[] columnsIndex = new int[columns.length];
            configs = new boolean[columns.length];
            cursor = database.query("configs",columns, "plug_id=?", new String[]{PlugId}, null, null, null, null);
            if(cursor.moveToFirst()) {
                for(int i = 0; i< columnsIndex.length; i++) {
                    columnsIndex[i] = cursor.getColumnIndex(columns[i]);

                    int config = cursor.getInt(columnsIndex[i]);
                    if(config == 1) {
                        configs[i] = true;
                    } else if(config == 0) {
                        configs[i] = false;
                    } else {
                        configs[i] = false;
                    }
                }
            }
        } finally {
            database.close();
            cursor.close();
        }

        PrefsFragment.configArray = Arrays.copyOf(configs, configs.length);
    }




    public static class PrefsFragment extends PreferenceFragment {

        public static boolean configArray[];

        SwitchPreference notice_checkBox1;
        SwitchPreference alert_checkBox1;
        SwitchPreference Auto_checkBox1;
        SwitchPreference notice_checkBox2;
        SwitchPreference alert_checkBox2;
        SwitchPreference Auto_checkBox2;
        SwitchPreference notice_checkBox3;
        SwitchPreference alert_checkBox3;
        SwitchPreference Auto_checkBox3;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);

            notice_checkBox1 = (SwitchPreference) findPreference("notice_checkBox1");
            alert_checkBox1 = (SwitchPreference) findPreference("alert_checkBox1");
            Auto_checkBox1 = (SwitchPreference) findPreference("Auto_checkBox1");
            notice_checkBox2 = (SwitchPreference) findPreference("notice_checkBox2");
            alert_checkBox2 = (SwitchPreference) findPreference("alert_checkBox2");
            Auto_checkBox2 = (SwitchPreference) findPreference("Auto_checkBox2");
            notice_checkBox3 = (SwitchPreference) findPreference("notice_checkBox3");
            alert_checkBox3 = (SwitchPreference) findPreference("alert_checkBox3");
            Auto_checkBox3 = (SwitchPreference) findPreference("Auto_checkBox3");

            SwitchPreference[] switchPreference = {notice_checkBox1, alert_checkBox1, Auto_checkBox1,
                                                    notice_checkBox2, alert_checkBox2, Auto_checkBox2,
                                                    notice_checkBox3, alert_checkBox3, Auto_checkBox3};
            for(int i = 0;i < switchPreference.length; i++) {
                //switchPreference[i].setChecked(plugData.getConfigArray()[i]);
                switchPreference[i].setChecked(configArray[i]);
            }
        }

        // 設定値が変更されたときのリスナーを登録
        @Override
        public void onResume() {
           // Log.d("Config","onResume()================");
            super.onResume();
//            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
//            sp.registerOnSharedPreferenceChangeListener(listener);
        }

        // 設定値が変更されたときのリスナー登録を解除
        @Override
        public void onPause() {
          //  Log.d("Config"," onPause()==========");

            super.onPause();
//            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
//            sp.unregisterOnSharedPreferenceChangeListener(listener);

            Log.d("Config"," onPause()==========");

            //if(!Arrays.equals(bList,userData.getPlugData(userData.findPlugData(PlugId)).getConfigArray())){//設定値に切り替えがあったか
            if(!Arrays.equals(configs, configArray)){//設定値に切り替えがあったか

                MyHttpClient client = new  MyHttpClient();

                client.setParam("plug_id", PlugId);
                for(int i=0;i<9;i++) {
                    //client.setParam("ConfigList["+i+"]", String.valueOf(userData.getPlugData(userData.findPlugData(PlugId)).getConfigArray(i)));
                    client.setParam("ConfigList["+i+"]", String.valueOf(PrefsFragment.configArray[i]));
                    // Log.d("ConfigA", "ConfigList["+i+"]  "+configItem.cList.get(no)[i].toString());
                }
                Log.d("ConfigA", "通信前");
                client.config_set("http://smartplug.php.xdomain.jp/config_set.php");
                client.removeParam("plug_id");
                for(int i = 0; i < 9; i++) {
                    client.removeParam("ConfigList[" + String.valueOf(i) + "]");
                }
            }
        }


            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                          //  Log.d("Config","onSharedPreferenceChanged()=================");

                            switch (key) {
                                case "notice_checkBox1":
                                    //configItem.cList.get(no)[0]=notice_checkBox1.isChecked() ? true : false;
                                    //plugData.getConfigArray()[0] = notice_checkBox1.isChecked();
                                    //plugData.setConfigArray(notice_checkBox1.isChecked(), 0);
                                    configArray[0] = notice_checkBox1.isChecked();
                                    break;
                                case "alert_checkBox1":
                                    //plugData.setConfigArray(alert_checkBox1.isChecked(), 1);
                                    configArray[1] = alert_checkBox1.isChecked();
                                    break;
                                case "Auto_checkBox1":
                                    //plugData.setConfigArray(Auto_checkBox1.isChecked(), 2);
                                    configArray[2] = Auto_checkBox1.isChecked();
                                    break;
                                case "notice_checkBox2":
                                    //plugData.setConfigArray(notice_checkBox2.isChecked(), 3);
                                    configArray[3] = notice_checkBox2.isChecked();
                                    break;
                                case "alert_checkBox2":
                                    //plugData.setConfigArray(alert_checkBox2.isChecked(), 4);
                                    configArray[4] = alert_checkBox2.isChecked();
                                    break;
                                case "Auto_checkBox2":
                                    //plugData.setConfigArray(Auto_checkBox2.isChecked(), 5);
                                    configArray[5] = Auto_checkBox2.isChecked();
                                    break;
                                case "notice_checkBox3":
                                    //plugData.setConfigArray(notice_checkBox3.isChecked(), 6);
                                    configArray[6] = notice_checkBox3.isChecked();
                                    break;
                                case "alert_checkBox3":
                                    //plugData.setConfigArray(alert_checkBox3.isChecked(), 7);
                                    configArray[7] = alert_checkBox3.isChecked();
                                    break;
                                case "Auto_checkBox3":
                                    //plugData.setConfigArray(Auto_checkBox3.isChecked(), 8);
                                    configArray[8] = Auto_checkBox3.isChecked();
                                    break;
                            }
                        }

                    };
        }

    public  static class MyHttpClient { //staticに
        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void config_set(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            // client.post(url, params, new JsonHttpResponseHandler() {//post通信
            client.post(url, params, new AsyncHttpResponseHandler() { //パラメータを伴い通信開始

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    msg = "システムエラー";
                    Log.d("Config",msg);
                    //Toast.makeText(new ConfigActivity(), msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    //Toastを
                    //msg = "設定を変更しました";
                    //Toast.makeText(new ConfigActivity(), msg, Toast.LENGTH_LONG).show();

                    //  String body = new String(bytes);
                    //  Log.d("Config","onSuccess " + body);
                }
            });
        }

        public void setParam(String key, String value) {
            params.put(key, value);
            // Log.d("Config","key: "+key+ " value:"+value);
        }

        public void removeParam(String key) {
            params.remove(key);
        }

    }
    }



