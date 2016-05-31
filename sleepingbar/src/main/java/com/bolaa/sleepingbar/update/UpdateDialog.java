package com.bolaa.sleepingbar.update;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.utils.AppUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by adelbert on 14-3-11.
 */
public class UpdateDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private TextView mDescriptionTv;
    private CheckBox mNoticeBox;
    private ImageView mProductPortrait;
    private TextView mProductName;
    private OnDialogClick listener;
    private boolean isCanCancel = true;
    int mClickCount = 0;
    private Handler mHandler = new Handler();


    public UpdateDialog(Context context) {
        super(context, R.style.dialog_style);

        mContext = context;
        initView();
    }

    public UpdateDialog(Context context, int theme) {
        super(context, theme);

        mContext = context;
        initView();
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        mContext = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.layer_update_dialog_new);

        mDescriptionTv = (TextView) findViewById(R.id.tv_dialog_description);
        mNoticeBox = (CheckBox) findViewById(R.id.check_no_notice);
        mProductPortrait = (ImageView) findViewById(R.id.img_portrait);
        mProductName = (TextView) findViewById(R.id.tv_product_name);

        findViewById(R.id.tv_ensure).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_no_tip).setOnClickListener(this);
        setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ensure:
                listener.onPositiveClick();
                dismiss();
                break;

            case R.id.tv_cancel:
                listener.onNegativeClick();
                dismiss();
                break;

            case R.id.tv_no_tip://增加点击区域，点击文本相当于单击了checkbox
                if (mNoticeBox.isChecked()) {
                    mNoticeBox.setChecked(false);
                } else {
                    mNoticeBox.setChecked(true);
                }
                break;

            default:
                break;
        }
    }


    public void setDescriptionText(String text) {
        mDescriptionTv.setText(text);
    }


    public boolean isNoNoticeCheck() {
        return mNoticeBox.isChecked();
    }


    public void hideNoUpdateTip() {
        findViewById(R.id.tv_no_tip).setVisibility(View.GONE);
        findViewById(R.id.check_no_notice).setVisibility(View.GONE);
        findViewById(R.id.tv_cancel).setVisibility(View.GONE);
    }

    public void setCanCancelByBackPress(boolean canCancel) {
        isCanCancel = canCancel;
    }


    public void setNoticeCheckChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mNoticeBox.setOnCheckedChangeListener(listener);
    }

    public void setOnPositiveListener(final OnDialogClick listener) {
        this.listener = listener;
    }

    public interface OnDialogClick {
        public void onPositiveClick();

        public void onNegativeClick();
    }

    @Override
    public void onBackPressed() {
        if (isCanCancel) {
            super.onBackPressed();
        } else {
            if (mClickCount++ < 1) {
                AppUtil.showToast(mContext, "再按一次就退出壹品倉");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mClickCount = 0;
                    }
                }, 2000);
                return;
            }
            HApplication.getInstance().exit();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        UpdateUtil.isChecking = false;
    }
}
