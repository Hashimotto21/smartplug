package com.example.a2140252.smartplug;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by 鈴木 on 2016/10/03.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 3000); //スプラッシュ時間
    }

    class splashHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getApplication(), TopActivity.class); //TOP画面へ
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }

}


