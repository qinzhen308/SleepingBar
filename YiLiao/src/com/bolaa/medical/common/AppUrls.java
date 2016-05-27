package com.bolaa.medical.common;

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
 
	public String URL_getTmpToken = HYH_URL+ "method=help/get" +
			"" +
			"Tmp_access_token&app_token=" + app_token;// 获取临时token
	public String URL_getRegion = HYH_URL + "method=help/area_list&app_token="+ app_token;// 获取地址列表
	public String URL_protocoll ="http://test.haiyuehui.com/index.php?act=app_helper&op=document&code=agreement";// 用户协议
	
	//会员相关
	public String URL_register = HYH_URL + "method=customer/register";// 注册
	public String URL_login = HYH_URL + "method=customer/login";// 登录
	public String URL_findPW = HYH_URL + "method=customer/findpassword";// 找回密码
	public String URL_logout = HYH_URL + "method=customer/logout";// 安全退出
	public String URL_avatar = HYH_URL + "method=customer/update_customer_avatar";// 修改头像
	public String URL_info = HYH_URL + "method=customer/update_customer_info";// 修改会员信息
	public String URL_feedback = HYH_URL + "method=customer/customer_feedback";//当前用户留言反馈
	public String URL_feedback_list = HYH_URL + "method=customer/get_feedback_items";//反馈列表
	public String URL_address_add = HYH_URL + "method=customer/add_edit_customer_address";//添加收货地址
	public String URL_address_list = HYH_URL + "method=customer/get_address_items";//收货地址列表
	public String URL_address_delete = HYH_URL + "method=customer/del_address_item";//删除收货地址
	public String URL_info_get = HYH_URL + "method=customer/get_login_customer_info";//查询已登录用户的信息
	public String URL_info_one_get = HYH_URL + "method=customer/get_customer_dashboard";//当前用户信息总览
	public String URL_info_safety_get = HYH_URL + "method=customer/get_safety_info";//获取用户安全信息
	public String URL_info_updatePW = HYH_URL + "method=customer/update_customer_password";//修改密码
	public String URL_info_validate_phone= HYH_URL + "method=customer/update_customer_mobile_phone";//手机认证
	public String URL_info_validate_email= HYH_URL + "method=customer/update_customer_email";//邮箱认证
	public String URL_info_update_payPW= HYH_URL + "method=customer/update_pay_password";//设置支付密码
	public String URL_hyb_list= HYH_URL + "method=customer/get_hyb_logs";//海悦币列表
	// 分类
	public String URL_catergoryOne = HYH_URL + "method=category/get_categories";// 分类的首页
	public String URL_CATEGORY_ALL = HYH_URL + "method=category/categories_list&app_token=6HDm4jAUv4w5W-ycjAvZFo630qHrXJS60yNKT1r";// 所有分类
	public String URL_brand = HYH_URL + "method=brand/index";//品牌列表

	// 商品相关
	public String URL_HOME_DATA = HYH_URL + "method=index/index";// 首页
	public String URL_GOODS_LIST = HYH_URL + "method=product/get_items";//商品列表
	public String URL_COLLECTION_STORE = HYH_URL + "method=product/favoritesstore";//收藏店铺
	public String URL_COLLECTION_GOODS = HYH_URL + "method=product/favoritesgoods";//收藏商品
	public String URL_COLLECTION_GOODS_LIST= HYH_URL + "method=customer/fgoodslist";//收藏商品列表
	public String URL_COLLECTION_STORE_LIST= HYH_URL + "method=customer/fstorelist";//收藏店铺列表
	public String URL_COLLECTION_DELETE = HYH_URL + "method=customer/delfavorites";//取消收藏
	public String URL_GOODS_DETAIL = HYH_URL + "method=product/get_product_detail";//商品详情
	public String URL_GOODS_FREIGHT = HYH_URL + "method=product/calc_goods_freight";//查询邮费
	public String URL_COLLECTION_DEL = HYH_URL + "method=customer/delfavorites";//删除收藏(商品/店铺)
	public String URL_FISHION_LIST = HYH_URL + "method=fashions/index";//时尚列表
	public String URL_FISHION_DETAIL = HYH_URL + "method=fashions/get_fashion_item";//时尚详情
	public String URL_GOODS_ATTR_LIST = HYH_URL + "method=product/get_attr_list";//商品属性筛选

	//购物卡相关
	public String URL_goodcard_list = HYH_URL + "method=showcard/card_list";//购物卡列表
	public String URL_order_good_list = HYH_URL + "method=cardorders/get_order_items";//购物卡订单列表
	public String URL_order_goodCard_detail = HYH_URL + "method=cardorders/get_card_orders_detail";//购物卡订单详情
	public String URL_goodCard_detail = HYH_URL + "method=cardorders/get_card_item";//用户的购物卡详情
	public String URL_order_good_delete = HYH_URL + "method=cardorders/del_order_item";//删除购物卡订单
	public String URL_goodCard_userList = HYH_URL + "method=cardorders/get_card_items";//用户的购物卡列表
	public String URL_goodCard_buy = HYH_URL + "method=showcard/buy_card";//购买购物卡
	public String URL_goodCard_bind = HYH_URL + "method=cardorders/bind_card";//绑定购物卡
	public String URL_goodCard_blance = HYH_URL + "method=cardorders/get_card_blance";//查询购物卡余额
	
	//订单相关
	public String URL_order_list = HYH_URL + "method=orders/get_order_items";//订单列表-订单管理
	public String URL_order_detail = HYH_URL + "method=orders/get_order_info";//订单详情
	public String URL_order_address_set= HYH_URL + "method=checkout/set_checkout_address";//设置订单收货地址
	public String URL_order_checkoutInfo = HYH_URL + "method=checkout/get_checkout_info";//获取结算信息
	public String URL_order_checkoutOrder = HYH_URL + "method=checkout/set_checkout_order";//生成订单
	public String URL_order_getPayInfo = HYH_URL + "method=checkout/get_pay_infor";//获取支付信息
	public String URL_order_pinglun = HYH_URL + "method=orders/get_order_comment_goods";//待评论的订单列表
	public String URL_order_update_state = HYH_URL + "method=orders/update_order_state";//修改订单的状态
	public String URL_order_evaluate = HYH_URL + "method=orders/set_order_comment";//评论订单
	public String URL_order_shipment = HYH_URL + "method=orders/get_order_shipment_info";//订单物流
	public String URL_order_refound_apply = HYH_URL + "method=orders/set_item_refund";//申请退款售后
	public String URL_order_create = HYH_URL + "method=checkout/set_checkout_order";//生成订单
	public String URL_order_refound_list = HYH_URL + "method=orders/get_refund_list";//退款售后列表
	public String URL_order_way_get = HYH_URL + "method=checkout/get_pay_info";//获取支付方式
	public String URL_order_way_get_gc = HYH_URL + "method=cardorders/get_pay_info";//获取支付方式-购物卡订单
	public String URL_order_way_yinlian = HYH_URL + "method=apppayment/get_unionpay_tn";//银联支付
	public String URL_order_way_wxpay = HYH_URL + "method=apppayment/get_wxpay_prepayid";//微信支付
	public String URL_order_calchgfee = HYH_URL + "method=checkout/calchgfee";//计算折后海关费
    //购物车相关
	public String URL_Cart_add = HYH_URL + "method=checkout/add_to_cart";//添加商品到购物车
	public String URL_Cart_list = HYH_URL + "method=checkout/get_cart_items";//购物车列表
	public String URL_Cart_setNum = HYH_URL + "method=checkout/set_cart_goods_num";//修改购物车的数量
	public String URL_Cart_delete = HYH_URL + "method=checkout/del_cart_item";//删除购物车里面的商品
	public String URL_Checkout_info_get = HYH_URL + "method=checkout/get_checkout_info";//获取购物车生成订单信息(结算)
	public String URL_Cart_num_get= HYH_URL + "method=checkout/get_cart_num";//获取购物车的数量
	
	public String URL_HOT_SEARCH = HYH_URL + "method=product/search_page";//搜索页热门搜索
	
	public String URL_STORE_HOME = HYH_URL + "method=storeinfo/index";//店铺详情首页
	public String URL_STORE_STORY = HYH_URL + "method=storeinfo/store_brand";//店铺详情故事
	public String URL_GOODS_COMMENT_LIST = HYH_URL + "method=product/get_product_comments";//商品评价列表

	public String URL_VOUCHER_LIST = HYH_URL + "method=cardorders/get_voucher_list";//抵用券列表
	public String URL_VOUCHER_SELECT = HYH_URL + "method=checkout/voucherselected";//勾选抵用券
	public String URL_VOUCHER_CANCEL = HYH_URL + "method=checkout/vouchercancel";//取消抵用券
	
	public String URL_MY_POINTS = HYH_URL + "method=customer/get_points";//我的积分
	
	public String URL_THIRD_LOGIN_QQ = HYH_URL + "method=customer/customer_login_by_qq";//尝试qq三方登录
	public String URL_THIRD_LOGIN_WX = HYH_URL + "method=customer/login_by_weichat";//尝试微信三方登录
	
	public String URL_THIRD_ACCOUNT_UNBIND = HYH_URL + "method=customer/set_account_unbind";//解除绑定
	
	//------------------------ 华丽的分割线----------------
	public String APP_KEY="test";
	public String BASE_URL="http://jk.m1ju.com/api/api.php?format=json&app_key="+APP_KEY;
	
	public String URL_LOGIN=BASE_URL+"&c=user&act=login_act";
	public String URL_LOGOUT=BASE_URL+"&c=user&act=login_out";//退出登录
	public String URL_GET_CAPTCHA=BASE_URL+"&c=user&act=send_sms";
	public String URL_REGISTER=BASE_URL+"&c=user&act=reg_act";
	public String URL_USER_INFO_SAVE=BASE_URL+"&c=user&act=edit_profile";//修改或保存个人资料
	public String URL_HOSPITAL_LIST=BASE_URL+"&c=hospital&act=hospital_list";//体检机构列表
	public String URL_HOSPITAL_DETAIL=BASE_URL+"&c=hospital&act=hospital_info";//医院详情
	public String URL_HOSPITAL_APPOINTMENT_DAY_INFO=BASE_URL+"&c=hospital&act=appointment_day_info";//某机构一天中的预约时间段
	public String URL_HOSPITAL_MAKE_AN_APPOINTMENT=BASE_URL+"&c=hospital&act=appointment";//预约
	public String URL_REGION_ALL=BASE_URL+"&c=index&act=region_list";//全部地区
	public String URL_ALREADY_BOOK=BASE_URL+"&c=order&act=index";//已经预约的体检（订单）
	public String URL_MY_SCORE=BASE_URL+"&c=user&act=get_point";//我当前积分
	public String URL_SCORE_HISTORY=BASE_URL+"&c=user&act=point_log";//积分记录
	public String URL_SCORE_CHANGE_MONEY=BASE_URL+"&c=user&act=exchange";//积分兑换
	public String URL_CASH_WITHDRAW=BASE_URL+"&c=account&act=docash";//提取现金
	public String URL_CURRENT_BALANCE=BASE_URL+"&c=account&act=get_money";//当前余额
	public String URL_CASH_WITHDRAW_PAGE_INFO=BASE_URL+"&c=account&act=cash";//提现页面历史记录
	public String URL_CASH_HISTORY=BASE_URL+"&c=account&act=cash_log";//提现记录
	public String URL_CASH_LOG=BASE_URL+"&c=account&act=money_log";//余额明细记录
	public String URL_REPORT_LIST=BASE_URL+"&c=index&act=index";//我的体检报告
	public String URL_SETTING_LIST=BASE_URL+"&c=index&act=set_article_list";//设置里的模块
	public String URL_SETTING_DETAIL=BASE_URL+"&c=index&act=article_info";//设置里的模块对应的内容  还有健康积分说明和提现说明
	public String URL_SCORE_HELP=BASE_URL+"&c=index&act=point_desc";//还有健康积分说明
	public String URL_WITHDRAW_HELP=BASE_URL+"&c=index&act=cash_desc";//提现说明
	public String URL_FIND_PWD_VERIFY_CAPTCHA=BASE_URL+"&c=user&act=get_password";//找回密码--校验验证码
	public String URL_FIND_PWD=BASE_URL+"&c=user&act=edit_password";//找回密码
	public String URL_COUNPONS_LIST=BASE_URL+"&c=bonus&act=mybonus";//优惠券 is_used  0可使用1过期2已使用3未到使用
	public String URL_PAY_PAGE_INFO=BASE_URL+"&c=hospital&act=checkout";//结算页面的信息
	public String URL_ORDER_PAY_PAGE_INFO=BASE_URL+"&c=order&act=pay";//订单去支付页面信息(订单详情)
	public String URL_DO_PAY=BASE_URL+"&c=order&act=dopay";//提交支付方式，返回支付单
	public String URL_HOSPITAL_EVALUATE_COMMIT=BASE_URL+"&c=order&act=save_comment";//机构评价
	public String URL_HOSPITAL_COMBO_LIST=BASE_URL+"&c=hospital&act=package_list";//体检套餐列表
	public String URL_HOSPITAL_EVALUATE_LIST=BASE_URL+"&c=hospital&act=comment_list";//评论列表

	/*支付宝回调地址*/
	public String URL_ZFB_NOTIFY="http://jk.m1ju.com/app/respond_alipay.php";




}
