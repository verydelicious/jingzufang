package com.xiangmu.tenement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.jingzufang.R;

public class JzfButtonFragment extends Fragment implements OnClickListener {
	
	public static interface OnJzfBottomClickListener {
		void onBottomClick(View v, int position);
	}

	public static final int SHOUYE_POSITION = 0;
	public static final int GRZX_POSITION = 1;
	private OnJzfBottomClickListener bottomClickListener;

//	ImageView shouye;
//	ImageView gerenzhongxin;

	TextView SY;
	TextView GRZX;
	
	RelativeLayout rl1;
	RelativeLayout rl2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.jzfbutton, null);
		
		SY = (TextView) view.findViewById(R.id.SY);
		GRZX = (TextView) view.findViewById(R.id.GRZX);
//		shouye = (ImageView) view.findViewById(R.id.shouye);
//		gerenzhongxin = (ImageView) view.findViewById(R.id.gerenzhongxin);
		rl1=(RelativeLayout) view.findViewById(R.id.rl1);
		rl2=(RelativeLayout) view.findViewById(R.id.rl2);
		rl1.setOnClickListener(this);
		rl2.setOnClickListener(this);

		return view;
	}

	// 设置事件监听
	public void setJzfBottomClickListener(OnJzfBottomClickListener bottomClickListener) {
		this.bottomClickListener = bottomClickListener;
	}

	public void onClick(View v) {
		int position = 0;
		switch (v.getId()) {
		case R.id.rl1:
			SY.setBackgroundColor(Color.parseColor("#ff1c76fd"));
			GRZX.setBackgroundColor(Color.parseColor("#ffffffff"));
			position=0;
			break;
		case R.id.rl2:
			SY.setBackgroundColor(Color.parseColor("#ffffffff"));
			GRZX.setBackgroundColor(Color.parseColor("#ff1c76fd"));
			position=1;
			break;
		}
		if (bottomClickListener != null) {
			bottomClickListener.onBottomClick(v, position);
		}
	}
	
	public void setSelected(int position){
		switch(position){
		case 0:
			SY.setBackgroundColor(Color.parseColor("#ff1c76fd"));
			GRZX.setBackgroundColor(Color.parseColor("#ffffffff"));
			break;
		case 1:
			SY.setBackgroundColor(Color.parseColor("#ffffffff"));
			GRZX.setBackgroundColor(Color.parseColor("#ff1c76fd"));
			break;
		}
	}
}
