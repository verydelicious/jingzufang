package com.xiangmu.tenement;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;
import com.team.jingzufang.R;

public class StartView extends Activity {
	private Intent intent;
	private String URL = ServerAdress.ServerPath;

	private static final int JUMP = 0x99;// 跳转
	private static final int DATA_OK = 0x02;// 信息组装完成
	private static boolean loadNotDone = true;// 加载未完成
	ArrayList<TenementBean> beans = new ArrayList<TenementBean>();

	static boolean canStart = true;

	int error;
	String msgs;
	// private String MapAdress = null;

	private String loc = null; // 保存定位信息
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(">>>>>>>>StartView onCreate");
		setContentView(R.layout.jinrujiemian);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(6000);// 设置每隔6秒请求一次当前位置
		mLocationClient.setLocOption(option);// 使用设置
		mLocationClient.start();// 开启定位SDK
		mLocationClient.requestLocation();// 开始请求位置
		//呵呵哒！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
	}

	public class MyLocationListener implements BDLocationListener {
		public synchronized void onReceiveLocation(BDLocation location) {
			if (location != null) {
				StringBuffer sb = new StringBuffer(128);// 接受服务返回的缓冲区
				sb.append(location.getCity());// 获得城市
				loc = sb.toString().trim();
				System.out.println("当前定位到的城市为：" + loc);
				if (loc.equals("null")) {
					System.out.println("没有定位到你当前的城市，请打开你的GPS和网络");
					Toast.makeText(StartView.this, "没有定位到你当前的城市，请打开你的GPS和网络", Toast.LENGTH_SHORT).show();
					new MyThread().start();
				} else if (!loc.equals("null")) {
					Toast.makeText(StartView.this, "你当前所在城市为：" + loc, Toast.LENGTH_SHORT).show();
					System.out.println("你当前所在城市为：" + loc + "哟");
					new Thread(){
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							getFWData();
						}
					}.start();
					new MyThread().start();
				}
			} else {
				Toast.makeText(StartView.this, "无法定位,请打开你的GPS和网络", Toast.LENGTH_SHORT).show();
				new MyThread().start();

			}
		}

		public void onReceivePoi(BDLocation arg0) {

		}

	}

	/**
	 * 停止，减少资源消耗
	 */
	public void stopListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();// 关闭定位SDK
			mLocationClient = null;
		}
	}

	private void getFWData() {
		System.out.println("》》》》》》》》》》》》》请求数据《《《《《《《《《《《《《");
		HttpParams params = new HttpParams();
		params.put("city", loc);
		HttpClientUtils.getInstance().post(URL, "query_data_fw_info.php", params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>>jsonObject=" + jsonObject);
						error = jsonObject.optInt("error");
						if (error == 0) {
							System.out.println(">>>>>>>>>error==0");
							JSONArray jsonArray = jsonObject.optJSONArray("list");
							zhuZhuangData(jsonArray);
						} else {
							msgs = jsonObject.optString("msg");
							Toast.makeText(StartView.this, msgs, Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void zhuZhuangData(JSONArray jsonArray) {
		System.out.println("》》》》》》》》》》》》》组装数据《《《《《《《《《《《《《");
		beans.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				TenementBean bean = new TenementBean();
				bean.fw_id = jsonObject.getInt("fw_id");
				bean.fbr_id = jsonObject.getInt("fbr_id");
				bean.fwdz = jsonObject.getString("fwdz");
				bean.xqm = jsonObject.getString("xqm");
				bean.hx = jsonObject.getString("hx");
				bean.czlx = jsonObject.getString("czlx");
				bean.czjg = jsonObject.getDouble("czjg");
				bean.photos_dir = jsonObject.getString("photos_dir");
				bean.title = bean.fwdz + " " + bean.xqm + " " + bean.czlx;
				beans.add(bean);
				System.out.println(">>>>>>beans.add(bean)" + i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Message message = new Message();
		message.what = DATA_OK;
		message.obj = beans;
		handler.sendMessage(message);
	}

	Handler handler = new Handler(new Handler.Callback() {

		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case DATA_OK:
				if (msg.obj != null) {
					@SuppressWarnings("unchecked")
					ArrayList<TenementBean> beans1 = (ArrayList<TenementBean>) msg.obj;
					intent = new Intent(StartView.this, TenementViewPager.class);
					intent.putParcelableArrayListExtra("data", beans1);
					intent.putExtra("city", loc);
					if (canStart) {
						loadNotDone = false;
						startActivity(intent);
						System.out
								.println(">>>>>>>>>>>>>>>>>>数据发送成功<<<<<<<<<<<<<<<<");
						finish();
					}
				}
				break;
			case JUMP:
				jump();
				System.out.println(">>>>>>>>>>>>>>>>>>数据发送失败<<<<<<<<<<<<<<<<");
				finish();
				break;
			}
			return true;
		}
	});

	// 监听用户是否在当前页面按下了返回键
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if(keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
	// flag = false;
	// }
	// finish();
	// return true;
	// }

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println(">>....>StartView  onPause");
		canStart = false;
		loadNotDone = false;
		finish();
		System.exit(0);// 是虚拟机停止运行并退出程序
	}

	@Override
	public void onDestroy() {
		stopListener();// 停止监听
		super.onDestroy();
	}

	public void jump() {
		intent = new Intent(this, TenementViewPager.class);
		startActivity(intent);
	}

	class MyThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
				if (loadNotDone) {
					handler.sendEmptyMessage(JUMP);
					canStart = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
