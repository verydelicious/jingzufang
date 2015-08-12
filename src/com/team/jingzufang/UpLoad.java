package com.team.jingzufang;

import com.team.jingzufang.FaBuActivity.Finish;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class UpLoad extends FragmentActivity implements Finish{

	protected static final int CHANGE = 0x12;
	protected static final int UP_DONE = 0x13;
	
	static UpLoad context;
	ProgressBar upProgress;
	boolean isRun;
	int sec=0;
	SecondProgress progress;
	TextView state;
	
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_data);
		layout=(LinearLayout) findViewById(R.id.liner);
		upProgress=(ProgressBar) findViewById(R.id.up_progress);
		state=(TextView) findViewById(R.id.t1);
		context=this;
		isRun=true;
		progress=new SecondProgress();
	}
	
	public static Context getUpLoad(){
		return context;
	}

	@Override
	public void fin() {
		isRun=false;
		finish();
	}

	@Override
	public void progress(int proNum) {
		System.out.println(">>>>>>>>set progress="+proNum);
		upProgress.setProgress(proNum);
		if(upProgress.getProgress()==100){
			handler.sendEmptyMessage(UP_DONE);
		}
		if(!progress.isAlive()){
			System.out.println(">>>>pro start!!!!");
			progress.start();
		}
	}
	
	Handler handler=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case CHANGE:
				upProgress.setSecondaryProgress(sec);
				break;
			case UP_DONE:
				upProgress.setVisibility(View.GONE);
				state.setVisibility(View.GONE);
				
				ProgressBar progressBar=new ProgressBar(UpLoad.this);
				progressBar.setVisibility(View.VISIBLE);
				LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layout.addView(progressBar, params);

				state=new TextView(UpLoad.this);
				state.setVisibility(View.VISIBLE);
				state.setTextSize(18);
				state.setText("上传完成，等待服务器...");
				state.setTextColor(0xff000000);
				layout.addView(state,params);
				break;
			}
			return true;
		}
	});
	
	class SecondProgress extends Thread{
		@Override
		public void run() {
			while(isRun){
				int pro=upProgress.getProgress();
				sec++;
				if(sec>=pro)
					sec=0;
				handler.sendEmptyMessage(CHANGE);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
