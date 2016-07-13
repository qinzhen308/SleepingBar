package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.TopicListStyle2Adapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.OtherUserInfo;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.TopicWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.OtherUserHomeHeader;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;

import java.util.List;

/**
 * 用户首页
 * Created by paulz on 2016/5/31.
 */
public class OtherUserHomeActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener {
    OtherUserHomeHeader header;

    private String userId;
    private String nikeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
        setListener();
        initData(false);
        loadHeaderData();
    }

    private void setExtra() {
        Intent intent =getIntent();
        userId=intent.getStringExtra("user_id");
        nikeName=intent.getStringExtra("nick_name");
    }

    @Override
    public void onResume() {
        super.onResume();
    }





    private void initData(boolean isRefresh){
        if(!isRefresh){
            showLoading();
        }
        ParamBuilder params=new ParamBuilder();
        params.append("uid",userId);
        if(isRefresh){
            immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_TOPIC_LIST), TopicWraper.class);
        }else {
            reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_TOPIC_LIST), TopicWraper.class);
        }
    }

    @Override
    protected BeanWraper newBeanWraper() {
        return new TopicWraper();
    }

    public void initView() {
        setActiviyContextView(R.layout.activity_other_user_home,true,true);
        setTitleText("",nikeName==null?"":nikeName,0,true);
        mPullListView=(PullListView)findViewById(R.id.pull_listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
        mListView=mPullListView.getRefreshableView();
        header=new OtherUserHomeHeader(this);
        mListView.addHeaderView(header);
        mAdapter= new TopicListStyle2Adapter(this);
        mListView.setAdapter(mAdapter);
    }

    private void setListener() {
        mListView.setOnScrollListener(new MyOnScrollListener());
        mPullListView.setOnRefreshListener(this);

    }

    private void loadHeaderData(){
        ParamBuilder params=new ParamBuilder();
        params.append("uid",userId);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_OTHER_USER_INFO), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<Content> obj= GsonParser.getInstance().parseToObj(result,Content.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK&&obj.data!=null&&obj.data.user_info!=null){
                            header.setVisibility(View.VISIBLE);
                            header.showHeader(obj.data.user_info);
                        }else {
//                            AppUtil.showToast(getActivity(),obj.info);
                            header.setVisibility(View.GONE);
                        }
                    }else {
//                        AppUtil.showToast(getActivity(),"解析出错");
                        header.setVisibility(View.GONE);
                    }
                }else {
                    header.setVisibility(View.GONE);
//                    AppUtil.showToast(getActivity(),"请检查网络");
                }
            }
        });

    }


    @Override
    protected void handlerData(List allData, List currentData, boolean isLastPage) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        if(AppUtil.isEmpty(allData)){
            showNodata();
        }else {
            showSuccess();
        }
        mAdapter.setList(allData);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void loadError(String message, Throwable throwable, int page) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();
    }

    @Override
    protected void loadTimeOut(String message, Throwable throwable) {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();
    }

    @Override
    protected void loadNoNet() {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();
    }

    @Override
    protected void loadServerError() {
        // TODO Auto-generated method stub
        mPullListView.onRefreshComplete();
        showFailture();

    }


    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if(!isLoading()){
            initData(true);
        }
    }

    @Override
    public void onAgainRefresh() {
        // TODO Auto-generated method stub
        initData(false);
    }

    public static void invoke(Context context,String userId,String nickName){
        Intent intent=new Intent(context,OtherUserHomeActivity.class);
        intent.putExtra("user_id",userId);
        intent.putExtra("nick_name",nickName);
        context.startActivity(intent);

    }

    public class Content{
        public OtherUserInfo user_info;
    }


}
