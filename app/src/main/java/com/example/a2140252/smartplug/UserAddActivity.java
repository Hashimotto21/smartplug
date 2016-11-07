package com.example.a2140252.smartplug;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
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
public class UserAddActivity extends AppCompatActivity {
    EditText id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useradd_layout);

        findViewById(R.id.Nextbutton).setOnClickListener(nextbutton); //次へボタン押下

    }

    View.OnClickListener nextbutton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        id = (EditText) findViewById(R.id.IDtextView);
        EditText pass = (EditText) findViewById(R.id.PASStextView);

        if(!validate(id, pass)){
            return;
        }

        MyHttpClient client = new MyHttpClient();
        client.setParam("user_id", id.getText().toString());
        client.setParam("password", pass.getText().toString());

        client.registerUser("http://smartplug.php.xdomain.jp/register_user.php");
        client.removeParam("user_id");
        client.removeParam("password");
        }
    };

    //入力項目の確認
    //IDとPASSが未入力であれば、false
    //入力されていれば、true
    private boolean validate(EditText id, EditText pass) {
        if (id.getText().toString().equals("")) {
            Toast.makeText(UserAddActivity.this, "Idを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.getText().toString().equals("")) {
            Toast.makeText(this, "Passwordを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void registerUser(String urlString) {
            url = urlString;
            final AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String status = response.getString("status");
                        String str = "status:" + status; // "passが正しいか"
                        Log.d("onSuccess:", str);
                        if (status.equals("false")) {
                            msg = "入力されたユーザーIDは既に使用されています。";
                            Toast.makeText(UserAddActivity.this, status + msg, Toast.LENGTH_LONG).show();
                        } else {
                            msg = "登録されました。";
                            Toast.makeText(UserAddActivity.this, status + msg, Toast.LENGTH_LONG).show();

                            //ユーザIDを保存
                            SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = data.edit();
                            id = (EditText) findViewById(R.id.IDtextView);
                            editor.putString("user_id", id.getText().toString());
                            editor.apply();

                            Intent intent = new Intent(getApplication(), PlugAddActivity.class); //プラグ追加Activity
                            intent.putExtra("user_id", id.getText().toString());
                            startActivity(intent);
                            UserAddActivity.this.finish();
                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(UserAddActivity.this, msg + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,  String errorStrings, Throwable throwable) {
                    super.onFailure(statusCode, headers, errorStrings, throwable);
                    msg = "接続エラー";
                    Toast.makeText(UserAddActivity.this, msg, Toast.LENGTH_LONG);
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
