package com.jingzufang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.jingzufang.R;

public class Title extends LinearLayout{
	private ImageButton icon;
	private TextView title;
	private Button add;
	public Title(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.title, this);
		icon = (ImageButton) findViewById(R.id.back_icon);
		title = (TextView) findViewById(R.id.title);
		add = (Button) findViewById(R.id.add);
	}
	public void setOnBack(){
		icon.setVisibility(View.VISIBLE);
	}
	public void setOffBack(){
		icon.setVisibility(View.INVISIBLE);
	}
	public void setTitle(String str){
		title.setText(str);
	}
	public void setOnAdd(){
		add.setVisibility(View.VISIBLE);
	}
	public void setOffAdd(){
		add.setVisibility(View.INVISIBLE);
	}
	public void setAddText(String str){
		add.setText(str);
	}
	public void setBackOnClick(OnClickListener listener){
		icon.setOnClickListener(listener);
	}
	public void setAddOnclick(OnClickListener listener){
		add.setOnClickListener(listener);
	}
}
