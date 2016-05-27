package com.bolaa.medical.view.banner;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: qz 有限循环banner（初始化时设置到int.max/2左右）
 */
public class LoopBannerAdapter extends PagerAdapter {
	public static final int MAXCOUNT = 1000000;

	private List<View> mViews;
	int realCount = 0;
	int originPosition;

	private int mChildCount = 0;

	@Override
	public void notifyDataSetChanged() {
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);

	}

	public LoopBannerAdapter(List<View> views) {
		this.mViews = views;
		if (mViews != null) {
			realCount = mViews.size();
			originPosition = Short.MAX_VALUE >> 1;
			originPosition -= realCount == 0 ? originPosition >> 1 : originPosition % realCount;
		}
	}

	public void setData(List<View> views) {
		this.mViews = views;
		if (mViews != null) {
			realCount = mViews.size();
			originPosition = Short.MAX_VALUE >> 1;
			originPosition -= realCount == 0 ? originPosition >> 1 : originPosition % realCount;
			notifyDataSetChanged();
		}
	}

	public View getItem(int position) {
		// Log.d("qz","getitem----position="+position+",index="+position%realCount);
		return realCount == 0 ? null : mViews.get(position % realCount);
	}

	/**
	 * @param position
	 *            viewpager的position
	 * @return viewpager的position所对应的views的position
	 */
	public int convertRealPosition(int position) {
		if (realCount == 0) {
			return 0;
		}
		return position % realCount;
	}

	/**
	 * 最中间，且views的第一个，即为原点
	 * 
	 * @return
	 */
	public int getOriginPosition() {
		return originPosition;
	}

	@Override
	public int getCount() {
		return mViews == null ? 0 : (mViews.size() <= 1 ? mViews.size() : Short.MAX_VALUE);
	}

	public int getRealCount() {
		return realCount;
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		// Log.d("qz","instantiateItem----position="+position+",index="+position%realCount);
		int index = convertRealPosition(position);
		View child = mViews.get(index);
		if (child.getParent() != null) {
			((ViewGroup) child.getParent()).removeView(child);
		}
		view.addView(child, 0);
		return mViews.get(index);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// Log.d("qz","destroyItem----position="+position+",index="+position%realCount);
		// container.removeView(mViews.get(position%realCount));
	}
}
