package com.jingzufang.onthread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingzufang.bean.UserEntity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.Toast;

public class SignUpThread extends HttpThreadInt{
	private static final String TAG = "onthread";
	private StringBuilder urls = new StringBuilder();
	private String json;
	private UserEntity userEntity;
	private Handler handler;
	//网络地址
	private static final String HOST = "http://jzf123.hk3020.hndan.com/";
	private static final String REQUEST_ADD = "jingzufangserver/user_query_user.php";
	private String arg1 = "sjh=";
	private String arg2 = "&passwds=";
	public SignUpThread(String host, String add, Handler handler, String... arg) {
		super(host, add);
		urls.append(super.getUrl());
		this.handler = handler;
		for(String str : arg){
			urls.append(str);
		}
		super.setUrl(urls.toString());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		doGet();
		json = super.getJson();
		if(doJson(json)){
			doToJson();
			Log.i(TAG, "===>解析成功");
			Message message = new Message();
			message.what=2;
			message.obj = userEntity;
			
			handler.sendMessage(message);
		}else{
			Log.i(TAG, "===>解析失败");
		}
	}
	public void doToJson(){
		try {
			JSONObject root = new JSONObject(json);
			String sjh = root.getString("sjh");
			String passwd = root.getString("passwd");
			userEntity = new UserEntity("", passwd, sjh, "", "", "", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
