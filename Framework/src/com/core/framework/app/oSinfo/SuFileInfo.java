package com.core.framework.app.oSinfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import com.core.framework.app.MyApplication;
import com.core.framework.develop.Su;


//关于系统中OS判断的系统
public class SuFileInfo {

    protected static SuFileInfo instance;

    public static SuFileInfo getInstance() {
        if (instance == null) {
            instance = new SuFileInfo(MyApplication.getInstance());
        }
        return instance;
    }

    public String basePath;
    public String SD_Path_Catch;// 总路径
    public String SYS_Path_Catch;// 总路径
    // public static String LinkMan_Obj;// 资料文件 .user
    // public static String LinkMan_Image;// 照片
    // public static String LinkMan_Sound;// 文件


    public String[] sysnames = {".tuan800Obj"};

    public String[] SD_SYS_names = {"._Image"};

    public String[] Path = new String[sysnames.length
            + SD_SYS_names.length];

    public boolean is_sdcard;// 是不是SD卡环境

    private Context mAPPContext;


    public SuFileInfo(Context mAPPContext) {
        this.mAPPContext = mAPPContext;
        basePath = "/.REAPP";
        initSDCach();
        initSYSCach();
        setSDLinstener();
    }

    private void initSYSCach() {

        File f = new File(mAPPContext.getFilesDir().getAbsolutePath()
                + basePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        SYS_Path_Catch = f.getAbsolutePath();
        testSYSFilesCach();

    }

    private String getExistFilePath(String pathkey) {
        File fi = new File(pathkey);
        if (!fi.exists()) {
            fi.mkdirs();
        }
        return fi.getAbsolutePath();
    }

    private void testSDFilesCach() {

        for (int i = 0; i < SD_SYS_names.length; i++) {
            Path[sysnames.length + i] = getExistFilePath(SD_Path_Catch + "/"
                    + SD_SYS_names[i]);
        }

    }

    private void testSYSFilesCach() {

        for (int i = 0; i < sysnames.length; i++) {
            Path[i] = getExistFilePath(SYS_Path_Catch + "/" + sysnames[i]);
        }
        if (!is_sdcard) {
            for (int i = 0; i < SD_SYS_names.length; i++) {
                Path[sysnames.length + i] = getExistFilePath(SYS_Path_Catch
                        + "/" + SD_SYS_names[i]);
            }
        }

    }

    private void initSDCach() {

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            File environmentFile = Environment.getExternalStorageDirectory();
            File f = new File(environmentFile.getAbsolutePath() + basePath);

            if (!f.exists()) {
                f.mkdirs();
            }

            SD_Path_Catch = f.getAbsolutePath();

            is_sdcard = true;

            testSDFilesCach();

        } else {
            is_sdcard = false;
            Su.log("is_sdcard   false");
        }

    }




    public  String getImagePath() {
        return Path[sysnames.length];
    }





    // public static boolean is_sdcard;
    // SD变化
    private BroadcastReceiver mSDChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction()
                    .equals("android.intent.action.MEDIA_MOUNTED")) {
                is_sdcard = true;

            } else if (intent.getAction().equals(
                    "android.intent.action.MEDIA_REMOVED")) {
                is_sdcard = false;
            } else if (intent.getAction().equals(
                    "android.intent.action.MEDIA_UNMOUNTED")) {
                is_sdcard = false;
            } else if (intent.getAction().equals(
                    "android.intent.action.MEDIA_BAD_REMOVAL")) {
                is_sdcard = false;
            }

            if (is_sdcard) {

                initSDCach();

            } else {
                testSYSFilesCach();
            }

            Su.log("接收SD监听   is_sdcard " + is_sdcard);

        }
    };

    private void setSDLinstener() {

        Su.log("setSDLinstener  ");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addDataScheme("file");

        mAPPContext.registerReceiver(this.mSDChangedReceiver, filter);

    }


    public File saveFile(ByteArrayOutputStream stream, File tempfile) {


            if (tempfile.exists())
                tempfile.delete();

            try {
                byte[] mContent = stream.toByteArray();
                FileOutputStream fos = new FileOutputStream(tempfile);
                fos.write(mContent);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        return tempfile;

    }
}
