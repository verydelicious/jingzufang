package com.jingzufang.onthread;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Handler;
import android.os.Message;

public class UserModAvatar extends HttpThreadInt{
	private Handler handler;
	private String path;
	public UserModAvatar(String url, String path, Handler handler) {
		super(url);
		this.path = path;
		this.handler = handler;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		uploadFile(path);
		String json = getJson();
		if(doJson(json)){
			System.out.println("解析成功");
			doToJson(json);
		}
		else{
			System.out.println("解析失败");
		}
	}
	public void doToJson(String json){
		System.out.println("json===>" + json);
		JSONObject root = null;
		try {
			root = new JSONObject(json);
			String path = root.getString("path");
			Message message = new Message();
			message.what = 3;
			message.obj = path;
			handler.sendMessage(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
