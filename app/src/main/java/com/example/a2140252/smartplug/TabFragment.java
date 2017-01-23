package com.example.a2140252.smartplug;

/**
 * Created by 2140104 on 2016/10/21.
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2140252.smartplug.dataPackage.GraphData;
import com.example.a2140252.smartplug.dataPackage.GraphDetailData;
import com.example.a2140252.smartplug.dataPackage.PlugData;
import com.example.a2140252.smartplug.dataPackage.UserData;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;


public class TabFragment extends Fragment {//Fragment

   //10/21
    private String id = "";

    private int position;//Listの添え字

    //UserData userData = null;

    //電源を入れる/切るﾎﾞﾀﾝのﾃｷｽﾄ切替用フラグ
    //static SwitchFlagList switchFlagList;

    @Override
    public void onCreate(Bundle savedInstanceState) {//1回のみ
        super.onCreate(savedInstanceState);
///確認のための表示-------
        Bundle args = getArguments();

        if (args != null) {

            position = Integer.parseInt(args.getSerializable("position").toString());//Tab位置　添え字になる
            id = args.getString("id");
            Log.d("TabF","onCreate() 渡されたタブid : "+id);
        }
        ////-------------

        //userData = (UserData) getActivity().getApplication();

        //switchFlagList = (SwitchFlagList) getContext();
        
    }

    @Override//画面の端に映るたびごとに
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {


        View v = inflater.inflate(R.layout.fragment_layout, null);

        Log.d("TabF","onCreateView()  渡された値（表示）: "+id);
        //注意　findViewByIdの前には v がいる
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.fragment_main_layout);

        //Chart表示
        com.example.a2140252.smartplug.Chart chart = new com.example.a2140252.smartplug.Chart(getContext(), id);
        chart.createLineChart((LineChart) v.findViewById(R.id.chart));

        TextView money = (TextView) v.findViewById(R.id.moneyTextView);
        MyOpenHelper helper = new MyOpenHelper(getActivity().getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String date = null;
            if(month < 10) {
                date = String.valueOf(year) + "0" + String.valueOf(month);
            } else {
                date = String.valueOf(year) + String.valueOf(month);
            }
            cursor = database.query("power", new String[]{"power"}, "plug_id=? AND date=?", new String[]{id, date}, null, null, null, null);
            if(cursor.moveToFirst()) {
                int powerIndex = cursor.getColumnIndex("power");
                int power = cursor.getInt(powerIndex);

                //power * 27 が消費電力金額になる。
                power *= 27;
                money.setText("今月の消費電力金額は\n" + String.valueOf(power) + "円です。\n   (1kwhあたり27円で計算しています。)");
            }
        } finally {
            database.close();
            if(cursor != null) {
                cursor.close();
            }
        }

        v.findViewById(R.id.TimerButton).setOnClickListener(timer_click);
        v.findViewById(R.id.SetButton).setOnClickListener(config_click);
        v.findViewById(R.id.StartButton).setOnClickListener(start_click);
        v.findViewById(R.id.StopButton).setOnClickListener(stop_click);

        //電源のOn/Off切替ボタンのﾃｷｽﾄをswitch_flgの値に応じて変更する
//        Button btn = (Button) v.findViewById(R.id.StartButton);
//        if (SwitchFlagList.switch_flag.get(id).equals("1")) {
////            btn.setText("電源を切る");
//        } else {
////            btn.setText("電源を入れる");
//        }


        return linearLayout;
    }

    View.OnClickListener timer_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), TimerActivity.class); //Next Activity
            intent.putExtra("plug_id", id);
            startActivity(intent);
        }
    };

    View.OnClickListener config_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), ConfigActivity.class); //Next Activity
            intent.putExtra("plug_id", id);
            intent.putExtra("no", position);//添え字
            startActivity(intent);
        }
    };

    View.OnClickListener start_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MyHttpClient client = new MyHttpClient();
            client.msg = "start";
            client.setParam("switch_flg", true);
            client.setParam("plug_id", id);
            Log.d("onClick", "通信前");
            client.toggle("http://smartplug.php.xdomain.jp/android/set_switch_flag.php");
            //client.toggle("http://smartplug.php.xdomain.jp/switch_flag.php");
            client.removeParam("switch_flg");
            client.removeParam("plug_id");
        }
    };

    View.OnClickListener stop_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MyHttpClient client = new MyHttpClient();
            client.msg = "stop";
            client.setParam("switch_flg", false);
            client.setParam("plug_id", id);
            Log.d("onClick", "通信前");
            client.toggle("http://smartplug.php.xdomain.jp/android/set_switch_flag.php");
            //client.toggle("http://smartplug.php.xdomain.jp/switch_flag.php");
            client.removeParam("switch_flg");
            client.removeParam("plug_id");
        }
    };

    public class MyHttpClient {

        String url; //接続先url
        final RequestParams params = new RequestParams(); //リクエストパラメータ
        String msg;

        public void toggle(String urlString) {
            url = urlString;
            AsyncHttpClient client = new AsyncHttpClient(); //通信準備
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.d("TabFragment", "成功");
                    if (msg.equals("start")) {
                        Toast.makeText(getContext(), "電源を入れました！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "電源を切りました。", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.d("TabFragment", "失敗");
                    Toast.makeText(getContext(), "通信に失敗しました。\n時間をおいてもう一度お試しください。", Toast.LENGTH_LONG).show();
                }
            });

        }

        public void setParam(String key, String value) {
            params.put(key, value);
        }

        public void setParam(String key, boolean value) {
            params.put(key, value);
        }

        public void removeParam(String key) {
            params.remove(key);
        }

    }
}