package com.example.a2140252.smartplug;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a2140252.smartplug.dataPackage.GraphData;
import com.example.a2140252.smartplug.dataPackage.GraphDetailData;
import com.example.a2140252.smartplug.dataPackage.PlugData;
import com.example.a2140252.smartplug.dataPackage.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class LoginActivity extends AppCompatActivity {
    //UserData userData;
    String user_id  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //userData=(UserData) this.getApplication();

        findViewById(R.id.Nextbutton).setOnClickListener(login_click); //ログインボタン押下
    }

    View.OnClickListener login_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText id = (EditText) findViewById(R.id.IDtextView);
            EditText pass = (EditText) findViewById(R.id.PASStextView);

            //空文字列の場合、処理を終了
            if(!validate(id, pass)){
                return;
            }

            MyHttpClient client = new MyHttpClient();
            client.setParam("user_id", id.getText().toString());
            client.setParam("password", pass.getText().toString());

            client.login("http://smartplug.php.xdomain.jp/login.php");
            client.removeParam("user_id");
            client.removeParam("password");
        }
    };

    //入力項目の確認
    //IDとPASSが未入力であれば、false
    //入力されていれば、true
    private boolean validate(EditText id, EditText pass) {
        if (id.getText().toString().equals("")) {
            Toast.makeText(this, "Idを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.getText().toString().equals("")) {
            Toast.makeText(this, "Passwordを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (id.getText().toString().getBytes().length > 16) {
            if (id.getText().toString().length() > 16) {
                Toast.makeText(this, "Idは半角英数16文字以内で入力してください。", Toast.LENGTH_SHORT).show();
            } else if (id.getText().toString().length() <= 10) {
                Toast.makeText(this, "Idは全角8文字以内で入力してください。", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Idは16byte以内で入力してください。", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        if (pass.getText().toString().getBytes().length > 16) {
            if (pass.getText().toString().length() > 16) {
                Toast.makeText(this, "Passwordは半角英数16文字以内で入力してください。", Toast.LENGTH_SHORT).show();
            } else if (pass.getText().toString().length() <= 10) {
                Toast.makeText(this, "Passwordは全角8文字以内で入力してください。", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Passwordは16byte以内で入力してください。", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;

        public void login(final String urlString) {
            url = urlString;
            final AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("bool").equals("false")) {
                            msg = "パスワードが間違っています。";
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.d("onSuccess:", msg);
                        } else {
                            //ログイン成功時、ユーザーIDを保存
                            //userData.setUserId(response.getString("user_id"));

                            user_id = response.getString("user_id");
                            SharedPreferences preferences = getSharedPreferences("smartplug", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user_id", user_id);
                            editor.apply();

                            //SQLite
                            register_users(user_id);

                            //ユーザーIDに紐づくPlugData、GraphDataを取得する
                            MyHttpClient client = new MyHttpClient();
                            client.setParam("user_id", user_id);
                            client.getPlugData("http://smartplug.php.xdomain.jp/android/get_plug_data.php");
                            client.removeParam("user_id");

                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(LoginActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("login", e.toString());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    msg = "接続エラー";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });

        }

        public void getPlugData(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray plugArray = response.getJSONArray("plug");

                        //ユーザーの持つプラグの数の配列を宣言
//                        userData.setPlugData(new PlugData[plugArray.length()]);
//                        for (int i = 0; i < userData.getPlugData().length; i++) {
//                            JSONObject plugObject = plugArray.getJSONObject(i);
//                            userData.setPlugData(new PlugData(), i);
//                            PlugData plugData = userData.getPlugData(i);
//                            plugData.setPlugId(plugObject.getString("id"));
//                            plugData.setPlugName(plugObject.getString("name"));
//                            plugData.setSwitchFlg(plugObject.getBoolean("switch_flg"));
//                            plugData.setOntime(plugObject.getString("ontime"));
//                            plugData.setOfftime(plugObject.getString("offtime"));
//                            plugData.setTime_flg(plugObject.getBoolean("time_flg"));
//                            boolean[] configs =
//                                {plugObject.getBoolean("power_notice"), plugObject.getBoolean("power_alert"), plugObject.getBoolean("power_auto"),
//                                plugObject.getBoolean("temperature_notice"), plugObject.getBoolean("temperature_alert"), plugObject.getBoolean("temperature_auto"),
//                                plugObject.getBoolean("accident_notice"), plugObject.getBoolean("accident_alert"), plugObject.getBoolean("accident_auto")};
//                            for(int j = 0; j < configs.length; j++) {
//                                plugData.setConfigArray(configs[j], j);
//                            }
//                        }

                        //SQLite
                        String plug_ids[] = register_plug_data(plugArray);

                        MyHttpClient client = new MyHttpClient();
                        client.setParam("user_id",  user_id);
                        client.getGraphData("http://smartplug.php.xdomain.jp/android/get_graph_data.php", plug_ids);
                        client.removeParam("user_id");

                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(LoginActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
                        Log.w("Login, getPlugData", msg);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    msg = "接続エラー";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        }

        public void getGraphData(String urlString, final String[] plug_ids) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
//                        for(int i = 0; i <  userData.getPlugData().length; i++) {
//                            //plug_idのJSONArrayを取得
//                            JSONArray plugArray = response.getJSONArray(userData.getPlugData(i).getPlugId());
//                            GraphData graphData;
//                            for(int j = 0; j < plugArray.length(); j++) {
//                                //plugArrayの下にあるyearとpowerの組み合わせでできているJSONObjectを取得
//                                JSONObject graphObject = plugArray.getJSONObject(j);
//                                //プラグが持つグラフ用のデータの年数分の配列を宣言（最大三年分）
//                                userData.getPlugData(i).setGraphData(new GraphData[plugArray.length()]);
//                                userData.getPlugData(i).setGraphData(new GraphData(), j);
//                                graphData = userData.getPlugData(i).getGraphData(j);
//                                graphData.setYear(new String(graphObject.getString("year")));
//                                JSONArray powerArray = graphObject.getJSONArray("power");
//                                //プラグが持つ一年分のデータのうちデータがある月数分の配列を宣言
//                                graphData.setDetailData(new GraphDetailData[powerArray.length()]);
//
//                                for(int k = 0; k < powerArray.length(); k++) {
//                                    JSONObject powerObject = powerArray.getJSONObject(k);
//                                    userData.getPlugData(i).getGraphData(j).setDetailData(new GraphDetailData(), k);
//                                    GraphDetailData graphDetailData = graphData.getDetailData(k);
//                                    graphDetailData.setMonth(powerObject.getString("month"));
//                                    graphDetailData.setPower(powerObject.getInt("power"));
//                                }
//                            }
//                        }

                        //SQLite
                        register_power(plug_ids, response);

                        //Homeへ移動
                        Intent intent = new Intent(getApplication(), HomeActivity.class); //Next Activity
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(LoginActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
                        Log.w("Login, getGraphData", e.toString());
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    msg = "接続エラー";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
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

    private void register_users(String user_id) {
        //SQLite
        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        try{
            EditText pass = (EditText) findViewById(R.id.PASStextView);

            ContentValues insertValues = new ContentValues();
            //String user_id = response.getString("user_id");
            insertValues.put("id", user_id);
            insertValues.put("password", pass.getText().toString());
            long id = database.update("users", insertValues, "id=?", new String[]{user_id});
            if(id == -1) {
                database.insert("users", user_id, insertValues);
            }
        }finally{
            database.close();
        }
    }

    private String[] register_plug_data(JSONArray plugArray) {
        String[] plug_ids = new String[plugArray.length()];

        //SQLite
        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        try{

            for(int i = 0; i < plugArray.length(); i++) {
                JSONObject plugObject = plugArray.getJSONObject(i);
                String plug_id =  plugObject.getString("id");
                plug_ids[i] = plug_id;

                ContentValues insertValues = new ContentValues();
                //plugsテーブルのデータ登録
                insertValues.put("name", plugObject.getString("name"));
                insertValues.put("switch_flg", plugObject.getBoolean("switch_flg"));
                EditText user_id = (EditText) findViewById(R.id.IDtextView);
                insertValues.put("user_id", user_id.getText().toString());
                long id =  database.update("plugs", insertValues, "id=?", new String[]{plug_id});
                if(id == -1) {
                    insertValues.put("id", plug_id);
                    database.insert("plugs", plug_id, insertValues);
                }

                insertValues.clear();
                //timersテーブルのデータ登録
                insertValues.put("ontime", plugObject.getString("ontime"));
                insertValues.put("offtime", plugObject.getString("offtime"));
                insertValues.put("time_flg", plugObject.getBoolean("time_flg"));
                id = database.update("timers", insertValues, "plug_id=?", new String[]{plug_id});
                if(id == -1) {
                    insertValues.put("plug_id", plug_id);
                    database.insert("timers", plug_id, insertValues);
                }

                insertValues.clear();
                //configsテーブルのデータ登録
                insertValues.put("power_notice", plugObject.getBoolean("power_notice"));
                insertValues.put("power_alert", plugObject.getBoolean("power_alert"));
                insertValues.put("power_auto", plugObject.getBoolean("power_auto"));
                insertValues.put("temperature_notice", plugObject.getBoolean("temperature_notice"));
                insertValues.put("temperature_alert", plugObject.getBoolean("temperature_alert"));
                insertValues.put("temperature_auto", plugObject.getBoolean("temperature_auto"));
                insertValues.put("accident_notice", plugObject.getBoolean("accident_notice"));
                insertValues.put("accident_alert", plugObject.getBoolean("accident_alert"));
                insertValues.put("accident_auto", plugObject.getBoolean("accident_auto"));
                id = database.update("configs", insertValues, "plug_id=?", new String[]{plug_id});
                if(id == -1) {
                    insertValues.put("plug_id", plug_id);
                    database.insert("configs", plug_id, insertValues);
                }
            }
        } catch (Exception e) {
            String msg = "システムエラー";
            Toast.makeText(LoginActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
            Log.w("Login, getPlugData", msg);
        } finally{
            database.close();
        }

        return plug_ids;
    }

    private void register_power(String[] plug_ids, JSONObject plugObject) {
        //SQLite
        MyOpenHelper helper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        try {
            ContentValues insertValues = new ContentValues();
            for(int i = 0; i < plug_ids.length; i++) {
                JSONArray plugArray = plugObject.getJSONArray(plug_ids[i]);
                for(int j = 0; j < plugArray.length(); j++) {
                    //plugArrayの下にあるyearとpowerの組み合わせでできているJSONObjectを取得
                    JSONObject graphObject = plugArray.getJSONObject(j);
                    String year = graphObject.getString("year");
                    JSONArray powerArray = graphObject.getJSONArray("power");
                    for(int k = 0; k < powerArray.length(); k++) {
                        JSONObject powerObject = powerArray.getJSONObject(k);
                        insertValues.put("power", powerObject.getInt("power"));

                        String date = year + powerObject.getString("month");
                        long id = database.update("power", insertValues, "plug_id=? and date=?", new String[]{plug_ids[i], date});
                        if(id == -1) {
                            insertValues.put("plug_id", plug_ids[i]);
                            insertValues.put("date", date);
                            database.insert("power", plug_ids[i], insertValues);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = "システムエラー";
            Toast.makeText(LoginActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
            Log.w("Login, getGraphData", e.toString());
        } finally {
            database.close();
        }
    }
}
