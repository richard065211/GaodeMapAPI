package com.example.richard.gaodemapapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.model.NaviLatLng;

public class Mapnavi extends AppCompatActivity {
    AMapNavi mAMapNavi;
    private AMapNaviView mAMapNaviView;
    private NaviLatLng mEndLatlng ;//自定义的起点
    private NaviLatLng mStartLatlng;//自定义的终点
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapnavi);
        mAMapNavi = AMapNavi.getInstance(getApplicationContext()); //获取AMapNavi实例
        mAMapNavi.addAMapNaviListener((AMapNaviListener) this);//添加监听回调，用于处理算路成功
        initMapNavi();
    }
    private void initMapNavi() {
        mEndLatlng= new NaviLatLng(32.1194970000,118.9709290000);
        mStartLatlng = new NaviLatLng(32.0865760000, 118.7775490000);
    }
}
