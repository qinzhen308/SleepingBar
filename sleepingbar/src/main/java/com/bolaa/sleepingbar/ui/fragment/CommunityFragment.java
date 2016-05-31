package com.bolaa.sleepingbar.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;

/**
 * 首页--社区
 * Created by paulz on 2016/5/31.
 */
public class CommunityFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void heavyBuz() {

    }


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_user_center, false);
        initView();
        setListener();
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // initData(false);
    }


    public void initView() {


    }

    private void setListener() {


    }
    @Override
    public void onClick(View v) {

    }
}
