package com.xiangmu.tenement;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.listery.networkbitmap.BitmapUtil;
import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;
import com.team.jingzufang.R;

public class TenementAdapter extends BaseAdapter{
	private String URL = "http://jzf123.hk3020.hndan.com/jingzufangserver/";
	ArrayList<TenementBean> list;
	LayoutInflater inflater;
	private static final int GET_PHOTO=0x11;
	public static final int DOWNLOAD_TAG = R.id.bmd__image_downloader;
	public TenementAdapter(Context context, ArrayList<TenementBean> list1) {
		this.list= list1;
		inflater = LayoutInflater.from(context);		
	}
	
	public int getCount() {
		if(list != null) {
			return list.size();
		}
		return 0;
	}

	public Object getItem(int arg0) {
		if(list !=null&& arg0 <list.size()){
			return list.get(arg0);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println(">>>>>posititon"+position);
		System.out.println(">>>>>list.size()"+list.size());
		Holder holder;
		if(convertView == null){
			System.out.println(">>>>>>>>>>>>>>>>>>>convertView == null<<<<<<<<<<<<<<<<");
			holder = new Holder();
			convertView = inflater.inflate(R.layout.jianjie, parent, false);
			holder.loupanicon = (ImageView) convertView.findViewById(R.id.loupanicon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.fwdz = (TextView) convertView.findViewById(R.id.dizhi1);
			holder.xqm = (TextView) convertView.findViewById(R.id.dizhi2);			
			holder.hx = (TextView) convertView.findViewById(R.id.status);
			holder.czlx = (TextView) convertView.findViewById(R.id.zufangxingshi);
			holder.czjg = (TextView) convertView.findViewById(R.id.price);
			convertView.setTag(holder);	
		}else{
			System.out.println(">>>>>>>>>>>>>>>>>>>convertView != null<<<<<<<<<<<<<<<<");
			holder = (Holder) convertView.getTag();
		}
		holder.loupanicon.setTag(DOWNLOAD_TAG, null);
		Resources resources = convertView.getResources();
		Drawable drawable = resources.getDrawable(R.drawable.loupanicon_photo_in_loading);		
		holder.loupanicon.setImageDrawable(drawable);
		if(position < list.size()){
			System.out.println(">>>>>>>>>>>>>>>>>>>数据加载<<<<<<<<<<<<<<<<");			
			if((list.get(position).photos_dir)!=null){
				final ImageView imageView=holder.loupanicon;
				//holder.loupanicon.setImageResource(list.get(position).loupanicon);				
				String photos_dir=list.get(position).photos_dir;
				HttpParams params=new HttpParams();
				System.out.println(">>>>>>>>photos_dir="+photos_dir);
				params.put("photos_dir", photos_dir);
				HttpClientUtils.getInstance().post(URL, "get_photos_file_list.php", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>>>>jsonObject="+jsonObject+"<<<<<<<<<<<<<<<<");
						try {
							if(jsonObject.getInt("error")==0){
								JSONArray jsonArray=jsonObject.getJSONArray("list");
								JSONObject object=jsonArray.getJSONObject(0);
								String path=object.getString("file_parent")+"/"+object.getString("file_name");
								System.out.println(">>>>>>>>>path="+path+"this is file path");
								ArrayList<Object> objects=new ArrayList<Object>();
								objects.add(path);
								objects.add(imageView);
								Message message=new Message();
								message.what=GET_PHOTO;
								message.obj=objects;
								handler.sendMessage(message);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}else{
				System.out.println("该房屋没有描述图片");
			}
			holder.title.setText(list.get(position).title);
			holder.fwdz.setText(list.get(position).fwdz);
			holder.xqm.setText(list.get(position).xqm);			
			holder.hx.setText(list.get(position).hx);
			holder.czlx.setText(list.get(position).czlx);
			holder.czjg.setText(String.valueOf(list.get(position).czjg));
		}
		return convertView;
	}
	class Holder{
		ImageView loupanicon;//保存楼盘图标
		TextView title;//保存楼盘名字		
		TextView fwdz;//保存地址
		TextView xqm;
		TextView hx;//保存楼盘状态
		TextView czlx;//保存住房形式
		TextView czjg;//保存楼盘单价
	}
//	public void onDateChange(ArrayList<TenementBean> list){
//		System.out.println("list3到底传过来了没得！！！！！！！！！！！！！！！");
//		this.list = list;		
//		//当有变化的时候，通知界面刷新数据
//		this.notifyDataSetChanged();
//	}
	
	Handler handler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case GET_PHOTO:
				if(msg.obj!=null){
					@SuppressWarnings("unchecked")
					ArrayList<Object> objects=(ArrayList<Object>) msg.obj;
					String path=(String) objects.get(0);
					ImageView imageView=(ImageView) objects.get(1);
					BitmapUtil.getInstance().download(URL, path, imageView);
				}
				break;
			}
			return true;
		}
	});		
	
}
