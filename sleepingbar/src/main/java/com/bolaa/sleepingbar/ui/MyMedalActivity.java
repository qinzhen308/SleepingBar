package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.MedalAdapter;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Medal;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.BaseObjectList;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.net.NetworkWorker;

/**
 * 我的勋章
 */
public class MyMedalActivity extends BaseActivity{

	private GridView mGridView;
    private MedalAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		initData();
		setListener();
	}
	

	private void initData() {
		// TODO Auto-generated method stub
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUPPORTER_LIST), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status== 200){
                    BaseObjectList<Medal> objectList= GsonParser.getInstance().parseToObj4List(result,Medal.class);
                    if(objectList.status == BaseObject.STATUS_OK){
                        if(!AppUtil.isEmpty(objectList.data)){
                            showSuccess();
                            mAdapter.setList(objectList.data);
                            mAdapter.notifyDataSetChanged();
                        }else {
                            showNodata();
                        }
                    }else {
                        showFailture();
                        AppUtil.showToast(getApplicationContext(),objectList.info);
                    }
                }else {
                    showFailture();
                }
            }
        });
	}

	private void setListener() {

	}


	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activity_my_medal, false, true);
		setTitleText("", "我的勋章", 0, true);
		mGridView=(GridView)findViewById(R.id.gridview);
        mAdapter=new MedalAdapter(this);
        mGridView.setAdapter(mAdapter);
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
	}


	public static void invoke(Context context){
		Intent intent=new Intent(context,MyMedalActivity.class);
		context.startActivity(intent);
	}
	
	
	
	

}
