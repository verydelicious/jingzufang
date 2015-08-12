package com.team.jingzufang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.listery.photoshow.loader.PhotosShow;
import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;

public class FaBuActivity extends FragmentActivity implements OnClickListener,
		OnCheckedChangeListener, OnItemSelectedListener {
	
	public interface Finish{
		public void fin();
		public void progress(int proNum);
	}

	Intent upLoad;//上传界面
	
	RadioGroup chuZhuLeiXing;// 出租类型
	RadioButton zhenZhu;// 出租类型-整租
	RadioButton heZhu;// 出租类型-合租

	RadioGroup zhuangXiuChengDu;// 装修程度
	RadioButton jingZhuang;// 装修程度-精装
	RadioButton jianZhuang;// 装修程度-简装

	EditText xiaoQuMing;// 小区名
	EditText louHao;// 楼号
	EditText huXing;// 户型
	EditText mianJi;// 面积
	EditText chuZhuJiaGe;// 出租价格
	EditText lianXiDianHua;// 联系电话
	static CheckBox sf;//沙发
	static CheckBox ds;//电视
	static CheckBox c;//床
	static CheckBox rsq;//热水器
	static CheckBox kt;//空调
	static CheckBox bx;//冰箱
	static CheckBox kd;//空调
	static CheckBox xyj;//洗衣机
	static CheckBox yt;//阳台

	//配套设施
	CheckBox[] ptss_cb=new CheckBox[]{
			 sf,//沙发
			 ds,//电视
			 c,//床
			 rsq,//热水器
			 kt,//空调
			 bx,//冰箱
			 kd,//空调
			 xyj,//洗衣机
			 yt//阳台
	};
	String[] ptss_arr=new String[]{	"沙发", "电视", "床",  "热水器", "空调", "冰箱", "宽带", "洗衣机", "阳台"};
	boolean[] ptss_boo=new boolean[9];
	int[] cb_id=new int[]{
		R.id.sf,
		R.id.ds,
		R.id.c,
		R.id.rsq,
		R.id.kt,
		R.id.bx,
		R.id.kd,
		R.id.xyj,
		R.id.yt
	};
	
	TextView photosCount;// 照片数量显示器

	Button faBu;// 发布
	Button quXiao;// 取消

	String leiXing_chuZhu = "";// 存储出租类型
	String leiXing_zhuangXiu = "";// 存储装修程度

	String dz = "";// 房屋地址
	String xqm = "";// 小区名
	String lh = "";// 楼号
	String hx = "";// 户型
	String mj = "";// 面积
	Double czjg = -1d;// 出租价格
	String lxdh = "";// 联系电话
	String ptss = "";// 配套设施
	String quyu = "";//区域
	String ct = "";//城市
	
	Spinner province;//省份选择器
	Spinner city;//市选择器
	Spinner county;//区县选择器

	LinearLayout upLoadPhotos;// 上传照片

	ArrayList<String> photos = new ArrayList<String>();// 照片文件路径集合

	String photos_dir = "";// 照片文件夹

	final static int SHOW_PHOTOS = 0x81;// 跳转房屋照片选择器

	final static int SEND_ZIDUAN = 0x00;// 发送房屋字段信息
	final static int RESULT_ZIDUAN = 0x01;// 接收到发送房屋字段信息的返回JSON
	final static int RESULT_ZIDUAN_OK = 0x02;// 服务器接收成功并没有错误返回

	final static int SEND_PHOTOS = 0x03;// 发送房屋照片信息
	final static int RESULT_PHOTOS = 0x04;// 接收到发送房屋照片信息返回的JSON
	final static int RESULT_PHOTOS_OK = 0x05;// 服务器接收成功并没有错误返回
	
	final static int SERVER_RECEIVE_FAIL = 0x06;// 服务器接收失败

	final static int RESULT_PHOTOS_FOR_ID = 0x07;// 接收到写房屋照片目录到数据库返回的JSON
	final static int RESULT_PHOTOS_FOR_ID_SUCCEED = 0x08;// 写成功
	final static int RESULT_PHOTOS_FOR_ID_FAIL = 0x09;// 写失败
	
	final static int PROVINCE_OK = 0x10;// 获取省份成功
	final static int CITY_OK = 0x11;// 获取市成功
	final static int COUNTY_OK = 0x12;// 获取区县成功
	
	int error = 1, fw_id = -1, fbr_id = -1;
	String msgs = null;

	boolean[] flags;// 检测标记集合
	final String[] strs = { "组件获取失败", "信息不完整", "输入不合法", "类型未选择",
			"照片数量不符合(至少1张)" };

	ArrayAdapter<String> provinces_adapter,citys_adapter,countys_adapter;
	HashMap<String, String> provinces_map=new HashMap<String, String>();
	HashMap<String, String> citys_map=new HashMap<String, String>();
	HashMap<String, String> countys_map=new HashMap<String, String>();
	ArrayList<String> provinces;
	ArrayList<String> citys;
	ArrayList<String> countys;
	
	boolean countyIsEmpty=false;//没有区县
	
	String[] pcc=new String[3];//存储省市区
	
	boolean uping=false;//上传中标志
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getIntent()!=null&&getIntent().hasExtra("userId"))
			fbr_id=getIntent().getIntExtra("userId", -1);
		
		setContentView(R.layout.fabu_info);
		
		province=(Spinner) findViewById(R.id.province);
		city=(Spinner) findViewById(R.id.city);
		county=(Spinner) findViewById(R.id.county);
		
		provinces=new ArrayList<String>();
		citys=new ArrayList<String>();
		countys=new ArrayList<String>();
		
		provinces.add("请选择省");
		citys.add("请选择市");
		countys.add("请选择区");
		
		provinces_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinces);
		citys_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citys);
		countys_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countys);
		
		province.setAdapter(provinces_adapter);
		city.setAdapter(citys_adapter);
		county.setAdapter(countys_adapter);
		
		HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "get_province.php", new HttpParams(), new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				System.out.println("get_province.php>>>jsonObject= "+jsonObject);
				try {
					error=jsonObject.getInt("error");
					if(error==0){
						JSONArray array=jsonObject.getJSONArray("list");
						Message message=new Message();
						message.obj=array;
						message.what=PROVINCE_OK;
						handler.sendMessage(message);
					}else{
						msgs=jsonObject.getString("msg");
						Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		faBu = (Button) findViewById(R.id.fabu);
		quXiao = (Button) findViewById(R.id.quxiao);

		chuZhuLeiXing = (RadioGroup) findViewById(R.id.chuzhuleixing);
		zhuangXiuChengDu = (RadioGroup) findViewById(R.id.zhuangxiuchengdu);

		zhenZhu = (RadioButton) findViewById(R.id.leixing_zhengzhu);
		heZhu = (RadioButton) findViewById(R.id.leixing_hezhu);
		jingZhuang = (RadioButton) findViewById(R.id.jingzhuang);
		jianZhuang = (RadioButton) findViewById(R.id.jianzhuang);

		xiaoQuMing = (EditText) findViewById(R.id.xiaoquming);
		louHao = (EditText) findViewById(R.id.louhao);
		huXing = (EditText) findViewById(R.id.huxing);
		mianJi = (EditText) findViewById(R.id.mianji);
		chuZhuJiaGe = (EditText) findViewById(R.id.chuzhujiage);
		lianXiDianHua = (EditText) findViewById(R.id.lianxidianhua);
		
		ptss_cb[0]=sf=(CheckBox) findViewById(R.id.sf);
		ptss_cb[1]=ds=(CheckBox) findViewById(R.id.ds);
		ptss_cb[2]=c=(CheckBox) findViewById(R.id.c);
		ptss_cb[3]=rsq=(CheckBox) findViewById(R.id.rsq);
		ptss_cb[4]=kt=(CheckBox) findViewById(R.id.kt);
		ptss_cb[5]=bx=(CheckBox) findViewById(R.id.bx);
		ptss_cb[6]=kd=(CheckBox) findViewById(R.id.kd);
		ptss_cb[7]=xyj=(CheckBox) findViewById(R.id.xyj);
		ptss_cb[8]=yt=(CheckBox) findViewById(R.id.yt);
		
		for(int i=0;i<ptss_cb.length;i++){
			ptss_cb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					for(int i=0;i<cb_id.length;i++){
						if(cb_id[i]==buttonView.getId()){
							ptss_boo[i]=isChecked;
						}
					}
				}
			});
		}

		photosCount = (TextView) findViewById(R.id.photo_count);

		upLoadPhotos = (LinearLayout) findViewById(R.id.upload_photos);
		
		province.setOnItemSelectedListener(this);
		city.setOnItemSelectedListener(this);
		county.setOnItemSelectedListener(this);

		upLoadPhotos.setOnClickListener(this);
		chuZhuLeiXing.setOnCheckedChangeListener(this);
		zhuangXiuChengDu.setOnCheckedChangeListener(this);

		faBu.setOnClickListener(this);
		quXiao.setOnClickListener(this);

	}

	Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {

		JSONArray array;
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case PROVINCE_OK:
				array=(JSONArray) msg.obj;
				try {
					provinces.clear();
					provinces_map.clear();
					provinces.add("请选择省");
					for(int i=1;i<array.length()+1;i++){
						String id=array.getJSONObject(i-1).getString("ID");
						String name=array.getJSONObject(i-1).getString("Name");
						provinces_map.put(name, id);
						provinces.add(name);
						System.out.println("province>>>= "+provinces.get(i));
					}
					provinces_adapter.notifyDataSetChanged();
					province.setAdapter(provinces_adapter);
					System.out.println("province>>> "+((province instanceof Spinner)&&(province!=null)));
					province.setSelection(0, true);//设置默认显示项
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case CITY_OK:
				array=(JSONArray) msg.obj;
				try {
					for(int i=1;i<array.length()+1;i++){
						String id=array.getJSONObject(i-1).getString("ID");
						String name=array.getJSONObject(i-1).getString("Name");
						citys_map.put(name, id);
						citys.add(name);
						System.out.println("province>>>= "+citys.get(i));
					}
					citys_adapter.notifyDataSetChanged();
					city.setAdapter(citys_adapter);
					System.out.println("city>>> "+((city instanceof Spinner)&&(city!=null)));
					city.setSelection(0, true);//设置默认显示项
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case COUNTY_OK:
				array=(JSONArray) msg.obj;
				try {
					for(int i=1;i<array.length()+1;i++){
						String id=array.getJSONObject(i-1).getString("ID");
						String name=array.getJSONObject(i-1).getString("Name");
						countys_map.put(name, id);
						countys.add(name);
						System.out.println("province>>>= "+countys.get(i));
					}
					countys_adapter.notifyDataSetChanged();
					county.setAdapter(countys_adapter);
					System.out.println("county>>> "+((county instanceof Spinner)&&(county!=null)));
					county.setSelection(0, true);//设置默认显示项
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
//			case SERVER_RECEIVE_FAIL:
//				if (msg.obj != null) {
//					msgs = (String) msg.obj;
//					Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
//				}
//				break;
			case SEND_ZIDUAN:
				sendFW_ZiDuan();
				break;
			case RESULT_ZIDUAN:
				JSONObject jsonObject = null;
				if (msg.obj != null) {
					jsonObject = (JSONObject) msg.obj;
					try {
						error = jsonObject.getInt("error");
						if (error != 1) {
							fw_id = jsonObject.getInt("fw_id");
							fbr_id = jsonObject.getInt("fbr_id");
							sendFW_Photos();
//							Message message=new Message();
//							message.what = RESULT_ZIDUAN_OK;
//							message.obj = fw_id;
//							handler.sendMessage(message);
						} else {
							msgs = jsonObject.getString("msg");
							Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
//							msg.what = SERVER_RECEIVE_FAIL;
//							msg.obj = msgs;
//							handler.sendMessage(msg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
//			case RESULT_ZIDUAN_OK:
//				sendFW_Photos();
//				break;
			case RESULT_PHOTOS:
				if(msg.obj!=null){
					JSONArray jsonArray=(JSONArray) msg.obj;
					for(int i=0;i<jsonArray.length();i++){
						JSONObject object=null;
						if(i==jsonArray.length()-1){
							try {
								object=jsonArray.getJSONObject(i);
								photos_dir=object.getString("file_dir");
								sendFW_PhotosForId();
								//handler.sendEmptyMessage(RESULT_PHOTOS_OK);
								System.out.println(">>>RESULT_PHOTOS_OK file_dir="+photos_dir);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						try {
							object=jsonArray.getJSONObject(i);
							if(object.getInt("error")==1){
								msgs=object.getString("error_info");
								Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
//								Message message=new Message();
//								message.what=SERVER_RECEIVE_FAIL;
//								message.obj=msgs;
//								handler.sendMessage(message);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				break;
//			case RESULT_PHOTOS_OK:
//				sendFW_PhotosForId();
//				break;
//			case RESULT_PHOTOS_FOR_ID:
//				if(msg.obj!=null){
//					JSONObject object=(JSONObject) msg.obj;
//					try {
//						error=object.getInt("error");
//						photos_dir=object.getString("photos_dir");
//						msgs=object.getString("msg");
////						Message message=new Message();
////						message.obj=object;
//						if(error==1){
//							Toast.makeText(FaBuActivity.this, "对不起,由于技术故障发布失败,请联系客服", Toast.LENGTH_SHORT).show();
//							((Finish) UpLoad.getUpLoad()).fin();
//							onFailRemove();
////							message.what=RESULT_PHOTOS_FOR_ID_FAIL;
//						}
//						else{
//							Toast.makeText(FaBuActivity.this, "发布成功", Toast.LENGTH_LONG).show();
//							((Finish) UpLoad.getUpLoad()).fin();
////							message.what=RESULT_PHOTOS_FOR_ID_SUCCEED;
//						}
////						handler.sendMessage(message);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				break;
			case RESULT_PHOTOS_FOR_ID_SUCCEED://提示使用者发布成功并结束当前Activity
//				JSONObject jsonObject1=(JSONObject) msg.obj;
//				msgs=jsonObject1.getString("msg");
				Toast.makeText(FaBuActivity.this, "发布成功", Toast.LENGTH_LONG).show();
				((Finish) UpLoad.getUpLoad()).fin();
				uping=false;
				break;
			case RESULT_PHOTOS_FOR_ID_FAIL:
//				JSONObject jsonObject2=(JSONObject) msg.obj;
//				msgs=jsonObject2.getString("msg");
//				photos_dir=jsonObject2.getString("photos_dir");
				Toast.makeText(FaBuActivity.this, "对不起,由于技术故障发布失败,请联系客服", Toast.LENGTH_SHORT).show();
				((Finish) UpLoad.getUpLoad()).fin();
				onFailRemove();
				uping=false;
				break;
			}
			return true;
		}
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upload_photos:
			Intent intent = new Intent(this, PhotosShow.class);
			intent.putExtra("showphotos", "");
			startActivityForResult(intent, SHOW_PHOTOS);
			break;
		case R.id.fabu:
			if(!uping){
				boolean flag = false;
				flag = checkAll();
				if (flag) {
					/*
					 * 发送数据等操作
					 */
					uping=true;
					upLoad=new Intent(this, UpLoad.class);
					handler.sendEmptyMessage(SEND_ZIDUAN);
					startActivity(upLoad);
				}
			}else{
				Toast.makeText(this, "上传中，请等待...", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.quxiao:
			this.finish();
			break;
		}
	}

	/**
	 * 未知原因引起的发布失败.删除已上传数据库的数据
	 */
	private void onFailRemove() {
		HttpParams params=new HttpParams();
		params.put("photo_dir", photos_dir);
		HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "fail_remove.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				System.out.println(">>>jsonObject="+jsonObject);
			}
		});
	}
	
	/**
	 * 写图片文件夹到数据库
	 */
	private void sendFW_PhotosForId() {
		HttpParams params=new HttpParams();
		params.put("fbr_id", fbr_id);
		params.put("fw_id",fw_id);
		params.put("photos_dir", photos_dir);
		HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "photos_for_fwid.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				System.out.println(">>>jsonObject="+jsonObject);
				try {
					error=jsonObject.getInt("error");
//					photos_dir=jsonObject.getString("photos_dir");
//					msgs=jsonObject.getString("msg");
					Message message=new Message();
					if(error==1){
						message.what=RESULT_PHOTOS_FOR_ID_FAIL;
					}
					else{
						message.what=RESULT_PHOTOS_FOR_ID_SUCCEED;
					}
					handler.sendMessage(message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 发送房屋照片
	 */
	private void sendFW_Photos() {
		HttpParams params = new HttpParams();
		for (int i = 0; i < photos.size(); i++) {
			String path = photos.get(i);
			String photo_fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
			System.out.println("photo_fileName=" + photo_fileName + ",photo_filePath=" + path);
			params.put(photo_fileName, path);
		}
		System.out.println("files=" + params.getParamsCount());
		HttpClientUtils.getInstance().upload(ServerAdress.SERVER_ADRESS, "upload_fw_photos.php", params, new AsyncHttpResponseHandler() {
					@Override
					public void onUploadProgress(int progress) {
						super.onUploadProgress(progress);
//						System.out.println(">>>>>>>>progress="+progress);
						((Finish) UpLoad.getUpLoad()).progress(progress);
					}

					@Override
					public void onUploadCompleted() {
						super.onUploadCompleted();
						// System.out.println(">>>>>>>>UpLoadCompleted");
					}

					@Override
					public void onSuccess(JSONArray jsonArray) {
						super.onSuccess(jsonArray);
						System.out.println(">>>>>>>>jsonArray=" + jsonArray.toString());
							for(int i=0;i<jsonArray.length();i++){
								JSONObject object=null;
								if(i==jsonArray.length()-1){
									try {
										object=jsonArray.getJSONObject(i);
										photos_dir=object.getString("file_dir");
										sendFW_PhotosForId();
										System.out.println(">>>RESULT_PHOTOS_OK file_dir="+photos_dir);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								try {
									object=jsonArray.getJSONObject(i);
									if(object.getInt("error")==1){
										msgs=object.getString("error_info");
										Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
										break;
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						Message message = new Message();
						message.what = RESULT_PHOTOS;
						message.obj = jsonArray;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 发送房屋字段
	 */
	private void sendFW_ZiDuan() {
		HttpParams params = new HttpParams();
		// (dz, xqm, lh, hx, mj, czjg, lxdh, ptss, photos);
		params.put("fbr_id", fbr_id);
		params.put("fwdz", dz);
		params.put("xqm", xqm);
		params.put("lh", lh);
		params.put("hx", hx);
		params.put("mj", mj);
		params.put("czlx", leiXing_chuZhu);
		params.put("czjg", czjg);
		params.put("lxdh", lxdh);
		params.put("zxcd", leiXing_zhuangXiu);
		params.put("ptss", ptss);
		params.put("qy", quyu);
		params.put("ct", ct);
		HttpClientUtils.getInstance().post(
				ServerAdress.SERVER_ADRESS, "fangwu_fabu.php",
				params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>>>>>>jonObject=" + jsonObject);
						Message message = new Message();
						message.what = RESULT_ZIDUAN;
						message.obj = jsonObject;
						handler.sendMessage(message);
					}
				});
	}

	/**
	 * 进行所有项的检查
	 */
	private boolean checkAll() {
		/*
		 * 分别标志: 组件null检测标记 内容为""检测标记 输入内容检测标记 RadioGroup检测标记
		 */
		flags = new boolean[] { false, false, false, false, false };
		if (
		/*--------------检测组件是否正常-----------------*/
		(flags[0] = isNull()) &&
		/*---------------检测内容是否为""----------------*/
		(flags[1] = hasEmpty()) &&
		/*---------------检测输入内容是否合法----------------*/
		(flags[2] = inputCheck()) &&
		/*----------------检测RadioGroup---------------*/
		(flags[3] = leiXingCheck()) &&
		/*----------------检测photos路径集合---------------*/
		(flags[4] = photosCheck())) {
			/*-----------------所有项均符合,进行存储--------------*/
			for(int i=0;i<ptss_boo.length;i++){
				if(ptss_boo[i]){
					ptss+=ptss_arr[i]+",";
				}
			}
			ptss=ptss.substring(0, ptss.length()-1);
			mj = mianJi.getText().toString();
			czjg = Double.parseDouble((chuZhuJiaGe.getText().toString()));
			lxdh = lianXiDianHua.getText().toString();
			System.out.println(">>>>>>>>>>>>>>>>地址:"+dz);
			System.out.println(">>>>>>>>>>>>>>>>配套设施:"+ptss);
			System.out.println("检查通过");
			return true;
		} else {
			String str = "";
			System.out.println("检查未通过");
			for (int i = 0; i < flags.length; i++) {
				if (!flags[i]) {
					str = strs[i];
					break;
				}
			}
			MyDialog myDialog = new MyDialog(this);
			myDialog.setIcon(R.drawable.message_fail);
			myDialog.setTitle("发布失败");
			myDialog.setMessage(str);
			myDialog.setPositiveButton("返回",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			myDialog.show();
			return false;
		}
	}

	private boolean photosCheck() {
		if (null == photos || photos.size() < 1) {
			System.out.println(">>>>>>>>>>photos.size=" + photos.size());
			return false;
		}
		return true;
	}

	private boolean inputCheck() {
		if (isDouble(mianJi.getText().toString())
				&& isDouble(chuZhuJiaGe.getText().toString())
				&& isPhoneNumber(lianXiDianHua.getText().toString())) {
			return true;
		}
		System.out.println(">>>inputCheck 未通过");
		return false;
	}

	private boolean leiXingCheck() {
		if (!((leiXing_chuZhu.equals("") && leiXing_chuZhu.equals("")) || (leiXing_zhuangXiu
				.equals("") && leiXing_zhuangXiu.equals("")))) {
			return true;
		}
		System.out.println(">>>leiXingCheck 未通过");
		return false;
	}

	private boolean hasEmpty() {
		if (!((pcc[0].equals("")||pcc[1].equals("")))
				&& !(xqm = xiaoQuMing.getText().toString()).equals("")
				&& !(lh = louHao.getText().toString()).equals("")
				&& !(hx = huXing.getText().toString()).equals("")) {
			System.out.println("=======>>>ptss_boo.length= "+ptss_boo.length);
			for(int i=0;i<ptss_boo.length;i++){
				System.out.println("=======>>>ptss_boo["+i+"]= "+ptss_boo[i]);
				if(ptss_boo[i])
					break;
				else if(i==ptss_boo.length-1&&!ptss_boo[ptss_boo.length-1]){
					System.out.println("=======<<<ptss_boo["+i+"]");
					return false;
				}
			}
			if(countyIsEmpty){
				System.out.println("county is empty");
				if(pcc[2].equals("")){
					ct=pcc[0];
					quyu=pcc[2];
					dz=pcc[0]+pcc[1]+pcc[2];
					return true;
				}else{
					return false;
				}
			}else{
				System.out.println("county not empty");
				if(pcc[2].equals("")){
					System.out.println("ERROR");
					return false;
				}else{
					dz=pcc[0]+pcc[1]+pcc[2];
					System.out.println("======>>>地址= "+dz);
					return true;
				}
			}
		}
		System.out.println(">>>has Empty!!!");
		return false;
	}

	private boolean isNull() {
		if (pcc[0]!=null 
				&& pcc[1]!=null
				&& pcc[2]!=null
				&& xiaoQuMing.getText() != null
				&& louHao.getText() != null && huXing.getText() != null
				&& mianJi.getText() != null && chuZhuJiaGe.getText() != null
				&& lianXiDianHua.getText() != null) {
			for(int i=0;i<ptss_cb.length;i++)
				if(ptss_cb[i]==null)
					return false;
			return true;
		}
		System.out.println(">>>is null!!!");
		return false;
	}

	private boolean isPhoneNumber(String str) {
		/*
		 * 中国移动号段：134、135、136、137、138、139、150、151、152、157、158、159、147、182、183、184
		 * 、187、188、1705 、178中国联通号段：130、131、132、145、155、156、185、186 、176、1709
		 * 、176中国电信号段：133 、153 、180 、181 、189、1700 、177
		 */
		Pattern pattern = Pattern.compile("^[1]{1}[3|4|5|7|8]{1}[0-9]{9,9}$");// 首位必须为1,第2位为{3,4,5,7,8}中的1个,后面9为为0-9中任意
		Matcher isphoneNumber = pattern.matcher(str);
		if (!isphoneNumber.matches()) {
			System.out.println(">>>is not PhoneNumber");
			return false;
		}
		return true;
	}

	private boolean isDouble(String str) {
		char[] arr = str.toCharArray();
		int pointCount = 0;
		for (int i = 0; i < str.length(); i++) {
			if (arr[i] == '.')
				pointCount++;
		}
		System.out.println(">>> " + str + " 小数点计数 pointCount=" + pointCount);
		if (pointCount > 1) {
			System.out.println(">>>is not Double");
			return false;
		}
		Pattern pattern = Pattern.compile("^[0-9]{0,}[.]{0,1}[0-9]{0,}$");// 以0-9开头匹配大于0位,小数点只能有最多1个,后面以0-9任意位数的数字结尾
		Matcher isdouble = pattern.matcher(str);
		if (!isdouble.matches()) {
			System.out.println(">>>is not Double");
			return false;
		}
		return true;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		System.out.println("onCheckedChanged");
		switch (group.getCheckedRadioButtonId()) {
		case R.id.leixing_zhengzhu:// 整租
			leiXing_chuZhu = "整租";
			System.out.println(">>>onCheckedChanged 出租类型=" + leiXing_chuZhu);
			break;
		case R.id.leixing_hezhu:// 合租
			leiXing_chuZhu = "合租";
			System.out.println(">>>onCheckedChanged 出租类型=" + leiXing_chuZhu);
			break;
		case R.id.jingzhuang:// 精装
			leiXing_zhuangXiu = "精装";
			System.out.println(">>>onCheckedChanged 装修程度=" + leiXing_zhuangXiu);
			break;
		case R.id.jianzhuang:// 简装
			leiXing_zhuangXiu = "简装";
			System.out.println(">>>onCheckedChanged 装修程度=" + leiXing_zhuangXiu);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SHOW_PHOTOS && resultCode == RESULT_OK && data != null && data.hasExtra("select_photos")) {
			System.out.println(">>>>>>>>>>RESULT_OK");
			photos = data.getStringArrayListExtra("select_photos");
			photosCount.setText("已选择 " + data.getStringArrayListExtra("select_photos").size() + " 张");
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
		case R.id.province:
			System.out.println("Spinner>>>>onItemSelected position= "+position);
			System.out.println(">>>>>>>>>>>"+provinces.get(position));
			changeProvince();
			if(position==0){
				pcc[0]="";
			}else if(position!=0){
				pcc[0]=provinces.get(position);
				String p_id=provinces_map.get(pcc[0]);
				HttpParams params=new HttpParams();
				params.put("province_id", p_id);
				HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "get_city.php", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println("get_city>>>jsonObject= "+jsonObject);
						try {
							error=jsonObject.getInt("error");
							if(error==0){
								JSONArray array=jsonObject.getJSONArray("list");
								Message message=new Message();
								message.obj=array;
								message.what=CITY_OK;
								handler.sendMessage(message);
							}else{
								int result=jsonObject.getInt("result");
								if(result!=2)
									return;
								msgs=jsonObject.getString("msg");
								Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
			break;
		case R.id.city:
			System.out.println("Spinner>>>>onItemSelected position= "+position);
			System.out.println(">>>>>>>>>>>"+citys.get(position));
			changeCity();
			if(position==0){
				pcc[1]="";
			}else if(position!=0){
				pcc[1]=citys.get(position);
				String c_id=citys_map.get(pcc[1]);
				HttpParams params=new HttpParams();
				params.put("city_id", c_id);
				HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "get_county.php", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println("get_city>>>jsonObject= "+jsonObject);
						try {
							error=jsonObject.getInt("error");
							if(error==0){
								JSONArray array=jsonObject.getJSONArray("list");
								Message message=new Message();
								message.obj=array;
								message.what=COUNTY_OK;
								handler.sendMessage(message);
							}else{
								int result=jsonObject.getInt("result");
								if(result==1){
									System.out.println(">>>countyIsEmpty= "+countyIsEmpty);
									countyIsEmpty=true;
									pcc[2]="";
									return;
								}
								msgs=jsonObject.getString("msg");
								Toast.makeText(FaBuActivity.this, msgs, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
			break;
		case R.id.county:
			System.out.println("Spinner>>>>onItemSelected position= "+position);
			System.out.println(">>>>>>>>>>>"+countys.get(position));
			pcc[2]="";
			if(position!=0){
				pcc[2]=countys.get(position);
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	private void changeProvince(){
		citys_map.clear();
		citys.clear();
		citys.add("请选择市");
		citys_adapter.notifyDataSetChanged();
	}
	
	private void changeCity(){
		countys.clear();
		countys.add("请选择区");
		countys_map.clear();
		countys_adapter.notifyDataSetChanged();
	}
	
}
