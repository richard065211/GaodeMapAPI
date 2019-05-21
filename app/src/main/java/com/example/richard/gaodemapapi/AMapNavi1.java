package com.example.richard.gaodemapapi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
导航功能启动页面
 */
public  class AMapNavi1 extends AppCompatActivity implements AMapNaviViewListener, AMapNaviListener, INaviInfoCallback {

    AMapNaviView mAMapNaviView;  //导航地图view
    AMapNavi mAMapNavi;   //导航实例
    Button button1;
    AmapNaviPage amapNaviPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_navi);
        button1=(Button)findViewById(R.id.begin1);
        //获取 AMapNaviView 实例
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.onCreate(savedInstanceState);
        initNaviView();
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);
    }

    private void initNaviView() {
        com.amap.api.navi.AMapNaviViewOptions options = mAMapNaviView.getViewOptions();

        mAMapNaviView.setViewOptions(options);
    }

    /**
     * calculateRideRoute
     * 参数1：起点坐标
     * 参数2：终点坐标
     */
    private void startNavi(){

        int strategy=0;
        strategy = mAMapNavi.strategyConvert(true, false, false, false, false);

        NaviLatLng endNaviLatLng=new NaviLatLng();
        NaviLatLng startNaviLatLng=new NaviLatLng();
        startNaviLatLng.setLatitude(MainActivity.latitud);
        startNaviLatLng.setLongitude(MainActivity.longitude);
        endNaviLatLng.setLongitude(MainActivity.getLatLng().longitude);
        endNaviLatLng.setLatitude(MainActivity.getLatLng().latitude);

        NaviLatLng step1=new NaviLatLng();
        step1.setLatitude(28.68063866);
        step1.setLongitude(116.02773587);

        List<NaviLatLng> pois1=new ArrayList<>();  //起始点
        pois1.add(startNaviLatLng);
        List<NaviLatLng> pois2=new ArrayList<>();  //终点
        pois2.add(endNaviLatLng);
        List<NaviLatLng> pois3=new ArrayList<>();  //途径点
        pois3.add(step1);
        mAMapNavi.calculateDriveRoute(pois1, pois2,pois3, strategy);
//        AmapNaviPage.getInstance().showRouteActivity();

    }
    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mAMapNaviView.onDestroy();
        mAMapNavi.stopGPS(); // 关闭GPS
        mAMapNavi.stopNavi();//关闭导航
        mAMapNavi.destroy();
        mAMapNaviView.onDestroy();

    }


    @Override
    public void onInitNaviFailure() {

    }

    /*
        初始化地图成功，开始计算路径
         */
    @Override
    public void onInitNaviSuccess() {
        startNavi();
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    /*
    计算路径成功返回毁掉方法
     */
    @Override
    public void onCalculateRouteSuccess(com.amap.api.navi.model.AMapCalcRouteResult aMapCalcRouteResult){
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    /**
     * 实时回调位置变更信息，获取地理位置
     * @param aMapNaviLocation
     */
    @Override
    public void onLocationChange(final AMapNaviLocation aMapNaviLocation) {
        final Handler handler=new Handler(){
            /**
             * 监听位置
             * @param msg
             */
          @Override
          public void handleMessage(Message msg){
              if(msg.what==0x1233){
                  Toast.makeText(AMapNavi1.this,aMapNaviLocation.getCoord().toString(),Toast.LENGTH_LONG).show();
              }
          }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x1233);
            }
        },0,50000);
    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    /**
     * 到达目的地
     */
    @Override
    public void onArriveDestination() {
        Toast.makeText(getApplicationContext(),"你已到达目的地",Toast.LENGTH_LONG);
        Log.e("destination","你已到达目的地");
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        mAMapNavi.stopNavi();
        mAMapNavi.stopGPS();
        mAMapNavi.destroy();
        onDestroy();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }
}
