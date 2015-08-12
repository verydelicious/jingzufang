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
import android.widget.Toast;

public class UserModThread extends HttpThreadInt{
	private StringBuilder urls = new StringBuilder();
	private String json;
	private UserEntity userEntity;
	private Handler handler;
	public UserModThread(String host, String add, Handler handler, String... arg) {
		super(host, add);
		this.handler = handler;
		urls.append(super.getUrl());
		for(String str : arg){
			urls.append(str);
		}
		super.setUrl(urls.toString());
	}
	public void echoUrls(){
		System.out.println(urls.toString());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>>>>>>>");
		doGet();
		json = super.getJson();
		if(doJson(json)){
			doToJson();
			System.out.println("解析成功");
			Message message = new Message();
			message.what=4;
			message.arg1 = 0;
			message.obj = userEntity;
			handler.sendMessage(message);
		}else{
			Message message = new Message();
			message.arg1 = -1;
			handler.sendMessage(message);
			System.out.println("解析失败");
		}
	}
	public void doToJson(){
		try {
			JSONObject root = new JSONObject(json);
			JSONObject sub = root.getJSONObject("list");
			String id = sub.getString("u_id");
			String passwd = sub.getString("passwd");
			String sjh = sub.getString("sjh");
			String gerden = sub.getString("gerden");
			String flag = sub.getString("flag");
			String name = sub.getString("name");
			String tx = sub.getString("tx_icon");
			userEntity = new UserEntity(id, passwd, sjh, name, gerden, flag, tx);
			System.out.println(userEntity.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
