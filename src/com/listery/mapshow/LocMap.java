package com.listery.mapshow;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.team.jingzufang.R;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class LocMap extends Activity {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	BDLocation location;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	RadioGroup mapType;
	CheckBox jiaoTongCheng;
	Button repeat;//重新定位
	TextView state;
	boolean isFirstLoc = true;// 是否首次定位
	int i=-1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loc_map);
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mMapView.getMap();
		
//		UiSettings settings=mBaiduMap.getUiSettings();
//		settings.
		
		mCurrentMode = LocationMode.NORMAL;
		mCurrentMarker = null;//标记图标样式
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		//mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
		
		mapType=(RadioGroup) findViewById(R.id.maptype);
		jiaoTongCheng=(CheckBox) findViewById(R.id.jiaotong);
		repeat=(Button) findViewById(R.id.repeat);
		state=(TextView) findViewById(R.id.state);
		mapType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.putong:
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					break;
				case R.id.weixing:
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					break;
				}
			}
		});
		jiaoTongCheng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//打开交通图层
					mBaiduMap.setTrafficEnabled(true);
				}else{
					mBaiduMap.setTrafficEnabled(false);
				}
			}
		});
		repeat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				i=mLocClient.requestLocation();
				handler.sendEmptyMessage(i);
			}
		});
		
		// 定位初始化
		mLocClient = new LocationClient(this);
		System.out.println(">>>KEY&SHA1= "+mLocClient.getAccessKey());
		System.out.println(">>>SDK_version= "+mLocClient.getVersion());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setAddrType("all");//接收所有位置信息
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	/**
	 * 获取当前地图信息
	 */
	public void getState(View view){
		sysP();
	}
	
	Handler handler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			//0：定位请求成功 1:service没有启动 2：无监听函数 6：两次请求时间太短
			switch(msg.what){
			case 0:
				Toast.makeText(LocMap.this, "定位请求成功", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(LocMap.this, "service没有启动", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(LocMap.this, "无监听函数", Toast.LENGTH_SHORT).show();
				break;
			case 6:
				Toast.makeText(LocMap.this, "两次请求时间太短", Toast.LENGTH_SHORT).show();
				break;
			}
			return true;
		}

	});
	private void sysP() {
		StringBuffer sb=new StringBuffer();
		MapStatus status=mBaiduMap.getMapStatus();
		float minZoomLevel=mBaiduMap.getMinZoomLevel();
		sb.append("最小缩放="+minZoomLevel+"\n");
		float maxZoomLevel=mBaiduMap.getMaxZoomLevel();
		sb.append("最大缩放="+maxZoomLevel+"\n");
		float zoom=status.zoom;
		sb.append("当前缩放="+zoom+"\n");
		float rotate=status.rotate;
		sb.append("当前地图旋转角度="+rotate+"\n");
		Point point=status.targetScreen;
		int x=point.x;
		int y=point.y;
		sb.append("地图操作中心点位于屏幕 x="+x+" y="+y+"\n");
		LatLng latLng=status.target;
		double lat=latLng.latitude;
		double lng=latLng.longitude;
		sb.append("地图操作中心点位于地球 经度="+lat+" 纬度="+lng+"\n");
		
		sb.append("location is null= "+(location==null)+"\n");
		if(location.hasAddr()){
			String addrStr=location.getAddrStr();
			sb.append("详细地址= "+addrStr+"\n");
			String provice=location.getProvince();
			sb.append("当前省份= "+provice+"\n");
			String city=location.getCity();
			sb.append("当前城市= "+city+"\n");
			String county=location.getDistrict();
			sb.append("当前区/县= "+county+"\n");
			String floor=location.getFloor();
			sb.append("当前楼层= "+floor+"\n");
			float direction=location.getDirection();
			sb.append("当前手机方向= "+direction+"\n");
		}
		state.setTextColor(Color.RED);
		state.setText(sb.toString());
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			System.out.println(">>>>>>>>>>onReceiveLocation");
			LocMap.this.location=location;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if(isFirstLoc){
				isFirstLoc=false;
				/**
				 * 重置地图位置
				 * LatLng 地理坐标基本数据结构
				 * MapStatusUpdate 描述地图状态将要发生的变化
				 * MapStatusUpdateFactory 生成地图状态将要发生的变化
				 * location.getLatitude()经度
				 * location.getLongitude()纬度
				 * newLatLng(LatLng latLng)设置地图中心点
				 * newLatLngZoom(LatLng latLng, float zoom)设置地图中心点以及缩放级别
				 */
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);
				mBaiduMap.animateMapStatus(u,50);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}