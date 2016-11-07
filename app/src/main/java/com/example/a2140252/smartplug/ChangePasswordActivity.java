package com.example.a2140252.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/11/02.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        findViewById(R.id.ChangePasswordButton).setOnClickListener(changePassButton); //パスワード変更ボタン押下
    }

    View.OnClickListener changePassButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText oldPass = (EditText) findViewById(R.id.OldPasstextView);
            EditText newPass = (EditText) findViewById(R.id.NewPasstextView);
            EditText confirmPass = (EditText) findViewById(R.id.NewPassConfirmtextView);

            if(!validate(oldPass, newPass, confirmPass)){
                return;
            }

            SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
            String user_id = data.getString("user_id", "null");
            if (user_id.equals("null")) {
                return;
            }

            MyHttpClient client = new MyHttpClient();
            client.setParam("user_id", user_id);
            client.setParam("old_pass", oldPass.getText().toString());
            client.setParam("new_pass", newPass.getText().toString());

            client.login("http://smartplug.php.xdomain.jp/change_password.php");
            client.removeParam("user_id");
            client.removeParam("old_pass");
            client.removeParam("new_pass");
        }
    };

    //入力項目の確認
    //IDとPASSが未入力であれば、false
    //入力されていれば、true
    private boolean validate(EditText oldpass, EditText newpass, EditText confirmpass) {
        if (oldpass.getText().toString().equals("")) {
            Toast.makeText(this, "現在のPASSWORDを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newpass.getText().toString().equals("")) {
            Toast.makeText(this, "新しいPASSWORDを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmpass.getText().toString().equals("")) {
            Toast.makeText(this, "PASSWORDの確認を入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newpass.getText().toString().equals(confirmpass.getText().toString())) {
            Toast.makeText(this, "新しいPASSWORDが一致していません。再入力してください。", Toast.LENGTH_SHORT).show();
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
                        String status = response.getString("status");
                        String str = "status:" + status; // "変更できたか"
                        Log.d("onSuccess:", str);

                        if (response.getString("status").equals("false")) {
                            msg = "現在のパスワードが間違っているため、変更できませんでした。";
                            Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                            Log.e("pass不一致:", msg);
                        } else {
                            msg = "PASSWORDを変更しました。";
                            Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    msg = "接続エラー";
                    Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
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
