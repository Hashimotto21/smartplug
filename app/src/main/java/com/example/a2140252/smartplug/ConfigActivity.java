package com.example.a2140252.smartplug;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by 2140306 on 2016/10/21.
 */
public class ConfigActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 設定画面の作成 非推奨？ config_layout.xmlはいらない？
        addPreferencesFromResource(R.xml.pref);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
    }

//    //ハンバーガーメニュー
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return  true;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent;
//        switch(item.getItemId()) {
//            case R.id.menu_user:
//                intent = new Intent(this, UserConfigActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.menu_add:
//                intent = new Intent(this, PlugAddMoreActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.menu_del:
//                intent = new Intent(this, PlugDeleteActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.menu_logout:
//                intent = new Intent(this, LogoutActivity.class);
//                startActivity(intent);
//                return true;
//            default:
//                break;
//        }
//        return false;
//    }
}
