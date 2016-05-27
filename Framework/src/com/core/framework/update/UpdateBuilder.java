package com.core.framework.update;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 2014 14-1-15
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates
 */
public class UpdateBuilder {
    public int iconId;           //各个应用的icon
    public int notifyLayout;     //各应用对应的notificationLayout
    public int progressBarId;    //进度条资源id
    public int notificationTvId; //
    public int imageViewId;      //
    public int imageId;          //
    public String failMsg;       //更新失败提示信息
    public String successMsg;    //更新成功提示信息
    public String downloadStr;   //notification 标题

    public UpdateUtil.ZheUpdateEntity partner;
    public String description;   //描述
    public String downloadUrl;   //下载url
    public String installFileName; //下载到本地文件名称

    public void setPartner(UpdateUtil.ZheUpdateEntity partner){
        this.partner = partner;
    }
}
