package com.example.a2140252.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class PlugAddMoreActivity extends AppCompatActivity {
    EditText plugid ;//ID
    EditText plugname;//コンセント名

    ArrayList<PageItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugaddmore_layout);

        //ID
        plugid =(EditText) findViewById(R.id.IDtextView);

        //コンセント名
        plugname=(EditText) findViewById(R.id.PASStextView);

        //追加ボタン押した
        findViewById(R.id.Nextbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(plugid.getText()) || plugid.getText().toString().trim().equals("") ){//nullかlength=0||空白
                    //Toastの第一引数がthisではエラーになるのでgetApp...にした
                    Toast.makeText(getApplicationContext(),"コンセントIDを入力してください",Toast.LENGTH_SHORT).show();
                    Log.d("PlugAddMore","id null");
                    return;

                }else if(TextUtils.isEmpty(plugname.getText()) || plugname.getText().toString().trim().equals("") ){
                    //Toastの第一引数がthisではエラーになるのでgetApp...にした
                    Toast.makeText(getApplicationContext(),"コンセント名を入力してください",Toast.LENGTH_SHORT).show();
                    return;

                }

                PlugAddMore();
            }
        });

    }

    //タブ追加処理
    public void PlugAddMore() {
        String id = plugid.getText().toString().trim();//id
        String name = plugname.getText().toString().trim();//タブ名


        Intent intent = getIntent();
        list = (ArrayList<PageItem>) intent.getSerializableExtra("list");//ArrayListを受け取る

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                Toast.makeText(this, "すでに存在するコンセントIDです", Toast.LENGTH_SHORT).show();
                return;
            }else if (list.get(i).getTitle().equals(name)) {
                    Toast.makeText(this, "すでに存在するコンセント名です", Toast.LENGTH_SHORT).show();
                    return;
            }
        }


        MyHttpClient client = new MyHttpClient();
        SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String user_id = data.getString("user_id", "");
        client.setParam("user_id", user_id);
        client.setParam("plug_id", id);
        client.setParam("name", name);
        Log.d("onClick", "通信前");
        client.plugAdd("http://smartplug.php.xdomain.jp/register_plug.php");
        client.removeParam("user_id");
        client.removeParam("plug_id");
        client.removeParam("name");

//        PageItem plug = new PageItem(name, id); //11/07変更
//        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
//        adapter.addItem(plug);
//        intent.putExtra("newPlug", plug);
        intent.putExtra("id", id);
        intent.putExtra("name", name);

        setResult(RESULT_OK, intent);//処理成功
        finish();//戻るボタンで戻らせない
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void plugAdd(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("status").equals("true")) {
                            msg = "プラグが登録されました。";
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        } else {
                            msg = "登録されませんでした。";
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        msg = "システムエラー";
                        Toast.makeText(getApplicationContext(), msg + e.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,  String errorStrings, Throwable throwable) {
                    super.onFailure(statusCode, headers, errorStrings, throwable);
                    msg = "接続エラー";
                    Toast.makeText(getApplicationContext(), errorStrings, Toast.LENGTH_LONG).show();
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
