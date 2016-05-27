package com.core.framework.store.sharePer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by qz on 2014/8/20.
 * <p/>
 * 只是存储的String 为了节省空间
 */
public class SearchHisUtil {

    static final int MAX = 10;
    static List<String> list;
    static HashSet<String> set;
    public static final String SIMPLE_STRING_KEY="simple_string_key";

    public static List<String> getDBList() {

        if (list == null) {
            Object oo = PreferencesUtils.paserObject(SIMPLE_STRING_KEY);
            if(oo!=null)list=(ArrayList)oo;
            if (list == null) list = new ArrayList<String>();
            set = new HashSet<String>();
            for (String key : list) {
                set.add(key);
            }
        }

        return list;
    }


    public static void addToList(String newKey) {
        if (set == null) {
            getDBList();
        }

        if (set == null ||set.contains(newKey)) return;

        list.add(0, newKey);
        set.add(newKey);
        if (list.size() > MAX) {
            list.remove(list.size() - 1);
        }
        PreferencesUtils.storeObject(list, SIMPLE_STRING_KEY);
    }

    public static void clearHistory() {
        PreferencesUtils.remove(SIMPLE_STRING_KEY);
        if (list != null) {
            list.clear();
        }
    }
}
