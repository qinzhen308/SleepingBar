package com.bolaa.sleepingbar.model;

/**
 * 交易记录
 * Created by paulz on 2016/6/3.
 */
public class DealLog {
    public String change_desc;
    public String change_time;
    public String log_id;
    public String pay_name;
    public String user_id;
    public String user_money_s;
    public String account_no;
    public String realname;
    public String in_out_str;//转入。。转出
    public int in_out;//1 转入
    public int change_type;//0=>'其他', 1=>'支付宝', 2=>'微信', 3=>'银联', 4=>'系统赠送', 5=>'睡眠基金', 6=>'提现', 7=>'赠送基金'
    public String trade_no;//支付宝或微信账号

}
