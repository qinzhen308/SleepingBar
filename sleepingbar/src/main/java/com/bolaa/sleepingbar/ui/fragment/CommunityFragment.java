package com.bolaa.sleepingbar.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.TopicListAdapter;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.base.BaseList2Activity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.Information;
import com.bolaa.sleepingbar.model.Topic;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.RankinglistItemWraper;
import com.bolaa.sleepingbar.model.wrapper.TopicWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.BBSSendPostsActivity;
import com.bolaa.sleepingbar.ui.MyFriendsActivity;
import com.bolaa.sleepingbar.ui.MyMsgActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.CommunityHeader;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;

import java.util.List;

/**
 * 首页--社区
 * Created by paulz on 2016/5/31.
 */
public class CommunityFragment extends BaseListFragment implements View.OnClickListener,LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener {
    CommunityHeader header;
    TextView tvPublish;
    ImageView ivMsg;
    ImageView ivFriends;
    Dialog loadDialog;

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_community, false);
        initView();
        setListener();
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initData(false);
        loadHeaderData();
    }

    private void initData(boolean isRefresh){
        if(!isRefresh){
            showLoading();
        }
        ParamBuilder params=new ParamBuilder();
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
        tvPublish=(TextView)baseLayout.findViewById(R.id.tv_publish);
        ivFriends=(ImageView) baseLayout.findViewById(R.id.iv_friends);
        ivMsg=(ImageView) baseLayout.findViewById(R.id.iv_msg);

        mPullListView=(PullListView)baseLayout.findViewById(R.id.pull_listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
        mListView=mPullListView.getRefreshableView();
        header=new CommunityHeader(getActivity());
        mListView.addHeaderView(header);
        mAdapter= new TopicListAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        loadDialog = DialogUtil.getCenterDialog(getActivity(), LayoutInflater.from(getActivity()).inflate(R.layout.load_doag, null));
    }

    private void setListener() {
        mListView.setOnScrollListener(new MyOnScrollListener());
        mPullListView.setOnRefreshListener(this);
        ivMsg.setOnClickListener(this);
        tvPublish.setOnClickListener(this);
        ivFriends.setOnClickListener(this);
        ((TopicListAdapter)mAdapter).setOnShowMenuListener(new TopicListAdapter.OnShowMenuListener() {
            @Override
            public void onShow(Topic topic) {

                showMenu(topic);
            }
        });
    }

    private void loadHeaderData(){
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_COMMUNITY_INFORMATION), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<CommunityInfo> obj= GsonParser.getInstance().parseToObj(result,CommunityInfo.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK&&obj.data!=null){
                            header.setVisibility(View.VISIBLE);
                            header.showHeader(obj.data);
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

    private void showMenu(final Topic topic){
        if(topic.has_been_cared==1){//已经被关注
            new IOSDialogUtil(getActivity()).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                    .addSheetItem("取消关注", IOSDialogUtil.SheetItemColor.Purple, new IOSDialogUtil.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            cancelCare(topic);
                        }
                    }).addSheetItem("举报", IOSDialogUtil.SheetItemColor.Red, new IOSDialogUtil.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    inform(topic);
                }
            }).show();
        }else {
            new IOSDialogUtil(getActivity()).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                    .addSheetItem("关注Ta", IOSDialogUtil.SheetItemColor.Purple, new IOSDialogUtil.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            doCare(topic,1);
                        }
                    }).addSheetItem("举报", IOSDialogUtil.SheetItemColor.Red, new IOSDialogUtil.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    inform(topic);
                }
            }).show();
        }
    }

    private void doCare(final Topic friends , int type){
        DialogUtil.showDialog(loadDialog);
        ParamBuilder params=new ParamBuilder();
        params.append("f_type",type);
        params.append("f_user_id",friends.user_id);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DO_CARE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!getActivity().isFinishing())DialogUtil.dismissDialog(loadDialog);
                if(status==200){
                    BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK){
                            ((TopicListAdapter)mAdapter).setCaredStatusByUid(friends.user_id,1);
                            AppUtil.showToast(getActivity(),obj.info);
                        }else {
                            AppUtil.showToast(getActivity(),obj.info);
                        }
                    }else {
                        AppUtil.showToast(getActivity(),"解析出错");
                    }
                }else {
                    AppUtil.showToast(getActivity(),"请检查网络");
                }
            }
        });
    }

    private void cancelCare(final Topic friends){
        DialogUtil.showDialog(loadDialog);
        ParamBuilder params=new ParamBuilder();
        params.append("f_user_id",friends.user_id);
        params.append("tab","me_care");
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CANCEL_CARE), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!getActivity().isFinishing())DialogUtil.dismissDialog(loadDialog);
                if(status==200){
                    BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK){
                            ((TopicListAdapter)mAdapter).setCaredStatusByUid(friends.user_id,0);
                            AppUtil.showToast(getActivity(),obj.info);
                        }else {
                            AppUtil.showToast(getActivity(),obj.info);
                        }
                    }else {
                        AppUtil.showToast(getActivity(),"解析出错");
                    }
                }else {
                    AppUtil.showToast(getActivity(),"请检查网络");
                }
            }
        });
    }

    private void inform(final Topic topic){
        DialogUtil.showDialog(loadDialog);
        ParamBuilder params=new ParamBuilder();
        params.append("f_user_id",topic.id);
        params.append("content",topic.content);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BBS_POSTS_INFORM), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!getActivity().isFinishing())DialogUtil.dismissDialog(loadDialog);
                if(status==200){
                    BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK){
                            topic.has_been_cared=0;
                        }else {
                            AppUtil.showToast(getActivity(),obj.info);
                        }
                    }else {
                        AppUtil.showToast(getActivity(),"解析出错");
                    }
                }else {
                    AppUtil.showToast(getActivity(),"请检查网络");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==tvPublish){
            BBSSendPostsActivity.invoke(getActivity());
        }else if(v==ivMsg){
            MyMsgActivity.invoke(getActivity());
        }else if(v==ivFriends){
            MyFriendsActivity.invoke(getActivity());
        }
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

    @Override
    public void heavyBuz() {

    }

    public class CommunityInfo{
        public List<Information> img_info;
        public List<Information> text_info;
    }

}
