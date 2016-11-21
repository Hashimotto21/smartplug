package com.example.a2140252.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;


public class HomeActivity extends AppCompatActivity {//AppCompatActivity FragmentActivity

    private static final int ADDMORE_CODE = 1;
    private static final int DELETE_CODE = 2;


    CustomFragmentPagerAdapter adapter;

    ArrayList<PageItem> list;

    ViewPager pager;

    ConfigItem configItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        //ViewPager
        pager = (ViewPager) findViewById(R.id.pager);

        // PagerTitleStrip のカスタマイズ   ----
        PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.strip);
        strip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        strip.setTextColor(0xFF303F9F);
        strip.setTextSpacing(50);
        strip.setNonPrimaryAlpha(0.3f);
        strip.setDrawFullUnderline(true);//下線表示
        strip.setTabIndicatorColor(0x303F9F);//インディケーターの色
        //-----------------

        list = adapter.getList();//ArrayList生成
        //---------------------------------

        configItem=(ConfigItem) this.getApplication();

        SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String user_id = data.getString("user_id", "");
        MyHttpClient client = new MyHttpClient();
        client.setParam("user_id", user_id);
        Log.d("onClick", "通信前");
        client.getGraphData("http://smartplug.php.xdomain.jp/get_graph_data.php");
        client.removeParam("user_id");


    }//onCreate()

    //HomeActivityのインスタンスが既に存在している場合
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {//startActivityForResult(intent,1);の１か？

            case (ADDMORE_CODE)://ADDMORE_CODE = 1  PlugAddMoreActivity()から戻ってきたら
                if (resultCode == RESULT_OK) {
                    //「コンセントを追加する」ボタンを押して戻ってきたときの処理
                    Intent intent = getIntent();
                    String id = intent.getStringExtra("id");
                    String name = intent.getStringExtra("name");
                    PageItem pageItem = new PageItem(name, id);
                    adapter.addItem(pageItem);
                    //adapter.addItem((PageItem) data.getSerializableExtra("newPlug"));
                    pager.setAdapter(adapter);

                    //設定ボタンのON/OFF
                    Boolean[] config3 ={false,false,false,false,false,false,false,false,false};
                    configItem.cList.add(config3);
                    Log.d("onActivityResult", "タブ追加後★RESULT_OK");
                }
                break;
            case(DELETE_CODE)://DELETE_CODE = 2  PlugDeleteActivity()から戻ってきたら
                if (resultCode == RESULT_OK) {
                    //「コンセントを削除する」ボタンを押して戻ってきたときの処理
                    int d  =Integer.parseInt(data.getSerializableExtra("delete").toString());

                    adapter.Delete(d);//ArrayList から削除

                    adapter.destroyAllItem(pager);//削除
                    Log.d("onActivityResult", "adapter.destroyAllItem(pager);　後");
                    adapter.notifyDataSetChanged();
                    pager.setAdapter(adapter);

                    //設定ON/OFFの情報も削除
                    configItem.cList.remove(d);
                    Log.d("onActivityResult", "タブ削除後★RESULT_OK");
                }
                break;
            default:
                break;
        }
    }

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;


        public void getGraphData(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        //Toast.makeText(getApplicationContext(), response.getJSONObject(0).get("plug_id_1").toString(), Toast.LENGTH_LONG).show();
                        //Log.d("HomeActivity", response.getJSONArray("plug").getJSONObject(0).getString("id"));
                        int count = Integer.parseInt(response.getString("count"));
                        //プラグ追加
                        for (int i = 0; i < count; i++) {
                            String id = response.getJSONArray("plug").getJSONObject(i).getString("id");
                            String name = response.getJSONArray("plug").getJSONObject(i).getString("name");
                            PageItem plug = new PageItem(name, id);//("タイトル","id")
                            adapter.addItem(plug);

                            //--設定のON/OFFを表す
                            Boolean[] config ={true,true,false,false,false,false,false,false,false};
                            configItem.cList.add(config);//追加
                            //電源のOn/Off切替ボタンのﾃｷｽﾄをswitch_flgの値に応じて変更する
//                            int switch_flg = response.getJSONArray("plug").getJSONObject(i).getInt("switch_flg");
//                            Button btn = (Button) findViewById(R.id.StartButton);
//                            if (switch_flg == 0) {
//                                btn.setText("電源を入れる");
//                            } else {
//                                btn.setText("電源を切る");
//                            }
                        }

//                        Toast.makeText(getApplicationContext(), String.valueOf(count) + ", " + response.getString("plug_id_1"), Toast.LENGTH_LONG).show();
                        pager.setAdapter(adapter);
                        //タブ切り替えた時　 Logを残すためのメソッド -------------------------------
                        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                            //findViewById(R.id.pager).addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                            @Override
                            public void onPageScrollStateChanged(int state) {
                                if (ViewPager.SCROLL_STATE_IDLE == state) {//ViewPagerの位置が落ち着いた状態が0(= CROLL_STATE_IDLE)

                                    Log.d("ViewPager", "現在選択中のタブ" + ( pager.getCurrentItem() + 1) + "つ目 -------");

                                }
                            }
                        });

//
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
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
                list= adapter.getList();
                intent.putExtra("list",list);//ArrayListを渡す

                startActivityForResult(intent, ADDMORE_CODE);//起動先からデータを返してもらえる
                return true;
            case R.id.menu_del:
                intent = new Intent(this, PlugDeleteActivity.class);
                list= adapter.getList();
                intent.putExtra("list",list);//ArrayListを渡す

                startActivityForResult(intent,DELETE_CODE);
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
