package com.jingzufang.activitys;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.jingzufang.bean.UserEntity;
import com.jingzufang.onthread.UserModThread;
import com.jingzufang.view.Title;
import com.team.jingzufang.R;

public class UserModAty extends Activity implements OnClickListener, OnCheckedChangeListener{
	private Title title;
	private EditText edit;
	private String passwd;//密码
	private String username;//用户名
	private int sex;//性别
	private String pnum;//手机号码
	private String tx;
	private RadioGroup rg;
	private Button but;
	
	private RadioButton rb_man;
	private RadioButton rb_woman;
	private UserEntity userEntity;
	//网络地址
	private static final String HOST = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD = "jingzufangserver/user_mod_up.php";
	private String arg1 = "sjh=";
	private String arg2 = "&passwd=";
	private String arg3 = "&name=";
	private String arg4 = "&gerden=";
	private String arg5 = "&tx=";
	private Handler handler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.obj != null){
				userEntity = (UserEntity) msg.obj;
				System.out.println("---->" + userEntity.toString());
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						echoInfo("修改成功");
					}
				});
				updata();
			}else if(msg.arg1==1){
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						echoInfo("修改失败");
					}
				});
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_mod);
		init();
		getData();
		edit.setText(username);
		if(sex == 1){
			rb_man.setChecked(true);
		}else if(sex == 0){
			rb_woman.setChecked(true);
		}
	}
	public void init(){
		title = (Title) findViewById(R.id.title4);
		title.setTitle("修改");
		title.setOnBack();
		title.setBackOnClick(this);
		edit = (EditText) findViewById(R.id.ed_0);
		rg = (RadioGroup) findViewById(R.id.mod_rg);
		but = (Button) findViewById(R.id.mod_ok);
		rb_man = (RadioButton) findViewById(R.id.man);
		rb_woman = (RadioButton) findViewById(R.id.woman);
		rg.setOnCheckedChangeListener(this);
		but.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.back_icon:
			finish();
			break;
		case R.id.mod_ok:
			username = edit.getText().toString();
			try {
				username=URLEncoder.encode(username, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			UserModThread umt = new UserModThread(HOST, REQUEST_ADD, handler, arg1, pnum, arg2, passwd, arg3,username, arg4, String.valueOf(sex), arg5, tx);
			umt.start();
			finish();
			break;
		}
	}
	public void getData(){
		Bundle bundle = getIntent().getExtras();
		pnum = bundle.getString("pnumber");
		passwd = bundle.getString("passwd");
		username = bundle.getString("uname");
		sex = bundle.getInt("gerden");
		tx = bundle.getString("tx");
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId){
		case R.id.man:
			sex = 1;
			System.out.println(sex);
			break;
		case R.id.woman:
			sex = 0;
			System.out.println(sex);
			break;
		}
		
	}
	public void updata(){
		Intent intent = new Intent();
		intent.setClass(this, UserAty.class);
		startActivity(intent);
	}
	public void echoInfo(String msg){
		Toast.makeText(UserModAty.this, msg, Toast.LENGTH_SHORT).show();
	}
}
