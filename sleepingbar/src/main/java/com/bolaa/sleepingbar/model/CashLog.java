package com.bolaa.sleepingbar.model;

/**
 * 提现记录
 * Created by paulz on 2016/6/3.
 */
public class CashLog {
    public String apply_code;//申请单号
    public int apply_status;//申请状态   ('1','5','2')  '1'=>'已申请', '2'=>'已同意', '3'=>'已拒绝', '4'=>'已完成', '5'=>'已取消'
    public String apply_status_str;//申请状态中文意义   ('已申请','已取消','已同意')
    public String bank_account;//提现账户
    public String bank_address;//	开户行地址
    public String bank_user_name;//	银行名称
    public String create_time;//申请时间戳
    public String create_time_str;//申请时间
    public String id;
    public String money;//申请金额

}
