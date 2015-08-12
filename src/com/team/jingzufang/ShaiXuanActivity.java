package com.team.jingzufang;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.listery.mapshow.LocMap;
import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;
import com.xiangmu.tenement.LoadListView;
import com.xiangmu.tenement.LoadListView.ILoadListener;
import com.xiangmu.tenement.TenementAdapter;
import com.xiangmu.tenement.TenementBean;

public class ShaiXuanActivity extends FragmentActivity implements OnClickListener,ILoadListener{
	
	ImageView goBcak;
	TextView jumpMap;
	TextView quYuMenu;
	TextView jiaGeMenu;
	
	RelativeLayout menu;
	ListView leftList;
	ListView rigthList;
	ListView jiageList;
	LoadListView content;
	ImageView bottom;
	
	Intent intent;
	
	String city;
	String county;
	
	int error;
	String msgs;
	
	HttpParams params=null;
	
	TenementAdapter content_adapter;
	ArrayAdapter<String> citys_adapter,countys_adapter,jiage_adapter;
	ArrayList<String> citys=new ArrayList<String>();
	ArrayList<String> countys=new ArrayList<String>();
	ArrayList<String> jiages=new ArrayList<String>();
	static HashMap<Integer, ArrayList<String>> link=new HashMap<Integer, ArrayList<String>>();
	
	ArrayList<TenementBean> beans = new ArrayList<TenementBean>();
	
	String[] jiage={
			"0-500",
			"500-800",
			"800-1000",
			"1000-1500",
			"1500-2000",
			"2000-3000",
			"3000-5000",
			"5000以上"
	};
	
	private boolean quyu_flag=true;
	private boolean jiage_flag=true;
	private boolean[] opt_flag=new boolean[]{true,false,false};//顺序为城市，区域，价格
	private boolean isClear=true;//是否需要清空数据源
	private boolean qy_t_c=true;//改变菜单头字体颜色
	private boolean jg_t_c=true;//改变菜单头字体颜色
	private boolean load_more=false;//改变菜单头字体颜色
	
	private final static int GET=0x91;
	private final static int CITY_CHANGE=0x94;
	private final static int COUNTY_CHANGE=0x95;
	private final static int DATA_OK=0x96;
	private final static int ERROR=0x97;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_for_shai_xuan);
		
		if(getIntent()!=null&&getIntent().hasExtra("city")){
			System.out.println(">>>>>>>>>>getIntent");
			intent=getIntent();
			city=intent.getStringExtra("city");
			System.out.println(">>>>>>>>>>city= "+city);
			handler.sendEmptyMessage(GET);
		}
		
		goBcak=(ImageView) findViewById(R.id.icon_break);
		jumpMap=(TextView) findViewById(R.id.jump_map);
		quYuMenu=(TextView) findViewById(R.id.quyu);
		jiaGeMenu=(TextView) findViewById(R.id.jiage);
		
		menu=(RelativeLayout) findViewById(R.id.menu);
		leftList=(ListView) findViewById(R.id.left_list);
		rigthList=(ListView) findViewById(R.id.rigth_list);
		jiageList=(ListView) findViewById(R.id.jg);
		content=(LoadListView) findViewById(R.id.listview);
		bottom=(ImageView) findViewById(R.id.bottom);
		
		content.setInterface(this);
		
		for(int i=0;i<jiage.length;i++)
			jiages.add(jiage[i]);
		
		content_adapter=new TenementAdapter(this, beans);
		content.setAdapter(content_adapter);
		citys_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, citys);
		leftList.setAdapter(citys_adapter);
		countys_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countys);
		rigthList.setAdapter(countys_adapter);
		jiage_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, jiages);
		jiageList.setAdapter(jiage_adapter);
		
		goBcak.setOnClickListener(this);
		jumpMap.setOnClickListener(this);
		quYuMenu.setOnClickListener(this);
		jiaGeMenu.setOnClickListener(this);
		
		leftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println(">>leftList.setOnItemClickListener position= "+position);
				countys.clear();
				countys.addAll(link.get(position));
				System.out.println("countys==null  "+(countys==null));
				System.out.println(countys.get(1));
				rigthList.setVisibility(View.VISIBLE);
				handler.sendEmptyMessage(COUNTY_CHANGE);
			}
		});
		
		rigthList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				county=countys.get(position);
				System.out.println(county);
				quYuMenu.setText(county);
				quYuMenu.setTextColor(0xff575757);
				qy_t_c=true;
				quyu_flag=true;
				allGone();
				params=new HttpParams();
				params.put("qy", county);
				opt_flag[0]=false;
				opt_flag[1]=true;
				opt_flag[2]=false;
				isClear=true;
				HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "opt_query_for_qy_do_fw_info.php", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>jsonObject= "+jsonObject);
						//处理返回数据
						error = jsonObject.optInt("error");
						if (error == 0) {
							System.out.println(">>>>>>>>>error==0");
							JSONArray jsonArray = jsonObject.optJSONArray("list");
							zhuZhuangData(jsonArray);
						} else {
							msgs = jsonObject.optString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					}
				});
			}
		});
		
		jiageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				jiaGeMenu.setText(jiage[position]);
				jiaGeMenu.setTextColor(0xff575757);
				jg_t_c=true;
				jiage_flag=true;
				allGone();
				params=new HttpParams();
				opt_flag[0]=false;
				opt_flag[1]=false;
				opt_flag[2]=true;
				isClear=true;
				switch(position){
				case 0:
					params.put("l", 0);
					params.put("r", 500);
					break;
				case 1:
					params.put("l", 500);
					params.put("r", 800);
					break;
				case 2:
					params.put("l", 800);
					params.put("r", 1000);
					break;
				case 3:
					params.put("l", 1000);
					params.put("r", 1500);
					break;
				case 4:
					params.put("l", 1500);
					params.put("r", 2000);
					break;
				case 5:
					params.put("l", 2000);
					params.put("r", 3000);
					break;
				case 6:
					params.put("l", 3000);
					params.put("r", 5000);
					break;
				case 7:
					params.put("l", 5000);
					break;
				}
				if(params.get(0)!=null){
					HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "opt_query_for_money_do_fw_info.php", params, new AsyncHttpResponseHandler(){
						public void onSuccess(JSONObject jsonObject) {
							System.out.println(">>jsonObject= "+jsonObject);
							//处理返回数据
							error = jsonObject.optInt("error");
							if (error == 0) {
								System.out.println(">>>>>>>>>error==0");
								JSONArray jsonArray = jsonObject.optJSONArray("list");
								zhuZhuangData(jsonArray);
							} else {
								msgs = jsonObject.optString("msg");
								handler.sendEmptyMessage(ERROR);
							}
						}
					});
				}
			}
		});
	}
	
	private void zhuZhuangData(JSONArray jsonArray) {
		System.out.println("》》》》》》》》》》》》》组装数据《《《《《《《《《《《《《");
		if(isClear)
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
		handler.sendEmptyMessage(DATA_OK);
	}

	private void allGone(){
		menu.setVisibility(View.GONE);
		leftList.setVisibility(View.GONE);
		rigthList.setVisibility(View.GONE);
		jiageList.setVisibility(View.GONE);
		bottom.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		allGone();
		switch(v.getId()){
		case R.id.bottom:
			break;
		case R.id.icon_break:
			finish();
			break;
		case R.id.jump_map:
			//跳转地图
			Intent intent=new Intent(this, LocMap.class);
			startActivity(intent);
			break;
		case R.id.quyu:
			if(qy_t_c){
				jg_t_c=true;
				jiaGeMenu.setTextColor(0xff575757);
				quYuMenu.setTextColor(Color.GREEN);
			}else{
				quYuMenu.setTextColor(0xff575757);
			}
			qy_t_c=!qy_t_c;
			if(citys.size()>0&&quyu_flag){
				quyu_flag=false;
				jiage_flag=true;
				menu.setVisibility(View.VISIBLE);
				leftList.setVisibility(View.VISIBLE);
				bottom.setVisibility(View.VISIBLE);
			}else if(citys.size()>0&&!quyu_flag){
				quyu_flag=true;
			}else if(citys.size()==0){
				Toast.makeText(this, "暂时没有获取到位置信息,请稍后重试", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.jiage:
			if(jg_t_c){
				qy_t_c=true;
				quYuMenu.setTextColor(0xff575757);
				jiaGeMenu.setTextColor(Color.GREEN);
			}else{
				jiaGeMenu.setTextColor(0xff575757);
			}
			jg_t_c=!jg_t_c;
			if(jiage_flag){
				quyu_flag=true;
				jiage_flag=false;
				menu.setVisibility(View.VISIBLE);
				jiageList.setVisibility(View.VISIBLE);
				bottom.setVisibility(View.VISIBLE);
			}else if(!jiage_flag){
				jiage_flag=true;
			}
			break;
		}
	}

	Handler handler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case ERROR:
				Toast.makeText(ShaiXuanActivity.this, msgs, Toast.LENGTH_SHORT).show();
				break;
			case DATA_OK:
				if(beans.size()!=0){
					content_adapter.notifyDataSetChanged();
					if(load_more){
						content.loadComplete();
						load_more=false;
					}
				}
				break;
			case COUNTY_CHANGE:
				countys_adapter.notifyDataSetChanged();
				break;
			case CITY_CHANGE:
				citys_adapter.notifyDataSetChanged();
				break;
			case GET:
				/**
				 * 上传province省地址获取到
				 */
				params=new HttpParams();
				params.put("city", city);
				isClear=true;
				HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "query_data_fw_info.php", params, new AsyncHttpResponseHandler(){
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>jsonObject= "+jsonObject);
						//处理返回数据
						error = jsonObject.optInt("error");
						if (error == 0) {
							System.out.println(">>>>>>>>>error==0");
							JSONArray jsonArray = jsonObject.optJSONArray("list");
							zhuZhuangData(jsonArray);
						} else {
							msgs = jsonObject.optString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					}
				});
				HttpParams params1=new HttpParams();
				params1.put("province", city);
				HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "get_city_and_county.php", params1, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>>>jsonObject="+jsonObject);
						if(jsonObject.optInt("error")==0){
							JSONArray jsonArray=jsonObject.optJSONArray("city_list");
							initLinks();
							for(int i=0;i<jsonArray.length();i++){
								JSONObject object=jsonArray.optJSONObject(i);
								String city=object.optString("city");
								citys.add(city);
								JSONArray array=object.optJSONArray("county");
								ArrayList<String> cty=new ArrayList<String>();
								for(int j=0;j<array.length();j++){
									JSONObject jsonObject2=array.optJSONObject(j);
									String county=jsonObject2.optString("Name");
									System.out.println(">>>>>>>>>>>>"+county);
									cty.add(county);
								}
								System.out.println("组装cty"+cty.size()+" i= "+i);
								link.put(i, cty);
							}
							handler.sendEmptyMessage(CITY_CHANGE);
						}else{
							msgs=jsonObject.optString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					}
				});
				break;
			}
			return true;
		}
	});
	
	private void initLinks(){
		citys.clear();
		countys.clear();
		link.clear();
	}

	@Override
	public void onLoad() {
		getLoadData();
	}

	private void getLoadData() {
		String url=null;
		if(opt_flag[0]){
			url="query_data_fw_info.php";
		}else if(opt_flag[1]){
			url="opt_query_for_qy_do_fw_info.php";
		}else if(opt_flag[2]){
			url="opt_query_for_money_do_fw_info.php";
		}
		if(url!=null&&params!=null){
			isClear=false;
			HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, url, params, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(JSONObject jsonObject) {
					System.out.println(">>jsonObject= "+jsonObject);
					//处理返回数据
					error = jsonObject.optInt("error");
					if (error == 0) {
						System.out.println(">>>>>>>>>error==0");
						JSONArray jsonArray = jsonObject.optJSONArray("list");
						zhuZhuangData(jsonArray);
						load_more=true;
					} else {
						msgs = jsonObject.optString("msg");
						handler.sendEmptyMessage(ERROR);
					}
				}
			});
		}
	}
	
}
