package com.example.a2140252.smartplug;

/**
 * Created by 2140104 on 2016/10/21.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * FragmentPagerAdapter.
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    /** {@link PageItem} のリスト. */
    ArrayList<PageItem> mList;

    int i;
    int j = 0;
    int aaa= 10;
/*
    public List<PageItem> CustomFragmentPagerAdapter(int q){
        //super(fm);
        Log.d("CustomF","---------get mList!");
        return mList;
    }
*/
    /**
     * コンストラクタ.
     * @param fm {@link FragmentManager}
     */
    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
       // if(mList.size() > 0) {
        //    mList = new ArrayList<PageItem>();
       // }
        Log.d("CustomF","コンストラクタ 後--------");
    }





    @Override
    public Fragment getItem(int position) {//1回のみ？ ここでFragmentをつくる？？

        int id = mList.get(position).getId();

        GridViewFragment gridViewFragment = new GridViewFragment();

        Bundle bundle = new Bundle();

        bundle.putInt("id", id);//idを渡す
        aaa+=10;
        Log.d("CustomF","getItem() :"+(position+1)+"つ目/id"+id);
        gridViewFragment.setArguments(bundle);

            return gridViewFragment;
        }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    //各Fragmentのタイトルを返す
    @Override
    public CharSequence getPageTitle(int position) {

        Log.d("CustomF", "getPageTitle()現在のタブ title："+mList.get(position).getTitle());
        return mList.get(position).getTitle();

    }
/*
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        PageItem plug3 = new PageItem();
        //タブ名
        plug3.title = "A";
        //mList.add(plug3);
        //adapter.addItem(plug3);
        return super.instantiateItem(container, position);
    }
*/
    //viewPagerにセットするFragmentの総数
    @Override
    public int getCount() {
        i++;
        if(i%10==0) {
            Log.d("CustomF", "getCount()" + mList.size() + "呼び出し" + i);
        }
        return mList.size();

    }

    /**
     * アイテムを追加する.
     * @param item {@link PageItem}
     */
    public void addItem(PageItem item) {//getItemより先に実行

        mList.add(item);

    }
    public ArrayList<PageItem> getList(){
        j++;
        if(j == 1) {
            mList = new ArrayList<PageItem>();
        }
        Log.d("CustomF","getList() "+j);
        return mList;
    }

}