package com.listery.photoshow.loader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.listery.photoshow.utils.CommonAdapter;
import com.team.jingzufang.R;

public class MyAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	/**
	 * 图片路径缓存
	 */
	public static ArrayList<String> photos = new ArrayList<String>();

	/**
	 * 照片数量限制标志
	 */
	private boolean flag = true;

	Context context;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.context = context;
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final com.listery.photoshow.utils.ViewHolder helper,
			final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select,
				R.drawable.picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				if (mSelectedImage.contains(mDirPath + "/" + item)) {
					// 已经选择过该图片
					mSelectedImage.remove(mDirPath + "/" + item);
					//photos.remove(photos.indexOf(mDirPath + "/" + item));
					photos.remove(mDirPath + "/" + item);
					if (photos.size() != 0) {
						int i = 1;
						for (String s : photos) {
							System.out.println(i + "---" + s);
							i++;
						}
					}
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else {
					if (photos.size() < 6) {
						// 未选择该图片
						mSelectedImage.add(mDirPath + "/" + item);
						photos.add(mDirPath + "/" + item);
						if (photos.size() != 0) {
							int i = 1;
							for (String s : photos) {
								System.out.println(i + "---" + s);
								i++;
							}
						}
						mSelect.setImageResource(R.drawable.pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
					}else{
						Toast.makeText(context, "对不起,最多只能上传6张图片", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}
}
