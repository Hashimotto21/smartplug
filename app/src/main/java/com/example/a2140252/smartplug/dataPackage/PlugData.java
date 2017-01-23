package com.example.a2140252.smartplug.dataPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * plug情報を保持
 *
 * Created by 2140306 on 2016/12/05.
 */
public class PlugData {
    private String plugId = "";
    private String plugName = "";
    //電源のOn/Offフラグ
    private boolean switchFlg = false;
    //タイマーの開始時刻、終了時刻
    private String ontime = null, offtime = null;
    //タイマーを使用するか否かのフラグ
    private boolean time_flg = false;
    //設定ボタンのON/OFF　を表す配列が格納される
    //public List<Boolean[]> cList =new ArrayList();
    private boolean configArray[] = {false,false,false,false,false,false,false,false,false};

    private GraphData graphData[];

    public String getPlugId() {
        return plugId;
    }

    public void setPlugId(String plugId) {
        this.plugId = plugId;
    }

    public String getPlugName() {
        return plugName;
    }

    public void setPlugName(String plugName) {
        this.plugName = plugName;
    }

    public boolean isSwitchFlg() {
        return switchFlg;
    }

    public void setSwitchFlg(boolean switchFlg) {
        this.switchFlg = switchFlg;
    }

    public String getOntime() {
        return ontime;
    }

    public void setOntime(String ontime) {
        this.ontime = ontime;
    }

    public String getOfftime() {
        return offtime;
    }

    public void setOfftime(String offtime) {
        this.offtime = offtime;
    }

    public boolean isTime_flg() {
        return time_flg;
    }

    public void setTime_flg(boolean time_flg) {
        this.time_flg = time_flg;
    }

    public boolean[] getConfigArray() {
        return configArray;
    }

    public boolean getConfigArray(int index) {
        return configArray[index];
    }

    public void setConfigArray(boolean[] configArray) {
        this.configArray = configArray;
    }

    public void setConfigArray(boolean config, int index) {
        this.configArray[index] = config;
    }

    public GraphData[] getGraphData() {
        return graphData;
    }

    public GraphData getGraphData(int index) {
        return graphData[index];
    }

    public void setGraphData(GraphData[] graphData) {
        this.graphData = graphData;
    }

    public void setGraphData(GraphData graphData, int index) {
        this.graphData[index] = graphData;
    }
}
