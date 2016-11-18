package com.example.a2140252.smartplug;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class PlugDeleteActivity extends AppCompatActivity {
    EditText plugid ;//ID
    EditText plugname;//コンセント名

    ArrayList<PageItem> list;//HomeActivity()から受け取るList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugdelete_layout);


        //ID
        plugid =(EditText) findViewById(R.id.IDtextView);

        //コンセント名
        plugname=(EditText) findViewById(R.id.PASStextView);

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

                if(list.get(i).getTitle().equals(name)) {//id name　ともに存在する

                    Intent intent3 = new Intent();
                    intent3.putExtra("delete",i);//削除対象のTabの情報を持つクラスの添え字を渡す

                    setResult(RESULT_OK, intent3);//処理成功

                    finish();//戻るボタンで戻らせない
                    return;

                }
            }
        }
        Toast.makeText(this,"存在しないコンセントです",Toast.LENGTH_LONG).show();

    }







    //ハンバーガーメニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return  true;

    }

  
}
