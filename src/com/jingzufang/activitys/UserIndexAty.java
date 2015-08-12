package com.jingzufang.activitys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingzufang.view.Title;
import com.team.jingzufang.R;

public class UserIndexAty extends Fragment implements android.view.View.OnClickListener{
	//组件
	private Title title;//标题栏
	private LinearLayout item1;//选项一
	private TextView tv;//登录/注册
	private RelativeLayout re;//用户信息
	private TextView tv2;//用户昵称
	private ImageView icon;//显示头像
	private View view;
	//用户信息
	private String name;//用户名
	private String pnumber;//手机号码
	private String tx;//头像地址
	//资源
	private Bitmap bitmap = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.user_content, null);
    	//init();
    	return view;
    }
    //初始化操作
    public void init(){
    	//初始化控件
    	tv = (TextView) view.findViewById(R.id.login);
    	title = (Title) view.findViewById(R.id.title0);
    	re = (RelativeLayout) view.findViewById(R.id.user);
    	tv2 = (TextView) view.findViewById(R.id.uname);
    	icon = (ImageView) view.findViewById(R.id.icon);
    	title.setTitle("我的");
    	item1 = (LinearLayout) view.findViewById(R.id.ll0);
    	item1.setOnClickListener(this);
    	getDate();
    	//判断是否有数据
    	if(!pnumber.equals("")){
    		tv.setVisibility(View.GONE);
    		re.setVisibility(View.VISIBLE);
    		tv2.setText(name);
    		icon.setImageBitmap(getLocalTx(tx));
    	}
    	else{
    		tv.setVisibility(View.VISIBLE);
    		re.setVisibility(View.GONE);
    	}
    }
    //获取数据
    public void getDate(){
    	SharedPreferences sp = getActivity().getSharedPreferences("user_infor", Context.MODE_PRIVATE);
    	name = sp.getString("name", "");
    	pnumber = sp.getString("pnumber", "");
    	tx = sp.getString("tx", "");
    	
    }
    //跳转到用户信息界面
    public void login(){
    	Intent intent = new Intent();
    	intent.setClass(this.getActivity(), UserAty.class);
    	startActivity(intent);
    }
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if(pnumber.equals("")){
			intent.setClass(this.getActivity(), SignUpAty.class);//跳转到注册/登录界面
			startActivity(intent);
		}
		else{
			login();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		init();
    	
	}
	//加载本地头像
	public Bitmap getLocalTx(String txurl){
		try {
			FileInputStream fis = new FileInputStream(new File(txurl));
			bitmap = BitmapFactory.decodeStream(fis);
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
