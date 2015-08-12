package com.jingzufang.activitys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jingzufang.bean.UserEntity;
import com.jingzufang.onthread.SignOnThread;
import com.jingzufang.onthread.UserUpdataAvatar;
import com.jingzufang.view.Title;
import com.team.jingzufang.R;
import com.xiangmu.tenement.TenementViewPager;

public class SignOnAty extends FragmentActivity implements OnClickListener{
	private static final String TAG = "signon";
	//组件
	private Title title;//标题栏
	private EditText user;//用户名输入框
	private EditText pass;//密码输入框
	private Button signon;//登录按钮
	//用户信息
	private String name;
	private String passwd;
	//资源
	private UserEntity userEntity;//用户实体
	private File sdFile;
	private File sdDir;
	private String sdPath = Environment.getExternalStorageDirectory().getAbsoluteFile().toString();
	private Bitmap bitmap;
	//网络地址
	private static final String BITHOST = "http://jzf123.hk3020.hndan.com/jingzufangserver/";
	private static final String HOST = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD = "jingzufangserver/user_query_user.php";
	private String arg1 = "sjh=";//请求参数1
	private String arg2 = "&passwd=";//请求参数2
	//Handler
	private Handler handler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:
				if(msg.obj == null){
					Log.i(TAG, "msg.obj==>null");
					echo("请求失败，请检查网络");
					
				}else{
					userEntity = (UserEntity) msg.obj;
					Log.i(TAG, "Entity--->" + userEntity.toString());
					if(!(userEntity.getTx().equals("null"))){
						getAvatar(BITHOST + userEntity.getTx());
						echo("登录成功，正在获取信息");
					}
					else{
						Log.i(TAG, "头像=null");
						getAvatar(BITHOST + "upload_files/user_headers/default.jpg");
						echo("登录成功，正在获取信息");
					}
				}
				break;
			case 5:
				Log.i(TAG, "===>获取到Bitmap");
				bitmap = (Bitmap) msg.obj;
				bitFile();
				login();
				echo("信息获取成功");
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.sign_on);
		initView();
		
	}
	public void initView(){
		title = (Title) findViewById(R.id.title2);
		title.setOnBack();
		title.setBackOnClick(this);
		title.setTitle("登录");
		user = (EditText) findViewById(R.id.user_name);
		pass = (EditText) findViewById(R.id.user_passwd);
		signon = (Button) findViewById(R.id.sign_on);
		signon.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sign_on:
			name = user.getText().toString();
			passwd = pass.getText().toString();
			SignOnThread sot = new SignOnThread(HOST, REQUEST_ADD,handler,arg1, name, arg2, passwd);
			sot.start();
			break;
		default:
			finish();
			break;
		}
	}
	public void login(){
		SharedPreferences sp = getSharedPreferences("user_infor", Activity.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt("userId", Integer.parseInt(userEntity.getId()));//用户id
		edit.putString("name", userEntity.getName());//用户名
		edit.putString("pnumber", userEntity.getSjh());//手机号
		edit.putString("passwd", userEntity.getPasswd());//密码
		edit.putString("type", userEntity.getFlag());//类型
		edit.putString("gerden", userEntity.getGerden());//性别
		edit.putString("tx", sdFile.getAbsolutePath().toString());//头像
		edit.commit();
		Intent intent = new Intent();
		intent.setClass(this, TenementViewPager.class);
		startActivity(intent);
	}
	public void echo(String str){
		Toast.makeText(SignOnAty.this, str, Toast.LENGTH_SHORT).show();
	}
	//获取头像
	public void getAvatar(String url){
		UserUpdataAvatar uua = new UserUpdataAvatar(url, handler);
		uua.start();
	}
	//bitmap转存到本地
	public void bitFile(){
		sdDir = new File(sdPath + "/jingzufang/user/");
		if(!sdDir.exists()){
			sdDir.mkdirs();
		}
		sdFile = new File(sdPath + "/jingzufang/user/", userEntity.getSjh()+ "_Avatar_.jpg");
		
		try {
			FileOutputStream fos = new FileOutputStream(sdFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			Log.i(TAG, "===>图片保存成功");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
