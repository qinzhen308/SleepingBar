package com.bolaa.sleepingbar.watch;

/**
 * Created by paulz on 2016/7/1.
 */
public class TipUtil {
    public static final String[] stepEvaluate ={"0","请绑定设备","好厉害","一般般","马马虎虎"};
    public static final String[] stepDescs={"没有检测到行走数据","只有绑定手环后才会记录数据","让我走啊走，走啊走","适当再多一点运动，有利身体健康","你都不怎么走路吗"};
    public static final String[] sleepTips={"未知","请绑定设备","优","良","差"};
    public static final String[] sleepDescs={"没有检测到睡眠数据，请正确使用设备","只有绑定设备后才会记录数据","你真的太棒了","你能得“优”吗？","睡眠质量这么差，真是让人心疼。"};

    public static String getStepEvaluate(int distance){
        if(distance>=100000){
            return stepEvaluate[2];
        }else if(distance>=40000){
            return stepEvaluate[3];
        }else if(distance>0){
            return stepEvaluate[4];
        }else if(distance==0){
            return stepEvaluate[0];
        }else {//小与0就是未绑定
            return stepEvaluate[1];
        }
    }

    public static String getStepTip(int distance){
        if(distance>=100000){
            return stepDescs[2];
        }else if(distance>=40000){
            return stepDescs[3];
        }else if(distance>0){
            return stepDescs[4];
        }else if(distance==0){
            return stepDescs[0];
        }else {//小与0就是未绑定
            return stepDescs[1];
        }
    }
}
