package com.example.a2140252.smartplug;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a2140252.smartplug.dataPackage.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class PlugDeleteActivity extends AppCompatActivity {
    EditText plugid ;//ID
    EditText plugname;//コンセント名

    ArrayList<PageItem> list;//HomeActivity()から受け取るList

    //UserData userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugdelete_layout);

        //userData = (UserData) getApplication();

        //ID
        plugid =(EditText) findViewById(R.id.IDtextView);
        //コンセント名
        plugname=(EditText) findViewById(R.id.PASStextView);

        //ToolBarを表示する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //ボタン押した
        findViewById(R.id.Nextbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(plugid.getText()) || plugid.getText().toString().trim().equals("") ){
                    //Toastの第一引数がthisではエラーになるのでgetApp...にした
                    Toast.makeText(getApplicationContext(),"コンセントIDを入力してください",Toast.LENGTH_SHORT).show();
                    Log.d("PlugAddMore","id null");
                    return;

                }else if(TextUtils.isEmpty(plugname.getText()) || plugname.getText().toString().trim().equals("") ){
                    //Toastの第一引数がthisではエラーになるのでgetApp...にした
                    Toast.makeText(getApplicationContext(),"コンセント名を入力してください",Toast.LENGTH_SHORT).show();
                    return;

                }

                PlugDelete();
            }
        });

    }

    public void PlugDelete(){

        String id =plugid.getText().toString().trim();//id
        String name = plugname.getText().toString().trim();//タブ

        Intent intent = getIntent();
        list = (ArrayList<PageItem>) intent.getSerializableExtra("list");//ArrayListを受け取る

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getId().equals(id)){
                if(list.get(i).getTitle().equals(name)) {
                    //id name　ともに存在する
                    //サーバに削除データを反映
                    MyHttpClient client = new MyHttpClient();
                    SharedPreferences preferences = getSharedPreferences("smartplug", Context.MODE_PRIVATE);
                    client.setParam("user_id", preferences.getString("user_id", ""));
                    client.setParam("plug_id", id);
                    Log.d("onClick", "通信前");
                    client.plugDelete("http://smartplug.php.xdomain.jp/delete_plug.php");
                    client.removeParam("user_id");
                    client.removeParam("plug_id");

                    Intent intent3 = new Intent();
                    intent3.putExtra("delete",i);//削除対象のTabの情報を持つクラスの添え字を渡す
                    intent3.putExtra("plug_id", id);

                    setResult(RESULT_OK, intent3);//処理成功

                    finish();//戻るボタンで戻らせない
                    return;

                }
            }
        }
        Toast.makeText(this,"存在しないコンセントです",Toast.LENGTH_LONG).show();

    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void plugDelete(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getString("status").equals("true")) {
                            msg = "プラグが削除されました。";
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        } else {
                            msg = "削除されませんでした。";
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

    //ハンバーガーメニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_user:
                intent = new Intent(this, UserConfigActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                intent = new Intent(this, PlugAddMoreActivity.class);
//                list= adapter.getList();
//                intent.putExtra("list",list);//ArrayListを渡す
//
//                startActivityForResult(intent, ADDMORE_CODE);//起動先からデータを返してもらえる
                return true;
            case R.id.menu_del:
                intent = new Intent(this, PlugDeleteActivity.class);
//                list= adapter.getList();
//                intent.putExtra("list",list);//ArrayListを渡す
//
//                startActivityForResult(intent,DELETE_CODE);
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
