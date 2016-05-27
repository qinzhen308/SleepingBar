package com.bolaa.medical.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.medical.R;
import com.bolaa.medical.adapter.WayPayAdapter;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.common.CustomToast;
import com.bolaa.medical.controller.LoadStateController.OnLoadErrorListener;
import com.bolaa.medical.httputil.HttpRequester;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.model.WXInfo;
import com.bolaa.medical.model.WayInfo;
import com.bolaa.medical.utils.PayUtil;
import com.bolaa.medical.utils.PayUtil.PayListener;
import com.bolaa.medical.utils.RSAUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 支付方式
 * 
 * @author paulz
 * 
 */
public class WayPayActivity extends BaseActivity implements PayListener {
	private TextView mMoneyTv;
	private BigDecimal priceAll = new BigDecimal(0);
	private ListView mListView;
	private List<WayInfo> mList;
	private WayPayAdapter mAdapter;

	private String pay_sn;
	private String orderType = "";
	BroadcastReceiver cReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_waypay, true, true);
		setTitleText("", "支付方式", 0, true);

		cReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				WayPayActivity.this.finish();
			}
		};

		registerReceiver(cReceiver, new IntentFilter("colsePay"));
		mMoneyTv = (TextView) findViewById(R.id.watPay_moneyTv);
		mListView = (ListView) findViewById(R.id.watPay_lv);
		mList = new ArrayList<WayInfo>();
		mAdapter = new WayPayAdapter(this, mList);
		mListView.setAdapter(mAdapter);

		pay_sn = getIntent().getStringExtra("pay_sn");// 支付单号
		orderType = getIntent().getStringExtra("orderType");
		getWayData(pay_sn);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = mList.get(position).getPayment_name();

				if (name.contains("支付宝")) {
					PayUtil.wayToZhifubao(WayPayActivity.this,
							String.valueOf(priceAll), pay_sn);
				} else if (name.contains("微信")) {
					wayWX(pay_sn);
				} else if (name.contains("招商")) {
					CustomToast.showToast(WayPayActivity.this, "暂未开通该功能", 1500);

				} else if (name.contains("银联")) {
					wayYinlian(pay_sn);
				} else {
					CustomToast.showToast(WayPayActivity.this, "暂未开通该功能", 1500);
				}

			}
		});

		mLoadStateController.setOnLoadErrorListener(new OnLoadErrorListener() {

			@Override
			public void onAgainRefresh() {
				getWayData(pay_sn);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(cReceiver);
	}

	/**
	 * 银联支付
	 * 
	 * @param pay_sn
	 */
	private void wayYinlian(String pay_sn) {
		DialogUtil.showDialog(lodDialog);
		HttpRequester requester = new HttpRequester();
		requester.mParams.put("pay_sn", pay_sn);
		NetworkWorker.getInstance().post(
				AppUrls.getInstance().URL_order_way_yinlian, new ICallback() {
					@Override
					public void onResponse(int status, String result) {
						DialogUtil.dismissDialog(lodDialog);

						JSONObject objec;

						try {
							objec = new JSONObject(result);
							if (objec != null
									&& "0".equals(objec.getString("status"))) {

								String tn = objec.getJSONObject("content")
										.getString("tn");
								PayUtil.wayToYinlian(WayPayActivity.this, tn);
							} else {
								Toast.makeText(
										WayPayActivity.this,
										objec == null ? "支付失败！" : objec
												.getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(WayPayActivity.this, "支付失败！",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}

					}
				}, requester);

	}

	/**
	 * 微信支付
	 * 
	 * @param pay_sn
	 */
	public void wayWX(String pay_sn) {
		DialogUtil.showDialog(lodDialog);
		HttpRequester requester = new HttpRequester();
		requester.mParams.put("pay_sn", pay_sn);
		NetworkWorker.getInstance().post(
				AppUrls.getInstance().URL_order_way_wxpay, new ICallback() {
					@Override
					public void onResponse(int status, String result) {

						DialogUtil.dismissDialog(lodDialog);

						JSONObject objec;
						try {
							objec = new JSONObject(result);
							if (objec != null
									&& "0".equals(objec.getString("status"))) {

								if (objec.has("content")
										&& !objec.isNull("content")) {
									WXInfo wxInfo = new Gson().fromJson(objec
											.getJSONObject("content")
											.toString(), WXInfo.class);
									if (wxInfo != null) {

										PayUtil.wayToWX(WayPayActivity.this,
												wxInfo);
									}
								}

							} else {
								Toast.makeText(
										WayPayActivity.this,
										objec == null ? "支付失败！" : objec
												.getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(WayPayActivity.this, "支付失败！",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}

					}
				}, requester);

	}

	/**
	 * 获取支付方式
	 * 
	 * @param pay_sn
	 */
	private void getWayData(String pay_sn) {
		showLoading();
		String orderType_way = "";
		if (TextUtils.isEmpty(orderType)) {
			orderType_way = AppUrls.getInstance().URL_order_way_get;
		} else {
			orderType_way = AppUrls.getInstance().URL_order_way_get_gc;
		}
		HttpRequester requester = new HttpRequester();
		requester.mParams.put("pay_sn", pay_sn);
		NetworkWorker.getInstance().post(orderType_way, new ICallback() {
			@Override
			public void onResponse(int status, String result) {
				JSONObject object;
				try {
					object = new JSONObject(result);

					if (object != null
							&& object.getString("status").equals("0")) {
						showSuccess();

						JSONObject object2 = object.getJSONObject("content");
						if (object2 != null) {
							List<WayInfo> wayList = null;

							if (object2.has("items")
									&& !object2.isNull("items")) {
//								List<OrderInfo> list = new Gson().fromJson(
//										object2.getString("items"),
//										new TypeToken<List<OrderInfo>>() {
//										}.getType());

//								if (list != null && list.size() > 0) {
//									if ("20".equals(list.get(0)
//											.getOrder_state())) {
//										resultForZhifubao(1, "");
//									}
////									orderList.addAll(list);
//
//									for (int i = 0; i < list.size(); i++) {
//										priceAll = priceAll.add(new BigDecimal(
//												list.get(i).getOrder_amount()));
//									}
//								}

								wayList = new Gson().fromJson(
										object2.getString("payment"),
										new TypeToken<List<WayInfo>>() {
										}.getType());
							} else if (object2.has("cardorder_info")
									&& !object2.isNull("cardorder_info")) {
								wayList = new Gson().fromJson(
										object2.getString("payment_info"),
										new TypeToken<List<WayInfo>>() {
										}.getType());
								String card_mount = object2.getJSONObject(
										"cardorder_info").getString(
										"card_amount");
								priceAll = new BigDecimal(card_mount);
							}
							mMoneyTv.setText(priceAll + "元");

							if (wayList != null && wayList.size() > 0) {
								mList.addAll(wayList);
								mAdapter.notifyDataSetChanged();
							}
						}
					} else {
						showFailture();
					}
				} catch (JSONException e) {
					showFailture();
					e.printStackTrace();
				}

			}
		}, requester);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*************************************************
		 * 步骤3：处理银联手机支付控件返回的支付结果
		 ************************************************/
		if (data == null) {
			return;
		}

		String msg = "";
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			// 支付成功后，extra中如果存在result_data，取出校验
			// result_data结构见c）result_data参数说明
			if (data.hasExtra("result_data")) {
				String result = data.getExtras().getString("result_data");
				try {
					JSONObject resultJson = new JSONObject(result);
					String sign = resultJson.getString("sign");
					String dataOrg = resultJson.getString("data");

					// 验签证书同后台验签证书
					// 此处的verify，商户需送去商户后台做验签
					boolean ret = RSAUtil.verify(dataOrg, sign,
							PayUtil.union_Model);
					if (ret) {
						// 验证通过后，显示支付结果
						msg = "支付成功！";
					} else {
						// 验证不通过后的处理
						// 建议通过商户后台查询支付结果
						msg = "支付失败！";
					}
				} catch (JSONException e) {
				}
			} else {
				// 未收到签名信息
				// 建议通过商户后台查询支付结果
				msg = "支付成功！";
			}

			resultForZhifubao(1, "支付成功");
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败！";
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "用户取消了支付";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("支付结果通知");
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				resultForZhifubao(0, "您已取消了支付");
			}
		});
		builder.create().show();
	}

	@Override
	public void resultForZhifubao(int state, String detail) {
		if (TextUtils.isEmpty(orderType)) {// 普通商品

			Intent intent = new Intent(this, PayResultActivity.class);
			intent.putExtra("pay_sn", pay_sn);
			intent.putExtra("state", state);
			intent.putExtra("detail", detail);
			startActivity(intent);
			finish();
		} else {
			// 购物卡订单
			if (state == 0) {
				Toast.makeText(this, detail, Toast.LENGTH_SHORT).show();
			} else {
				finish();
			}
		}
	}
	
	
	public static void invoke(Context context,Order order){
		Intent intent =new Intent(context,WayPayActivity.class);
		Bundle data=new Bundle();
		data.putSerializable("order", order);
		intent.putExtra("data", data);
		context.startActivity(intent);
		
	}

}
