package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.Topic;
import com.bolaa.sleepingbar.model.TopicComments;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.BBSPostsDetailActivity;
import com.bolaa.sleepingbar.ui.OtherUserHomeActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.IOSDialogUtil;

/**
 * 社区首页---专题列表 适配器
 * auther paulz
 */
public class TopicListAdapter extends AbstractListAdapter<Topic> {

	private OnShowMenuListener mOnShowMenuListener;

	public TopicListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_topic, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final Topic item=mList.get(i);
		holder.tvName.setText(item.nick_name);
		String content=item.content+"  全文";
		SpannableString spannableString1 = new SpannableString(content);
		spannableString1.setSpan(new ClickableSpan(){

			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setColor(ds.linkColor);
				ds.setUnderlineText(false);
			}

			@Override
			public void onClick(View widget) {
				AppUtil.showToast(mContext,"点了个几");
			}
		}, content.length()-2, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.main)),content.length()-2, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.tvContent.setText(spannableString1);
        holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvDate.setText(item.c_time);
		holder.ivPraise.setImageResource(item.is_praise==1?R.drawable.ic_heart_purple:R.drawable.ic_heart_purple2);
		holder.tvCommtenCount.setText("留言："+item.comment_num);
		holder.tvPraiseCount.setText("赞："+item.praise_num);
		Image13Loader.getInstance().loadImage(item.avatar,holder.ivAvatar,R.drawable.user2);
		holder.pictureAdapter.setList(item.topic_imgs);
        holder.pictureAdapter.setImagePath(item.img_path);
        holder.pictureAdapter.notifyDataSetChanged();
		holder.ivMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if(mOnShowMenuListener!=null){
                    mOnShowMenuListener.onShow(item);
                }
			}
		});

        holder.ivPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGood(item);
            }
        });

		holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OtherUserHomeActivity.invoke(mContext,item.user_id,item.nick_name);
			}
		});

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BBSPostsDetailActivity.invoke(mContext,item.id);
            }
        });
		return view;
	}




    private void clickGood(final Topic posts) {
        if (posts.is_praise == 1) {
            AppUtil.showToast(mContext, "您已点过赞");
            return;
        }
        ParamBuilder params = new ParamBuilder();
        params.append("id", "" + posts.id);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_BBS_POSTS_GOOD), new NetworkWorker.ICallback() {

                    @Override
                    public void onResponse(int status, String result) {
                        // TODO Auto-generated method stub
                        if (status == 200) {
                            BaseObject<String> object = GsonParser
                                    .getInstance().parseToObj(result,
                                            Object.class);
                            if (object != null && object.status == BaseObject.STATUS_OK) {
                                posts.praise_num = posts.praise_num + 1;
                                posts.is_praise=1;
                                notifyDataSetChanged();
                            } else {
                                AppUtil.showToast(mContext, object != null ? object.info : "操作失败");
                            }
                        } else {
                            AppUtil.showToast(mContext, "操作失败");
                        }
                    }
                });
    }

    public void setCaredStatusByUid(String user_id,int has_been_cared){
        int size=getCount();
        for(int i=0;i<size;i++){
            Topic topic =mList.get(i);
            if(user_id.equals(topic.user_id)){
                topic.has_been_cared=has_been_cared;
            }
        }
    }
	
	class ViewHolder{
		public TextView tvName;
		public TextView tvContent;
		public TextView tvDate;
		public TextView tvPraiseCount;
		public TextView tvCommtenCount;
		public ImageView ivAvatar;
		public ImageView ivMenu;
		public ImageView ivPraise;
		public GridView gvPics;
		public PictureAdapter pictureAdapter;

		public ViewHolder(View view){
			tvName=(TextView)view.findViewById(R.id.tv_name);
			tvPraiseCount=(TextView)view.findViewById(R.id.tv_praise_count);
			tvCommtenCount=(TextView)view.findViewById(R.id.tv_comments_count);
			tvContent =(TextView)view.findViewById(R.id.tv_content);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			ivAvatar=(ImageView) view.findViewById(R.id.iv_avatar);
			ivPraise=(ImageView) view.findViewById(R.id.iv_praise);
			ivMenu=(ImageView) view.findViewById(R.id.iv_menu);
			gvPics=(GridView) view.findViewById(R.id.gv_pics);
			pictureAdapter=new PictureAdapter(mContext);
			pictureAdapter.setWidth(ScreenUtil.WIDTH-ScreenUtil.dip2px(mContext,70),3);
			gvPics.setAdapter(pictureAdapter);
		}
	}

	public void setOnShowMenuListener(OnShowMenuListener onShowMenuListener){
        mOnShowMenuListener=onShowMenuListener;
	}

	public interface OnShowMenuListener{
		public void onShow(Topic topic);
	}

}
