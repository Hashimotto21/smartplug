package com.example.a2140252.smartplug;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

import java.net.URI;

/**
 * Created by 2140306 on 2016/10/26.
 */
public class MyHttpClient {

    String url; //接続先url
    final RequestParams params = new RequestParams(); //リクエストパラメータ
    boolean re; //connect()の戻り値
    String msg;

    public MyHttpClient(String url) {
        this.url = url;
    }

    public boolean login() {
        AsyncHttpClient client = new AsyncHttpClient(); //通信準備
        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String str = "userid:" + response.getString("userid"); // "user"
                    str += "\npass:" + response.getString("pass"); // "pass"
                    str += "\nbool:" + response.getString("bool"); // "passが正しいか"

                    re = true;
                    if (response.getString("bool").equals("false")) {
                        re = false;
                        msg = "パスワードが間違っています。";
                    }
                    Log.d("onSuccess:", str);
                } catch (Exception e) {
                    msg = "システムエラー";
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                msg = "接続エラー";
            }
        });

        return re;
    }

    public boolean registerUser() {
        AsyncHttpClient client = new AsyncHttpClient(); //通信準備
        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String str = "userid:" + response.getString("userid"); // "user"
                    str += "\npass:" + response.getString("pass"); // "pass"
                    str += "\nbool:" + response.getString("bool"); // "passが正しいか"

                    re = true;
                    if (response.getString("bool").equals("false")) {
                        re = false;
                        msg = "パスワードが間違っています。";
                    }
                    Log.d("onSuccess:", str);
                } catch (Exception e) {
                    msg = "システムエラー";
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                msg = "接続エラー";
            }
        });

        return re;
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void removeParam(String key) {
        params.remove(key);
    }
}
