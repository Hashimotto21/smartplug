package com.example.a2140252.smartplug.dataPackage;

/**
 * グラフに表示するデータを保持するクラス
 *
 * Created by 2140306 on 2016/12/05.
 */
public class GraphData {
    private String year = "";

    //月と消費電力を持つ
    private GraphDetailData detailData[];

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public GraphDetailData[] getDetailData() {
        return detailData;
    }

    public GraphDetailData getDetailData(int index) {
        return detailData[index];
    }

    public void setDetailData(GraphDetailData[] detailData) {
        this.detailData = detailData;
    }

    public void setDetailData(GraphDetailData detailData, int index) {
        this.detailData[index] = detailData;
    }
}
