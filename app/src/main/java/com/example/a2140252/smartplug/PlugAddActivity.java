package com.example.a2140252.smartplug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by 2140252 on 2016/10/05.
 */



public class PlugAddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugadd_layout);

        findViewById(R.id.Nextbutton).setOnClickListener(plugstartbutton);
        //SmartPlugを始めるボタン押下

    }

    View.OnClickListener plugstartbutton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplication(), HomeActivity.class); //HomeActivity
            startActivity(intent);
            PlugAddActivity.this.finish();
        }
    };

}
