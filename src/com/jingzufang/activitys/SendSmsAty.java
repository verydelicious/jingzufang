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
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.jingzufang.bean.UserEntity;
import com.jingzufang.onthread.SignOnThread;
import com.jingzufang.onthread.SignUpThread;
import com.jingzufang.onthread.UserUpdataAvatar;
import com.jingzufang.view.Title;
import com.team.jingzufang.R;
import com.xiangmu.tenement.TenementViewPager;

public class SendSmsAty extends FragmentActivity implements OnClickListener{

	private static final String TAG = "SendSmsAty";
	//资源
	private File sdFile;
	private File sdDir;
	private String sdPath = Environment.getExternalStorageDirectory().getAbsoluteFile().toString();
	private Bitmap bitmap;
	
	private String number;//手机号码
	private String password;//密码
	private String type;//类型
	private TextView mes;
	private Button button;
	private EditText text;
	private Title title;
	//APPKEY
	private static String APPKEY = "906aa984e80c";
	//APPSECRET
	private static String APPSECRET = "9d30d0647c3b3c3475ddaef05f200d94";
	//验证码
	private String verString;
	//网络地址
	private static final String BITHOST = "http://jzf123.hk3020.hndan.com/jingzufangserver/";
	private static final String HOST = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD = "jingzufangserver/user_register.php";
	private String arg1 = "sjh=";
	private String arg2 = "&passwd=";
	private String arg3 = "&flag=";
	//网络地址2
	private static final String HOST1 = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD1 = "jingzufangserver/user_query_user.php";
	private String arg11 = "sjh=";//请求参数1
	private String arg21 = "&passwds=";//请求参数2
	private UserEntity userEntity = new UserEntity("", "", "", "", "1", "1", "");
	private Handler han = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 2:
				userEntity = (UserEntity) msg.obj;
				Log.i(TAG, userEntity.toString());
				signOn(userEntity.getSjh(), userEntity.getPasswd());
				//login();
				break;
			case 1:
				if(msg.obj == null){
					Log.i(TAG, "msg.obj==>null");
					echo("请求失败，请检查网络");
					
				}else{
					userEntity = (UserEntity) msg.obj;
					Log.i(TAG, userEntity.toString());
					if(!(userEntity.getTx().equals("null"))){
						Log.i(TAG, "Entity.Tx--->" + userEntity.getTx());
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
				login2();
				echo("信息获取成功");
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		mes = (TextView) findViewById(R.id.mes);
		button = (Button) findViewById(R.id.verify);
		text = (EditText) findViewById(R.id.vernum);
		title = (Title) findViewById(R.id.title3);
		title.setTitle("短信验证");
		title.setOnBack();
		title.setBackOnClick(this);
		//获取传递数据
		Bundle bundle = getIntent().getExtras();
		number = bundle.getString("number");
		password = bundle.getString("passwd");
		type = String.valueOf(bundle.getInt("type"));
		mes.setText("正在向"+ number + "发送验证短信");
		init();
		sendSms(number);
		button.setOnClickListener(this);
	}
	public void init(){
		SMSSDK.initSDK(this,APPKEY,APPSECRET);
		EventHandler eh=new EventHandler(){

			@Override
			public void afterEvent(int event, int result, Object data) {
				
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			
		};
		SMSSDK.registerEventHandler(eh);
	}
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 9){
				System.out.println("9999");
			}
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
					db_in();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					Toast.makeText(getApplicationContext(), "验证码发送成功", Toast.LENGTH_SHORT).show();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	//发送验证短信
	public void sendSms(String number){
		SMSSDK.getVerificationCode("86", number);
	}
	//校验验证短信
	public void verifySms(String verNum){
		SMSSDK.submitVerificationCode("86", number, verNum);
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.verify:
			verString = text.getText().toString();
			verifySms(verString);//校验短信
			break;
		default:
			finish();
			break;
		}
	}
	public void db_in(){
		System.out.println(type);
		SignUpThread sut = new SignUpThread(HOST, REQUEST_ADD, han, arg1, number, arg2, password, arg3, type);
		sut.start();
	} 
	public void signOn(String name, String passwd){
		SignOnThread sot = new SignOnThread(HOST1, REQUEST_ADD1,han,arg11, name, arg21, passwd);
		sot.start();
	}
	public void login2(){
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
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	//获取头像
		public void getAvatar(String url){
			UserUpdataAvatar uua = new UserUpdataAvatar(url, han);
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
