package com.bolaa.sleepingbar.watch;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.bolaa.sleepingbar.model.Watch;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.DateUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.util.Arrays;
import java.util.UUID;

/**
 * 保持手环与app的数据交互，处理数据逻辑
 * 直接通过设备的address连接
 * 这里不进行扫描设备,只尝试连接
 * 同时只能连接一支手环,如果传入的address不同，那么进行连接
 * Created by paulz on 2016/5/27.
 */
public class WatchService extends Service{
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    public static final String FLAG_CURRENT_DEVICE_ADDRESS="flag_current_device_address";
    public static final String FLAG_CURRENT_DEVICE_NAME="flag_current_device_name";


    /**搜索BLE终端*/
    private BluetoothAdapter mBluetoothAdapter;
    /**读写BLE终端*/
    private BluetoothLeClass mBLE;
    private boolean mScanning;
    private Handler mHandler=new Handler();
    private String currentAddress;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;

    WatchCMDReceiver mReceiver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        registBroadcast();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        scanLeDevice(true);
        if(intent!=null){
            currentAddress=intent.getStringExtra(FLAG_CURRENT_DEVICE_ADDRESS);
        }
        if(currentAddress==null){
            currentAddress= PreferencesUtils.getString(FLAG_CURRENT_DEVICE_ADDRESS);
        }
        tryConnect(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregistBroadcast();
        mBLE.close();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.d("watch---onLowMemory-->执行");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.d("watch---onTrimMemory-->执行 level="+level);
    }

    private void registBroadcast(){
        if(mReceiver!=null)return;
        mReceiver=new WatchCMDReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(WatchConstant.ACTION_WATCH_CMD_SET_DATE);
        filter.addAction(WatchConstant.ACTION_WATCH_CMD_SET_INFO);
        filter.addAction(WatchConstant.ACTION_WATCH_CMD_GET_SLEEP);
        registerReceiver(mReceiver,filter);
    }

    private void unregistBroadcast(){
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver=null;
        }
    }

    private void init() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            LogUtil.d("watch----没有4.0蓝牙权限");
            stopSelf();
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
            stopSelf();
            return;
        }
        //直接开启蓝牙
        mBluetoothAdapter.enable();
        //初始化蓝牙工具类
        mBLE = new BluetoothLeClass(this);
        if (!mBLE.initialize()) {
            LogUtil.d("watch-----Unable to initialize Bluetooth");
            stopSelf();
            return;
        }
        //发现BLE终端的Service时回调
        mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);
        //收到BLE终端数据交互的事件
        mBLE.setOnDataAvailableListener(mOnDataAvailable);

        mBLE.setOnConnectListener(new BluetoothLeClass.OnConnectListener() {
            @Override
            public void onConnect(BluetoothGatt gatt) {
                LogUtil.d("watch---onConnect---device="+gatt.getDevice()+"("+gatt.getDevice().getName()+") ,connected Devices="+bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size());
            }
        });

        mBLE.setOnDisconnectListener(new BluetoothLeClass.OnDisconnectListener() {
            @Override
            public void onDisconnect(BluetoothGatt gatt) {
                LogUtil.d("watch---onDisconnect---device="+gatt.getDevice()+"("+gatt.getDevice().getName()+") ,connected Devices="+bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size());
            }
        });
    }

    private void tryConnect(Intent intent){
        if(TextUtils.isEmpty(currentAddress)){
            LogUtil.d("watch---无效mac地址,关闭服务...");
            stopSelf();
            return;
        }
        boolean isSuc=mBLE.connect(currentAddress);
        LogUtil.d("watch---尝试连接:"+isSuc);
        if(!isSuc){
            //尝试连接失败，直接关闭
            stopSelf();
        }
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

    /**
     * 搜索到BLE终端服务的事件
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new BluetoothLeClass.OnServiceDiscoverListener(){

        @Override
        public void onServiceDiscover(final BluetoothGatt gatt) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    enableNotification(gatt,true);
//                    enableNotificationWrite(gatt);
                }
            });

        }

    };

    /**
     * 收到BLE终端数据交互的事件
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new BluetoothLeClass.OnDataAvailableListener(){

        /**
         * BLE终端数据被读的事件
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                LogUtil.d("watch---onCharRead "+gatt.getDevice().getName()
                        +" read "
                        +characteristic.getUuid().toString()
                        +" -> "
                        +Utils.bytesToHexString(characteristic.getValue()));
            LogUtil.d("watch---onCharRead "+gatt.getDevice().getName()
                    +" read "
                    +characteristic.getUuid().toString()
                    +" -> "
                    + Arrays.toString(characteristic.getValue()));
        }

        /**
         * 收到BLE终端写入数据回调
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            LogUtil.d("watch---onCharWrite "+gatt.getDevice().getName()
                    +" write "
                    +characteristic.getUuid().toString()
                    +" -> "
                    +Arrays.toString(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //收到手环的上报消息
            LogUtil.d("watch---onCharNotify----"+gatt.getDevice().getName()+"----notify--"+characteristic.getUuid().toString()+"--orig-->"+Arrays.toString(characteristic.getValue())+"--cmd(0x"+Utils.bytesToHexString(new byte[]{characteristic.getValue()[0]})+")");
            LogUtil.d("watch---onCharNotify----"+gatt.getDevice().getName()+"----notify--"+characteristic.getUuid().toString()+"--hex-->"+Utils.bytesToHexString(characteristic.getValue())+"--cmd(0x"+Utils.bytesToHexString(new byte[]{characteristic.getValue()[0]})+")");
            LogUtil.d("watch---onCharNotify----"+gatt.getDevice().getName()+"----notify--"+characteristic.getUuid().toString()+"--dest-->"+Arrays.toString(Utils.bytesToIntArrayV2(characteristic.getValue()))+"--end-");
            LogUtil.d("totalcount---="+(++notifyCount));

            CMDHandler.synchronizedMovement(WatchService.this,characteristic.getValue());
            //读取最近两天的数据
            if(CMDHandler.saveSleep(characteristic.getValue())){
                //昨天的
                CMDHandler.cmdGetSleepInfo(writeCharacteristic,(byte)1);
                mBLE.writeCharacteristic(writeCharacteristic);
            }
        }
    };
    int notifyCount=0;

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (device != null&&device.getName().contains("aceband")){
                                if (mScanning) {
                                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                    mScanning = false;
                                }
                                mBLE.connect(device.getAddress());
                            }
                        }
                    });
                }
            };

    //必须在主线程执行
    public void enableNotification(BluetoothGatt mBluetoothGatt,boolean b){
            BluetoothGattService service =mBluetoothGatt.getService(UUID.fromString("000056ff-0000-1000-8000-00805f9b34fb"));
            if(service==null){
                AppUtil.showToast(getApplicationContext(),"请连接小趣手环");
                return;
            }
            readCharacteristic =service.getCharacteristic(WatchConstant.UUID_CHARA_READ);
            writeCharacteristic =service.getCharacteristic(WatchConstant.UUID_CHARA_WRITE);

            boolean set = mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true);

            LogUtil.d("watch--- setnotification = " + set);
            BluetoothGattDescriptor dsc =readCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            if(dsc==null){
                AppUtil.showToast(getApplicationContext(),"该设备无可用特征通道");
                return;
            }
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    CMDHandler.cmdGetSleepInfo(writeCharacteristic,(byte)0);
                    mBLE.writeCharacteristic(writeCharacteristic);
                }
            },3*1000);
            dsc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean success =mBluetoothGatt.writeDescriptor(dsc);
            LogUtil.d("watch---writing enabledescriptor:" + success);
            Toast.makeText(getApplicationContext(),"通知开起:"+set+"--写入:"+success,Toast.LENGTH_LONG).show();
            if(set&&success){
                sendBroadcast(new Intent(WatchConstant.ACTION_WATCH_CONNECTED_SUCCESS).putExtra("device_name",mBluetoothGatt.getDevice().getName()).putExtra("device_address",mBluetoothGatt.getDevice().getAddress()));
            }
        }

    //必须在主线程执行
    public void enableNotificationWrite(BluetoothGatt mBluetoothGatt){

        boolean set = mBluetoothGatt.setCharacteristicNotification(writeCharacteristic, true);
        LogUtil.d("watch 33f3--- setnotification = " + set);
        if(AppUtil.isEmpty(writeCharacteristic.getDescriptors()))return;
        BluetoothGattDescriptor dsc =writeCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        dsc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean success =mBluetoothGatt.writeDescriptor(dsc);
        LogUtil.d("watch 33f3---writing enabledescriptor:" + success);
        Toast.makeText(getApplicationContext(),"33f3通知开起:"+set+"--写入:"+success,Toast.LENGTH_LONG).show();
    }


    public class WatchCMDReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(WatchConstant.ACTION_WATCH_CMD_SET_INFO.equals(action)){
                byte[] info=intent.getByteArrayExtra(WatchConstant.FLAG_USER_INFO);
                CMDHandler.cmdSetInfo(readCharacteristic,info[0],info[1],info[2],info[3]);
                mBLE.writeCharacteristic(readCharacteristic);
            }else if(WatchConstant.ACTION_WATCH_CMD_SET_DATE.equals(action)){
                if(intent.getIntExtra(WatchConstant.FLAG_DEVICE_DATE,0)>0){
                    CMDHandler.cmdSetDate(writeCharacteristic,(int)(System.currentTimeMillis()/1000));
                    mBLE.writeCharacteristic(writeCharacteristic);
                }
            }else if(WatchConstant.ACTION_WATCH_CMD_GET_SLEEP.equals(action)){//读手环的睡眠信息
                //不一定需要做
            }
        }
    }

}
