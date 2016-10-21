package com.example.a2140252.smartplug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by 2140306 on 2016/10/19.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        findViewById(R.id.Nextbutton).setOnClickListener(login_click); //ログインボタン押下
    }

    View.OnClickListener login_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplication(), HomeActivity.class); //Next Activity
            startActivity(intent);
            LoginActivity.this.finish();
        }
    };
}
