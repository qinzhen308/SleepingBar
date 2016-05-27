/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.bolaa.medical.view.pulltorefreshgrid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bolaa.medical.view.pulltorefreshgrid.PullToRefreshBase.Mode;
import com.bolaa.medical.view.pulltorefreshgrid.PullToRefreshBase.Orientation;
import com.bolaa.medical.R;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {

	static final String LOG_TAG = "PullToRefresh-LoadingLayout";

	static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

	private FrameLayout mInnerLayout;

	protected final RelativeLayout mHeaderImageRlayout;
	protected final ImageView mHeaderImage;
//	protected final ImageView sloganImage;
	protected final ProgressBar mHeaderProgress;

	private boolean mUseIntrinsicAnimation;
	private boolean isShowHeader;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;
	// private final TextView mLoadingText;

	protected final Mode mMode;
	protected final Orientation mScrollDirection;

	private CharSequence mPullLabel;
	private CharSequence mRefreshingLabel;
	private CharSequence mReleaseLabel;

	public LoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
		super(context);
		mMode = mode;
		mScrollDirection = scrollDirection;

		switch (scrollDirection) {
			case HORIZONTAL:
				LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this);
				break;
			case VERTICAL:
			default:
				LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);
				break;
		}

		mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
		mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
		mHeaderProgress = (ProgressBar) mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
		mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
		// mLoadingText = (TextView) mInnerLayout.findViewById(R.id.pull_loading_text);
        mHeaderImageRlayout = (RelativeLayout) mInnerLayout.findViewById(R.id.rlayer_pull_to_refresh_image);
		mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
//        sloganImage = (ImageView) mInnerLayout.findViewById(R.id.img_slogan);

		LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();

        isShowHeader = attrs.getBoolean(R.styleable.PullToRefresh_waterpullshowheader, true);

        if (!isShowHeader) {
            mInnerLayout.setVisibility(GONE);
        }

		switch (mode) {
			case PULL_FROM_END:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT;

				// Load in labels
				mPullLabel = context.getString(R.string.up_to_refresh);
				mRefreshingLabel = context.getString(R.string.label_loading);
				mReleaseLabel = context.getString(R.string.pull_to_refresh_release);
				break;

			case PULL_FROM_START:
			default:
				lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT;

				// Load in labels
				mPullLabel = context.getString(R.string.pull_to_refresh);
				mRefreshingLabel = context.getString(R.string.label_loading);
				mReleaseLabel = context.getString(R.string.pull_to_refresh_release);
				break;
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				ViewCompat.setBackground(this, background);
			}
		}

		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance, styleID);
			setTextAppearance(styleID.data);
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
			TypedValue styleID = new TypedValue();
			attrs.getValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance, styleID);
			setSubTextAppearance(styleID.data);
		}

		// Text Color attrs need to be set after TextAppearance attrs
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
			ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
			if (null != colors) {
				setTextColor(colors);
			}
		}
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
			ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
			if (null != colors) {
				setSubTextColor(colors);
			}
		}

		// Try and get defined drawable from Attrs
		Drawable imageDrawable = null;
		if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
			imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawable);
		}

		// Check Specific Drawable from Attrs, these overrite the generic
		// drawable attr above
		switch (mode) {
			case PULL_FROM_START:
			default:
				if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
				} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
					Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
				}
				break;

			case PULL_FROM_END:
				if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
				} else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
					Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
					imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
				}
				break;
		}

		// If we don't have a user defined drawable, load the default
		// Set Drawable, and save width/height
		// setLoadingDrawable(imageDrawable);

		reset();
	}

	public final void setHeight(int height) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.height = height;
		requestLayout();
	}

	public final void setWidth(int width) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) getLayoutParams();
		lp.width = width;
		requestLayout();
	}

	public final int getContentSize() {
		switch (mScrollDirection) {
			case HORIZONTAL:
				return mInnerLayout.getWidth();
			case VERTICAL:
			default:
				return mInnerLayout.getHeight();
		}
	}

	public final void onPull(float scaleOfLayout) {
		onPullImpl(scaleOfLayout);
//        sloganImage.setVisibility(View.VISIBLE);
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImageRlayout.setVisibility(View.VISIBLE);
        mHeaderText.setVisibility(VISIBLE);
        mSubHeaderText.setVisibility(GONE);
	}

	public final void pullToRefresh() {
        mHeaderText.setText(mPullLabel);
//        sloganImage.setVisibility(View.VISIBLE);
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImageRlayout.setVisibility(View.VISIBLE);
        mHeaderText.setVisibility(VISIBLE);
        mSubHeaderText.setVisibility(GONE);
        // Now call the callback
		pullToRefreshImpl();
	}

	public final void refreshing() {
        mHeaderText.setVisibility(GONE);
        mSubHeaderText.setText(mRefreshingLabel);
        mSubHeaderText.setVisibility(VISIBLE);
//        sloganImage.setVisibility(View.GONE);
        mHeaderProgress.setVisibility(View.VISIBLE);
        mHeaderImageRlayout.setVisibility(View.GONE);
	}

	public final void releaseToRefresh() {
		mHeaderText.setText(mReleaseLabel);
        mHeaderText.setVisibility(VISIBLE);
        mSubHeaderText.setVisibility(GONE);
//        sloganImage.setVisibility(View.VISIBLE);
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImageRlayout.setVisibility(View.VISIBLE);
		// Now call the callback
		releaseToRefreshImpl();
	}

	public final void reset() {
		mHeaderText.setText(mPullLabel);
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImageRlayout.setVisibility(View.VISIBLE);
        mHeaderText.setVisibility(VISIBLE);
        mSubHeaderText.setVisibility(GONE);

        if (mUseIntrinsicAnimation) {
            // ((AnimationDrawable) mHeaderImage.getDrawable()).stop();
        } else {
            // Now call the callback
            resetImpl();
        }
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		setSubHeaderText(label);
	}

	public final void setLoadingDrawable(Drawable imageDrawable) {
	}

	public void setPullLabel(CharSequence pullLabel) {
		mPullLabel = pullLabel;
	}

	public void setRefreshingLabel(CharSequence refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(CharSequence releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	@Override
	public void setTextTypeface(Typeface tf) {
		mHeaderText.setTypeface(tf);
	}

	/**
	 * Callbacks for derivative Layouts
	 */

	protected abstract int getDefaultDrawableResId();

	protected abstract void onLoadingDrawableSet(Drawable imageDrawable);

	protected abstract void onPullImpl(float scaleOfLayout);

	protected abstract void pullToRefreshImpl();

	protected abstract void refreshingImpl();

	protected abstract void releaseToRefreshImpl();

	protected abstract void resetImpl();

	private void setSubHeaderText(CharSequence label) {
		if (null != mSubHeaderText) {
			if (TextUtils.isEmpty(label)) {
				mSubHeaderText.setVisibility(View.GONE);
			} else {
				mSubHeaderText.setText(label);

				// Only set it to Visible if we're GONE, otherwise VISIBLE will
				// be set soon
				if (View.GONE == mSubHeaderText.getVisibility()) {
					mSubHeaderText.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setSubTextAppearance(int value) {
        mSubHeaderText.setTextAppearance(getContext(), value);
	}

	private void setSubTextColor(ColorStateList color) {
        mSubHeaderText.setTextColor(color);
	}

	private void setTextAppearance(int value) {
        mHeaderText.setTextAppearance(getContext(), value);
        mSubHeaderText.setTextAppearance(getContext(), value);
	}

	private void setTextColor(ColorStateList color) {
        mHeaderText.setTextColor(color);
        mSubHeaderText.setTextColor(color);
	}

}
