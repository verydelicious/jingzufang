package com.xiangmu.tenement;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.team.jingzufang.R;

public class LoadListView extends ListView implements OnScrollListener {

	View footer;// 底部布局、、
	int totalItemCount;// 总list列表item的数量
	int lastVisibleItem;// 最后一个可见的item；
	boolean isLoading = false;// 判断是否在加载
	ILoadListener iLoadListener;

	public LoadListView(Context context) {
		super(context);
		initView(context);
	}

	public LoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public LoadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 把底部的加载提示布局添加到listview
	 */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.footer_layout, null);
		// 设置将加载提示布局隐藏
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
		// 添加底部布局
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	/**
	 * firstVisibleItem list列表第一个可见的item项 visibleItemCount 可见item的数量
	 * totalItemCount 总item的数量
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 监听是否滑动到屏幕底部，并且停止了滑动
		if (!isLoading) {
			if (totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
				isLoading = true;
				footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
				iLoadListener.onLoad();
			}
		}
	}

	// 当加载完毕
	public void loadComplete() {
		isLoading = false;
		// 设置将加载提示布局隐藏
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
	}

	// 获得回调接口
	public void setInterface(ILoadListener iLoadListener) {
		this.iLoadListener = iLoadListener;
	}

	// 加载更多数据的回调接口
	public interface ILoadListener {
		public void onLoad();
	}

}
