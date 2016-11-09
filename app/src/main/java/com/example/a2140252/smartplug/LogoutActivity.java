package com.example.a2140252.smartplug;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class LogoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // 確認ダイアログの生成
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("確認");
        alertDlg.setMessage("ログアウトしますか？");
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        //ユーザIDを削除
                        SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = data.edit();
                        editor.remove("user_id");
                        editor.apply();

                        //Topへ移動
                        Intent intent = new Intent(getApplication(), TopActivity.class); //Next Activity
                        startActivity(intent);
                        LogoutActivity.this.finish();
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                        Intent intent = new Intent(getApplication(), HomeActivity.class); //Next Activity
                        startActivity(intent);
                        LogoutActivity.this.finish();
                    }
                });
        alertDlg.setCancelable(false);

        // 表示
        alertDlg.create().show();
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
