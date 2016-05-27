package com.core.framework.auth;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 12-7-10
 * Time: 下午9:04
 * To change this template use File | Settings | File Templates.
 */
public interface LoginObserver<T> {
    public void addExecutors(LoginExecutor<T> executor);
    public void removeObserver(LoginExecutor<T> executor);
    public void removeAll();
    public void notifyExecutors(T result);
}
