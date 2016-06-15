package com.bolaa.sleepingbar.view.banner;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.listener.BannerOnClickListener;
import com.bolaa.sleepingbar.model.Banner;
import com.bolaa.sleepingbar.model.Information;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulz on 2015/6/23.
 */
public class AutoScollBanner extends LinearLayout implements
		BannerViewPager.OnSingleTouchListener {

	private Activity mContext;
	private boolean isFromHome; // 是否是首页的banner

	private BannerViewPager mViewPager;
	private LoopCirclePageIndicator mPageIndicator;
	private LoopBannerAdapter mBannerAdater;
	private ImageView mEmptyBannerIv;

	public AutoScollBanner(Context context) {
		super(context);
		mContext = (Activity) context;
		init();
	}

	public AutoScollBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = (Activity) context;
		init();
	}

	public void isFromHome(boolean isfromHome) {
		this.isFromHome = isfromHome;
	}

	private void init() {
		LayoutInflater.from(mContext)
				.inflate(R.layout.auto_scroll_banner, this);
		mViewPager = (BannerViewPager) findViewById(R.id.vp_ad_view_pager);
		mPageIndicator = (LoopCirclePageIndicator) findViewById(R.id.idc_ad_indicator);
		mEmptyBannerIv = (ImageView) findViewById(R.id.iv_empty_banner);
	}

	private boolean dataVisibility() {
		return getVisibility() == VISIBLE;
	}

	public void setScale(float scale){
		ViewGroup.LayoutParams lp=getLayoutParams();
		lp.height=(int)(scale * ScreenUtil.WIDTH);
		setLayoutParams(lp);
	}

	// 设置视图的Adapter
	private void setPageAdapter(LoopBannerAdapter adapter) {
		mBannerAdater = adapter;
		mViewPager.setAdapter(mBannerAdater);
		mViewPager.setLoopMode(true, mBannerAdater.getOriginPosition());
		if (mBannerAdater.convertRealPosition(mViewPager.getAutoCurrentIndex()) >= mBannerAdater
				.getRealCount()) {
			mViewPager.resetAutoCurrentIndex(mBannerAdater.getOriginPosition());
		} else {
			mViewPager.setCurrentItem(mViewPager.getAutoCurrentIndex(), false);
		}

		mViewPager.setOnSingleTouchListener(this);

		mViewPager.setLoopPageIndicator(mPageIndicator,
				mBannerAdater.getRealCount());
		mPageIndicator.setViewPager(mViewPager);

		if (dataVisibility() && mViewPager != null) {
			mViewPager.startCircleView();
		}
	}

	private void showAdvertisementView() {
		RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.rlayout_total_advertisement);
		mLayout.setVisibility(View.VISIBLE);
		RelativeLayout mLayout1 = (RelativeLayout) findViewById(R.id.rlayout_advertisement);
		mLayout1.setVisibility(View.VISIBLE);
		mEmptyBannerIv.setVisibility(GONE);
	}

	public void showEmptyBanner() {
		mEmptyBannerIv.setVisibility(View.VISIBLE);
	}

	public void showBannerViews(List<Banner> banners) {
		List<View> mViews = null;
		if (banners != null) {
			mViews = new ArrayList<View>(banners.size());

			if (banners.size() == 1) {
				mPageIndicator.setNeedCircle(false);
			} else {
				mPageIndicator.setNeedCircle(true);
			}
			mEmptyBannerIv.setVisibility(View.GONE);
		} else if (banners == null || banners.size() == 0) {
			mEmptyBannerIv.setVisibility(View.VISIBLE);
			return;
		}

		int index = 0;
		for (Banner banner : banners) {
			index++;
			final ImageView view = new ImageView(mContext);
			view.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			view.setScaleType(ImageView.ScaleType.FIT_XY);

			mViews.add(view);

			view.setTag(mViews.size());
			view.setOnClickListener(new BannerOnClickListener(mContext, banner));
			Image13Loader.getInstance().loadNoStubImageFade(
					banner.media_gallery, view);
		}

		if (mBannerAdater == null) {
			mBannerAdater = new LoopBannerAdapter(mViews);
			setPageAdapter(mBannerAdater);
		} else {
			mBannerAdater.setData(mViews);
			if (mBannerAdater.convertRealPosition(mViewPager
					.getAutoCurrentIndex()) >= mBannerAdater.getRealCount()) {
				mViewPager.resetAutoCurrentIndex(mBannerAdater
						.getOriginPosition());
			} else {
				mViewPager.setCurrentItem(mViewPager.getAutoCurrentIndex(),
						false);
			}

			mViewPager.setOnSingleTouchListener(this);

			mViewPager.setLoopPageIndicator(mPageIndicator,
					mBannerAdater.getRealCount());
			mPageIndicator.notifyDataSetChanged();

			/*
			 * if (dataVisibility() && mViewPager != null) {
			 * mViewPager.startCircleView(); }
			 */
		}

		showAdvertisementView();
	}

	public void showInformationViews(List<Information> informations) {
		List<View> mViews = null;
		if (informations != null) {
			mViews = new ArrayList<View>(informations.size());

			if (informations.size() == 1) {
				mPageIndicator.setNeedCircle(false);
			} else {
				mPageIndicator.setNeedCircle(true);
			}
			mEmptyBannerIv.setVisibility(View.GONE);
		} else if (informations == null || informations.size() == 0) {
			mEmptyBannerIv.setVisibility(View.VISIBLE);
			return;
		}

		int index = 0;
		for (final Information information : informations) {
			index++;
			final ImageView view = new ImageView(mContext);
			view.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			view.setScaleType(ImageView.ScaleType.FIT_XY);

			mViews.add(view);

			view.setTag(mViews.size());
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AppUtil.showToast(mContext,information.title);
				}
			});
			Image13Loader.getInstance().loadNoStubImageFade(
					information.file_url, view);
		}

		if (mBannerAdater == null) {
			mBannerAdater = new LoopBannerAdapter(mViews);
			setPageAdapter(mBannerAdater);
		} else {
			mBannerAdater.setData(mViews);
			if (mBannerAdater.convertRealPosition(mViewPager
					.getAutoCurrentIndex()) >= mBannerAdater.getRealCount()) {
				mViewPager.resetAutoCurrentIndex(mBannerAdater
						.getOriginPosition());
			} else {
				mViewPager.setCurrentItem(mViewPager.getAutoCurrentIndex(),
						false);
			}

			mViewPager.setOnSingleTouchListener(this);

			mViewPager.setLoopPageIndicator(mPageIndicator,
					mBannerAdater.getRealCount());
			mPageIndicator.notifyDataSetChanged();

			/*
			 * if (dataVisibility() && mViewPager != null) {
			 * mViewPager.startCircleView(); }
			 */
		}

		showAdvertisementView();
	}

	public void showUrlPicViews(List<String> banners) {
		List<View> mViews = null;
		if (banners != null) {
			mViews = new ArrayList<View>(banners.size());

			if (banners.size() == 1) {
				mPageIndicator.setNeedCircle(false);
			} else {
				mPageIndicator.setNeedCircle(true);
			}
			mEmptyBannerIv.setVisibility(View.GONE);
		} else if (banners == null || banners.size() == 0) {
			mEmptyBannerIv.setVisibility(View.VISIBLE);
			return;
		}

		int index = 0;
		for (String url : banners) {
			index++;
			final ImageView view = new ImageView(mContext);
			view.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			view.setScaleType(ImageView.ScaleType.CENTER_CROP);

			mViews.add(view);

			view.setTag(mViews.size());
			Image13Loader.getInstance().loadNoStubImageFade(url, view);
		}

		if (mBannerAdater == null) {
			mBannerAdater = new LoopBannerAdapter(mViews);
			setPageAdapter(mBannerAdater);
		} else {
			mBannerAdater.setData(mViews);
			if (mBannerAdater.convertRealPosition(mViewPager
					.getAutoCurrentIndex()) >= mBannerAdater.getRealCount()) {
				mViewPager.resetAutoCurrentIndex(mBannerAdater
						.getOriginPosition());
			} else {
				mViewPager.setCurrentItem(mViewPager.getAutoCurrentIndex(),
						false);
			}

			mViewPager.setOnSingleTouchListener(this);

			mViewPager.setLoopPageIndicator(mPageIndicator,
					mBannerAdater.getRealCount());
			mPageIndicator.notifyDataSetChanged();

			if (dataVisibility() && mViewPager != null) {
				// mViewPager.startCircleView();
			}
		}

		showAdvertisementView();
	}

	public void hideBanner() {
		findViewById(R.id.rlayout_advertisement).setVisibility(View.INVISIBLE);
	}

	public void startSroll() {
		if (mViewPager != null)
			mViewPager.startCircleView();
	}

	public void stopSroll() {
		if (mViewPager != null)
			mViewPager.stopCircleView();
	}

	@Override
	public void onSingleTouch() {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
				mBannerAdater.getItem(mViewPager.getCurrentItem())
						.performClick();
			} else {
				mBannerAdater.getItem(mViewPager.getCurrentItem())
						.callOnClick();
			}
		} catch (Exception ex) {
			LogUtil.w(ex);
		}
	}
}
