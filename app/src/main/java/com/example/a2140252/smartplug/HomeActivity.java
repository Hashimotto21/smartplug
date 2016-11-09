package com.example.a2140252.smartplug;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.CombinedChart;

import java.util.ArrayList;

/**
 * Created by 2140252 on 2016/10/17.
 */
public class HomeActivity extends AppCompatActivity {
    private static final int ADDMORE_CODE = 1;
    private static final int DELETE_CODE = 2;
    //private Toolbar toolbar;
    CustomFragmentPagerAdapter adapter;

    ArrayList<PageItem> list;// = adapter.getList();

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        //ViewPager
        pager = (ViewPager) findViewById(R.id.pager);

        // PagerTitleStrip のカスタマイズ   ？？----
        PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.strip);
        strip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        strip.setTextColor(0xff9acd32);
        //strip.setTextColor(0xff0000);
        strip.setTextSpacing(50);
        strip.setNonPrimaryAlpha(0.3f);
        strip.setDrawFullUnderline(true);//下線表示
        strip.setTabIndicatorColor(0xff9acd32);//インディケーターと下線の色
        //-----------------

        //初期表示のタブ 1,2-----
        // タブ 1
        PageItem plug1 = new PageItem("1",111);

        list = adapter.getList();
        Log.d("Home","onCreate() getList();後");
        adapter.addItem(plug1);
        Log.d("Home","onCreate() adapter.addItem(plug1);後");
        //タブ 2
        PageItem plug2 = new PageItem("2",222);

        adapter.addItem(plug2);
//////////////
        /*
        Intent intent = new Intent(this, UserConfigActivity.class);
        ArrayList<a> lista = new  ArrayList<a>();
        lista.add(new a());
        Bundle bundle = new Bundle();
        // bundle.putExtra(list2);
        bundle.putSerializable("a",lista);
        */
        //
        /*
        List<PageItem> list2 = new  ArrayList<PageItem>();
        List<String> list3 = new  ArrayList<String>();
        list3.add("33");
        PageItem p1 = new PageItem();
        p1.title ="11";
        list2.add(p1);
        Bundle bundle = new Bundle();
       // bundle.putExtra(list2);
       // startActivityForResult(intent, list2);
       // intent.putExtra("2",list2);
        intent.putExtra("111",list3);
        bundle.putSerializable("2",list2);
        intent.putParcelableArrayListExtra("key", list2);
        intent.putExtra("bundle",bundle);
        ArrayList<Group> ag;
        ag.add(new PageItem("2",333));
        Intent .putExtra("ag", ag);
        startActivity(intent);*/
////////////////////////

        Log.d("Home", "onCreate() adapter.addItem(plug2)後");
        pager.setAdapter(adapter);
        //------------------------------
        Log.d("Home", "●onCreate() pager.setAdapter(adapter);後 ");

        //タブ切り替えた-------------------------------
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            //findViewById(R.id.pager).addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (ViewPager.SCROLL_STATE_IDLE == state) {
                    int a = pager.getCurrentItem();

                    Log.d("ViewPager", "現在選択中のタブ" + (a + 1) + "つ目 --★-----");//ViewPagerの位置が落ち着いた状態が0(=SCROLL_STATE_IDLE)

                }
            }
        });
    }

    //HomeActivityのインスタンスが既に存在している場合
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {//startActivityForResult(intent,1);の１か？

            case (ADDMORE_CODE)://PlugAddMoreActivity()から戻ってきた
                if (resultCode == RESULT_OK) {
                    //「コンセントを追加する」ボタンを押して戻ってきたときの処理

                    adapter.addItem((PageItem) data.getSerializableExtra("newPlug"));
                    pager.setAdapter(adapter);
                    Log.d("onActivityResult", "タブ追加後★RESULT_OK");
                } else if (resultCode == RESULT_CANCELED) {
                    //戻る▽ボタンを押して戻ってきたときの処理
                    //なにもしない
                    Log.d("onActivityResult", "タブ追加後★RESULT_CANCELED");

                }
                break;
            case(DELETE_CODE)://PlugDeleteActivity()から戻ってきた
                if (resultCode == RESULT_OK) {
                    //「コンセントを削除する」ボタンを押して戻ってきたときの処理

                    Log.d("onActivityResult", "タブ削除後★RESULT_OK");
                } else if (resultCode == RESULT_CANCELED) {
                    //戻る▽ボタンを押して戻ってきたときの処理

                    Log.d("onActivityResult", "タブ削除後★RESULT_CANCELED");

                }
                break;
            default:
                break;
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
