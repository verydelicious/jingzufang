package com.xiangmu.tenement;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingzufang.activitys.UserIndexAty;
import com.team.jingzufang.R;
import com.xiangmu.tenement.JzfButtonFragment.OnJzfBottomClickListener;

public class TenementViewPager extends FragmentActivity implements OnJzfBottomClickListener{
	JzfButtonFragment jzfButtonFragment = new JzfButtonFragment();
	ViewPager viewPager;
	JzfButtonFragment buttonFragment;
	List<Fragment> list = new ArrayList<Fragment>();
	Fragment shouYeFragment, gerenzhongxinFragment;

	ArrayList<TenementBean> beans;

	Intent intent;

	TextView sy;
	TextView grxx;
	ImageView imageView;
	
	String city;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		if (getIntent() != null && getIntent().hasExtra("data") && getIntent().hasExtra("city")) {
			intent = getIntent();
			beans = intent.getParcelableArrayListExtra("data");
			city=intent.getStringExtra("city");
			System.out.println(">>>>>>>>>>>>>>>>>>TenementViewPager数据接收成功<<<<<<<<<<<<<<<<");
		}else{
			System.out.println("没有接收到StartView的数据");
		}
		

		setContentView(R.layout.first_view);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		buttonFragment = (JzfButtonFragment) getSupportFragmentManager()
				.findFragmentById(R.id.botton);
		buttonFragment.setJzfBottomClickListener(this);
		// viewPager.setOnPageChangeListener(this);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			// 当某页被选中
			@Override
			public void onPageSelected(int arg0) {
				buttonFragment.setSelected(arg0);
			}

			/*
			 * 当某页滚动的时候 position 页码的下标 positionOffset 位置的偏移 positionOffsetPixels
			 * 位置的像素偏移
			 */
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			/*
			 * 当某页滚动状态改变时回调(non-Javadoc)
			 * 
			 * @see android.support.v4.view.ViewPager.OnPageChangeListener#
			 * onPageScrollStateChanged(int) state状态
			 */
			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// shouye = (ImageView) findViewById(R.id.shouye);
		// gerenzhongxin = (ImageView) findViewById(R.id.gerenzhongxin);
		
		sy = (TextView) findViewById(R.id.SY);
		grxx = (TextView) findViewById(R.id.GRZX);

		/**
		 * content = new ZhuFangContent(); Bundle bundle = new Bundle();
		 * bundle.putInt("fw_id", fw_id); content.setArguments(bundle);
		 * 
		 * FragmentManager fragmentManager = getFragmentManager();
		 * FragmentTransaction fragmentTransaction =
		 * fragmentManager.beginTransaction();
		 * fragmentTransaction.add(R.id.zhufang_content_scrollview, content);
		 * fragmentTransaction.commit();
		 */

		shouYeFragment = new ShouYeFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("data", beans);
		bundle.putString("city", city);
		shouYeFragment.setArguments(bundle);
		System.out.println(">>>>>>>>>>>>>>往ShouYeFragment发送数据成功<<<<<<<<<<<<<<");

		// FragmentManager fragmentManager = getSupportFragmentManager();
		// //开始Fragment事务
		// FragmentTransaction fTransaction =
		// fragmentManager.beginTransaction();
		// //将Fragment添加到事务中，并指定一个TAG
		// fTransaction.add(R.id.viewpager, shouYeFragment);
		// //提交Fragment事务
		// fTransaction.commit();

		gerenzhongxinFragment = new UserIndexAty();

		list.add(shouYeFragment);
		list.add(gerenzhongxinFragment);
		MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager(), list);
		viewPager.setAdapter(adapter);
	}

	public void onBottomClick(View v, int position) {

		switch (position) {

		case JzfButtonFragment.SHOUYE_POSITION:
			viewPager.setCurrentItem(0, true);
			break;
		case JzfButtonFragment.GRZX_POSITION:
			viewPager.setCurrentItem(1, true);
			break;
		}

	}
	public void jump(){
		Intent intent = new Intent(this,StartView.class);
		startActivity(intent);
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		List<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}
	}

	private static boolean isExit = false;// 用来标识是否退出

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {//当keyCode等于退出事件时
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void exit(){
		if(!isExit){
			isExit = true;
			Toast.makeText(getApplicationContext(), "在按一次退出精租房", Toast.LENGTH_SHORT).show();
			//利用handler延迟发送更改状态的消息
			mHandler.sendEmptyMessageDelayed(0,2000);//设置2秒后发送消息
		}else{				
			finish();		
			System.exit(0);//是虚拟机停止运行并退出程序
		}
	}	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
