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
package com.bolaa.sleepingbar.view.pulltorefreshgrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.view.pulltorefreshgrid.PullToRefreshBase.Mode;
import com.bolaa.sleepingbar.view.pulltorefreshgrid.PullToRefreshBase.Orientation;

public class RotateLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 400;

	private final Animation mRotateAnimation, resetRotateAnimation;
	private final Matrix mHeaderImageMatrix;

	private float mRotationPivotX, mRotationPivotY;

	private final boolean mRotateDrawableWhilePulling;

	public RotateLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

		mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

        final Interpolator interpolator = new LinearInterpolator();
        mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(interpolator);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);

        resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        resetRotateAnimation.setInterpolator(interpolator);
        resetRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        resetRotateAnimation.setFillAfter(false);
	}

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.list_action_down;
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	protected void onPullImpl(float scaleOfLayout) {
	}

	@Override
	protected void refreshingImpl() {
		mHeaderImage.clearAnimation();
	}

	@Override
	protected void resetImpl() {
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
        mHeaderImage.clearAnimation();
        mHeaderImage.startAnimation(resetRotateAnimation);
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
        mHeaderImage.clearAnimation();
        mHeaderImage.startAnimation(mRotateAnimation);
	}

}
