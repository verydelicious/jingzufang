package com.xiangmu.tenement;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.jingzufang.activitys.SignUpAty;
import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;
import com.team.jingzufang.FaBuActivity;
import com.team.jingzufang.MyDialog;
import com.team.jingzufang.R;
import com.team.jingzufang.ShaiXuanActivity;
import com.team.jingzufang.ZhuFangContentActivity;
import com.xiangmu.tenement.LoadListView.ILoadListener;

//首页碎片，listview加载房源信息，两个按钮：要出租和要租房。
public class ShouYeFragment extends Fragment implements OnClickListener,
		OnItemClickListener, ILoadListener {
	private ImageView imageView;
	private Button chuzu, zufang;
	private TextView shuaxinziti;
	private ProgressBar jiazaixianshi;
	private LoadListView listView;
	private View view;
	private TenementAdapter adapter;
	private static ArrayList<TenementBean> list1;

	LayoutInflater inflater;

	private int error;
	private String msgs;

	private static final int DATA_OK = 0x11;
	private static final int ERROR = 0x12;

	private boolean hasData = true;// 标记是否有数据
	private int sta;// 用户标记
	
	HttpParams params = null;

	String city;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(">>>ShouYeFragment onCreate");
		if (getArguments() != null && getArguments().containsKey("data") && getArguments().containsKey("city")) {
			list1 = getArguments().getParcelableArrayList("data");
			city=getArguments().getString("city");
			params = new HttpParams();
			params.put("city", city);
			if (list1 != null) {
				System.out.println(">>>list1!=null!接收到TenementViewPager的数据"
						+ "   list1.size=" + list1.size());
			} else {
				hasData = false;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		System.out.println(">>>ShouYeFragment onCreateView.............");
		if (hasData) {
			view = inflater.inflate(R.layout.shouye_fragment, null);
			listView = (LoadListView) view.findViewById(R.id.listview);
			listView.setOnItemClickListener(this);

			chuzu = (Button) view.findViewById(R.id.iwchuzu);
			zufang = (Button) view.findViewById(R.id.iwzufang);
			chuzu.setOnClickListener(this);
			zufang.setOnClickListener(this);

			showListView();
		} else {
			view = inflater.inflate(R.layout.not_data, null);
			imageView = (ImageView) view.findViewById(R.id.shuaxintupian);

			shuaxinziti = (TextView) view.findViewById(R.id.shuaxinziti);
			jiazaixianshi = (ProgressBar) view.findViewById(R.id.jiazaixianshi);

			chuzu = (Button) view.findViewById(R.id.nochuzu);
			zufang = (Button) view.findViewById(R.id.nozufang);

			chuzu.setOnClickListener(this);
			zufang.setOnClickListener(this);
			imageView.setOnClickListener(this);
		}
		return view;
	}

	private void showListView() {
		if (adapter == null) {
			System.out.println(">>>>>>>>>>>>>>>adapter == null");
			listView.setInterface(this);
			if (list1 == null) {
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			}
			adapter = new TenementAdapter(this.getActivity(), list1);
			listView.setAdapter(adapter);
		} else {
			System.out.println(">>>>>>>>>>>>>>adapter != null ");
			// 重新刷新界面
			adapter.notifyDataSetChanged();
			// adapter.onDateChange(list);
		}

	}

	public void getLoadData() {
		if (params == null) {
			msgs = "没有获取到位置信息，请重试";
			handler.sendEmptyMessage(ERROR);
			return;
		}
		HttpClientUtils.getInstance().post(ServerAdress.ServerPath,
				"query_data_fw_info.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>>jsonObject=" + jsonObject);
						error = jsonObject.optInt("error");
						if (error == 0) {
							System.out.println(">>>>>>>>>error==0");
							JSONArray jsonArray = jsonObject
									.optJSONArray("list");
							zhuZhuangData(jsonArray);
						} else {
							msgs = jsonObject.optString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					}
				});
	}

	private void zhuZhuangData(JSONArray jsonArray) {
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
				System.out.println(">>>>>>beans.add(bean)" + i);
				list1.add(bean);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(DATA_OK);
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case ERROR:
				Toast.makeText(getActivity(), msgs, Toast.LENGTH_SHORT).show();
				break;
			case DATA_OK:
				showListView();
				// 通知listview加载完毕
				listView.loadComplete();
				break;
			}
			return true;
		}
	});

	@Override
	public void onLoad() {
		System.out
				.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>onLoad继续加载！！！<<<<<<<<<<<<<<<<<<<<<");

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				getLoadData();
			}
			// 设置延时，间隔2秒钟以后执行Runnable()里面的功能。
		}, 2000);
	}

	// 点击listview单个选项时，跳转到相对应房源更加详细介绍的页面
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// System.out.println(list1.get(position));
		Intent intent = new Intent(getActivity(), ZhuFangContentActivity.class);
		// 传值房屋id。
		intent.putExtra("fw_id", list1.get(position).fw_id);
		startActivity(intent);
	}

	private String loc = null; // 保存定位信息
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	// 出租，租房页面跳转
	public void onClick(View v) {
		get_status();
		switch (v.getId()) {
		case R.id.iwchuzu:
			if (sta == -1) {
				MyDialog myDialog = new MyDialog(getActivity());
				myDialog.setIcon(R.drawable.message_fail);
				myDialog.setTitle("亲...您是还没登陆还是没有注册呢？");
				myDialog.setMessage("现在去注册--》");
				myDialog.setPositiveButton("GO",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								jumpDZ();
							}
						});
				myDialog.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				myDialog.show();
			} else {
				jumpchuzu();
			}
			break;
		case R.id.iwzufang:
			jumpzufang();
			break;
		case R.id.nochuzu:
			Toast.makeText(getActivity(), "亲...您的网速不给力啊。要不在刷新一下呗。。",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.nozufang:
			Toast.makeText(getActivity(), "亲...您的网速不给力啊。要不在刷新一下呗。。",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.shuaxintupian:

			
//			shuaxinziti.setVisibility(View.GONE);
//			jiazaixianshi.setVisibility(View.VISIBLE);
//
//			mLocationClient = new LocationClient(getActivity()
//					.getApplicationContext()); // 声明LocationClient类
//			mLocationClient.registerLocationListener(myListener); // 注册监听函数
//			LocationClientOption option = new LocationClientOption();
//			option.setOpenGps(true);// 打开GPS
//			option.setAddrType("all");// 返回的定位结果包含地址信息
//			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//			option.setScanSpan(6000);// 设置每隔6秒请求一次当前位置
//			mLocationClient.setLocOption(option);// 使用设置
//			mLocationClient.start();// 开启定位SDK
//			mLocationClient.requestLocation();// 开始请求位置
			 Intent intent = new Intent(getActivity(), StartView.class);
			 startActivity(intent);
			 System.out.println("我重新去 加载数据了");
			 getActivity().finish();
			 System.exit(0);
			break;

		}

	}

	public class MyLocationListener implements BDLocationListener {
		public synchronized void onReceiveLocation(BDLocation location) {
			if (location != null) {
				StringBuffer sb = new StringBuffer(128);// 接受服务返回的缓冲区
				sb.append(location.getCity());// 获得城市
				// if(again)
				// loc1=sb.toString().trim();
				// else
				loc = sb.toString().trim();
				System.out.println("当前定位到的城市为：" + loc);
				if (loc.equals("null")) {
					System.out.println("没有定位到你当前的城市，请打开你的GPS和网络");
					Toast.makeText(getActivity(), "没有定位到你当前的城市，请打开你的GPS和网络",
							Toast.LENGTH_SHORT).show();
				} else if (!loc.equals("null")) {
					Toast.makeText(getActivity(), "你当前所在城市为：" + loc,
							Toast.LENGTH_SHORT).show();
					System.out.println("你当前所在城市为：" + loc + "哟");
					getReloadData();
				}
			} else {
				msgs = "无法定位,请打开你的GPS和网络";
				handler.sendEmptyMessage(ERROR);

			}
		}

		public void onReceivePoi(BDLocation arg0) {

		}

	}

	public void getReloadData() {
		System.out.println(">>>>>>>>>>>>>>>>我正在重新刷新数据<<<<<<<<<<<<<<<<<");
		params = new HttpParams();
		params.put("city", loc);
		HttpClientUtils.getInstance().post(ServerAdress.ServerPath,
				"query_data_fw_info.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>>jsonObject=" + jsonObject);
						error = jsonObject.optInt("error");
						if (error == 0) {
							System.out.println(">>>>>>>>>error==0");
							JSONArray jsonArray = jsonObject
									.optJSONArray("list");
							zhuZhuangData(jsonArray);
						} else {
							msgs = jsonObject.optString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					}
				});
	}

	// 获取当前用户状态。是否登陆注册
	public void get_status() {
		SharedPreferences sp = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
		sta = sp.getInt("userId", -1);
	}

	// 跳转到注册页面
	public void jumpDZ() {
		Intent intent = new Intent(this.getActivity(), SignUpAty.class);
		startActivity(intent);
	}

	// 跳转到出租的界面
	public void jumpchuzu() {
		SharedPreferences sp=getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
		boolean flag=(Integer.parseInt(sp.getString("type", ""))==0)?true:false;
		if(flag){
			Intent intent = new Intent(this.getActivity(), FaBuActivity.class);
			//传入用户id
			intent.putExtra("userId", sta);
			startActivity(intent);
		}else{
			msgs="对不起，您不是房东";
			handler.sendEmptyMessage(ERROR);
		}
	}

	// 跳转到租房的界面
	public void jumpzufang() {
		if(city!=null){
			Intent intent = new Intent(this.getActivity(), ShaiXuanActivity.class);
			intent.putExtra("city", city);
			startActivity(intent);
		}else{
			msgs="没有位置信息，请重试";
			handler.sendEmptyMessage(ERROR);
		}
	}

	public void onActivityGreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

}
