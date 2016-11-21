package com.example.a2140252.smartplug;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.github.mikephil.charting.charts.CombinedChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 2140306 on 2016/10/21.
 */
public class ConfigActivity extends AppCompatActivity {//設定ボタンを押したときPreferenceActivity  AppCompatActivity
    CustomFragmentPagerAdapter adapter;
    ViewPager pager;

    static int no;//Tabの位置

    static ConfigItem configItem;

    static Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);
      
        intent = getIntent();
        no =Integer.parseInt(intent.getSerializableExtra("no").toString());//Tabの位置　ArrayListから取り出すときの添え字になる

        configItem =(ConfigItem) this.getApplication();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

      //  Log.d("Config","-------------- "+configItem.cList.get(0)+ "===List title "+configItem.cList.get(no)[0]);
    }




    public static class PrefsFragment extends PreferenceFragment {

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


            notice_checkBox1.setChecked(configItem.cList.get(no)[0]);
            alert_checkBox1.setChecked(configItem.cList.get(no)[1]);
            Auto_checkBox1.setChecked(configItem.cList.get(no)[2]);
            notice_checkBox2.setChecked(configItem.cList.get(no)[3]);
            alert_checkBox2.setChecked(configItem.cList.get(no)[4]);
            Auto_checkBox2.setChecked(configItem.cList.get(no)[5]);
            notice_checkBox3.setChecked(configItem.cList.get(no)[6]);
            alert_checkBox3.setChecked(configItem.cList.get(no)[7]);
            Auto_checkBox3.setChecked(configItem.cList.get(no)[8]);
        }

        // 設定値が変更されたときのリスナーを登録
        @Override
        public void onResume() {
           // Log.d("Config","onResume()================");
            super.onResume();
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            sp.registerOnSharedPreferenceChangeListener(listener);
        }

        // 設定値が変更されたときのリスナー登録を解除
        @Override
        public void onPause() {
          //  Log.d("Config"," onPause()==========");

            super.onPause();
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            sp.unregisterOnSharedPreferenceChangeListener(listener);
        }


            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                          //  Log.d("Config","onSharedPreferenceChanged()=================");

                            switch (key) {
                                case "notice_checkBox1":

                                    configItem.cList.get(no)[0]=notice_checkBox1.isChecked() ? true : false;
                                    break;
                                case "alert_checkBox1":
                                    configItem.cList.get(no)[1]=alert_checkBox1.isChecked() ? true : false;
                                    break;
                                case "Auto_checkBox1":
                                    configItem.cList.get(no)[2]=Auto_checkBox1.isChecked() ? true : false;
                                    break;
                                case "notice_checkBox2":
                                    configItem.cList.get(no)[3]=notice_checkBox2.isChecked() ? true : false;
                                    break;
                                case "alert_checkBox2":
                                    configItem.cList.get(no)[4]= alert_checkBox2.isChecked() ? true : false;
                                    break;
                                case "Auto_checkBox2":
                                    configItem.cList.get(no)[5]= Auto_checkBox2.isChecked() ? true : false;
                                    break;
                                case "notice_checkBox3":
                                    configItem.cList.get(no)[6]=notice_checkBox3.isChecked() ? true : false;
                                    break;
                                case "alert_checkBox3":
                                    configItem.cList.get(no)[7]= alert_checkBox3.isChecked() ? true : false;
                                    break;
                                case "Auto_checkBox3":
                                    configItem.cList.get(no)[8]=Auto_checkBox3.isChecked() ? true : false;
                                    break;

                            }

                        }

                    };
        }


    }



