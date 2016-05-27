package com.bolaa.medical.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.R;

/**
 * 修改提示
 * 
 * @author jjj
 * 
 * @time 2015-12-18
 */
public class UpdateNoticeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_updatenotice);
		setTitleText("", "修改提示", 0, true);

		findViewById(R.id.updateNotice_okBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						startActivity(new Intent(UpdateNoticeActivity.this,
								MainActivity.class));
						setResult(RESULT_OK);
						UpdateNoticeActivity.this.finish();

					}
				});
	}
}
