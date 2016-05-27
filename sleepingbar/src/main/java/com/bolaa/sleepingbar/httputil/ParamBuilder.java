package com.bolaa.sleepingbar.httputil;

import com.bolaa.sleepingbar.HApplication;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-5-19
 * Time: 下午4:20
 * To change this template use File | SettingsActivity | File Templates.
 */
public class ParamBuilder {
	
	public static final String APP_TOKEN="app_token";//APP权限认证token
	public static final String ACCESS_TOKEN="token";//APP权限认证token
	public static final String KEYWORD="keyword";//商品搜索关键字
	public static final String BRAND_ID="brand_id";//品牌ID
	public static final String STORE_ID="store_id";//店铺ID
	public static final String AREA_ID="area_id";//区域ID
	public static final String ATTR_ID="attr_id";// 属性id,以下划线链接;demo:1_2_3
	public static final String SORT_KEY="key";//排序字段 enum 1,2,3;排序字段 1=>goods_salenum(热销),2=>goods_click(商品浏览量),3=>goods_price(商品价格)
	public static final String ORDER="order";//排序方式 enum 1/mixed,商品排序顺序 1=>asc mixed=>desc
	public static final String CATE_ID="cate_id";//分类id
	public static final String GOODS_ID="goods_id";//商品ID
	public static final String FAV_ID="fav_id";//商品ID
	public static final String TYPE="type";//类型
	public static final String GOODS_WEIGHT="goods_weight";//商品重量
	public static final String ARTICLE_ID="article_id";//时尚id

    public static final String ID = "id";

    public static final String COUNT = "count";

    public static final String URL_NAME = "url_name";

    public static final String URL = "url";

    public static final String DEVICE_ID = "deviceid";
    public static final String DEVICE_ID_ = "device_id";

    public static final String VERSION = "version";

    public static final String DIV = "div";

    public static final String CHANNEL = "channelid";

    public static final String CHANNEL_EXPOSE = "channel";//曝光的渠道字段名，与其他接口区分开

    public static final String PLATFORM = "platform";

    public static final String PERPAGE_COUNT = "per_page_count";

    public static final String USER_TYPE = "user_type";

    //是否是学生身份
    public static final String STUDENT = "student";

    public static final String USER_ROLE = "user_role";

    public static final String CATEGORY_TAG_URL = "tag_url";//商品所属分类


    public static final String MuYing_TAG_URL = "list_type";

    public static final String CATEGORY_QUERY = "category_query_value";//分类列表查询属性，若key为这个拼接url的时候和普通的有区别

    public static final String HOT_BANNER_TYPE = "pagetype";

    public static final String USER_ID = "user_id";

    public static final String ORDER_ID = "order_id";

    public static final String DEFAULT = "default";

    public static final String IS_DEFAULT = "isDefault";

    @Deprecated
    public static final int PAGE_SIZE_2G = 10;

    public static final int PAGE_SIZE_WIFI = 20;

    public static final int PAGE_EVERDAT = 10;

    public static final String PAGE = "page";

    public static final String DATE = "date";

    public static final String LIMIT = "limit";

    public static final String OFFSET = "offset";

    public static final String PAGE_SET = "page_set";

    public static final String PER_PAGE = "per_page";

    public static final String IMAGE_TYPE = "image_type";
    public static final String EXCLUDE = "exclude";

    public static final String DATE_TOMORROW = "tomorrow";

    public static final String IMAGE_ALL = "all";

    public static final String IMAGE_WEBP = "webp";

    public static final String C_ID = "cids";

    public static final String CITY_ID = "cityid";

    public static final String CITY_NAME = "cityname";

    public static final String LAT_HISTORY = "lat_history";

    public static final String LNG_HISTORY = "lng_history";

    public static final String FORMAT = "format";

    public static final String JSON = "json";

    public static final String TAO_TAG_ID = "tao_tag_id";

    public static final String MAC = "mac";


    public static final String OOS = "oos";

    public static final String GRADE = "grade";

    public static final String INTEGRATION_PARAM = "return_to";

    public static final String BEGIN_TIME = "begin_time";

    public static final String IMAGE_MODEL = "image_model";

    public static final String BIRTH_YEAR = "year";
    public static final String BIRTH_MONTH = "month";
    public static final String BIRTH_STATE = "baby_status";

    public static final String COUNTS = "counts";
    public static final String TAG = "tag";
    //category
    public static final String CATEGORY_ORDER = "order";//排序规则
    public static final String CATEGORY_MIN_PRICE = "min_price";
    public static final String CATEGORY_MAX_PRICE = "max_price";
    public static final String CATEGORY_SHOP_TYPE = "shop_type";//商铺类型
    // cateogry parent_url_name
    public static final String CATEGORY_PARENT_URL_NAME = "parent_url_name";


    // cateogry parent_url_name
    public static final String CATEGORY_URL_NAME = "url_name";
    //当前分类的上级分类
    public static final String CATEGORY_PARENT_TAG = "parent_tag";

    public static final String DELETE_OLD_CACHE = "delete_old_cache";

    public static final String MUYING_AGE = "age";
    public static final String MUYING_SEX = "gender";

    // date
    public static final long MINUTE = 1000 * 60;
    public static final long HALF_HOUR = 30 * MINUTE;
    public static final long HOUR = 2 * HALF_HOUR;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    public static final String GIFT_ORDER_TYPE = "gift_order_type";
    public static final String WELFARE_TYPE = "welfare";
    public static final String RAFFLE_TYPE = "raffle";
    public static final String AUCTION_TYPE = "auction";

    //收货地址
    public static final String PARA_CONSIGNEE_NAME = "receiver_name";
    public static final String PARA_PROVINCE_ID = "province_id";
    public static final String PARA_CITY_ID = "city_id";
    public static final String PARA_AREA_ID = "county_id";
    public static final String PARA_PROVINCE_NAME = "province_name";
    public static final String PARA_CITY_NAME = "city_name";
    public static final String PARA_AREA_NAME = "county_name";
    public static final String PARA_ADDRESS_INFO = "address";
    public static final String PARA_PHONE_NUMBER = "mobile";
    public static final String PARA_POSTCODE = "post_code";

    //曝光打点参数
    public static final String PARA_TYPE_GUANG = "1";
    public static final String PARA_TYPE_JU = "0";
    public static final String PARA_TYPE_GUANG_BANNER = "17";
    //登录domain

    //public static String DOMAIN = "xiongmaoz.com";
    public static String DOMAIN = "zhe800.com";

    //获取用户优惠券
    public static final String STATUS = "status";
    public static final String OVER_FLAG = "over_flag";

    //share
    public static final String SHARE_TYPE = "share_type";

    //手机周边
    public static final String MODEL = "model";
    public static final String ANDROID = "android";

    //根据ids获取商品信息
    public static final String SHOW_OFFLINE = "show_offline";

    //是否显示品牌团、主题馆、优品会等等商品
    public static final String DEAL_TYPE = "deal_type";
    public static final String DEAL_TYPE_BRAND_GROUP = "1";
    public static final String DEAL_TYPE_THEME = "2";
    public static final String DEAL_TYPE_OPTIMAL = "3";

    //控制列表超级品牌团显示的字段
    public static final String SUPER = "super";
    //H5banner，首页浮层广告，锁屏大图参数
    public static final String AD_TYPE = "ad_type";

    //消息中心
    //表示客户端支持的消息组类型。多个类型用逗号分隔；目前只支持活动公告和到期提醒。活动公告：activities，到期提醒：expire_remind
    public static final String MSG_CENTER_GROUPS = "groups";
    //表示客户端支持的消息类型。多个类型用逗号分隔；目前只支持代金券，标识符: coupon
    public static final String MSG_CENTER_MTYPES = "mtypes";
    //表示客户端支持的消息组类型,目前只支持到期提醒，标识符:expire_remind
    public static final String MSG_CENTER_GROUP = "group";
    //表示客户端支持的消息类型,，目前只支持活动公告和代金券,标识符:activities,coupon  (后台消息分类好乱)  删除时用
    public static final String MSG_CENTER_MTYPE = "mtype";



    public ParamBuilder() {
		// TODO Auto-generated constructor stub
    	append(ParamBuilder.ACCESS_TOKEN, HApplication.getInstance().token);
	}
    /**
     * A string contains a group status ID  and separated in "," .
     */
    public static final String IDS = "ids";

    /**
     * Create a container to hold request parameters.
     */
    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

    /**
     * Support parameter : since_id,max_id,user_id.
     *
     * @return
     */
    public String buildQuerySelection() {
        String result = null;
        if (!params.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("1 = 1");
            for (NameValuePair value : params) {
                if (value.getName().equals(CITY_ID)) {
                    buffer.append(" and " + ID + " = " + value.getValue());
                }
            }
            result = buffer.toString();
        }
        return result;
    }

    /**
     * Support parameters: count.
     *
     * @return
     */
    public String buildQueryOrderby() {
        String result = null;
        if (!params.isEmpty()) {
            NameValuePair countValue = null;
            for (NameValuePair value : params) {
                if (value.getName().equals(COUNT)) {
                    countValue = value;
                    break;
                }
            }
            if (countValue != null) {
                result = ID + " Desc limit " + countValue.getValue();
            }
        }
        return result;
    }

    public void append(String param, String value) {
        //if (Tao800Util.isNull(value)) return;
        params.add(new BasicNameValuePair(param, value));
    }

    public void append(String param, long value) {
        params.add(new BasicNameValuePair(param, String.valueOf(value)));
    }

    public void append(String param, int value) {
        params.add(new BasicNameValuePair(param, String.valueOf(value)));
    }

    public void append(String param, double value) {
        params.add(new BasicNameValuePair(param, String.valueOf(value)));
    }

    public List<NameValuePair> getParamList() {
        return params;
    }

    /**
     * Clear cache data.
     */
    public void clear() {
        params.clear();
    }

}
