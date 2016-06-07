package com.bolaa.sleepingbar.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.DeviceBindingListAdapter;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.watch.BluetoothLeClass;
import com.bolaa.sleepingbar.watch.WatchConstant;
import com.core.framework.develop.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 绑定手环
 * 直接输入手机号码---获取验证码---绑定手环（可跳过）---微信授权（可跳过）
 * Created by paulz on 2016/6/1.
 */
public class QuickBindWatchActivity extends BaseActivity{
    TextView tvSearch;
    TextView tvSkip;
    ListView lvDevices;
    private DeviceBindingListAdapter mAdapter;

    private Handler mHandler=new Handler();
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
        initBLE();
    }

    private void initBLE(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            LogUtil.d("watch----没有4.0蓝牙权限");
            AppUtil.showToast(getApplicationContext(),"无4.0蓝牙使用权限");
            return;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            LogUtil.d("watch----获取蓝牙适配器失败");
            AppUtil.showToast(getApplicationContext(),"获取蓝牙适配器失败");
            return;
        }
        //直接开启蓝牙
        if(mBluetoothAdapter.isEnabled()){
            //待定
        }
        mBluetoothAdapter.enable();
        //初始化蓝牙工具类
        scanLeDevice(true);
    }

    private void setListener() {
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBLE();
            }
        });
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_quick_bind_watch, false, false);
        tvSearch=(TextView)findViewById(R.id.tv_search_devices);
        tvSkip =(TextView)findViewById(R.id.tv_skip);
        lvDevices =(ListView) findViewById(R.id.lv_devices);
        mAdapter=new DeviceBindingListAdapter(this);
        lvDevices.setAdapter(mAdapter);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(new UUID[]{WatchConstant.UUID_SERVICE},mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(mAdapter.getList()==null){
                        List<BluetoothDevice> deviceList=new ArrayList<>();
                        deviceList.add(device);
                        mAdapter.setList(deviceList);
                        mAdapter.notifyDataSetChanged();
                    }else if(!mAdapter.getList().contains(device)){
                        mAdapter.getList().add(device);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            };

    public static void invoke(Context context){
        Intent intent=new Intent(context,QuickBindWatchActivity.class);
        context.startActivity(intent);
    }

}
