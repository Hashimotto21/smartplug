package com.example.a2140252.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Created by 2140306 on 2016/10/19.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

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
        return true;
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void login(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        //デバッグ用コード
                        String str = "user_id:" + response.getString("user_id"); // "user"
                        str += "\npassword:" + response.getString("password"); // "pass"
                        str += "\nbool:" + response.getString("bool"); // "passが正しいか"
                        Log.d("onSuccess:", str);

                        if (response.getString("bool").equals("false")) {
                            msg = "パスワードが間違っています。";
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.d("onSuccess:", msg);
                        } else {
                            //ユーザIDを保存
                            SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = data.edit();
                            editor.putString("user_id", response.getString("user_id"));
                            editor.apply();

                            //Homeへ移動
                            Intent intent = new Intent(getApplication(), HomeActivity.class); //Next Activity
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
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

}
