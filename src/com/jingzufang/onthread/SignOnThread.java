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
import android.widget.Toast;

public class SignOnThread extends HttpThreadInt{
	private static final String TAG = "onthread";
	private StringBuilder urls = new StringBuilder();
	private String json;
	private UserEntity userEntity;
	private Handler handler;
	public SignOnThread(String host, String add, Handler handler, String... arg) {
		super(host, add);
		this.handler = handler;
		urls.append(super.getUrl());
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
			if(doTo2Json()){
				Log.i(TAG, "==>解析成功0");
			}else{
				Log.i(TAG, "==>解析成功1");
				doToJson();
			}
		}else{
			Message message = new Message();
			message.obj = null;
			message.arg2 = -1;
			message.what=1;
			handler.sendMessage(message);
			Log.i(TAG, "===>解析失败");
		}
	}
	public void doToJson(){
		try {
			JSONObject root = new JSONObject(json);
			JSONArray listarray = root.getJSONArray("list");
			for(int i=0; i<listarray.length(); i++){
				JSONObject sub = listarray.getJSONObject(i);
				String id = sub.getString("u_id");
				String passwd = sub.getString("passwd");
				String sjh = sub.getString("sjh");
				String gerden = sub.getString("gerden");
				String flag = sub.getString("flag");
				String name = sub.getString("name");
				String tx = sub.getString("tx");
				userEntity = new UserEntity(id, passwd, sjh, name, gerden, flag, tx);
				Log.i(TAG, "Entity-->" + userEntity.toString());
				Message message = new Message();
				message.obj = userEntity;
				message.arg1 = 0;
				message.what=1;
				handler.sendMessage(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public boolean doTo2Json(){
		try {
			JSONObject root = new JSONObject(json);
			JSONArray listarray = root.getJSONArray("list");
			for(int i=0; i<listarray.length(); i++){
				JSONObject sub = listarray.getJSONObject(i);
				int exist = sub.getInt("exist");
				if(exist==1){
					Log.i(TAG, "===>存在用户");	
					Message message = new Message();
					message.arg1 = 1;
					message.what=1;
					handler.sendMessage(message);
				}else{
					Log.i(TAG, "===>不存在用户");
					Message message = new Message();
					message.arg1 = -1;
					message.what=1;
					handler.sendMessage(message);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
