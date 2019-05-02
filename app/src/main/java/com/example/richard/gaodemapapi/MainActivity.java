package com.example.richard.gaodemapapi;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.RemoteException;
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
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.AMapCameraInfo;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.TileProjection;
import com.amap.api.maps.model.VisibleRegion;
import com.autonavi.amap.mapcore.interfaces.IProjection;

public class MainActivity extends AppCompatActivity implements AMap.OnMapClickListener {
    MapView mMapView;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    public AMapLocationClient mLocationClient = null;  //声明AMapLocationClient类对象
    public AMapLocationListener mLocationListener;  //声明定位回调监听器
    public AMapLocationClientOption mLocationOption = null; //声明AMapLocationClientOption对象
    Button button1,button2; //开始定位起点
    public static double latitud;
    public static double longitude;

    static LatLng latLng;//通过小蓝点获取到的坐标
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
        initMap(); //初始化地图
        initMyLocation(); // 初始化定位
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, AMapNavi1.class);
                startActivity(intent);
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
        aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图
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
                StringBuffer buffer = new StringBuffer();
                buffer.append("经度"+aMapLocation.getLatitude()+"纬度"+aMapLocation.getLongitude()+"定位来源"+aMapLocation.getLocationType()
                );
                Toast.makeText(getApplicationContext(),buffer.toString(),Toast.LENGTH_LONG).show();
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