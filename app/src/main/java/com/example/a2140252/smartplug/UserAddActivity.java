package com.example.a2140252.smartplug;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
            EditText id = (EditText) findViewById(R.id.IDtextView);
            EditText pass = (EditText) findViewById(R.id.PASStextView);

//            if(!validate(id, pass)){
//                return;
//            }

            MyHttpClient client = new MyHttpClient("http://smartplug.php.xdomain.jp/register_user.php");
            client.setParam("user_id", id.getText().toString());
            client.setParam("password", pass.getText().toString());
            if (!client.registerUser()) {
                Toast.makeText(UserAddActivity.this, client.msg, Toast.LENGTH_LONG);
                client.removeParam("userid");
                client.removeParam("pass");
                return;
            }

        Intent intent = new Intent(getApplication(), PlugAddActivity.class); //プラグ追加Activity
        startActivity(intent);
        }
    };

    //入力項目の確認
    //IDとPASSが未入力であれば、false
    //入力されていれば、true
    private boolean validate(EditText id, EditText pass) {
        if (id.getText().toString().equals("")) {
            Toast.makeText(UserAddActivity.this, "Idを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.getText().toString().equals("")) {
            Toast.makeText(this, "Passwordを入力してください。", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
