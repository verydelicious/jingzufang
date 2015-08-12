package com.listery.mapshow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.team.jingzufang.R;

public class MapShow extends FragmentActivity implements OnGetGeoCoderResultListener{

	Intent intent;
	String fwdz;
	String xqm;
	
	MapView mMapView;
	BaiduMap mBaiduMap;
	GeoCoder mSearch;
	
	RadioGroup mapType;
	CheckBox jiaoTongCheng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_map);
		if(getIntent()!=null&&getIntent().hasExtra("fwdz")&&getIntent().hasExtra("xqm")){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getintent");
			intent=getIntent();
			fwdz=intent.getStringExtra("fwdz");
			xqm=intent.getStringExtra("xqm");
			System.out.println(">>>>fwdz= "+fwdz);
			System.out.println(">>>>xqm= "+xqm);
		}else{
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>intent == null");
		}
		
		//初始化地图
		mMapView=(MapView) findViewById(R.id.show_map);
		mBaiduMap=mMapView.getMap();
		
		mapType=(RadioGroup) findViewById(R.id.maptype);
		jiaoTongCheng=(CheckBox) findViewById(R.id.jiaotong);
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
		
		//初始化收缩模块,注册监听
		mSearch=GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		search();
	}

	private void search() {
		if(fwdz!=null&&xqm!=null&&!fwdz.equals("")&&!xqm.equals("")){
			mSearch.geocode(new GeoCodeOption().city(fwdz.trim()).address(xqm.trim()));
		}
	}

	/**
	 * 通过地址搜索返回
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		mBaiduMap.clear();
		//设置覆盖图层选择器
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
		//地图变化描述
		MapStatusUpdate statusUpdate=MapStatusUpdateFactory.newLatLngZoom(result.getLocation(), 17);
		//设置变化动画并设置动画时间
		mBaiduMap.animateMapStatus(statusUpdate, 100);
		String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
	}

	/**
	 * 通过编码搜索返回
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		
	}
	
}
