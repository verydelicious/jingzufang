package com.team.jingzufang;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.team.http.AsyncHttpResponseHandler;
import com.team.http.HttpClientUtils;
import com.team.http.HttpParams;

public class ZhuFangContentActivity extends Activity implements OnClickListener, LxdhToActivity{

	Fragment content;
	
	Intent intent;

	ScrollView scrollView;
	
	ImageView goBack;
	ImageView shouChang_image;
	
	Button callPhone;
	Button shouChang;
	
	static String phone;
	
	int fw_id=-1;//房屋id
	int u_id=-1;//用户id
	
	boolean flag;//收藏标识
	boolean query_flag;//收藏标识
	int error;
	String msgs;
	
	static ZhuFangContentActivity activity;
	
	final static int SHOW_SC=0x10;//查询收藏
	final static int SHOU_CANG=0x11;//操作收藏
	final static int ERROR=0x12;//操作收藏
	
	public ZhuFangContentActivity() {}
	
	public static ZhuFangContentActivity getZfcaIntance(){
		if(activity==null)
			activity=new ZhuFangContentActivity();
		return activity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zhufang_content_activity);
		
		scrollView=(ScrollView) findViewById(R.id.zhufang_content_scrollview);
		
		goBack=(ImageView) findViewById(R.id.icon_break);
		callPhone=(Button) findViewById(R.id.call_phone);
		shouChang_image=(ImageView) findViewById(R.id.shouchang_image);
		shouChang=(Button) findViewById(R.id.shouchang_button);
		
		shouChang_image.setOnClickListener(this);
		goBack.setOnClickListener(this);
		callPhone.setOnClickListener(this);
		shouChang.setOnClickListener(this);
		
		if(getIntent()!=null){
			intent=getIntent();
			System.out.println("getIntent");
			if(intent.hasExtra("fw_id")){
				System.out.println("intent.hasExtra(fw_id)");
				fw_id=intent.getIntExtra("fw_id", -1);
			}
			if(intent.hasExtra("u_id")){
				System.out.println("intent.hasExtra(u_id)");
				u_id=intent.getIntExtra("u_id", -1);
			}
		}

		content = new ZhuFangContent();
		Bundle bundle = new Bundle();
		bundle.putInt("fw_id", fw_id);
		content.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.zhufang_content_scrollview, content);
		fragmentTransaction.commit();
		
		querySCforServer();
		
	}

	private void querySCforServer() {
		System.out.println(">>>querySCforServer<<<");
		HttpParams params=new HttpParams();
		params.put("u_id", u_id);
		params.put("fw_id", fw_id);
		HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "query_user_sc.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				System.out.println(">>>jsonObject="+jsonObject);
				Message message=new Message();
				message.what=SHOW_SC;
				message.obj=jsonObject;
				handler.sendMessage(message);
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.icon_break:
			finish();
			break;
		case R.id.call_phone:
			System.out.println(">>>call_phone");
			if(phone!=null&&!phone.equals("")){
				Intent call=new Intent(Intent.ACTION_DIAL);
				Uri uri=Uri.parse("tel:"+phone);
				call.setData(uri);  
				call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(call);
			}
			break;
		case R.id.shouchang_button:
			operationShouChang();
			break;
		case R.id.shouchang_image:
			operationShouChang();
			break;
		}
	}

	
	public void operationShouChang() {
		System.out.println(">>>shou chang");
		if(u_id!=-1){
			if(fw_id!=-1){
				HttpParams params=new HttpParams();
				params.put("u_id", u_id);
				params.put("fw_id", fw_id);
				params.put("flag", flag==true?1:0);
				HttpClientUtils.getInstance().post(ServerAdress.SERVER_ADRESS, "user_sc.php", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject jsonObject) {
						System.out.println(">>>jsonObject="+jsonObject);
						Message message=new Message();
						message.what=SHOU_CANG;
						message.obj=jsonObject;
						handler.sendMessage(message);
					}
				});
				if(flag){
					Toast.makeText(ZhuFangContentActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
					shouChang_image.setImageResource(R.drawable.shouchang_normal);
					flag=!flag;
				}else{
					Toast.makeText(ZhuFangContentActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
					shouChang_image.setImageResource(R.drawable.shouchang_selected);
					flag=!flag;
				}
			}else{
				Toast.makeText(this, "未知网络错误,请返回重试", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
		}
	}
	
	Handler handler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case ERROR:
				Toast.makeText(ZhuFangContentActivity.this, msgs, Toast.LENGTH_SHORT).show();
				break;
			case SHOU_CANG:
				if(msg.obj!=null){
					System.out.println("case SHOU_CANG");
					JSONObject jsonObject=(JSONObject) msg.obj;
					try {
						error=jsonObject.getInt("error");
						System.out.println(">>>error= "+error);
						if(error==0){
							flag=jsonObject.getBoolean("flag");
							System.out.println(">>>flag= " +flag);
							if(flag){
								shouChang_image.setImageResource(R.drawable.shouchang_selected);
							}else{
								shouChang_image.setImageResource(R.drawable.shouchang_normal);
							}
						}else{
							msgs=jsonObject.getString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case SHOW_SC:
				if(msg.obj!=null){
					JSONObject jsonObject=(JSONObject) msg.obj;
					try {
						error=jsonObject.getInt("error");
						if(error==0){
							flag=jsonObject.getBoolean("flag");
							System.out.println(">>>>>>flag="+flag);
							if(flag){
								shouChang_image.setImageResource(R.drawable.shouchang_selected);
							}else{
								shouChang_image.setImageResource(R.drawable.shouchang_normal);
							}
						}else{
							msgs=jsonObject.getString("msg");
							handler.sendEmptyMessage(ERROR);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			return true;
		}
	});

	@Override
	public void lxdh_activity(String lxdh) {
		phone=lxdh;
		System.out.println(">>>getPhoneNumberForFragment= "+phone);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		HttpClientUtils.getInstance().killAllThread();
	}
	
}
