package com.example.a2140252.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/10/21.
 */
public class TimerActivity extends AppCompatActivity {
    int ontime_h, ontime_m;
    int offtime_h, offtime_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //タイマーがセットされているとき、値を反映する
        SharedPreferences data = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        if (data.getInt("ontime_h", 0) != 0) {
            TimePicker timePicker1 = (TimePicker) findViewById(R.id.startTime);
            TimePicker timePicker2 = (TimePicker) findViewById(R.id.endTime);
            int ontime_h = data.getInt("ontime_h", 0);
            int ontime_m = data.getInt("ontime_m", 0);
            timePicker1.setCurrentHour(ontime_h);
            timePicker1.setCurrentMinute(ontime_m);
            int offtime_h = data.getInt("offtime_h", 0);
            int offtime_m = data.getInt("offtime_m", 0);
            timePicker2.setCurrentHour(offtime_h);
            timePicker2.setCurrentMinute(offtime_m);
        }

        findViewById(R.id.setTimerButton).setOnClickListener(timer_set);
    }

    View.OnClickListener timer_set = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("TimerActivity", "param get start");
            //サーバーに値を送信する
//            SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
//            String plug_id = data.getString("user_id", "");
            Intent intent = getIntent();
            int plug_id = intent.getIntExtra("plug_id", 0);
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
            Log.d("onClick", "通信前");
            client.setTimer("http://smartplug.php.xdomain.jp/set_timer.php");
            Log.d("onClick", "通信後");
            client.removeParam("plug_id");
            client.removeParam("ontime");
            client.removeParam("offtime");
        }
    };

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

                            //SharedPreferences は "plugId" をキーにする
                            SharedPreferences data = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = data.edit();
                            editor.putInt("ontime_h", ontime_h);
                            editor.putInt("ontime_m", ontime_m);
                            editor.putInt("offtime_h", offtime_h);
                            editor.putInt("offtime_m", offtime_m);
                            editor.apply();
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

        public void setParam(String key, String value) {
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
