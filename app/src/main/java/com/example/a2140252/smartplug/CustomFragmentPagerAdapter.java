package com.example.a2140252.smartplug;

/**
 * Created by 2140104 on 2016/10/21.
 */

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    /** {@link PageItem} のリスト. */
    ArrayList<PageItem> mList;

    int j = 0;//getListのカウント

    public boolean fList = true;//ArrayList 初期化前ならTRUE


    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        Log.d("CustomF","コンストラクタ 後--------");
    }


    @Override
    public Fragment getItem(int position) {

        String id = mList.get(position).getId();

        TabFragment tabFragment = new TabFragment();

        Bundle bundle = new Bundle();
        bundle.putString("id", id);//idを渡す  Log記録のために
        bundle.putInt("position", position);

        Log.d("CustomF","getItem() :"+(position+1)+"つ目/id"+id);
        tabFragment.setArguments(bundle);

            return tabFragment;
        }

    //コンセント削除↓--------------------------------------

    @Override
    public int getItemPosition(Object object) {
       // return super.getItemPosition(object);
        Log.d("CustomF","getItemPosition()☆");
        return POSITION_NONE;//ページ位置のキャッシュを無効にする
    }

    public void destroyAllItem(ViewPager pager) {
        for (int i = 0; i < getCount() - 1; i++) {
            Log.d("CustomF","destroyAllItem() ☆"+i);
            try {
                Object obj = this.instantiateItem(pager, i);
                if (obj != null)
                    destroyItem(pager, i, obj);
            } catch (Exception e) {
            }
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if (position <= getCount()) {
            Log.d("CustomF","destroyItem() ☆ position:" +position+" object :"+object.toString());
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();

            trans.remove((Fragment) object);//Fragmentを削除
            trans.commitAllowingStateLoss();//反映

        }
    }

    public void Delete(int a){//ArrayListから削除
        mList.remove(a);
    }
    //コンセント削除↑---------------------------------------

    //各Fragmentのタイトルを返す
    @Override
    public CharSequence getPageTitle(int position) {

        Log.d("CustomF", "getPageTitle()現在のタブ title："+mList.get(position).getTitle());
        return mList.get(position).getTitle();

    }

    //viewPagerにセットするFragmentの総数
    @Override
    public int getCount() {
        return mList.size();
    }

    //タブを追加（Listに追加）
    public void addItem(PageItem item) {//getItemより先に実行
        mList.add(item);
    }

    //Listを返す
    public ArrayList<PageItem> getList(){

        j++;
        if(fList) {
            mList = new ArrayList<PageItem>();//HomeActivity()のonCreate()で初めに呼ばれる
            fList = false;
        }
        Log.d("CustomF","getList() "+j+"回目の呼び出し");
        return mList;
    }



}