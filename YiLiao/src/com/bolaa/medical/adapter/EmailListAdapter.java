package com.bolaa.medical.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bolaa.medical.controller.AbstractListAdapter;
import com.bolaa.medical.R;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 13-6-28
 * Time: 下午2:36
 * To change this template use File | Settings | File Templates.
 */
public class EmailListAdapter extends AbstractListAdapter<String> {

    private OnEmailListener mEmailClickListener;
    private String mContent;
    public EmailListAdapter(Activity context) {
        super(context);
    }

    public void setContent (String content) {
        mContent = content;
    }

    private View newView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layer_email_item, null);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = newView();
            holder = new ViewHolder();

            holder.mEmailTv = (TextView) view.findViewById(R.id.email_tv);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mEmailTv.setText(mContent + mList.get(i));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailClickListener.showEmailChoose(holder.mEmailTv.getText().toString());
            }
        });

        return view;
    }

    class ViewHolder {
        TextView mEmailTv;
    }

    public void setEmailOnClickListener (OnEmailListener listener) {
        mEmailClickListener = listener;
    }

    public interface OnEmailListener {
        public void showEmailChoose(String content);
    }
}
