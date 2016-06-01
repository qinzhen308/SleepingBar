package com.bolaa.sleepingbar.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.model.RegionInfo;
import com.bolaa.sleepingbar.model.tables.RegionTable;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类view
 * paul'z
 */
public class RegionSelectLayout extends LinearLayout implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ListView mListView;
    private ListView mListView1;
    private ListView mListView2;

    private MListAdapter mListAdapter;
    private MListAdapter mListAdapter1;
    private MListAdapter mListAdapter2;

    private List<RegionInfo> provinceList;
    public int selectParentPosition[] = {0,0,0};//存选中的城市 region的(type-1)作为数组的index  0省1市2区
    //private int maxTextLength;
    private boolean isAutoScroll;

    public RegionSelectLayout(Context context) {
        super(context);
        mContext = context;
        initView();
        registerListener();
    }

    public RegionSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        registerListener();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.category_layout_base_view, this);
        mListView = (ListView) findViewById(R.id.listView);
        mListView1 = (ListView) findViewById(R.id.listView2);
        mListView2 = (ListView) findViewById(R.id.listView3);

        mListAdapter = new MListAdapter(mContext);
        mListAdapter1 = new MListAdapter(mContext);
        mListAdapter2= new MListAdapter(mContext);
        mListView.setAdapter(mListAdapter);

        mListView1.setAdapter(mListAdapter1);
        mListView2.setAdapter(mListAdapter2);
        listviews=new AbsListView[]{mListView,mListView1,mListView2};
        adapters=new AbstractListAdapter[]{mListAdapter,mListAdapter1,mListAdapter2};
    }

    public void setList(List<RegionInfo> listData) {
    	if(AppUtil.isEmpty(listData))return;
        provinceList = listData;
        //getMaxTextLength();
        mListAdapter.setList(provinceList);
        mListAdapter.notifyDataSetChanged();
        setListAndGridSelect(0,provinceList.get(0));
    }

    /**
     * 获取一级分类最大字数
     */
    /*private void getMaxTextLength() {
        maxTextLength = 0;
        for (int i = 0, length = parentCategoryData.size(); i < length; i++) {
            if (parentCategoryData.get(i).name.length() > maxTextLength) {
                maxTextLength = parentCategoryData.get(i).name.length();
            }
        }
    }*/

    AbstractListAdapter adapters[];
    AbsListView listviews[];
    
    public void setListAndGridSelect(final int position,RegionInfo region) {
        try {
        	selectParentPosition[region.region_type-1] = position;
            for(int i=region.region_type-1;i<adapters.length;i++){
            	if(i==region.region_type-1){
            		//这里是为了改变视图选中颜色
            		adapters[i].notifyDataSetChanged();
            	}else if(i==region.region_type){
            		//选中的下一级，展示数据
            		selectParentPosition[i]=0;
            		adapters[i].setList(getRegionHasAll(region));
            		listviews[i].setAdapter(adapters[i]);
				}else {
					//选中项的下下一级或下更多级，不展示
					selectParentPosition[i]=0;
					adapters[i].setList(new ArrayList<RegionInfo>());
					listviews[i].setAdapter(adapters[i]);
				}
            }

        } catch (Exception e) {
            LogUtil.w(e);
        }
    }
    
    private List<RegionInfo> getRegionHasAll(RegionInfo region){
    	List<RegionInfo> list=RegionTable.getInstance().getListByParentId(region.region_id);
    	//加个全部进来
    	RegionInfo first=new RegionInfo();
    	first.parent_id=region.region_id;
    	first.region_id=-1;
    	first.region_type=region.region_type+1;
    	first.region_name="全部";
    	list.add(0, first);
    	return list;
    }

    private void registerListener() {
        mListView.setOnItemClickListener(this);
        mListView.setScrollContainer(true);
        mListView.setFocusable(true);
        mListView.setFocusableInTouchMode(true);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCREEN_STATE_OFF && isAutoScroll) {
                    isAutoScroll = false;
                    if (mListAdapter1.getCount() == 1) {
                        RegionInfo category = (RegionInfo) mListView.getAdapter().getItem(0);
                        LogUtil.d("未定义效果");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView1.setOnItemClickListener(this);
        mListView1.setScrollContainer(true);
        mListView1.setFocusable(true);
        mListView1.setFocusableInTouchMode(true);
        mListView2.setOnItemClickListener(this);
        mListView2.setScrollContainer(true);
        mListView2.setFocusable(true);
        mListView2.setFocusableInTouchMode(true);
    }
    
    private void selectAndBack(){
    	Activity activity=((Activity)mContext);
		Intent intent=new Intent();
		for(int i=0;i<selectParentPosition.length;i++){
			AbstractListAdapter adapter=adapters[i];
			if(adapter.getCount()==0){
				break;
			}else {
				intent.putExtra("regionId_"+i, ((RegionInfo)adapter.getItem(selectParentPosition[i])).region_id);
			}
		}
		activity.setResult(activity.RESULT_OK, intent);
		activity.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
    	RegionInfo regionInfo=(RegionInfo)parent.getItemAtPosition(position);
    	if(regionInfo.region_id<0||regionInfo.region_type==3){
    		//点击到了全部，返回
    		selectParentPosition[regionInfo.region_type-1] = position;
    		selectAndBack();
    		return;
    	}
    	if (selectParentPosition[regionInfo.region_type-1] != position) {
    		setListAndGridSelect(position,regionInfo);
    		if (parent == mListView1) {//一级分类的单击事件
				
	        } else if (parent == mListView2) {//二级的单击事件
	            
	        }
    		//处理滑动的效果的
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    			if ((position == 0 && view.getTop() == 0) || (parent.getLastVisiblePosition() == parent.getCount() - 1 && parent.getHeight() == parent.getChildAt(parent.getCount() - 1 - parent.getFirstVisiblePosition()).getBottom())) {
    				
    			} else {
    				isAutoScroll = true;
    			}
//    			((ListView)parent).smoothScrollToPositionFromTop(position, 0, 500);
    			((ListView)parent).setSelection(position);

    		} else {
    			parent.setSelection(position);
    		}
    		
    	}
        
    }


    class MListAdapter extends AbstractListAdapter<RegionInfo> {

        public MListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.category_layout_list_item, null);
                holder.mItemTv = (TextView) convertView.findViewById(R.id.parent_category_name);
                holder.indicator =  convertView.findViewById(R.id.parent_indicator_category);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mItemTv.setText(mList.get(position).region_name);
            if (selectParentPosition[mList.get(position).region_type-1] == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.purple));
                holder.mItemTv.setTextColor(getResources().getColor(R.color.white));
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.gray1));
                holder.mItemTv.setTextColor(getResources().getColor(R.color.black));
                holder.indicator.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            private TextView mItemTv;
            private View indicator;
        }


        /**
         * 保证每个一级分类都凑够四个数，为了ui对齐好看
         *
         * @param str
         * @return
         */
        /*private String setTitleLengthTo4(String str) {
            StringBuilder sBuffer = new StringBuilder();
            sBuffer.append(str);
            for (int i = 0, size = maxTextLength - str.length(); i < size; i++) {
                sBuffer.append(getResources().getText(R.string.blank_space));
            }
            return sBuffer.toString();
        }*/
    }

    private int measureImage() {
        if (ScreenUtil.WIDTH == 0) {
            ScreenUtil.setDisplay((Activity) mContext);
        }
        //一级分类和二级分类宽度比是1.4:3.6   二级分类的列数为3
        return ((int) ((ScreenUtil.WIDTH) / 5 * 3.6) / 3) - ScreenUtil.dip2px(mContext, 20);
    }
}
