package com.core.framework.callbacks;

/**
 * 通用回调
 */
public interface CommCallBack<T> {
    boolean callBack(T... t);
}
