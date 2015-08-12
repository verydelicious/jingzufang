package com.team.jingzufang;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.team.jingzufang.R;

public class ProgressBarTest extends Activity {

	ProgressBar progressBar1;
	ProgressBar progressBar2;
	boolean flag = true;
	
	SeekBar seekBar;

	RatingBar ratingBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress);
		progressBar1 = (ProgressBar) findViewById(R.id.progressbar1);
		progressBar2 = (ProgressBar) findViewById(R.id.progressbar3);
		seekBar=(SeekBar) findViewById(R.id.seekbar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.d("r&c", "ֹͣ�϶�������");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.d("r&c", "��ʼ�϶�������");
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.d("r&c", "progress="+progress+" fromUser="+fromUser);
			}
		});
		
		ratingBar=(RatingBar) findViewById(R.id.ratingbar);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) {
				Log.d("r&c", "��������="+rating);
			}
		});
		new MyThread().start();
	}

	// ��Ϣ����
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 12:
				int progress = progressBar1.getProgress();
				int secondaryProgress = progressBar1.getSecondaryProgress();
				progressBar1.setProgress((progress + 2) % 101);
				progressBar1.setSecondaryProgress((secondaryProgress + 2) % 101);
				
				int progress1 = progressBar2.getProgress();
				int secondaryProgress1 = progressBar2.getSecondaryProgress();
				progressBar2.setProgress((progress1 + 2) % 101);
				progressBar2.setSecondaryProgress((secondaryProgress1 + 2) % 101);
				
				int pro=seekBar.getProgress();
				seekBar.setProgress((pro+2)%101);
				
				break;
			}
			return true;
		}
	});

	class MyThread extends Thread {
		@Override
		public void run() {
			while (flag) {
				// ����һ����Ϣ
				Message message = new Message();
				message.what = 12;
				// ������Ϣ
				handler.sendMessage(message);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag = false;
	}

}
