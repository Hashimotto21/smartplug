package com.example.a2140252.smartplug;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.a2140252.smartplug.dataPackage.PlugData;
import com.example.a2140252.smartplug.dataPackage.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/10/21.
 */
public class TimerActivity extends AppCompatActivity {

    boolean time_flag;

    boolean begin_time_flg;
    //UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //userData = (UserData) this.getApplication();

        Intent intent = getIntent();
        String plug_id = intent.getStringExtra("plug_id");

        String ontime = "", offtime = "";
        int time_flg = 0;

        //timer情報の取得
        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] columns = {"ontime", "offtime", "time_flg"};
            String where = "plug_id=?";
            cursor = database.query("timers", columns, where, new String[]{plug_id}, null,null,null,null);
            if(cursor.moveToFirst()) {
                int ontimeIndex = cursor.getColumnIndex("ontime");
                int offtimeIndex = cursor.getColumnIndex("offtime");
                int time_flgIndex = cursor.getColumnIndex("time_flg");

                ontime = cursor.getString(ontimeIndex);
                offtime = cursor.getString(offtimeIndex);
                time_flg = cursor.getInt(time_flgIndex);
                Log.w("time_flag", String.valueOf(time_flg));
            }
        } finally {
            database.close();
            cursor.close();
        }

        //タイマーがセットされているとき、値を反映する
        if(!ontime.equals("")) {
            TimePicker startTimePicker = (TimePicker) findViewById(R.id.startTime);
            startTimePicker.setCurrentHour(Integer.parseInt(ontime.substring(0, 2)));
            startTimePicker.setCurrentMinute(Integer.parseInt(ontime.substring(3, 5)));
        }
        if(!offtime.equals("")) {
            TimePicker startTimePicker = (TimePicker) findViewById(R.id.endTime);
            startTimePicker.setCurrentHour(Integer.parseInt(offtime.substring(0, 2)));
            startTimePicker.setCurrentMinute(Integer.parseInt(offtime.substring(3, 5)));
        }
        Switch flag_switch = (Switch) findViewById(R.id.flagswitch);
        if(time_flg == 1) {
            flag_switch.setChecked(true);
            begin_time_flg = true;
        } else {
            flag_switch.setChecked(false);
            begin_time_flg = false;
        }

        findViewById(R.id.setTimerButton).setOnClickListener(timer_set);
        findViewById(R.id.flagswitch).setOnClickListener(flag_change);
    }

    View.OnClickListener timer_set = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("TimerActivity", "param get start");
            //サーバーに値を送信する
//            SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
//            String plug_id = data.getString("user_id", "");

            int ontime_h, ontime_m;
            int offtime_h, offtime_m;

            Intent intent = getIntent();
            String plug_id = intent.getStringExtra("plug_id");
            TimePicker timePicker1 = (TimePicker) findViewById(R.id.startTime);
            TimePicker timePicker2 = (TimePicker) findViewById(R.id.endTime);
            ontime_h = timePicker1.getCurrentHour();
            ontime_m = timePicker1.getCurrentMinute();
            String ontime = String.valueOf(ontime_h + ":" + ontime_m);
            offtime_h = timePicker2.getCurrentHour();
            offtime_m = timePicker2.getCurrentMinute();
            String offtime = String.valueOf(offtime_h + ":" + offtime_m);

            Log.d("TimerActivity", "param get end");
            Log.d("TimerActivity", ontime + "～" + offtime);

            MyHttpClient client = new MyHttpClient();
            client.setParam("plug_id", String.valueOf(plug_id));
            client.setParam("ontime", ontime);
            client.setParam("offtime", offtime);
            client.setTimer("http://smartplug.php.xdomain.jp/set_timer.php");
            client.removeParam("plug_id");
            client.removeParam("ontime");
            client.removeParam("offtime");

            MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
            SQLiteDatabase database = helper.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put("ontime", ontime);
                values.put("offtime", offtime);
                long id = database.update("timers", values, "plug_id=?", new String[]{plug_id});
                if(id != -1) {
                    Log.w("databaseUpdate", "成功");
                }
            } finally {
                database.close();
            }
        }
    };

    View.OnClickListener flag_change = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getApplicationContext(), "flag_change", Toast.LENGTH_SHORT).show();
            Switch aSwitch = (Switch)findViewById(R.id.flagswitch);
            time_flag = aSwitch.isChecked();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        String plug_id = getIntent().getStringExtra("plug_id");
        if(time_flag != begin_time_flg) {
            MyHttpClient client = new MyHttpClient();
            client.setParam("plug_id", plug_id);
            client.setParam("time_flg", time_flag);
            client.setTimeFlag("http://smartplug.php.xdomain.jp/set_time_flag.php");
            client.removeParam("plug_id");
            client.removeParam("time_flg");

            MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
            SQLiteDatabase database = helper.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                if(time_flag) {
                    values.put("time_flg", 1);
                } else {
                    values.put("time_flg", 0);
                }
                long id = database.update("timers", values, "plug_id=?", new String[]{plug_id});
                if(id == -1) {
                    if(time_flag) {
                        Log.w("time_flag", String.valueOf(1));
                    } else {
                        Log.w("time_flag", String.valueOf(0));
                    }
                }
            } finally {
                database.close();
            }
        }
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void setTimer(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String status = response.getString("status");
                        String str = "status:" + status; // "変更できたか"
                        Log.d("onSuccess:", str);

                        if (response.getString("status").equals("false")) {
                            msg = "タイマーをセットできませんでした。";
                            Toast.makeText(TimerActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.e("タイマーセット失敗:", msg);
                        } else {
                            msg = "タイマーをセットしました。";
                            Toast.makeText(TimerActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(TimerActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.d("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,  String errorStrings, Throwable throwable) {
                    super.onFailure(statusCode, headers, errorStrings, throwable);
                    msg = "接続エラー";
                    Toast.makeText(TimerActivity.this, errorStrings, Toast.LENGTH_LONG).show();
                }
            });

        }

        public void setTimeFlag(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("status").equals("false")) {
                            Toast.makeText(getApplicationContext(), "失敗", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("setTimeFlag", e.toString());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,  String errorStrings, Throwable throwable) {
                    super.onFailure(statusCode, headers, errorStrings, throwable);
                    msg = "接続エラー";
                    Toast.makeText(TimerActivity.this, errorStrings, Toast.LENGTH_LONG).show();
                    Log.e("setTimeFlag", errorStrings);
                }
            });

        }

        public void setParam(String key, String value) {
            params.put(key, value);
        }

        public void setParam(String key, boolean value) {
            params.put(key, value);
        }

        public void removeParam(String key) {
            params.remove(key);
        }

    }

    //ハンバーガーメニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_user:
                intent = new Intent(this, UserConfigActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                intent = new Intent(this, PlugAddMoreActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_del:
                intent = new Intent(this, PlugDeleteActivity.class);
                startActivity(intent);
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
