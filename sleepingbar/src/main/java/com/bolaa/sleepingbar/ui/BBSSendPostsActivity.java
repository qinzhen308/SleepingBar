package com.bolaa.sleepingbar.ui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.ImageUtil;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.callbacks.CommCallBack;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class BBSSendPostsActivity extends BaseActivity {
	// private String hintTexts[] = { "分享身边新鲜事...", "请详细描述您想出售或购买的二手物品" };
	PicItem addItem;
	PicAdapter mAdapter;
	GridView mGridView;
	EditText etContent;
	private Dialog mIconDailog;
	private String mFilePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData(false);

	}

	private void setExtra() {
		Intent intent = getIntent();

	}

	private void initView() {
		setActiviyContextView(R.layout.activity_bbs_send_posts, false, true);
		setTitleTextRightText("", "发布话题", "发布", true);
		mGridView = (GridView) findViewById(R.id.gv_posts);
		etContent = (EditText) findViewById(R.id.et_bbs_posts_content);

	}

	private void setListener() {

	}

	private void initData(boolean isRefresh) {
		addItem = new PicItem();
		addItem.image = getResources().getDrawable(R.drawable.img_add);
		addItem.type = 0;
		mAdapter = new PicAdapter(this);
		List<PicItem> list = new ArrayList<PicItem>();
		list.add(addItem);
		mAdapter.setList(list);
		mGridView.setAdapter(mAdapter);

	}

	// 发帖
	private void sendPosts() {
		final HttpRequester requester = new HttpRequester();

		if (etContent.getText() != null) {
			String content = etContent.getText().toString().trim();
			if (!AppUtil.isNull(content)) {
				requester.getParams().put("content", content);
			} else {
				AppUtil.showToast(this, "话题内容不能为空");
				return;
			}
		}
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				DialogUtil.showDialog(lodDialog);
			};

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub

				for (int i = 0; i < mAdapter.getCount(); i++) {
					PicItem item = mAdapter.getList().get(i);
					if (item.type == 1) {
						// String path = saveBitmap(item.bitmap,
						// "send_posts_temp_" + i + ".jpg");
						// LogUtil.d("sendposts----save--" + path);
						// item.path = path;
						File file = new File(item.path);
						if (file != null) {
							requester.getParams().put("topic_img_" + (i + 1), file);
						}
					}
				}
				String result = NetworkWorker.getInstance().postSync(
						AppUrls.getInstance().URL_COMMUNITY_SEND_POSTS, requester);
				mAdapter.clearTempImgFile();
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (!isFinishing())
					DialogUtil.dismissDialog(lodDialog);
				if (!AppUtil.isNull(result)) {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
						int status = object.optInt("status");
						if (status == 1) {// 成功
							AppUtil.showToast(BBSSendPostsActivity.this, "发帖成功");
							setResult(RESULT_OK);
							finish();
						} else {
							AppUtil.showToast(BBSSendPostsActivity.this,
									object.optString("info"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						AppUtil.showToast(BBSSendPostsActivity.this, "发帖失败");
					}
				} else {
					AppUtil.showToast(BBSSendPostsActivity.this, "发帖失败");
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public String saveBitmap(Bitmap bitmap, String name) {
		if (bitmap == null)
			return null;
		File file = null;
		try {
			File pathFile = new File(ImageUtil.filePath);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			file = new File(ImageUtil.filePath, name);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileOutputStream fileOut = new FileOutputStream(file);
			// int size = 100;
			// if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
			// size = 80;
			// }
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);

			fileOut.flush();
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}

	@Override
	public void onRightClick() {
		sendPosts();
	}

	@Override
	public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_updateIcon_photoTv:// 拍照
				mIconDailog.dismiss();
				File file = new File(ImageUtil.filePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent1.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(ImageUtil.filePath, "123.jpg")));
				startActivityForResult(intent1, 2);

				break;
			case R.id.dialog_updateIcon_pictureTv:// 相册
				mIconDailog.dismiss();
				Intent intent2 = new Intent(
						Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent2.setType("image/*");
				startActivityForResult(intent2, 1);
				break;
			case R.id.dialog_updateIcon_cancelTv:// 相册
				mIconDailog.dismiss();
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	public static void invoke(Activity context) {
		Intent intent = new Intent(context, BBSSendPostsActivity.class);
		context.startActivityForResult(intent,8886);
	}

	class PicAdapter extends AbstractListAdapter<PicItem> {
		private int size;

		public PicAdapter(Activity context) {
			super(context);
			// TODO Auto-generated constructor stub
			size = computeItemSize();
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {
			// TODO Auto-generated method stub
			final int type = getItemViewType(i);
			ViewHolder0 holder0 = null;
			ViewHolder1 holder1 = null;
			if (view == null) {
				if (type == 1) {
					view = View.inflate(mContext,
							R.layout.item_send_posts_grid_pic, null);
					holder1 = new ViewHolder1();
					holder1.ivCancel =  view
							.findViewById(R.id.iv_cancel);
					holder1.ivPic = (ImageView) view
							.findViewById(R.id.iv_thumbnail);
					AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
							size, size);
					view.setLayoutParams(lp);
					view.setTag(holder1);
				} else {
					ImageView iv = new ImageView(mContext);
					iv.setScaleType(ScaleType.FIT_XY);
					view = iv;
					holder0 = new ViewHolder0();
					holder0.ivPic = iv;
					AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
							size, size);
					view.setLayoutParams(lp);
					view.setTag(holder0);
				}

			} else {
				if (type == 1) {
					holder1 = (ViewHolder1) view.getTag();
				} else {
					holder0 = (ViewHolder0) view.getTag();
				}
			}
			if (type == 1) {
				// holder1.ivPic.setImageURI(mList.get(i).uri);
				// holder1.ivPic.setImageBitmap(mList.get(i).bitmap);
				holder1.ivPic.setImageURI(Uri.parse(mList.get(i).path));
				holder1.ivCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!mList.contains(addItem)) {
							mList.add(addItem);
						}
						PicItem removed = mList.remove(i);
						// if (!removed.bitmap.isRecycled()) {
						// removed.bitmap.recycle();
						// }
						notifyDataSetChanged();
					}
				});
			} else {
				holder0.ivPic.setImageDrawable(mList.get(i).image);
			}
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (type == 0) {
						showIconDialog();

						// Intent intent2 = new Intent(
						// Intent.ACTION_PICK,
						// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						// intent2.setType("image/*");
						// startActivityForResult(intent2, 1);
					} else {
						ArrayList<Uri> uris = new ArrayList<Uri>();
						for (int j = 0; j < mList.size(); j++) {
							PicItem item = mList.get(j);
							if (item.type == 1) {
								uris.add(Uri.fromFile(new File(item.path)));
							}
						}
//						PhotoViewerActivity.invoke(BBSSendPostsActivity.this,
//								uris, i);
					}
				}
			});
			return view;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return mList.get(position).type;
		}

		public void clearTempImgFile() {
			for (int i = 0; i < getCount(); i++) {
				PicItem item = mList.get(i);
				if (item.type == 1 && AppUtil.isNull(item.path)) {
					File file = new File(item.path);
					if (file != null) {
						LogUtil.d("sendposts----delete file---" + file.delete());
					}
				}
			}
		}

	}

	class ViewHolder1 {
		public ImageView ivPic;
		public View ivCancel;
	}

	class ViewHolder0 {
		public ImageView ivPic;
	}

	class PicItem {
		public Drawable image;
		// public Uri uri;
		// public Bitmap bitmap;
		public int type;
		public String path;

	}

	public int computeItemSize() {
		return (ScreenUtil.WIDTH - ScreenUtil.dip2px(this, 40 + 15)) / 4;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri imgUri = data.getData();

				List<PicItem> list = mAdapter.getList();

				// Bitmap bitmap = ImageUtil.compressImage(ImageUtil
				// .getImageAbsolutePath(this, imgUri));
				PicItem item = new PicItem();
				String pString = ImageUtil.getImageAbsolutePath(this, imgUri);
				if (!TextUtils.isEmpty(pString)) {
					String path2 = ImageUtil.bitmap2File(pString,
							new Date().getTime() + ".jpg");
					item.type = 1;
					item.path = path2;

					list.add(list.size() - 1, item);
					if (list.size() > 9) {
						list.remove(9);
					}
					mGridView.setAdapter(mAdapter);
				}
				// item.uri = imgUri;
				// item.bitmap = bitmap;
			} else if (requestCode == 2) {

				mFilePath = ImageUtil.filePath + "123.jpg";
				mFilePath = ImageUtil.bitmap2File(mFilePath,
						new Date().getTime() + ".jpg");

				List<PicItem> list = mAdapter.getList();
				// Uri uri=Uri.fromFile(new File(mFilePath));

				// Bitmap bitmap=ImageUtil.compressImage(mFilePath);

				// Bitmap bitmap = ImageUtil.compressImage(ImageUtil
				// .getImageAbsolutePath(this,uri));
				PicItem item = new PicItem();
				// item.uri = uri;
				item.path = mFilePath;
				// item.bitmap = bitmap;
				item.type = 1;
				list.add(list.size() - 1, item);
				if (list.size() > 9) {
					list.remove(9);
				}
				mGridView.setAdapter(mAdapter);
			}
		}
	}

	// -------------------------------test----分割-------------------------------------

	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
																			// 随机生成
	private static final String CHARSET = "utf-8"; // 设置编码

	public static String uploadFile(String RequestURL,
			Map<String, String> param, File file,
			CommCallBack faceCommCallBackPro) throws Exception {

		String result = null;
		URL url = new URL(RequestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(TIME_OUT);
		conn.setConnectTimeout(TIME_OUT);
		conn.setDoInput(true); // 允许输入流
		conn.setDoOutput(true); // 允许输出流
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST"); // 请求方式
		conn.setRequestProperty("Charset", CHARSET); // 设置编码
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
				+ BOUNDARY);
		// conn.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");

		/**
		 * 当文件不为空，把文件包装并且上传
		 */
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		StringBuffer sb = null;
		String params = "";

		/***
		 * 以下是用于上传参数
		 */
		if (param != null && param.size() > 0) {
			Iterator<String> it = param.keySet().iterator();
			while (it.hasNext()) {
				sb = null;
				sb = new StringBuffer();
				String key = it.next();
				String value = param.get(key);
				sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"")
						.append(key).append("\"").append(LINE_END)
						.append(LINE_END);
				sb.append(value).append(LINE_END);
				params = sb.toString();

				dos.write(params.getBytes());
				// dos.flush();
			}
		}

		sb = null;
		params = null;
		sb = new StringBuffer();
		/**
		 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
		 * 比如:abc.png
		 */
		sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
		sb.append("Content-Disposition:form-data; name=\"" + "img_1"
				+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
		sb.append("Content-Type:image/jpeg" + LINE_END); // 这里配置的Content-type很重要的
															// ，用于服务器端辨别文件的类型的
		sb.append(LINE_END);
		params = sb.toString();
		sb = null;
		dos.write(params.getBytes());

		InputStream is = new FileInputStream(file);
		byte[] bytes = new byte[1024];
		int len = 0;
		int allLen = 0;
		long filebytes = file.length();
		while ((len = is.read(bytes)) != -1) {
			dos.write(bytes, 0, len);
			// faceCommCallBackPro.callBack((int) (allLen * 100d / filebytes));
		}
		is.close();
		dos.write(LINE_END.getBytes());
		byte[] end_data = (PREFIX + PREFIX + LINE_END).getBytes();
		dos.write(end_data);
		dos.flush();
		// faceCommCallBackPro.callBack(99);
		int res = conn.getResponseCode();
		if (res == 200) {
			InputStream input = conn.getInputStream();
			StringBuffer sb1 = new StringBuffer();
			int ss;
			while ((ss = input.read()) != -1) {
				sb1.append((char) ss);
			}
			result = sb1.toString();
		} else {
			throw new Exception("!200");
		}

		// faceCommCallBackPro.callBack(100);
		return result;
	}

	private void showIconDialog() {
		if (mIconDailog == null) {
			View iconView = LayoutInflater.from(this).inflate(
					R.layout.dialog_updateicon, null);
			iconView.findViewById(R.id.dialog_updateIcon_photoTv)
					.setOnClickListener(this);
			iconView.findViewById(R.id.dialog_updateIcon_pictureTv)
					.setOnClickListener(this);
			iconView.findViewById(R.id.dialog_updateIcon_cancelTv)
					.setOnClickListener(this);
			mIconDailog = DialogUtil.getCenterDialog(this, iconView);
			mIconDailog.show();
		} else {
			mIconDailog.show();
		}
	}

}
