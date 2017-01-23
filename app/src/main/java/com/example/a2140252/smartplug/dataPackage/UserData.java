package com.example.a2140252.smartplug.dataPackage;

import android.app.Application;

import java.io.Serializable;

/**
 * UserIdの保持
 * UserIdに紐づくPlug情報を保持
 *
 * Created by 2140306 on 2016/12/05.
 */
public class UserData extends Application{
    private String userId = "";

    private PlugData plugData[];

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PlugData[] getPlugData() {
        return plugData;
    }

    public PlugData getPlugData(int index) {
        return plugData[index];
    }

    public void setPlugData(PlugData[] plugData) {
        this.plugData = plugData;
    }

    public void setPlugData(PlugData plugData, int index) {
        this.plugData[index] = plugData;
    }

    public int findPlugData(String plugId) {
        int plugIndex = 0;
        for(int i = 0; i < plugData.length; i++) {
            if(plugId.equals(plugData[i].getPlugId())) {
                plugIndex = i;
            }
        }

        return plugIndex;
    }
}
