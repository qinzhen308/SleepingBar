package com.bolaa.sleepingbar.utils;

import java.util.ArrayList;
import java.util.List;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.ShareGridAdapter;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.model.ShareChannel;
import com.core.framework.util.DialogUtil;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * 分享工具类
 * 
 * @author jjj
 * 
 * @date 2015-8-24
 */
public class ShareUtil {
	private Dialog shareDialog;
	private Activity mContext;
	private String title = " ";
	private String content = " ";
	private UMImage mUmImage;
	private String targeUrl = "http://www.jglinxin.com/";

	public ShareUtil(Activity cActivity, String title, String content,
			String url) {
		this.mContext = cActivity;
		this.title = title;
		this.content = content;
		this.targeUrl = url;
		mUmImage = new UMImage(mContext, R.drawable.logo);
	}

	public ShareUtil(Activity cActivity, String title, String content,
			String url, String imgPath) {
		this.mContext = cActivity;
		this.title = title;
		this.content = content;
		this.targeUrl = url;
		mUmImage = new UMImage(mContext, imgPath);
	}

	public static void initShareData() {

		PlatformConfig.setWeixin(AppStatic.WX_APPID, AppStatic.WX_secret);
		// 新浪微博
		PlatformConfig.setSinaWeibo(AppStatic.Sina_APPKEY,
				AppStatic.Sina_secret);
		PlatformConfig.setQQZone(AppStatic.QQ_APPID, AppStatic.QQ_APPKEY);
	}

	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Toast.makeText(mContext, platform + " 分享成功啦", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(mContext, platform + " 分享失败啦", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(mContext, platform + " 分享取消了", Toast.LENGTH_SHORT)
					.show();
		}
	};

	public void showShareDialog() {

		if (shareDialog == null) {

			ShareChannel sh0 = new ShareChannel(0, "QQ好友",
					R.drawable.ic_share_qq);

			ShareChannel sh2 = new ShareChannel(0, "微信好友",
					R.drawable.ic_share_wx_friend);
			ShareChannel sh3 = new ShareChannel(0, "新浪微博",
					R.drawable.ic_share_weibo);

			List<ShareChannel> list = new ArrayList<ShareChannel>();
			list.add(sh0);
			list.add(sh2);
			list.add(sh3);

			View view = LayoutInflater.from(mContext).inflate(
					R.layout.dialog_share_posts, null);
			GridView gv = (GridView) view.findViewById(R.id.gridview);

			ShareGridAdapter sAdapter = new ShareGridAdapter(mContext);
			sAdapter.setList(list);
			gv.setAdapter(sAdapter);

			gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					shareDialog.dismiss();
					switch (position) {
					case 0:
						new ShareAction(mContext).setPlatform(SHARE_MEDIA.QQ)
								.setCallback(umShareListener).withText(content)
								.withTitle(title).withTargetUrl(targeUrl)
								.withMedia(mUmImage).share();
						break;
					case 1:
						new ShareAction(mContext)
								.setPlatform(SHARE_MEDIA.WEIXIN)
								.setCallback(umShareListener).withText(content)
								.withTitle(title).withTargetUrl(targeUrl)
								.withMedia(mUmImage).share();

						break;
					case 2:

						new ShareAction(mContext).setPlatform(SHARE_MEDIA.SINA)
								.setCallback(umShareListener).withText(content)
								.withTitle(title).withTargetUrl(targeUrl)
								.withMedia(mUmImage).share();
						break;
					}

				}
			});
			view.findViewById(R.id.tv_cancel).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							shareDialog.dismiss();
						}
					});
			shareDialog = DialogUtil.getMenuDialog(mContext, view);
			shareDialog.show();
		} else {
			shareDialog.show();
		}
	}

}
