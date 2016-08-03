package com.bolaa.sleepingbar.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.PraiseResult;
import com.bolaa.sleepingbar.model.Topic;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.BBSPostsDetailActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;

/**
 * 别人的话题列表 适配器
 * auther paulz
 */
public class TopicListStyle2Adapter extends AbstractListAdapter<Topic> {

	private OnCancelEventListener mOnCancelEventListener;

	public TopicListStyle2Adapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(view==null){
			view=View.inflate(mContext, R.layout.item_topic_style2, null);
			holder=new ViewHolder(view);
			view.setTag(holder);
		}else {
			holder=(ViewHolder)view.getTag();
		}

		final Topic item=mList.get(i);
		if(item.content!=null&&item.content.length()>40){
			holder.tvContent.setText(item.content.substring(0,37)+"...");
		}else {
			holder.tvContent.setText(item.content);
		}
        holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvDate.setText(item.c_time);
		holder.tvCommtenCount.setText("留言："+item.comment_num);
		holder.tvPraiseCount.setText("赞："+item.praise_num);
		holder.pictureAdapter.setList(item.topic_imgs);
        holder.pictureAdapter.setImagePath(item.img_path);
        holder.pictureAdapter.notifyDataSetChanged();

//        holder.tvPraiseCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickGood(item);
//            }
//        });

        holder.tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BBSPostsDetailActivity.invoke(mContext,item.id);
            }
        });
		return view;
	}

    private void clickGood(final Topic posts) {
        ParamBuilder params = new ParamBuilder();
        params.append("id", "" + posts.id);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_BBS_POSTS_GOOD), new NetworkWorker.ICallback() {

                    @Override
                    public void onResponse(int status, String result) {
                        // TODO Auto-generated method stub
                        if (status == 200) {
							BaseObject<PraiseResult> object = GsonParser.getInstance().parseToObj(result, PraiseResult.class);
							if (object != null && object.status == BaseObject.STATUS_OK&&object.data!=null) {
								posts.praise_num = posts.praise_num + (object.data.op_status==1?1:-1);
								posts.is_praise=object.data.op_status==1?1:0;
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
	
	class ViewHolder{
		public TextView tvContent;
		public TextView tvDate;
		public TextView tvPraiseCount;
		public TextView tvCommtenCount;
		public TextView tvDetail;
		public GridView gvPics;
		public PictureAdapter pictureAdapter;

		public ViewHolder(View view){
			tvDetail=(TextView)view.findViewById(R.id.tv_all);
			tvPraiseCount=(TextView)view.findViewById(R.id.tv_praise_count);
			tvCommtenCount=(TextView)view.findViewById(R.id.tv_comments_count);
			tvContent =(TextView)view.findViewById(R.id.tv_content);
			tvDate =(TextView)view.findViewById(R.id.tv_date);
			gvPics=(GridView) view.findViewById(R.id.gv_pics);
			pictureAdapter=new PictureAdapter(mContext);
			pictureAdapter.setWidth(ScreenUtil.WIDTH-ScreenUtil.dip2px(mContext,20),3);
			gvPics.setAdapter(pictureAdapter);
		}
	}

	public void setOnCancelEventListener(OnCancelEventListener onCancelEventListener){
		mOnCancelEventListener=onCancelEventListener;
	}

	public interface OnCancelEventListener{
		public void onCancel(Friends friends);
	}

}
