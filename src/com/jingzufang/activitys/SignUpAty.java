package com.jingzufang.activitys;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jingzufang.bean.UserEntity;
import com.jingzufang.onthread.SignOnThread;
import com.jingzufang.view.Title;
import com.team.jingzufang.MyDialog;
import com.team.jingzufang.R;

public class SignUpAty extends FragmentActivity implements OnClickListener{
	private static final String TAG = "SignUpAty";
	//组件
	private Title title;//标题栏
	private Button signUp;//注册按钮
	private EditText nums;//输入框（用户名）
	private EditText pass;//输入框（密码）
	private Button signon;//登录按钮
	//用户信息
	private String number;//号码
	private String passwd;//密码
	//资源
	private UserEntity userEntity;
	//网络地址
	private static final String HOST = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD = "jingzufangserver/user_query_user_bc.php";
	private String arg1 = "sjh=";//请求参数1
	//Handler
	private Handler handler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				if(msg.arg1==-1){
					Log.i(TAG, "msg.arg1--->" + msg.arg1);
					showDialog();
				}else if(msg.arg2 == -1){
					Log.i(TAG, "msg.arg2--->" + msg.arg2);
					echo("网络连接失败！");
				}else if(msg.arg1 == 1){
					Log.i(TAG, "msg.arg1--->" + msg.arg1);
					echo("该手机号码已注册");
				}
				break;
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		init();
	}
	//初始化
	public void init(){
		//初始化控件
		title = (Title) findViewById(R.id.titlev);
		signUp = (Button) findViewById(R.id.sign_up);
		signon = (Button) findViewById(R.id.login);
		nums = (EditText) findViewById(R.id.number);
		pass = (EditText) findViewById(R.id.pass);
		title.setTitle("手机注册");//设置标题栏
		title.setOnBack();//设置返回按钮
		title.setBackOnClick(this);
		signUp.setOnClickListener(this);//注册按钮点击事件
		signon.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.sign_up:
			number = nums.getText().toString();
			passwd = pass.getText().toString();
			//判断正确输入
			if(number.length()<11 ){
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
				break;
			}
			else if(passwd.length()<6){
				Toast.makeText(this, "请设置6-16位的密码", Toast.LENGTH_SHORT).show();
				break;
			}
			SignOnThread sot = new SignOnThread(HOST, REQUEST_ADD,handler,  arg1, number);
			sot.start();
			break;
		case R.id.login:
			intent.setClass(this, SignOnAty.class);
			startActivity(intent);
			break;
		default:
			finish();
			break;
		}
	}
	public void showDialog(){
		final Intent intent = new Intent();
		MyDialog dialog = new MyDialog(this);
		dialog.setTitle("请选择用户类型");
		dialog.setPositiveButton("我是租客", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "item--->0");
				Bundle bundle = new Bundle();
				bundle.putString("number", number);
				bundle.putString("passwd", passwd);
				bundle.putInt("type", 1);
				intent.setClass(SignUpAty.this, SendSmsAty.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		dialog.setNegativeButton("我是房东", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "item--->1");
				Bundle bundle2 = new Bundle();
				bundle2.putString("number", number);
				bundle2.putString("passwd", passwd);
				bundle2.putInt("type", 0);
				intent.setClass(SignUpAty.this, SendSmsAty.class);
				intent.putExtras(bundle2);
				startActivity(intent);
			}
		});
		dialog.show();
	}
	public void echo(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
}
