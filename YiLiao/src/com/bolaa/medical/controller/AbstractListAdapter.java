package com.bolaa.medical.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.bolaa.medical.common.GlobeFlags;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.store.sharePer.PreferencesUtils;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 12-10-24
 * Time: 上午11:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractListAdapter <T> extends BaseAdapter {

    protected List<T> mList;
    protected Context mContext;
    private AdapterView mListView;
    protected boolean isGridMode;

    protected LayoutInflater mInflater;

    public AbstractListAdapter(Activity context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public AbstractListAdapter(Context context) {
    	mContext=context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    /**
     * 设置宫格或列表模式
     */
    public void setViewMode(boolean isGridMode){
        this.isGridMode=isGridMode;
        PreferencesUtils.putInteger(GlobeFlags.MODE_STATUS,isGridMode ? GlobeFlags.MODE_BIG_PIC_MODE:GlobeFlags.MODE_LIST_MODE);
        notifyDataSetChanged();
        fitPosition();
    }

    /**
     * 设置宫格或列表模式
     */
    public void setViewMode(boolean isGridMode,int oriPosition){
        this.isGridMode=isGridMode;
        PreferencesUtils.putInteger(GlobeFlags.MODE_STATUS,isGridMode ? GlobeFlags.MODE_BIG_PIC_MODE:GlobeFlags.MODE_LIST_MODE);
        notifyDataSetChanged();
        fitPosition(oriPosition);
    }

    /**
     * 设置宫格或列表模式，用于不可切换的listview的初始化设置模式
     */
    public void setFixedViewMode(boolean isGridMode){
        this.isGridMode=isGridMode;
        notifyDataSetChanged();
        fitPosition();
    }

    /**
     * 适配mode对应的position
     */
    protected void fitPosition(){
    }

    /**
     * 适配mode对应的position
     *
     */
    protected void fitPosition(int oriPosition){

    }


    @Override
    public Object getItem(int i) {
        return mList == null ? 0 : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public abstract View getView(int i, View view, ViewGroup viewGroup);

    public void setList(List<T> list) {
        this.mList = list;
    }

    public List<T> getList() {
        return this.mList;
    }

    public AdapterView getListView(){
        return mListView;
    }

    public void setListView(AdapterView listView){
        mListView = listView;
    }

    public Context getContext(){
        return mContext;
    }

    public void setList(T[] list){
        if (list == null) return;
        ArrayList<T> arrayList = new ArrayList<T>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setList(arrayList);
    }

    public void clear() {
        if (mList != null && mList.size() > 0){
            mList.clear();
        }
    }

    public int getItemCount () {
        if (AppUtil.isEmpty(mList)) {
            return 0;
        } else {
            if(isGridMode){
                if (mList.size() % 2 == 0) {
                    return mList.size() / 2;
                } else {
                    return (mList.size() / 2) + 1;
                }
            }else{
                return mList.size();
            }
        }
    }
}
