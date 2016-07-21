package com.bolaa.sleepingbar.ui;

import java.util.ArrayList;
import java.util.List;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.utils.ImageUtil;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.image.image13.Image13lLoader;
import com.core.framework.image.universalimageloader.core.assist.FailReason;
import com.core.framework.image.universalimageloader.core.assist.ImageLoadingListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotoViewerActivity extends BaseActivity {
	ViewPager mViewPager;
	PhotoAdapter mAdapter;
	List<View> views;
	ArrayList<Uri> uris;
	int startPosition;
	List<String> url_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		showPhoto();
	}

	private void setExtra() {
		Intent intent = getIntent();
		url_list = intent.getStringArrayListExtra("url_list");
		uris = intent.getParcelableArrayListExtra("uri_list");
		startPosition = intent.getIntExtra("start_position", 0);
	}

	private void initView() {
		setActiviyContextView(R.layout.activity_photo_viewer, false, false);
		mViewPager = (ViewPager) findViewById(R.id.vp_photo_viewer);
		mAdapter = new PhotoAdapter();
	}

	private void showPhoto() {
		views = new ArrayList<View>();
		int size = uris == null ? url_list.size() : uris.size();
		for (int i = 0; i < size; i++) {
			views.add(obtainView());
		}
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(startPosition);
	}

	private View obtainView() {
		ImageView view = new ImageView(this);
		ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
		lp.height = ViewPager.LayoutParams.MATCH_PARENT;
		lp.width = ViewPager.LayoutParams.MATCH_PARENT;
		view.setLayoutParams(lp);
		view.setScaleType(ScaleType.CENTER);
		return view;
	}

	class PhotoAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			View view = views.get(position);
			((ImageView) view).destroyDrawingCache();
			container.removeView(view);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			View view = views.get(position);
			ViewParent parent = view.getParent();
			if (parent != null) {
				((ViewGroup) parent).removeView(view);
			}
			container.addView(view);

			if (url_list != null && url_list.size() > 0) {

				Image13lLoader.getInstance().loadImage(url_list.get(position),
						((ImageView) view), new ImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {

							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								if(view instanceof ImageView){
									ImageView tagView=(ImageView)view;
									if(loadedImage==null){
										tagView.setScaleType(ScaleType.CENTER);
										tagView.setImageResource(R.drawable.img_list_default);
									}else {
										float scale=((float)loadedImage.getHeight())/(float)loadedImage.getWidth();
										float scaleScreen=((float) ScreenUtil.HEIGHT)/(float)ScreenUtil.WIDTH;
										tagView.setScaleType(scale>scaleScreen?ScaleType.CENTER_CROP:ScaleType.FIT_CENTER);
										tagView.setImageBitmap(loadedImage);
									}

								}
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {

							}
						});

			} else if (uris != null && uris.size() > 0) {
				Bitmap bitmap = ImageUtil.compressImage(ImageUtil
						.getImageAbsolutePath(PhotoViewerActivity.this,
								uris.get(position)));
				((ImageView) view).setImageBitmap(bitmap);
			}
			return view;
		}

	}

	public static void invoke(Context context, ArrayList<Uri> uris,
			int startPosition) {
		Intent intent = new Intent(context, PhotoViewerActivity.class);
		intent.putParcelableArrayListExtra("uri_list", uris);
		intent.putExtra("start_position", startPosition);
		context.startActivity(intent);
	}

	public static void invoke(Context context, List<String> urlPaths,
			int startPosition) {
		Intent intent = new Intent(context, PhotoViewerActivity.class);
		intent.putStringArrayListExtra("url_list", (ArrayList<String>) urlPaths);
		intent.putExtra("start_position", startPosition);
		context.startActivity(intent);
	}

}
