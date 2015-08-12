package com.team.jingzufang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.listery.mapshow.MapShow;
import com.listery.networkbitmap.BitmapUtil;
import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;
import com.team.http.Param;

import android.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ImageView.ScaleType;

interface LxdhToActivity{
	public void lxdh_activity(String lxdh);
}

public class ZhuFangContent extends Fragment implements OnClickListener{

	Context mContext;
	
	View view;
	ViewFlipper photosFipper;
	RadioGroup radioGroup;
	
	ImageView[] photos_arr;
	RadioButton[] buttons_arr;
	
	String[] photos_file_path_for_server;

	TextView jj_title;// 简介title
	TextView zj;// 租金
	TextView hx;// 户型
	TextView mj;// 面积
	TextView lh;// 楼号
	TextView zx;// 装修
	TextView lxdh;// 联系电话
	TextView ptss;// 配套设施
	TextView dz;// 地址(街道+小区名)
	TextView zbss;// 周边设施
	
	int fw_id=-1;

	int fbr_id = -1;// 发布人id
	String fwdz;// 房屋地址
	String xqm;// 小区名
	String louhao;// 楼号
	String huxin;// 户型
	String mianji;// 面积
	String czlx;// 出租类型
	Double czjg;// 出租价格
	String lianxidianhua;// 联系电话
	String zxcd;// 装修程度
	String peitaosheshi;// 配套设施
	String photos_dir;// 房屋照片文件夹地址
	String fbsj;// 发布时间

	ImageView map_cut;// 静态地图

	RelativeLayout jumpMap;// 跳转地图

	GestureDetector detector;// ViewFlipper检测器

	static int photos_count=0;//照片数量
	
	Intent intent;

	private static final int RESULT_FOR_FW_ZIDUAN = 0x33;// 接收到服务器返回的数据

	private static final int SEND_FOR_FW_PHOTOS = 0x39;// 发送请求获取房屋照片
	
	private static final int GET_MAP_IMAGE_OK = 0x40;// 获取静态图片成功
	
	private static final int PHOTOS_FIPPER_CHANGE = 0x41;// 更改ViewFipper和RadioGroup子控件数量
	
	private static final int TO_NEXT = 0x42;// 线程显示下一张图片
	
	private static final int ERROR = 0x43;// 线程显示下一张图片
	
	JSONArray photos_jsonArray;//照片文件夹
	
	byte[] picByte;

	int error = -1;
	String msgs = "";
	
	int map_width;
	int map_height;
	
	private static int count=0;
	
	private static boolean flag=false;
//	private static boolean thread_flag=true;
//	private static boolean thread_run_flag=false;

	Bundle bundle=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("===========onCreate");
		mContext = getActivity();
		if(getArguments()!=null&&getArguments().containsKey("fw_id")){
			bundle = getArguments();
			fw_id=bundle.getInt("fw_id");
		}
//		if(fipperThread==null)
//			fipperThread=new FipperThread();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("===========onCreateView");
		System.out.println("*************************"+mContext);
		view = inflater.inflate(R.layout.zhufang_content, null);
		
		photosFipper = (ViewFlipper) view.findViewById(R.id.show_photos);
		radioGroup = (RadioGroup) view.findViewById(R.id.rg1);

		jj_title = (TextView) view.findViewById(R.id.title);
		zj = (TextView) view.findViewById(R.id.zj);
		hx = (TextView) view.findViewById(R.id.fangxing);
		mj = (TextView) view.findViewById(R.id.mianji);
		lh = (TextView) view.findViewById(R.id.louhao);
		zx = (TextView) view.findViewById(R.id.zhuangxiuchengdu);
		lxdh = (TextView) view.findViewById(R.id.lianxidianhua);
		ptss = (TextView) view.findViewById(R.id.peitaosheshi);
		dz = (TextView) view.findViewById(R.id.dizhi);
		zbss = (TextView) view.findViewById(R.id.zhoubiansheshi);

		map_cut = (ImageView) view.findViewById(R.id.load_map_cut);
		
		ViewTreeObserver treeObserver=map_cut.getViewTreeObserver();
		
		jumpMap = (RelativeLayout) view.findViewById(R.id.show_map);

		jumpMap.setOnClickListener(this);
		zbss.setOnClickListener(this);

		photosFipper.setOnTouchListener(new MyTouchListener());
		detector = new GestureDetector(photosFipper.getContext(), new MyGuestureListener());
		
		treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if(!flag){
					flag=!flag;
					map_width=jumpMap.getMeasuredWidth();
					map_height=map_width;
					System.out.println(">>>map_width="+map_width);
					System.out.println(">>>map_height="+map_height);
				}
			}
		});
		
		if(fw_id!=-1){
			doDownLoadData(fw_id);
		}else{
			Toast.makeText(getActivity(), "获取信息失败，请重试", Toast.LENGTH_SHORT).show();
		}
		
		return view;

	}
	
	class GetMapImage extends Thread{
		String center;
		String markers;
		public GetMapImage(String center, String markers) {
			this.center=center;
			this.markers=markers;
		}
		@Override
		public void run() {
			while(true){
				if(map_width!=0){
					onHttpUrlConnectionGet(center, markers);
					break;
				}
			}
		}
	}
	
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case ERROR:
				Toast.makeText(mContext, msgs, Toast.LENGTH_SHORT).show();
				break;
			case TO_NEXT:
				photosFipper.setInAnimation(photosFipper.getContext(), R.anim.in_rightleft);
				photosFipper.setOutAnimation(photosFipper.getContext(), R.anim.out_rightleft);
				photosFipper.showNext();// 显示下一个
				if(buttons_arr.length>1){
					count++;
					if(count==buttons_arr.length)
						count=0;
					buttons_arr[count].setChecked(true);
				}
				break;
			case RESULT_FOR_FW_ZIDUAN:
				if (msg.obj != null) {
					System.out.println(">>>RESULT_FOR_FW_ZIDUAN");
					JSONObject jsonObject1 = (JSONObject) msg.obj;
					try {
						error = jsonObject1.getInt("error");
						System.out.println(">>>error="+error);
						if (error == 0) {
							JSONArray jsonArray = jsonObject1.getJSONArray("list");
							JSONObject jsonObject=jsonArray.getJSONObject(0);
							fbr_id = jsonObject.getInt("fbr_id");
							fwdz = jsonObject.getString("fwdz");
							xqm = jsonObject.getString("xqm");
							louhao = jsonObject.getString("lh");
							huxin = jsonObject.getString("hx");
							mianji = jsonObject.getString("mj");
							czlx = jsonObject.getString("czlx");
							czjg = jsonObject.getDouble("czjg");
							lianxidianhua = jsonObject.getString("lxdh");
							zxcd = jsonObject.getString("zxcd");
							peitaosheshi = jsonObject.getString("ptss");
							photos_dir = jsonObject.getString("photos_dir");
							fbsj = jsonObject.getString("fbsj");
							showDataForZiDuan();
							//回调方法传递电话号码
							ZhuFangContentActivity.getZfcaIntance().lxdh_activity(lianxidianhua);
							//组装地址
							String center=fwdz+"|"+xqm;
							String markers=xqm;
							new GetMapImage(center, markers).start();//静态地图获取
							handler.sendEmptyMessage(SEND_FOR_FW_PHOTOS);//发送获取房屋照片请求
						} else if (error == 1) {
							msgs = jsonObject1.getString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case SEND_FOR_FW_PHOTOS:
				doShowPhotos();
				break;
			case PHOTOS_FIPPER_CHANGE:
				photosFipperChange();
				if(photos_arr.length==photos_count){
					photos_file_path_for_server=new String[photos_count];
					try {
						for(int i=0;i<photos_count;i++){
							JSONObject jsonObject1;
							jsonObject1 = photos_jsonArray.getJSONObject(i);
							//组装照片文件绝对路径
							photos_file_path_for_server[i]=jsonObject1.getString("file_parent")+"/"+jsonObject1.getString("file_name");
							System.out.println(">>>photos_file_path_for_server["+i+"]= "+photos_file_path_for_server[i]);
							//获取照片
							BitmapUtil.getInstance().download(ServerAdress.SERVER_ADRESS, photos_file_path_for_server[i], photos_arr[i]);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				}
				break;
			case GET_MAP_IMAGE_OK:
				if (picByte != null) {
					System.out.println(">>>GET_MAP_IMAGE_OK");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                    map_cut.setImageBitmap(bitmap);
                    map_cut.setScaleType(ScaleType.FIT_XY);
                    map_cut.setVisibility(View.VISIBLE);
                }
				break;
			}
			return true;
		}
	});
	
	/**
	 * 动态改变photosFipper和radiogroup
	 */
	private void photosFipperChange() {
		photos_arr=new ImageView[photos_count];
		buttons_arr=new RadioButton[photos_count];
		for(int i=0;i<photos_count;i++){
			photos_arr[i]=new ImageView(mContext);
			photos_arr[i].setImageResource(R.drawable.photo_in_loading);
			buttons_arr[i]=new RadioButton(mContext);
			buttons_arr[i].setClickable(false);
			buttons_arr[i].setButtonDrawable(R.drawable.point_radiobutton_selector);
			photosFipper.addView(photos_arr[i]);
			radioGroup.addView(buttons_arr[i]);
			photosFipper.invalidate();//更新
			radioGroup.invalidate();
		}
		buttons_arr[0].setChecked(true);
		buttons_arr[0].setBackgroundResource(R.drawable.point_selected);
		radioGroup.invalidate();
	}

	/**
	 * 从服务器获取图片列表
	 */
	private void doShowPhotos() {
		HttpParams params=new HttpParams();
		params.put("photos_dir", photos_dir);
		HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "get_photos_file_list.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				System.out.println(">>>doShowPhotos jsonObject"+jsonObject);
				try {
					error=jsonObject.getInt("error");
					if(error==0){
						photos_jsonArray=jsonObject.getJSONArray("list");
						photos_count=photos_jsonArray.length();
						handler.sendEmptyMessage(PHOTOS_FIPPER_CHANGE);
					}else if(error==1){
						msgs=jsonObject.getString("msg");
						handler.sendEmptyMessage(ERROR);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 更新界面-字段信息 TextView jj_title;//简介title TextView zj;//租金 TextView hx;//户型
	 * TextView mj;//面积 TextView lh;//楼号 TextView zx;//装修 TextView lxdh;//联系电话
	 * TextView ptss;//配套设施 TextView dz;//地址(街道+小区名) TextView zbss;//周边设施
	 * 
	 * int fbr_id=-1;//发布人id String fwdz;//房屋地址 String xqm;//小区名 String
	 * louhao;//楼号 String huxin;//户型 String mianji;//面积 String czlx;//出租类型
	 * String czjg;//出租价格 String lianxidianhua;//联系电话 String zxcd;//装修程度 String
	 * peitaosheshi;//配套设施 String photos_dir;//房屋照片文件夹地址 String fbsj;//发布时间
	 */
	private void showDataForZiDuan() {
		jj_title.setText(fwdz + " " + xqm + " " + huxin + " " + zxcd);
		zj.setText(czjg.toString());
		hx.setText(huxin);
		mj.setText(mianji);
		lh.setText(louhao);
		zx.setText(zxcd);
		lxdh.setText(lianxidianhua);
		ptss.setText(peitaosheshi);
		dz.setText(fwdz + "-" + xqm);
		jj_title.postInvalidate();
		zj.invalidate();
		hx.invalidate();
		mj.invalidate();
		lh.invalidate();
		zx.invalidate();
		lxdh.invalidate();
		ptss.invalidate();
		dz.invalidate();
		zbss.invalidate();
	}

	/**
	 * 请求服务器并获取数据
	 * @param fw_id
	 */
	private void doDownLoadData(int fw_id) {
		HttpParams params = new HttpParams();
		params.put("fw_id", fw_id);
		HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS,
				"query_data_for_fwid.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>jsonObject=" + jsonObject);
						Message message = new Message();
						message.what = RESULT_FOR_FW_ZIDUAN;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 拼接GET方法的URL地址
	 * @param serverAddress 请求的server地址前缀
	 * @param url 请求的路径地址
	 * @param params 请求的参数列表
	 * @return 拼接后的完整的URL地址
	 */
	private String getGetUrl(String serverAddress, String url, HttpParams params) {
		String realUrl ="";
		//去掉地址后面的/
		if(serverAddress != null && serverAddress.charAt(serverAddress.length() - 1) == '/') {
			serverAddress = serverAddress.substring(0, serverAddress.length() -1);
		}
		//去掉url前面的/
		if(url != null && url.length() > 0 && url.charAt(0) == '/') {
			url = url.substring(1, url.length());
		}
		//去掉url后面的？
		if(url != null && url.length() > 0 && url.charAt(url.length() - 1) == '?') {
			url = url.substring(0, url.length() -1);
		}
		if(params != null && params.getParamsCount() > 0) {
			realUrl = serverAddress + "/" + url + "?";
			for(int i=0; i< params.getParamsCount(); i++) {
				Param param = params.get(i);
				if(i != 0) { //调整优化选择
					if(param.vObject != null) {
						try {
							String value = URLEncoder.encode(param.vObject.toString(), "utf-8");
							//System.out.println(">>>value="+value);
							realUrl += "&" + param.key + "=" + value; 
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							realUrl += "&" + param.key + "=" + param.vObject; 
						}
					} else {
						realUrl += "&" + param.key + "="; 
					}
				} else {
					if(param.vObject != null) {
						try {
							String value = URLEncoder.encode(param.vObject.toString(), "utf-8");
							//System.out.println(">>>value="+value);
							realUrl += param.key + "=" + value;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							realUrl += param.key + "=" + param.vObject;
						}
					} else {
						realUrl += param.key + "=";
					}
				}
			}
		} else {
			realUrl = serverAddress + "/" + url;
		}
		return realUrl;
	}
	
	/**
	 * 请求静态地图
	 * http://api.map.baidu.com/staticimage?
	 * center=%E4%B8%8A%E6%B5%B7%E5%B8%82%E5%B8%82%E8%BE%96%E5%8C%BA%E6%B5%A6%E4%B8%9C%E6%96%B0%E5%8C%BA%E7%B9%81%E8%8D%A3%E5%AE%89%E5%B1%85|%E7%B9%81%E8%8D%A3%E5%AE%89%E5%B1%85
	 * &markers=%E4%B8%8A%E6%B5%B7%E5%B8%82%E5%B8%82%E8%BE%96%E5%8C%BA%E6%B5%A6%E4%B8%9C%E6%96%B0%E5%8C%BA%E7%B9%81%E8%8D%A3%E5%AE%89%E5%B1%85
	 * &zoom=19
	 * &markerStyles=l,,0xffff0000
	 * &height=970
	 * &width=1023
	 */
	public void onHttpUrlConnectionGet(String center, String markers) {
		try {
			String address="";
			HttpParams params=new HttpParams();
			params.put("center", center);
			params.put("markers", markers);
			params.put("zoom", "19");
			params.put("markerStyles", "l,,0xffff0000");
			if(map_height>1024)
				map_height=1023;
			params.put("height", ""+map_height);
			if(map_width>1024)
				map_width=1023;
			params.put("width", ""+map_width);
			address=this.getGetUrl("http://api.map.baidu.com/", "staticimage", params);
			System.out.println(address);
			URL url = new URL(address);
			URLConnection connection = url.openConnection();
			// 设置通用属性
			connection.setDoInput(true);// 允许输入
			connection.setRequestProperty("accept", "*/*");// 设置可以接收所有的MIME类型数据
			connection.setRequestProperty("connection", "Keep-Alive");// 保持长连接
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh,q=0.8");
			connection.setConnectTimeout(5000);// 设置超时相应时间

			connection.connect();// 建立连接

			// 获取相应的header
			Map<String, List<String>> map = connection.getHeaderFields();
			Iterator<Entry<String, List<String>>> iterator = map.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, List<String>> entry = iterator.next();
				System.out.print(entry.getKey() + ":");
				if (entry.getValue() != null) {
					for (int i = 0; i < entry.getValue().size(); i++) {
						 System.out.print(entry.getValue().get(i));
					}
				}
				 System.out.println();
			}
			String str = connection.getHeaderField(null);// 这个只是针对URLConnection和HttpURLConnection
			System.out.println("status line=" + str);
			System.out.println("================================================================");
			if (str.contains("200")) {
				InputStream inputStream = connection.getInputStream();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inputStream.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				picByte = bos.toByteArray();
				handler.sendEmptyMessage(GET_MAP_IMAGE_OK);
				inputStream.close();
				bos.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_map:
			// 跳转地图
			if(fwdz!=null&&xqm!=null&&!fwdz.equals("")&&!xqm.equals("")){
				Intent jm=new Intent(mContext, MapShow.class);
				jm.putExtra("fwdz", fwdz);
				jm.putExtra("xqm", xqm);
				startActivity(jm);
			}else{
				Toast.makeText(mContext, "无地址信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.zhoubiansheshi:
			// 跳转周边设施界面
			break;
		}
	}

	class MyTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return detector.onTouchEvent(event);
		}
	}

	class MyGuestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			System.out.println("onDown");
			// 返回true，事件下发，屏幕才能被拖动
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			System.out.println("onShowPress");
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			System.out.println("onSingleTapUp");
			String photo_file_path_for_server=photos_file_path_for_server[count];
			System.out.println(">>>photo_file_path_for_server= "+photo_file_path_for_server);
			showPopipWindow(photo_file_path_for_server);
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			System.out.println("onScroll");
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			System.out.println("onLongPress");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.GestureDetector.OnGestureListener#onFling(android.view
		 * .MotionEvent, android.view.MotionEvent, float, float) e1 最开始的触屏事件
		 * e2移动中的触屏事件 velocityX x的移动量 velocityY y方向的移动量
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			int count = 0;
//			int parentId = 0;
//			int childId = 0;
//			ImageView child;
//
//			child = (ImageView) photosFipper.getCurrentView();// 获取当前view
//
//			count = photosFipper.getChildCount();// 获取子控件数量
//			parentId = photosFipper.getId();// 获取当前父view控件id
//			childId = child.getId();// 获取当前子view控件id
//
//			System.out.println("count=" + count);
//			System.out.println("parent=" + parentId);
//			System.out.println("childId=" + childId);

			 System.out.println("onFling");
			 System.out.println("e1.getX="+e1.getX());
			 System.out.println("e2.getX="+e2.getX());
			 System.out.println("e1.getY="+e1.getY());
			 System.out.println("e2.getY="+e2.getY());

			if (e1.getX() - e2.getX() > 100) {// right to left
				photosFipper.setInAnimation(photosFipper.getContext(), R.anim.in_rightleft);
				photosFipper.setOutAnimation(photosFipper.getContext(), R.anim.out_rightleft);
				photosFipper.showNext();// 显示下一个
				if(buttons_arr.length>1){
					count++;
					if(count==buttons_arr.length)
						count=0;
					buttons_arr[count].setChecked(true);
				}
				return true;// 事件执行完毕
			}
			if (e2.getX() - e1.getX() > 100) {// left to right
				photosFipper.setInAnimation(photosFipper.getContext(), R.anim.in_leftright);
				photosFipper.setOutAnimation(photosFipper.getContext(), R.anim.out_leftright);
				photosFipper.showPrevious();// 显示前一个
				if(buttons_arr.length>1){
					count--;
					if(count==-1)
						count=buttons_arr.length-1;
					buttons_arr[count].setChecked(true);
				}

				return true;// 事件执行完毕
			}

			return false;
		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
//		thread_run_flag=true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		thread_run_flag=false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy");
//		thread_run_flag=false;
//		thread_flag=false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.out.println("onDestroyView");
//		thread_run_flag=false;
//		thread_flag=false;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		System.out.println("onDetach");
//		thread_run_flag=false;
//		thread_flag=false;
	}
	
	private void showPopipWindow(String photo_file_path_for_server) {
		//创建一个popupWindow对象
		final PopupWindow popupWindow=new PopupWindow(mContext);
		ImageView imageView=new ImageView(mContext);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		BitmapUtil.getInstance().download(ServerAdress.SERVER_ADRESS, photo_file_path_for_server, imageView);
		//view2.setBackgroundResource(R.drawable.edittext_normal);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		//消失的监听
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				System.out.println("popup dismiss");
			}
		});
		//设置宽高包裹内容
		popupWindow.setWindowLayoutMode(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setTouchable(true);//设置可点击
		popupWindow.setOutsideTouchable(true);//设置外部可点击，点击取消
		popupWindow.setFocusable(true);//设置可聚焦
		//设置背景
		//这里设置了一个空背景
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittext_normal));
		popupWindow.setContentView(imageView);
		//设置触屏事件
		popupWindow.setTouchInterceptor(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//返回false，事件才会下发
				return false;
			}
		});
		//显示
		//popupWindow.showAsDropDown(view, 100 ,0);
		popupWindow.showAtLocation(imageView, Gravity.CENTER, 0, 0);
	}

//	class FipperThread extends Thread{
//		@Override
//		public void run() {
//			while(thread_flag){
//				if(thread_run_flag){
//					try {
//						Thread.sleep(2500);
//						handler.sendEmptyMessage(TO_NEXT);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
	
}

