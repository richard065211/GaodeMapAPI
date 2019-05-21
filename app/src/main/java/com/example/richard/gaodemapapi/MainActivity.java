package com.example.richard.gaodemapapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.entity.DriveMode;
import com.amap.api.track.query.entity.HistoryTrack;
import com.amap.api.track.query.entity.Track;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.DistanceResponse;
import com.amap.api.track.query.model.HistoryTrackResponse;
import com.amap.api.track.query.model.LatestPointResponse;
import com.amap.api.track.query.model.OnTrackListener;
import com.amap.api.track.query.model.ParamErrorResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.amap.api.track.query.model.QueryTrackRequest;
import com.amap.api.track.query.model.QueryTrackResponse;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AMap.OnMapClickListener {
    MapView mMapView;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    public AMapLocationClient mLocationClient = null;  //声明AMapLocationClient类对象
    public AMapLocationListener mLocationListener;  //声明定位回调监听器
    public AMapLocationClientOption mLocationOption = null; //声明AMapLocationClientOption对象
    Button button1,button2,button3,button4,button5; //开始定位起点
    public static double latitud; //经度
    public static double longitude; //纬度
    static LatLng latLng;//通过小蓝点获取到的坐标

    long serviceId=16164;
    String terminalName="richard1";
    long terminalId=54609527;
    long trackId=0;
    AMapTrackClient aMapTrackClient;
    OnTrackLifecycleListener onTrackLifecycleListener;
    TrackParam trackParam=new TrackParam(serviceId,terminalId);

    QueryTrackRequest queryTrackRequest; //查询终端轨迹点信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        button1=(Button)findViewById(R.id.mark);
        button2=(Button)findViewById(R.id.route);
        button3=(Button)findViewById(R.id.route1);
        button4=(Button)findViewById(R.id.routestop);
        button5=(Button)findViewById(R.id.search_route1);
        initMap(); //初始化地图
        initMyLocation(); // 初始化定位
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, AMapNavi1.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图
                aMap.reloadMap();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMapTrackClient=new AMapTrackClient(getApplicationContext());
                 initAMapTrackClient();
                 getTerminal();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMapTrackClient.stopGather(onTrackLifecycleListener);//停止采集
                aMapTrackClient.stopTrack(trackParam,onTrackLifecycleListener); //停止轨迹跟踪
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMapTrackClient=new AMapTrackClient(getApplicationContext());
                initQueryTrack();
            }
        });
    }

    /**
     * 查询轨迹点信息
     */
    private void initQueryTrack() {
         queryTrackRequest = new QueryTrackRequest(
                serviceId,
                terminalId,
                -1,	// 轨迹id，传-1表示查询所有轨迹
                System.currentTimeMillis() - 12 * 60 * 60 * 1000,
                System.currentTimeMillis(),
                0,      // 不启用去噪
                0,   // 绑路
                0,      // 不进行精度过滤
                DriveMode.DRIVING,  // 当前仅支持驾车模式
                 0,     // 距离补偿
                5000,   // 距离补偿，只有超过5km的点才启用距离补偿
                1,  // 结果应该包含轨迹点信息
                1,  // 返回第1页数据，由于未指定轨迹，分页将失效
                100    // 一页不超过100条
        );
        aMapTrackClient.queryTerminalTrack(queryTrackRequest, new OnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
            }

            @Override
            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {

            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {

            }

            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {

            }

            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                if(historyTrackResponse.isSuccess()){
                    HistoryTrack historyTrack=historyTrackResponse.getHistoryTrack();
                    Toast.makeText(getApplicationContext(),historyTrack.toString(),Toast.LENGTH_LONG);
                }else{
                    Toast.makeText(getApplicationContext(),"查询失败",Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onQueryTrackCallback(QueryTrackResponse queryTrackResponse) {
                if (queryTrackResponse.isSuccess()) {
                    List<Track> trackList=queryTrackResponse.getTracks();

                    String string1= String.valueOf(queryTrackResponse.getTracks());
                    // 查询成功，tracks包含所有轨迹及相关轨迹点信息
                    Toast.makeText(MainActivity.this,string1, Toast.LENGTH_SHORT).show();
                    System.out.println(string1);

                    System.out.println(trackList);
                } else {
                    // 查询失败
                    Toast.makeText(MainActivity.this,queryTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {

            }

            @Override
            public void onParamErrorCallback(ParamErrorResponse paramErrorResponse) {

            }
        });
    }

    /**
     * 初始化AMapTrackClient实例
     */
    private void initAMapTrackClient() {
        aMapTrackClient.setInterval(5, 30);//采集周期为5秒上传周期为30秒
        aMapTrackClient.setCacheSize(20); //设置本地缓存大小20M
        onTrackLifecycleListener=new OnTrackLifecycleListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {

            }
            /**
             *判断定位是否采集成功，并开始监听
             * @param status
             * @param msg
             */
            @Override
            public void onStartGatherCallback(int status, String msg) {
                if (status == ErrorCode.TrackListen.START_GATHER_SUCEE ||
                        status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                    Toast.makeText(MainActivity.this, "定位采集开启成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "定位采集启动异常，" + msg, Toast.LENGTH_SHORT).show();
                }
            }
            /**
             *
             * @param status
             * @param msg
             */
            @Override
            public void onStartTrackCallback(int status, String msg) {
                if (status == ErrorCode.TrackListen.START_TRACK_SUCEE ||
                        status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK ||
                        status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED){
                    aMapTrackClient.startGather(this); //开启轨迹上报
                }else{
                    //服务启动异常打印错误信息
                    Toast.makeText(MainActivity.this, "轨迹上报服务服务启动异常，" + msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStopGatherCallback(int i, String s) {

            }
            @Override
            public void onStopTrackCallback(int i, String s) {
            }
        };
    }

    /**
     *查询终端的一些信息，若存在则直接开启轨迹上报，若不存在则创建之后再开启轨迹上报
     */
    private void getTerminal() {
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(serviceId,terminalName), new OnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if(queryTerminalResponse.isSuccess()){
                    if(queryTerminalResponse.getTid()<=0){
                        //Terminal不存在需要创建
                        aMapTrackClient.addTerminal(new AddTerminalRequest(terminalName,serviceId), new OnTrackListener() {
                            @Override
                            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {

                            }

                            @Override
                            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                                if (addTerminalResponse.isSuccess()) {
                                    if(trackId==0) {
                                        addTrack();//上报到制定轨迹点
                                    }
                                    // 创建完成，开启猎鹰服务
                                    long terminalId = addTerminalResponse.getTid();
                                    TrackParam trackParam1=new TrackParam(serviceId,terminalId);
                                    trackParam1.setTrackId(trackId);
                                    trackParam=trackParam1;
                                    aMapTrackClient.startTrack(trackParam1, onTrackLifecycleListener);
                                }else{
                                    // 请求失败
                                    Toast.makeText(MainActivity.this, "请求失败，" + addTerminalResponse.getErrorMsg(), Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onDistanceCallback(DistanceResponse distanceResponse) {

                            }
                            @Override
                            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {

                            }
                            @Override
                            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {

                            }
                            @Override
                            public void onQueryTrackCallback(QueryTrackResponse queryTrackResponse) {

                            }
                            @Override
                            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {

                            }
                            @Override
                            public void onParamErrorCallback(ParamErrorResponse paramErrorResponse) {

                            }
                        });
                    }else{
                        if(trackId==0){
                            addTrack();
                        }
                        // terminal已经存在，直接开启猎鹰服务
                        long terminalId = queryTerminalResponse.getTid();
                        TrackParam trackParam2=new TrackParam(serviceId,terminalId);
                        trackParam=trackParam2;
                        trackParam2.setTrackId(trackId);
                        aMapTrackClient.startTrack(trackParam2, onTrackLifecycleListener);
                    }
                }
            }

            @Override
            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {

            }
            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {

            }
            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {

            }
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {

            }
            @Override
            public void onQueryTrackCallback(QueryTrackResponse queryTrackResponse) {

            }
            @Override
            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {

            }
            @Override
            public void onParamErrorCallback(ParamErrorResponse paramErrorResponse) {

            }
        });
    }

    /**
     * 开启指定轨迹上报
     */
    private void addTrack() {
        aMapTrackClient.addTrack(new AddTrackRequest(serviceId, terminalId), new OnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {

            }
            @Override
            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {

            }
            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {

            }
            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {

            }
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {

            }
            @Override
            public void onQueryTrackCallback(QueryTrackResponse queryTrackResponse) {

            }
            @Override
            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                if (addTrackResponse.isSuccess()) {
                    trackId = addTrackResponse.getTrid();
                    Toast.makeText(MainActivity.this, "轨迹ID，" + trackId, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this, "网络请求失败，" + addTrackResponse.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onParamErrorCallback(ParamErrorResponse paramErrorResponse) {

            }
        });
    }

    /*
    *初始化地图
    * */
    private void initMap() {
        if(aMap==null) {
            aMap = mMapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();  //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        myLocationStyle.interval(10000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle));
        aMap.setMyLocationStyle(myLocationStyle);  //设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        // aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图

        aMap.setOnMapClickListener(this);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
    }

    /*
    * 初始化定位到当前位置
    * */
    private void initMyLocation() {
        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getLocationType(); //获取定位来源
                latitud=aMapLocation.getLatitude();
                longitude=aMapLocation.getLongitude();
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy); //设置定位模式为高精度模式
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if(mLocationClient!=null){
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }


    /**
     * 点击地图事件，可以通过参数获取到蓝标所在位置的坐标
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        //点击地图后清理图层插上图标，在将其移动到中心位置
        aMap.clear();
        MarkerOptions otMarkerOptions = new MarkerOptions();
        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)); //设置小蓝点的样式
        otMarkerOptions.position(latLng);
        aMap.addMarker(otMarkerOptions);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        StringBuffer buffer = new StringBuffer();
        buffer.append("经度"+latLng.latitude+"纬度"+latLng.longitude+"定位来源"
        );
        this.latLng=latLng;
        Toast.makeText(getApplicationContext(),buffer.toString(),Toast.LENGTH_SHORT).show(); //每点击一个小蓝点将会进行一次打印位置信息
    }

    /**
     * 获取到小蓝点坐标
     * @return latLng
     */
    public static LatLng getLatLng(){
        return latLng;
    }
}