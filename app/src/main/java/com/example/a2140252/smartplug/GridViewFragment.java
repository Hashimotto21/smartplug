package com.example.a2140252.smartplug;

/**
 * Created by 2140104 on 2016/10/21.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;


public class GridViewFragment extends Fragment {//Fragment

   //10/21
    private int id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {//1回のみ
        super.onCreate(savedInstanceState);
///確認のための表示-------
        Bundle args = getArguments();

        if (args != null) {

            id = args.getInt("id");
            Log.d("GridViewF","onCreate() 渡されたタブid : "+id);
        }
        ////--------------
    }


    @Override//画面の端に映るたびごとに
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        View v = inflater.inflate(R.layout.fragment_layout,null);

        Log.d("GridViewF","onCreateView()  渡された値（表示）: "+id);
        //注意　findViewByIdの前には v がいる
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.fragment_main_layout);
        //Chart表示
        Chart chart = new Chart((CombinedChart) v.findViewById(R.id.chart));

        v.findViewById(R.id.TimerButton).setOnClickListener(timer_click); //タイマーボタン押下
        v.findViewById(R.id.SetButton).setOnClickListener(config_click); //設定ボタン押下

        TextView tv = (TextView) v.findViewById(R.id.textview1);
         tv.setText(String.valueOf(id));// 渡されたもの

        return linearLayout;
    }

    View.OnClickListener timer_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), TimerActivity.class); //Next Activity
            startActivity(intent);
        }
    };

    View.OnClickListener config_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), ConfigActivity.class); //Next Activity
            startActivity(intent);
        }
    };


    /*　旧　10/21
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GridView gridView = new GridView(getActivity());

        //画面の向きを調べる
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横向きの場合
            gridView.setNumColumns(3);
        } else {
            //縦向きの場合
            gridView.setNumColumns(2);
        }

        @SuppressWarnings("unchecked")
        ArrayList<App> list = (ArrayList<App>) getArguments().get("list");
        gridView.setAdapter(new AppListAdapter(getActivity(), R.layout.item_app, list));

        return gridView;
    }
*/
}