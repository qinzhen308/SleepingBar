// SU 11.28

package com.core.framework.develop;

import android.util.Log;

import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.util.StringUtil;


//suweg
//日志打印
public class Su {
	public static boolean is_log =true;
    static {
        is_log = !AppConfig.LOG_CLOSED;
    }

	public static void log(String str) {
        if (is_log) {     str=getNewString(str);
            Log.w("SuSul", str + "");
        }
    }
	

	
	public static void Log(String str) {
	if (is_log) {
        str = getNewString(str);
        Log.w("SuSuL", str + "");
    }
		
	}


	public static void log(Class<?> cla, String str) {
	if (is_log) {
        str = getNewString(str);
        Log.w("SuSu", cla.getCanonicalName() + ":" + str);
    }
	}

	public static void LogE(String str) {

	if (is_log) {
        str = getNewString(str);
        Log.w("SuSu", str);
    }
		
	}

    public static void logIM(String str) {
        if (is_log) {
            str = getNewString(str);
            Log.w("SuSuIm", str + "");
        }
    }

    public static void logPullView(String str) {
        if (is_log) {
            str = getNewString(str);
            Log.w("SuSuView", str + "");
        }
    }




    public static void logE(String string) {
        if (is_log)
            Log.e("SuSuE",string);

    }

    public static void logPush(String tag) {

        if (is_log)
            Log.w("SuSuPUSH",tag);

    }
    public static void logApp(String tag) {
        if (is_log)
            Log.w("SuSuApp",tag);
    }


    static  String getNewString(String info){
        if(StringUtil.isEmpty(info))return "null";
        int l=info.length();
        int size=100;
        if(l>size) {
            StringBuffer sb=new StringBuffer();
            int time=l/size;
            int i=0;
            for(i=0;i<time;i++){
                sb.append(info.substring(size*i,size*(i+1))).append("\n");
            }
            if(l>size*(i)){
                sb.append(info.substring(size*(i),l));
            }
            return sb.toString();
        }

        return info;
    }

}
