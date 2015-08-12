package com.jingzufang.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingzufang.bean.ItemBean;
import com.team.jingzufang.R;

public class MyAdapter extends BaseAdapter{
	private List<ItemBean> list;
	private LayoutInflater inflater;
	public MyAdapter(Context context, List<ItemBean> list) {
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.user_item, null);
		}
		TextView title = (TextView) convertView.findViewById(R.id.utit);
		TextView context = (TextView) convertView.findViewById(R.id.value);
		title.setText(list.get(position).title);
		context.setText(list.get(position).context);
		return convertView;
	}
}
