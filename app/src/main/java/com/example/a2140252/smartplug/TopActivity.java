package com.example.a2140252.smartplug;

/**
 * Created by 2140252 on 2016/10/03.
 */
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TopActivity extends AppCompatActivity {

    //新規登録ボタン処理
    View.OnClickListener newtouroku_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplication(), UserAddActivity.class); //Next Activity
            startActivity(intent);
            TopActivity.this.finish();
        }
    };

    View.OnClickListener login_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplication(), LoginActivity.class); //Next Activity
            startActivity(intent);
            TopActivity.this.finish();

            //デバッグ用コード
//            Intent intent = new Intent(getApplication(), HomeActivity.class); //Next Activity
//            startActivity(intent);
//            TopActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_layout);

        findViewById(R.id.newtouroku).setOnClickListener(newtouroku_click); //新規登録ボタン押下
        findViewById(R.id.loginbutton).setOnClickListener(login_click); //ログインボタン押下

    }
}