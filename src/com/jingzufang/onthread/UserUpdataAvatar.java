package com.jingzufang.onthread;


import android.os.Handler;
import android.os.Message;

public class UserUpdataAvatar extends HttpThreadInt{
	private Handler handler;
	public UserUpdataAvatar(String url, Handler handler) {
		super(url);
		// TODO Auto-generated constructor stub
		this.handler=handler;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(dlTX()){
			System.out.println("获取成功");
			Message message = new Message();
			message.what=5;
			message.obj = getBit();
			handler.sendMessage(message);
			System.out.println("发送成功");
		}else{
			System.out.println("获取失败");
		}
	}
}
