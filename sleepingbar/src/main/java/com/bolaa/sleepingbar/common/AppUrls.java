package com.bolaa.sleepingbar.common;

public class AppUrls {
	private static AppUrls mUrls;

	public static AppUrls getInstance() {
		if (mUrls == null) {
			synchronized (AppUrls.class) {
				if (mUrls == null) {
					mUrls = new AppUrls();
				}
			}
		}
		return mUrls;
	}
	public String app_token = "6HDm4jAUv4w5W-ycjAvZFo630qHrXJS60yNKT1r";// 测试拟定的手机标识码

//	public String HYH_URL = "http://haiyuehuitest.m1ju.com/api.php?";// 测试地址
	public String HYH_URL = "http://test.haiyuehui.com/api.php?";// 正式环境的测试服务器


	public String URL_THIRD_LOGIN_QQ = HYH_URL + "method=customer/customer_login_by_qq";//尝试qq三方登录
	public String URL_THIRD_LOGIN_WX = HYH_URL + "method=customer/login_by_weichat";//尝试微信三方登录
	
	public String URL_THIRD_ACCOUNT_UNBIND = HYH_URL + "method=customer/set_account_unbind";//解除绑定
	
	//------------------------ 华丽的分割线----------------
	public String APP_KEY="test";
//	public String BASE_URL="http://192.168.2.126/beijingsleep/app/";
	public String BASE_URL="http://bjsleep.m1ju.com/app/";

	public String URL_REGISTER=BASE_URL+"&c=user&act=reg_act";
	public String URL_USER_INFO_SAVE=BASE_URL+"user.php?act=do_edit_userinfo";//修改或保存个人资料

	public String URL_HOSPITAL_MAKE_AN_APPOINTMENT=BASE_URL+"&c=hospital&act=appointment";//预约
	public String URL_REGION_ALL=BASE_URL+"&c=index&act=region_list";//全部地区

	public String URL_CASH_HISTORY=BASE_URL+"&c=account&act=cash_log";//提现记录

	public String URL_FIND_PWD_VERIFY_CAPTCHA=BASE_URL+"&c=user&act=get_password";//找回密码--校验验证码
	public String URL_FIND_PWD=BASE_URL+"&c=user&act=edit_password";//找回密码
	public String URL_COUNPONS_LIST=BASE_URL+"&c=bonus&act=mybonus";//优惠券 is_used  0可使用1过期2已使用3未到使用
	public String URL_PAY_PAGE_INFO=BASE_URL+"&c=hospital&act=checkout";//结算页面的信息
	public String URL_ORDER_PAY_PAGE_INFO=BASE_URL+"&c=order&act=pay";//订单去支付页面信息(订单详情)
	public String URL_DO_PAY=BASE_URL+"&c=order&act=dopay";//提交支付方式，返回支付单
	public String URL_HOSPITAL_EVALUATE_COMMIT=BASE_URL+"&c=order&act=save_comment";//机构评价
	public String URL_HOSPITAL_COMBO_LIST=BASE_URL+"&c=hospital&act=package_list";//体检套餐列表
	public String URL_HOSPITAL_EVALUATE_LIST=BASE_URL+"&c=hospital&act=comment_list";//评论列表
	public String URL_CHECK_UPDATE=BASE_URL+"sync.php?act=get_versions";//升级

	public String URL_HOME=BASE_URL+"sleep.php?act=index";//睡吧首页
	public String URL_SUPPORTER_LIST=BASE_URL+"sleep.php?act=my_funds";//我的基金页面，也就是基金支持者列表
	public String URL_SUPPORTER_DETAIL=BASE_URL+"sleep.php?act=fund_detail";//某支持者对我支持基金的明细
	public String URL_FUNDS_RANKING_LIST=BASE_URL+"sleep.php?act=fund_ranking";//基金排行
	public String URL_FUNDS_RANKING_LIST_PRAISE=BASE_URL+"sleep.php?act=do_praise";//基金排行点赞
	public String URL_MEDAL_LIST=BASE_URL+"user.php?act=my_medal";//我的勋章
	public String URL_MEDAL_DETAIL=BASE_URL+"user.php?act=medal_detail";//我的勋章详情
	public String URL_GET_CAPTCHA=BASE_URL+"user.php?act=send_verify_code";
	public String URL_LOGIN=BASE_URL+"user.php?act=login";//登录
	public String URL_LOGOUT=BASE_URL+"user.php?act=logout";//退出登录
	public String URL_DEAL_HISTORY=BASE_URL+"user.php?act=account_log";//历史交易记录
	public String URL_ACCOUNT_INFO=BASE_URL+"user.php?act=user_account";//个人中心我的账户
	public String URL_CASH_WITHDRAW=BASE_URL+"user.php?act=do_apply_cash";//提现
	public String URL_CASH_WITHDRAW_PAGE_INFO=BASE_URL+"user.php?act=apply_cash";//提现页面信息
	public String URL_DO_RECHARGE=BASE_URL+"user.php?act=do_recharge";//充值
	public String URL_DEVICE_HISTORY=BASE_URL+"user.php?act=equipment_list";//已经绑定的设备列表
	public String URL_MY_FRIENDS_LIST=BASE_URL+"community.php?act=my_friend";//好友
	public String URL_SEARCH_FRIENDS=BASE_URL+"community.php?act=search_index";//添加好友页面
	public String URL_DO_CARE=BASE_URL+"community.php?act=do_care";//关注
	public String URL_CANCEL_CARE=BASE_URL+"community.php?act=cancel_care";//取消关注
	public String URL_TOPIC_LIST=BASE_URL+"community.php?act=topic_list";//社区首页的话题
	public String URL_COMMUNITY_INFORMATION=BASE_URL+"community.php?act=index";//社区首页的图文和文字咨询
	public String URL_COMMUNITY_SEND_POSTS=BASE_URL+"community.php?act=add_topic";//发帖
	public String URL_TOPIC_DETAIL=BASE_URL+"community.php?act=topic_detail";//话题详情
	public String URL_TOPIC_COMMENTS_LIST=BASE_URL+"community.php?act=comment_list";//话题详情的评论
	public String URL_PUBLISH_COMMENTS=BASE_URL+"community.php?act=do_comment";//话题详情的评论
	public String URL_BBS_POSTS_GOOD=BASE_URL+"community.php?act=do_praise";//话题点赞
	public String URL_OTHER_USER_INFO=BASE_URL+"community.php?act=user_info";//其他用户的主页
	public String URL_INFORMATION_DETAIL=BASE_URL+"community.php?act=info_detail";//资讯详情
	public String URL_MY_MSG=BASE_URL+"community.php?act=my_message";//我的消息
	public String URL_USER_PAGE_INFO=BASE_URL+"user.php?act=edit_userinfo";//我的个人信息页面数据
	public String URL_PRIVATE_SETTING=BASE_URL+"user.php?act=setting_privacy";//隐私设置
	public String URL_SUPPORT_FUNDS_TO=BASE_URL+"sleep.php?act=send_fund";//基金支持
	public String URL_GET_USER_INFO=BASE_URL+"user.php?act=userinfo";//获取个人全部信息
	public String URL_GET_MSG_COUNT=BASE_URL+"user.php?act=message_count";//获取消息数量

	public String URL_ACTIVE_HOME=BASE_URL+"activity.php?act=list";//首页活动页

	public String URL_BBS_POSTS_INFORM=BASE_URL+"community.php?act=to_report";//举报
	public String URL_BIND_PUSH_INFO=BASE_URL+"user.php?act=mobile_key";//上报push registration id

	public String URL_BIND_WX_USER_INFO=BASE_URL+"user.php?act=bind_weixin";//微信绑定
	public String URL_BIND_WATCH_MAC_ADDRESS=BASE_URL+"user.php?act=bind_equipment";//手环绑定
	public String URL_UNBIND_WATCH_MAC_ADDRESS=BASE_URL+"user.php?act=unbind_equipment";//手环绑定
	public String URL_WATCH_SYNC_SLEEP=BASE_URL+"sync.php?act=sleep_save";//同步睡眠信息到服务器
	public String URL_WATCH_SYNC_STEP=BASE_URL+"sync.php?act=walk_save";//同步运动信息到服务器
	public String URL_GET_SLEEP_IN_YEAR=BASE_URL+"sync.php?act=sleep_trend";//获取周月年的数据
	public String URL_SLEEP_DATA_COLLECT_TIME=BASE_URL+"sync.php?act=sleep_time";//采集睡眠数据的时间段

	public String URL_SYNCH_LOCATION=BASE_URL+"sync.php?act=lng_lat";//同步位置信息

	public String URL_ARTICAL_SERVICE_PROTOCAL=BASE_URL+"user.php?act=service_terms";
	public String URL_ARTICAL_LAW=BASE_URL+"user.php?act=legal_notice";
	public String URL_ARTICAL_ABOUT=BASE_URL+"user.php?act=about_sleepbar";
	public String URL_SLEEP_HELP=BASE_URL+"sleep.php?act=fund_spec";

	public String URL_MEDAL_SHARE=BASE_URL+"user_share.php?act=share_sleepinfo";
	public String URL_MOVEMENT_SHARE=BASE_URL+"user_share.php?act=share_walkinfo";
	public String URL_SLEEP_SHARE=BASE_URL+"user_share.php?act=share_sleepinfo";


	/*支付宝回调地址*/
	public String URL_ZFB_NOTIFY="http://jk.m1ju.com/app/respond_alipay.php";


}
