package com.core.framework.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 12-7-10
 * Time: 下午9:28
 * To change this template use File | Settings | File Templates.
 */
public class LoginSuccessObserver implements LoginObserver<String> {
    private List<LoginExecutor<String>> executorList = new ArrayList<LoginExecutor<String>>();
    @Override
    public void addExecutors(LoginExecutor<String> executor) {
        for(LoginExecutor<String> mLoginExecutor:executorList){
            mLoginExecutor.getClass().equals(executor.getClass());
           return;
        }
        executorList.add(executor);
    }

    @Override
    public void removeObserver(LoginExecutor<String> executor) {
        executorList.remove(executor);
    }

    @Override
    public void removeAll() {
        executorList.clear();
    }

    @Override
    public void notifyExecutors(String result) {
        for (LoginExecutor<String> executor:executorList){
            executor.update(result);
        }
    }
}
