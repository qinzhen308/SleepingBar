package com.bolaa.sleepingbar.ui;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.PictureAdapter;
import com.bolaa.sleepingbar.adapter.TopicCommentsAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.PraiseResult;
import com.bolaa.sleepingbar.model.Topic;
import com.bolaa.sleepingbar.model.TopicComments;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.CommentsWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.view.ResizeLinearLayout;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class BBSPostsDetailActivity extends BaseListActivity implements
		PullToRefreshBase.OnRefreshListener, LoadStateController.OnLoadErrorListener {

	private View header;
	private TextView header2;
	private TextView btnPublish;
	private Topic posts;


	EditText etComment;
	ResizeLinearLayout rootLayout;

	private TextView tvName;
	private TextView tvContent;
	private TextView tvDate;
	private TextView tvPraiseCount;
	private TextView tvCommtenCount;
	private ImageView ivAvatar;
	private ImageView ivMenu;
	private ImageView ivPraise;
	private GridView gvPics;
	private PictureAdapter pictureAdapter;

	private String postsId;
	private int curIv = 0;
	private int posts_position = -1;//从帖子列表进来的，再列表中的索引
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		initPosts();
		setListener();
		loadPosts();
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		postsId = intent.getStringExtra("posts_id");
		posts_position=intent.getIntExtra("posts_position", -1);
		Bundle data = intent.getBundleExtra("data");
		if (data != null) {
			posts = (Topic) data.getSerializable("posts");
		}
	}

	private void initView() {
		setActiviyContextView(R.layout.activity_bbs_posts_detail, true, true);
		setTitleText("", "话题内容", 0, true);
		etComment = (EditText) findViewById(R.id.et_bbs_posts_comment);
		rootLayout = (ResizeLinearLayout) findViewById(R.id.layout_root);
		btnPublish = (TextView) findViewById(R.id.btn_publish);

		mPullListView = (PullListView) findViewById(R.id.pull_listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mPullListView.setOnRefreshListener(this);
		mListView = mPullListView.getRefreshableView();
		header = View.inflate(this, R.layout.layout_topic_detail_header, null);
		mListView.addHeaderView(header);
		addTitleHeader();
		mAdapter = new TopicCommentsAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	private void initPosts() {
		// TODO Auto-generated method stub
		tvName=(TextView)header.findViewById(R.id.tv_name);
		tvPraiseCount=(TextView)header.findViewById(R.id.tv_praise_count);
		tvCommtenCount=(TextView)header.findViewById(R.id.tv_comments_count);
		tvContent =(TextView)header.findViewById(R.id.tv_content);
		tvDate =(TextView)header.findViewById(R.id.tv_date);
		ivAvatar=(ImageView) header.findViewById(R.id.iv_avatar);
		ivPraise=(ImageView) header.findViewById(R.id.iv_praise);
		ivMenu=(ImageView) header.findViewById(R.id.iv_menu);
		gvPics=(GridView) header.findViewById(R.id.gv_pics);
		pictureAdapter=new PictureAdapter(this);
		pictureAdapter.setWidth(ScreenUtil.WIDTH-ScreenUtil.dip2px(this,70),3);
		gvPics.setAdapter(pictureAdapter);
	}

	private void setPostsView() {
		tvName.setText(posts.nick_name);
		tvContent.setText(posts.content);
		tvContent.setMovementMethod(LinkMovementMethod.getInstance());
		tvDate.setText(posts.c_time);
		ivPraise.setImageResource(posts.is_praise==1?R.drawable.ic_heart_purple:R.drawable.ic_heart_purple2);
		tvCommtenCount.setText("留言："+posts.comment_num);
		tvPraiseCount.setText(""+posts.praise_num);
		Image13Loader.getInstance().loadImage(posts.avatar,ivAvatar,R.drawable.user2);
		pictureAdapter.setImagePath(posts.img_path);
		pictureAdapter.setList(posts.topic_imgs);
		pictureAdapter.notifyDataSetChanged();
	}

	private void addTitleHeader() {
		header2 = new TextView(this);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.WRAP_CONTENT,
				AbsListView.LayoutParams.WRAP_CONTENT);
		header2.setLayoutParams(lp);
		header2.setPadding(ScreenUtil.dip2px(this, 10),
				ScreenUtil.dip2px(this, 10), ScreenUtil.dip2px(this, 10),
				ScreenUtil.dip2px(this, 10));
		header2.setCompoundDrawablePadding(ScreenUtil.dip2px(this, 5));
//		header2.setCompoundDrawablesWithIntrinsicBounds(
//				R.drawable.shape_rectangle_vertical_yellow, 0, 0, 0);
		header2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimensionPixelSize(R.dimen.text_size));
		header2.setTextColor(getResources().getColor(R.color.text_grey));
		header2.setText("评论");
		mListView.addHeaderView(header2);
	}

	private void setListener() {
        mPullListView.setOnRefreshListener(this);
		mListView.setOnScrollListener(new MyOnScrollListener());
		btnPublish.setOnClickListener(this);
        ivPraise.setOnClickListener(this);
		ivMenu.setOnClickListener(this);
		rootLayout.setOnResizeListener(new ResizeLinearLayout.OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub

			}
		});

		((TopicCommentsAdapter)mAdapter).setOnShowMenuListener(new TopicCommentsAdapter.OnShowMenuListener() {
			@Override
			public void onShow(TopicComments comments) {
				showMenu(comments);
			}
		});
	}

	private void initData(boolean isRefresh) {

		if (!isRefresh) {
			// showLoading();
		}
		ParamBuilder params = new ParamBuilder();
		params.append("id", posts.id + "");
		params.append("type", 2);
		if (isRefresh) {
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_TOPIC_COMMENTS_LIST),
					CommentsWraper.class);
		} else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_TOPIC_COMMENTS_LIST),
					CommentsWraper.class);
		}
	}

	@Override
	protected BeanWraper newBeanWraper() {
		return new CommentsWraper();
	}

	private void loadPosts() {
		showLoading();
		ParamBuilder params=new ParamBuilder();
		params.append("topic_id", postsId);

		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_TOPIC_DETAIL), new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						// TODO Auto-generated method stub
						if (status == 200) {
							BaseObject<Topic> object = GsonParser
									.getInstance().parseToObj(result,
											Topic.class);
							if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
								showSuccess();
								posts = object.data;
								setPostsView();
								initData(false);
							} else {
								showNodata();
							}
						} else {
							showFailture();
						}
					}
				});

	}

	@Override
	protected void handlerData(List allData, List currentData,
			boolean isLastPage) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		if (AppUtil.isEmpty(allData)) {
			// AppUtil.showToast(this, "暂无评论");
			return;
		}
		mAdapter.setList(allData);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void loadError(String message, Throwable throwable, int page) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
	}

	@Override
	protected void loadNoNet() {
		// TODO Auto-generated method stub

		mPullListView.onRefreshComplete();
		AppUtil.showToast(this, "请检查网络");
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		AppUtil.showToast(this, "连接失败");
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!isLoading()) {
			initData(true);
		}
	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			// imm.toggleSoftInput(0,
			// InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
			imm.hideSoftInputFromWindow(etComment.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub'
		if (v == btnPublish) {
			if (AppStatic.getInstance().isLogin) {
				publishComment();
			} else {
				UserLoginActivity.invoke(this);
			}
		} else if (v == ivPraise) {
			if (AppStatic.getInstance().isLogin) {
				clickGood();
			} else {
				UserLoginActivity.invoke(this);
			}
		}else if(v==ivMenu){
			showMenu(posts);
		}
//		else if (v == ivCollection) {
//			if (AppStatic.getInstance().isLogin) {
//				collection();
//			} else {
//				UserLoginActivity.invoke(this);
//			}
//		}else if (v == tvInform) {
//			// 举报
//			if (AppStatic.getInstance().isLogin) {
//				inform();
//			} else {
//				UserLoginActivity.invoke(this);
//			}
//		}
		else {
			super.onClick(v);
		}
	}
	
	
	private void sendStickyUpdateNotify(int type,int count){
		if(posts_position<0||type<=0)return;
		Intent intent =new Intent();
		if(type==1){//点赞
			intent.setAction("com.bolaa.cang.ACTION.POSTS.UPDATE.PRAISE");
		}else if (type==2) {//评论了
			intent.setAction("com.bolaa.cang.ACTION.POSTS.UPDATE.RECOMMENT");
		}
		intent.putExtra("target_count", count);
		intent.putExtra("target_id", posts!=null?posts.id:(postsId+""));
		intent.putExtra("posts_position", posts_position);
		sendStickyBroadcast(intent);
	}

	private void showMenu(final Topic topic){
		if(topic.has_been_cared==1){//已经被关注
			new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
					.addSheetItem("取消关注", IOSDialogUtil.SheetItemColor.Purple, new IOSDialogUtil.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							cancelCare(topic);
						}
					}).addSheetItem("举报", IOSDialogUtil.SheetItemColor.Red, new IOSDialogUtil.OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					inform(topic.id,topic.content,1);
				}
			}).show();
		}else {
			new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
					.addSheetItem("关注Ta", IOSDialogUtil.SheetItemColor.Purple, new IOSDialogUtil.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							doCare(topic,1);
						}
					}).addSheetItem("举报", IOSDialogUtil.SheetItemColor.Red, new IOSDialogUtil.OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					inform(topic.id,topic.content,1);
				}
			}).show();
		}
	}

	private void showMenu(final TopicComments comments){
		if(comments.has_been_cared==1){//已经被关注
			new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
					.addSheetItem("取消关注", IOSDialogUtil.SheetItemColor.Purple, new IOSDialogUtil.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							cancelCare(comments);
						}
					}).addSheetItem("举报", IOSDialogUtil.SheetItemColor.Red, new IOSDialogUtil.OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					inform(comments.id,comments.content,2);
				}
			}).show();
		}else {
			new IOSDialogUtil(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
					.addSheetItem("关注Ta", IOSDialogUtil.SheetItemColor.Purple, new IOSDialogUtil.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							doCare(comments,1);
						}
					}).addSheetItem("举报", IOSDialogUtil.SheetItemColor.Red, new IOSDialogUtil.OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					inform(comments.id,comments.content,2);
				}
			}).show();
		}
	}

	private void doCare(final Object friends , int type){
		String user_id="";
		if(friends instanceof Topic){
			user_id=((Topic)friends).user_id;
		}else if(friends instanceof TopicComments){
			user_id=((TopicComments)friends).user_id;
		}else {
			return;
		}
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("f_type",type);
		params.append("f_user_id",user_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DO_CARE), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null){
						if(obj.status==BaseObject.STATUS_OK){
							if(friends instanceof Topic){
								((Topic)friends).has_been_cared=1;
							}else if(friends instanceof TopicComments){
								((TopicCommentsAdapter)mAdapter).setCaredStatusByUid(((TopicComments) friends).user_id,1);
							}
                            AppUtil.showToast(getApplicationContext(),obj.info);
						}else {
							AppUtil.showToast(getApplicationContext(),obj.info);
						}
					}else {
						AppUtil.showToast(getApplicationContext(),"操作失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(),"请检查网络");
				}
			}
		});
	}

	private void cancelCare(final Object friends){
		String user_id="";
		if(friends instanceof Topic){
			user_id=((Topic)friends).user_id;
		}else if(friends instanceof TopicComments){
			user_id=((TopicComments)friends).user_id;
		}else {
			return;
		}
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("f_user_id",user_id);
		params.append("tab","me_care");
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CANCEL_CARE), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null){
						if(obj.status==BaseObject.STATUS_OK){
							if(friends instanceof Topic){
								((Topic)friends).has_been_cared=0;
							}else if(friends instanceof TopicComments){
                                ((TopicCommentsAdapter)mAdapter).setCaredStatusByUid(((TopicComments) friends).user_id,0);
                            }
                            AppUtil.showToast(getApplicationContext(),obj.info);
                        }else {
							AppUtil.showToast(getApplicationContext(),obj.info);
						}
					}else {
						AppUtil.showToast(getApplicationContext(),"操作失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(),"请检查网络");
				}
			}
		});
	}

	private void inform(String id,String content,int type){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("o_id",id);
		params.append("r_type", type);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_BBS_POSTS_INFORM), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null){
						if(obj.status==BaseObject.STATUS_OK){
							AppUtil.showToast(getApplicationContext(),obj.info);
						}else {
							AppUtil.showToast(getApplicationContext(),obj.info);
						}
					}else {
						AppUtil.showToast(getApplicationContext(),"举报失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(),"请检查网络");
				}
			}
		});
	}

	private void inform() {
		DialogUtil.showDialog(lodDialog);
		HttpRequester httpRequester = new HttpRequester();
		httpRequester.getParams().put(ParamBuilder.ACCESS_TOKEN,
				NetworkWorker.getInstance().ACCESS_TOKEN);
		httpRequester.getParams().put("id", "" + posts.id);
		httpRequester.getParams().put("content", posts.content);

		NetworkWorker.getInstance().post(
				AppUrls.getInstance().URL_BBS_POSTS_INFORM, new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						// TODO Auto-generated method stub
						if (!isFinishing())
							DialogUtil.dismissDialog(lodDialog);
						if (status == 200) {
							BaseObject<String> object = GsonParser
									.getInstance().parseToObj(result,
											Object.class);
							if (object != null
									&& object.status == BaseObject.STATUS_OK) {
								AppUtil.showToast(getApplicationContext(),
										object.info);
							} else {
								AppUtil.showToast(getApplicationContext(),
										object != null ? object.info : "操作失败");
							}
						} else {
							AppUtil.showToast(getApplicationContext(), "操作失败");
						}
					}
				}, httpRequester);
	}

	private void clickGood() {

		ParamBuilder params = new ParamBuilder();
        params.append("id", "" + posts.id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_BBS_POSTS_GOOD), new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						// TODO Auto-generated method stub
						if (status == 200) {
							BaseObject<PraiseResult> object = GsonParser.getInstance().parseToObj(result, PraiseResult.class);
							if (object != null && object.status == BaseObject.STATUS_OK&&object.data!=null) {
								posts.praise_num = posts.praise_num + (object.data.op_status==1?1:-1);
								posts.is_praise=object.data.op_status==1?1:0;
                                ivPraise.setImageResource(posts.is_praise==1?R.drawable.ic_heart_purple:R.drawable.ic_heart_purple2);
								tvPraiseCount.setText("" + posts.praise_num);
//								sendStickyUpdateNotify(1, posts.praise_num);
							} else {
								AppUtil.showToast(getApplicationContext(), object != null ? object.info : "操作失败");
							}
						} else {
							AppUtil.showToast(getApplicationContext(), "操作失败");
						}
					}
				});
	}

	private void publishComment() {
		if (posts == null)
			return;
		if (!AppStatic.getInstance().isLogin) {
			UserLoginActivity.invokeForResult(this, 1);
			return;
		}
		String comment = etComment.getText().toString();
		if (comment == null || comment.trim().length() <= 0) {
			AppUtil.showToast(this, "评论不能为空");
			return;
		}
		HttpRequester requester = new HttpRequester();
		requester.getParams().put("id", "" + posts.id);
		requester.getParams().put("content", comment);
		requester.getParams().put("type", "2");//话题评论

		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_PUBLISH_COMMENTS, new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						// TODO Auto-generated method stub
						if (status == 200) {
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(result);
								int code = jsonObject.optInt("status");
								if (code == 1) {// 成功
									initData(true);
									closeInputMethod();
									etComment.clearFocus();
									etComment.setText("");
                                    posts.comment_num=posts.comment_num+1;
                                    tvCommtenCount.setText("留言："+posts.comment_num);
								}
								AppUtil.showToast(getApplicationContext(),
										jsonObject.optString("info"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								AppUtil.showToast(getApplicationContext(),
										"评论失败");
							}
						} else {
							AppUtil.showToast(getApplicationContext(), "评论失败");
						}
					}
				}, requester);

	}

//	private void collection() {
//		if (posts == null)
//			return;
//		if (!AppStatic.getInstance().isLogin) {
//			UserLoginActivity.invokeForResult(this, 1);
//			return;
//		}
//
//		HttpRequester requester = new HttpRequester();
//		requester.getParams().clear();
//		requester.getParams().put(ParamBuilder.ACCESS_TOKEN, NetworkWorker.getInstance().ACCESS_TOKEN);
//		requester.getParams().put(ParamBuilder.BBS_ID, "" + posts.id);
//		NetworkWorker.getInstance().post(
//				AppUrls.getInstance().URL_BBS_POSTS_COLLECTION,
//				new ICallback() {
//
//					@Override
//					public void onResponse(int status, String result) {
//						// TODO Auto-generated method stub
//						if (status == 200) {
//							JSONObject jsonObject;
//							try {
//								jsonObject = new JSONObject(result);
//								int code = jsonObject.optInt("status");
//								if (code == 1) {// 成功
//									if (posts.is_collection == 0) {
//										posts.is_collection = 1;
//									} else {
//										posts.is_collection = 0;
//									}
//									ivCollection
//											.setImageResource(posts.is_collection == 1 ? R.drawable.ic_level_star2
//													: R.drawable.ic_level_star);
//								}
//								AppUtil.showToast(getApplicationContext(),
//										jsonObject.optString("info"));
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//								AppUtil.showToast(getApplicationContext(),
//										"操作失败");
//							}
//						} else {
//							AppUtil.showToast(getApplicationContext(), "操作失败");
//						}
//					}
//				}, requester);
//
//	}

	// @Deprecated
	// private void showShareWindow() {
	// if (shareWindow == null) {
	// View view = LayoutInflater.from(this).inflate(
	// R.layout.dialog_share_posts, null);
	// view.findViewById(R.id.tv_cancel).setOnClickListener(
	// new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if (!isFinishing()) {
	// shareWindow.dismiss();
	// }
	// }
	// });
	//
	// List<ShareChannel> list = new ArrayList<ShareChannel>();
	// ShareChannel channel = new ShareChannel(0, "qq好友",
	// R.drawable.ic_share_qq);
	// list.add(channel);
	// channel = new ShareChannel(1, "qq空间", R.drawable.ic_share_qq_zore);
	// list.add(channel);
	// channel = new ShareChannel(2, "微信好友", R.drawable.ic_share_wx_friend);
	// list.add(channel);
	// channel = new ShareChannel(3, "新浪微博", R.drawable.ic_share_weibo);
	// list.add(channel);
	// channel = new ShareChannel(4, "朋友圈", R.drawable.ic_share_wx_center);
	// list.add(channel);
	// final ShareGridAdapter shareGridAdapter = new ShareGridAdapter(this);
	// shareGridAdapter.setList(list);
	// ((GridView) view.findViewById(R.id.gridview))
	// .setAdapter(shareGridAdapter);
	// ((GridView) view.findViewById(R.id.gridview))
	// .setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent,
	// View view, int position, long id) {
	// // TODO Auto-generated method stub
	// ShareChannel sChannel = (ShareChannel) shareGridAdapter
	// .getItem(position);
	// sChannel.share(BBSPostsDetailActivity.this,
	// "http://www.baidu.com", null, null, null);
	// shareWindow.dismiss();
	// }
	// });
	//
	// shareWindow = DialogUtil.getMenuDialog(this, view);
	// shareWindow.show();
	// } else {
	// shareWindow.show();
	// }
	// }

	/**
	 * 掉这个方法启动页面，就重新加载帖子内容
	 * 
	 * @param context
	 * @param postsId
	 */
	public static void invoke(Context context, String postsId) {
		Intent intent = new Intent(context, BBSPostsDetailActivity.class);
		intent.putExtra("posts_id", postsId);
		context.startActivity(intent);
	}
	
	public static void invoke(Context context, String postsId,int position) {
		Intent intent = new Intent(context, BBSPostsDetailActivity.class);
		intent.putExtra("posts_id", postsId);
		intent.putExtra("posts_position", position);
		context.startActivity(intent);
	}

	@Override
	public void onAgainRefresh() {
		// TODO Auto-generated method stub
		initData(false);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}
}
