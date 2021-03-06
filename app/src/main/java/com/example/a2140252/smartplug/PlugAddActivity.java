package com.example.a2140252.smartplug;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140252 on 2016/10/05.
 */



public class PlugAddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugadd_layout);

        findViewById(R.id.Nextbutton).setOnClickListener(nextbutton);
        //SmartPlugを始めるボタン押下

    }

    View.OnClickListener nextbutton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText id = (EditText) findViewById(R.id.IDtextView);
            EditText name = (EditText) findViewById(R.id.NametextView);

            if(!validate(id, name)){
                return;
            }

            SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
            String user_id = data.getString("user_id", "null");
            if (user_id.equals("null")) {
                return;
            }

            MyHttpClient client = new MyHttpClient();
            client.setParam("plug_id", id.getText().toString());
            client.setParam("name", name.getText().toString());
            client.setParam("user_id", user_id);

            client.registerPlug("http://smartplug.php.xdomain.jp/register_plug.php");
            client.removeParam("plug_id");
            client.removeParam("name");
            client.removeParam("user_id");
        }
    };

    //入力項目の確認
    //IDとNameが未入力であれば、false
    //入力されていれば、true
    private boolean validate(EditText id, EditText name) {
        if (id.getText().toString().equals("")) {
            Toast.makeText(PlugAddActivity.this, "Idを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.getText().toString().equals("")) {
            Toast.makeText(this, "Nameを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void registerPlug(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String status = response.getString("status");
                        String str = "status:" + status; // "passが正しいか"
                        Log.d("onSuccess:", str);
                        if (status.equals("false")) {
                            msg = "入力されたプラグIDは既に登録されています。";
                            Toast.makeText(PlugAddActivity.this, status + msg, Toast.LENGTH_LONG).show();
                        } else {
                            MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
                            SQLiteDatabase database = helper.getWritableDatabase();
                            try {
                                ContentValues values = new ContentValues();
                                EditText id = (EditText) findViewById(R.id.IDtextView);
                                String plug_id = id.getText().toString();
                                EditText name = (EditText) findViewById(R.id.NametextView);
                                SharedPreferences preferences = getSharedPreferences("smartplug", Context.MODE_PRIVATE);

                                //plugs Table
                                values.put("id", plug_id);
                                values.put("name", name.getText().toString());
                                values.put("user_id", preferences.getString("user_id", ""));
                                database.insert("plugs", null, values);

                                //timers Table
                                values.clear();
                                values.put("plug_id", plug_id);
                                values.put("ontime", "");
                                values.put("offtime", "");
                                values.put("time_flg", 0);
                                database.insert("timers", null, values);

                                //configs Table
                                String[] configs = {"power_notice", "power_alert", "power_auto",
                                        "temperature_notice", "temperature_alert", "temperature_auto",
                                        "accident_notice", "accident_alert", "accident_auto"};
                                values.clear();
                                values.put("plug_id", plug_id);
                                for(int i = 0; i < configs.length; i++) {
                                    values.put(configs[i], 1);
                                }
                                database.insert("configs", "1", values);
                            } finally {
                                database.close();
                            }

                            msg = "登録されました。";
                            Toast.makeText(PlugAddActivity.this, status + msg, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplication(), HomeActivity.class);
                            startActivity(intent);
                            PlugAddActivity.this.finish();
                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(PlugAddActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,  String errorStrings, Throwable throwable) {
                    super.onFailure(statusCode, headers, errorStrings, throwable);
                    msg = "接続エラー";
                    Toast.makeText(PlugAddActivity.this, msg, Toast.LENGTH_LONG);
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

}
