package com.jingzufang.activitys;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingzufang.adapter.MyAdapter;
import com.jingzufang.bean.ItemBean;
import com.jingzufang.bean.UserEntity;
import com.jingzufang.onthread.SignOnThread;
import com.jingzufang.onthread.UserModAvatar;
import com.jingzufang.onthread.UserModThread;
import com.jingzufang.onthread.UserUpdataAvatar;
import com.jingzufang.view.Title;
import com.team.jingzufang.R;

public class UserAty extends FragmentActivity implements OnClickListener, OnItemLongClickListener{
	ArrayList<String> photos=new ArrayList<String>();
	private String name;//用户名
	private String pnumber;//手机号码
	private String passwd;//密码
	private String type;//类型
	private String gerden;//性别
	private String tx;//头像
	private UserEntity userEntity = null;//= new UserEntity("", "", "", "", "1", "1", "");
	private static String path;
	private Bitmap txbit;
	//数据源
	private File sdFile;
	private File sdDir;
	private ListView list;
	//private MyListView list;
	private Title title;
	private TextView tv;
	private ImageView userIcon;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private String[] titles = {"用户名", "性别", "用户类型", "手机号码"}; 
	private String[] contexts = new String[titles.length];
	private MyAdapter myAdapter;
	private List<ItemBean> datas = new ArrayList<ItemBean>();
	//组件
	private Button out;
	private SharedPreferences sp;
	//网络地址
	private static final String BITHOST = "http://jzf123.hk3020.hndan.com/jingzufangserver/";
	private static final String HOST = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD = "jingzufangserver/user_query_user.php";
	private String arg1 = "sjh=";
	private String arg2 = "&passwds=";
	private static final String HOST2 = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD2 = "jingzufangserver/user_mod_up.php";
	private String arg10 = "sjh=";
	private String arg20 = "&passwd=";
	private String arg30 = "&name=";
	private String arg40 = "&gerden=";
	private String arg50 = "&tx=";
	private Handler ht = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				if(msg.obj == null){
					System.out.println("null");
					echo("获取信息失败，请检查网络");
					ht.post(new Runnable() {
						@Override
						public void run() {
							getLocalTx();
						}
					});
					
				}else{
					echo("刷新信息成功");
					userEntity = (UserEntity) msg.obj;
					System.out.println(userEntity.toString());
					ht.post(new Runnable() {
						
						@Override
						public void run() {
							init();
							getContext();
							getDatas();
							myAdapter.notifyDataSetChanged();
							list.setAdapter(myAdapter);
						}
					});
					updata();
					if(!(userEntity.getTx().equals("null"))){
						getTx(BITHOST + userEntity.getTx());
					}
					else{
						getTx(BITHOST + "upload_files/user_headers/default.jpg");
					}
				}
				break;
			case 3:
				echo("头像上传成功");
				path =  (String) msg.obj;
				System.out.println(path);
				upTX(path);
				sdFile.delete();
				break;
			case 4:
				if(msg.arg1==0){
					echo("数据库更新成功");
				}else{
					echo("数据库更新失败");
				}
				break;
			case 5:
				txbit = (Bitmap) msg.obj;
				System.out.println("获取到map");
				ht.post(new Runnable() {
					
					@Override
					public void run() {
						userIcon.setImageBitmap(txbit);	
					}
				});
				break;
			
			}
		}
	};
	private String sdPath = Environment.getExternalStorageDirectory().getAbsoluteFile().toString();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_bak);
		getInfor();
		init();
		initList();
	}
	public void init(){
		userIcon=(ImageView)findViewById(R.id.avatar);
		title = (Title) findViewById(R.id.title1);
		tv = (TextView) findViewById(R.id.name);
		list = (ListView) findViewById(R.id.list);
		out = (Button) findViewById(R.id.out);
		title.setOnBack();
		title.setOnAdd();
		title.setTitle("个人中心");
		title.setAddText("修改");
		title.setBackOnClick(this);
		title.setAddOnclick(this);
		tv.setText(userEntity.getName());
		out.setOnClickListener(this);
		userIcon.setOnClickListener(this);
	}
	public void initList(){
		getContext();
		getDatas();
		myAdapter = new MyAdapter(this, datas);
		//adapter = new SimpleAdapter(this, data, R.layout.user_item, from, to);
		list.setAdapter(myAdapter);
		list.setOnItemLongClickListener(this);
	} 
	public void getData(){
		for(int i=0; i<titles.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", titles[i]);
			map.put("context", contexts[i]);
			data.add(map);
		}
	}
	public void getInfor(){
		sp = getSharedPreferences("user_infor", Context.MODE_PRIVATE);
    	pnumber = sp.getString("pnumber", "");
    	passwd = sp.getString("passwd", "");
    	name = sp.getString("name", "");
    	type = sp.getString("type", "");
    	gerden = sp.getString("gerden", "");
    	tx = sp.getString("tx", "");
    	System.out.println(tx);
    	userEntity = new UserEntity("", passwd, pnumber, name, gerden, type, tx);
    	new Thread(){
    		public void run() {
    			ht.post(new Runnable() {
					
					@Override
					public void run() {
						getLocalTx();
					}
				});
    		}
    	}.start();
    	SignOnThread sot = new SignOnThread(HOST, REQUEST_ADD, ht, arg1, pnumber, arg2, passwd);
    	sot.start();
    	echo("正在刷新信息");
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.out:
			doOut();
			break;
		case R.id.add:
			modDialog();
			break;
		case R.id.avatar:
			picker();
			break;
		default:
			finish();
			break;
		}
	}
	public void getContext(){
			contexts[0] = userEntity.getName();
		
		if(Integer.valueOf(userEntity.getGerden())==1){
			contexts[1] = "男";
		}else if(Integer.valueOf(userEntity.getGerden())==0){
			contexts[1] = "女";
		}
		if(Integer.valueOf(userEntity.getFlag()) == 0){
			contexts[2] = "房东";
		}
		else if(Integer.valueOf(userEntity.getFlag()) == 1){
			contexts[2] = "租客";
		}
		contexts[3] = userEntity.getSjh();
	}
	public void doOut(){
		Editor edit = sp.edit();
		edit.remove("name");
		edit.remove("pnumber");
		edit.remove("passwd");
		edit.commit();
		finish();
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		System.out.println("长按" + arg2 + "----" + arg1);
		return true; 
	}
	public void getDatas(){
		datas.clear();
		for(int i=0; i<titles.length; i++){
			ItemBean bean = new ItemBean(titles[i], contexts[i]);
			datas.add(bean);
		}
	}
	public void modDialog(){
		final String[] items = {"修改个人信息", "修改密码", "修改头像"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择修改项");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				switch (which) {
				case 0:
					Bundle bundle = new Bundle();
					bundle.putString("pnumber", userEntity.getSjh());
					bundle.putString("passwd", userEntity.getPasswd());
					bundle.putString("uname", userEntity.getName());
					bundle.putInt("gerden", Integer.valueOf(userEntity.getGerden()));
					bundle.putString("tx", userEntity.getTx());
					intent.setClass(UserAty.this, UserModAty.class);
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				case 1:
					System.out.println(sdPath);
					break;
				case 2:
					picker();
					break;
				}
			}
		});
		builder.create().show();
	}
	public void updata(){
		SharedPreferences sp = getSharedPreferences("user_infor", Activity.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.remove("name");
		edit.remove("pnumber");
		edit.remove("passwd");
		edit.commit();
		edit.putString("name", userEntity.getName());
		edit.putString("pnumber", userEntity.getSjh());
		edit.putString("passwd", userEntity.getPasswd());
		edit.commit();
	}
	@Override
	protected void onResume() {
		super.onResume();
		getInfor();
		init();
		initList();
	}
	//图片选择
	public void picker(){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 512);
		intent.putExtra("outputY", 512);
		sdDir = new File(sdPath + "/jingzufang/temp/");
		if(!sdDir.exists()){
			sdDir.mkdirs();
		}
		sdFile = new File(sdPath + "/jingzufang/temp/", Calendar.getInstance().getTimeInMillis() + "" + ((int)Math.random()*1000) + ".jpg");
		intent.putExtra("output", Uri.fromFile(sdFile));
		startActivityForResult(intent, 11);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode,  data);
		switch (requestCode) {
		case 11:
			if(resultCode == RESULT_OK){
				UserModAvatar uma = new UserModAvatar("http://jzf123.hk3020.hndan.com/jingzufangserver/upload_photo.php",sdFile.getAbsolutePath(), ht);
				uma.start();
				echo("正在上传头像");
			}
			break;
		}
	}
	//更新头像地址
	public void upTX(String path){
		echo("正在更新数据库");
		try {
			String username=URLEncoder.encode(userEntity.getName(), "utf-8");
			UserModThread umt = new UserModThread(HOST2, REQUEST_ADD2, ht, arg10, userEntity.getSjh(), arg20, userEntity.getPasswd(), arg30 ,username, arg40, userEntity.getGerden(), arg50, path);
			umt.start();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	//更新头像显示
	public void getTx(String url){
		UserUpdataAvatar uua = new UserUpdataAvatar(url, ht);
		uua.start();
	}
	//显示信息
	public void echo(String str){
		Toast.makeText(UserAty.this, str, Toast.LENGTH_SHORT).show();
	}
	public void getLocalTx(){
		try {
			FileInputStream fis = new FileInputStream(new File(tx));
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			userIcon.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
