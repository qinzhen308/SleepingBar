package com.bolaa.sleepingbar.ui.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.FundsRankinglistAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.RankinglistItem;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.RankinglistItemWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

import java.util.List;

/**
 * 医疗机构的评价
 * 
 * @author paulz
 *
 */
public class FundsRankinglistFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener ,LoadStateController.OnLoadErrorListener,View.OnClickListener{

	private int pageType;
	public static final int PAGE_TYPE_FRIENDS=0;//好友排行
	public static final int PAGE_TYPE_ALL=1;//总排行

	private ViewGroup layoutMyFunds;

	public TextView tvName;
	public TextView tvRanking;
	public TextView tvSupportCount;
	public TextView tvFundsTotal;
	public ImageView ivAvatar;
	public ImageView ivHeart;
	private Dialog supportDialog;
	private Dialog loadDialog;



	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		setView(inflater, R.layout.fragment_funds_rankinglist, true);
		initView();
		setListener();
		return baseLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		initData(false);
	}

	private void setExtra(){
		Bundle data=getArguments();
		if(data==null)return;
		pageType =data.getInt("page_type",PAGE_TYPE_FRIENDS);
	}

	public void initView() {
		layoutMyFunds=(ViewGroup) baseLayout.findViewById(R.id.layout_my_funds);
		tvName=(TextView)layoutMyFunds.findViewById(R.id.tv_name);
		tvRanking =(TextView)layoutMyFunds.findViewById(R.id.tv_ranking);
		tvSupportCount =(TextView)layoutMyFunds.findViewById(R.id.tv_support);
		tvFundsTotal =(TextView)layoutMyFunds.findViewById(R.id.tv_funds_total);
		ivHeart=(ImageView) layoutMyFunds.findViewById(R.id.checkbox);
		ivAvatar=(ImageView) layoutMyFunds.findViewById(R.id.iv_avatar);

		mPullListView=(PullListView) baseLayout.findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new FundsRankinglistAdapter(getActivity());
		mListView.setAdapter(mAdapter);

        loadDialog=DialogUtil.getCenterDialog(getActivity(),LayoutInflater.from(getActivity()).inflate(R.layout.load_doag,null));
	}

	private void setListener() {
		mPullListView.setOnRefreshListener(this);
		mLoadStateController.setOnLoadErrorListener(this);
		mListView.setOnScrollListener(new MyOnScrollListener());
		ivHeart.setOnClickListener(this);
		((FundsRankinglistAdapter)mAdapter).setOnSupportEventListener(new FundsRankinglistAdapter.OnSupportEventListener() {
			@Override
			public void onSupport(RankinglistItem item) {
				showSupportWindow(item);
			}
		});
	}

	private void setMyFundsInfo(){
		BeanWraper wraper=getBeanWraper();
		if(wraper!=null){
			RankinglistItemWraper data=((RankinglistItemWraper)wraper);
			tvFundsTotal.setText(AppUtil.getTwoDecimal(data.my_sleep_fund));
			tvName.setText(data.my_nickname);
			tvRanking.setText(""+data.my_sleep_rank);
			tvSupportCount.setText(""+data.my_support_num);
			Image13Loader.getInstance().loadImageFade(data.my_avatar,ivAvatar);
		}
	}



	@Override
	public void heavyBuz() {

	}

    private TextView tvSupportName;
    private TextView tvWayDirect;
    private TextView tvWay5Day;
    private TextView tvWay10Day;
    private TextView tvMoney1;
    private TextView tvMoney2;
    private TextView tvMoney3;
    private TextView tvMoney4;
    private TextView btnSupport;
    private EditText etMoney;
    private int[] supportDays={0,5,10};
    private float[] moneys={5,10,20,50,0};
    private TextView[] dayViews=null;
    private TextView[] moneyViews=null;
    private int curMoneyItem=-1;
    private int curDayItem=-1;

	private void showSupportWindow(final RankinglistItem item){
		if(supportDialog==null){
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_support_funds, null);
			supportDialog = DialogUtil.getMenuDialog(getActivity(), view);
            tvSupportName=(TextView) view.findViewById(R.id.tv_support_name);
            tvSupportName.setText("支持给："+item.nick_name);
            tvWayDirect=(TextView) view.findViewById(R.id.tv_way_direct_give);
            tvWay5Day=(TextView) view.findViewById(R.id.tv_way_after_5);
            tvWay10Day=(TextView) view.findViewById(R.id.tv_way_after_10);
            tvMoney1=(TextView) view.findViewById(R.id.tv_money1);
            tvMoney2=(TextView) view.findViewById(R.id.tv_money2);
            tvMoney3=(TextView) view.findViewById(R.id.tv_money3);
            tvMoney4=(TextView) view.findViewById(R.id.tv_money4);
            btnSupport=(TextView) view.findViewById(R.id.btn_support);

            etMoney=(EditText) view.findViewById(R.id.et_money);
            dayViews=new TextView[]{tvWayDirect,tvWay5Day,tvWay10Day};
            moneyViews=new TextView[]{tvMoney1,tvMoney2,tvMoney3,tvMoney4,etMoney};
            etMoney.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    int i=s.toString().indexOf(".");
                    if(i>=0&&(s.length()-3)>i){
                        etMoney.setText(s.subSequence(0, i+3));
                    }
                }
            });
            for(int i=0;i<dayViews.length;i++){
                dayViews[i].setOnClickListener(new SelectListener(i) {
                    @Override
                    public void onClick(View v) {
                        check(dayViews,this.position);
                        curDayItem=this.position;
                    }
                });
            }
            for(int i=0;i<moneyViews.length;i++){
                moneyViews[i].setOnClickListener(new SelectListener(i) {
                    @Override
                    public void onClick(View v) {
                        check(moneyViews,this.position);
                        curMoneyItem=this.position;
                        if(curMoneyItem!=moneyViews.length-1){
                            etMoney.setText("");
                        }
                    }
                });
            }
            btnSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(curDayItem<0){
                        AppUtil.showToast(getActivity(),"请选择支持方式");
                        return;
                    }
                    if(curMoneyItem<0){
                        AppUtil.showToast(getActivity(),"请选择支持金额");
                        return;
                    }
                    if(curMoneyItem==moneyViews.length-1){
                        String moneyStr=etMoney.getText().toString().trim();
                        moneys[moneys.length-1]=AppUtil.isNull(moneyStr)?0:Float.valueOf(moneyStr);
                    }
                    AppUtil.showToast(getActivity(),supportDays[curDayItem]+","+moneys[curMoneyItem]);
                    support(item.user_id,supportDays[curDayItem]+"",moneys[curMoneyItem]+"");
                }
            });

		}else {
            moneys[moneys.length-1]=0;
            etMoney.setText("");
            if(curMoneyItem>=0){
                moneyViews[curMoneyItem].setSelected(false);
            }
            if(curDayItem>=0){
                dayViews[curDayItem].setSelected(false);
            }
            curDayItem=-1;
            curMoneyItem=-1;
            tvSupportName.setText("支持给："+item.nick_name);
		}
		DialogUtil.showDialog(supportDialog);
	}

    public abstract class SelectListener implements View.OnClickListener{
        public int position;
        public SelectListener(int position){
            this.position=position;
        }
    }

    private void check(TextView[] views,int position){
        for(int i=0;i<views.length;i++){
            if(position==i){
                views[i].setSelected(true);
            }else {
                views[i].setSelected(false);
            }
        }
    }


    public void support(String id, String way,String money){
        if(!AppStatic.getInstance().isLogin)return;
        DialogUtil.showDialog(loadDialog);
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("by_user_id", id);
        requester.getParams().put("money", money);
        requester.getParams().put("stages_num", way);
        NetworkWorker.getInstance().post(AppUrls.getInstance().URL_SUPPORT_FUNDS_TO, new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                // TODO Auto-generated method stub
                if(!getActivity().isFinishing())DialogUtil.dismissDialog(loadDialog);
                if(status==200){
                    LogUtil.d("jpush---bind push info="+result);
                    BaseObject<Object> obj= GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK){
                            AppUtil.showToast(getActivity(),obj.info);
                            if(!getActivity().isFinishing())DialogUtil.dismissDialog(supportDialog);
                        }else {
                            AppUtil.showToast(getActivity(),obj.info);
                        }
                    }else {
                        AppUtil.showToast(getActivity(),"操作失败");
                    }
                }else {
                    AppUtil.showToast(getActivity(),"请检查网络");
                }
            }
        },requester);
    }



	public static FundsRankinglistFragment createInstance(int type){
		FundsRankinglistFragment instance=new FundsRankinglistFragment();
		Bundle data=new Bundle();
		data.putInt("page_type",type);
		instance.setArguments(data);
		return instance;
	}

	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append(ParamBuilder.ACCESS_TOKEN, HApplication.getInstance().token);
		if(pageType==PAGE_TYPE_ALL){
			params.append("tab", "all");
		}
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_FUNDS_RANKING_LIST), RankinglistItemWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_FUNDS_RANKING_LIST), RankinglistItemWraper.class);
		}
	}

	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new RankinglistItemWraper();
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
		setMyFundsInfo();
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
	public void onClick(View v) {
		if(v==ivAvatar){

		}
	}
}
