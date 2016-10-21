package com.example.a2140252.smartplug;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by 2140252 on 2016/10/05.
 */
public class UserAddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useradd_layout);

        findViewById(R.id.Nextbutton).setOnClickListener(plugaddbutton); //次へボタン押下


    }

    View.OnClickListener plugaddbutton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplication(), PlugAddActivity.class); //プラグ追加Activity
            startActivity(intent);
        }
    };
}
