package com.bolaa.medical.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.BaseObj;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.FileUtils;
import com.bolaa.medical.utils.HttpDownloader;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import java.io.File;

/**
 * Created by paulz on 2016/5/17.
 */
public class HospitalEvaluateActivity extends BaseActivity {

    private TextView tvStar;
    private RatingBar ratingBar;
    private EditText etContent;
    private String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
        setListener();
    }

    private void setListener() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    tvStar.setText((int)rating+"颗星星");
                }
            }
        });
    }

    private void setExtra(){
        Intent intent=getIntent();
        order_id=intent.getStringExtra("order_id");
    }

    private void initView(){
        setActiviyContextView(R.layout.activity_hospital_evaluate, true, true);
        setTitleTextRightText("","机构评价","确定",true);
        tvStar=(TextView)findViewById(R.id.tv_star);
        ratingBar=(RatingBar) findViewById(R.id.ratingbar);
        setStar(0);
        etContent=(EditText) findViewById(R.id.et_content);
    }

    private void setStar(int star){
        ratingBar.setRating(star);
        tvStar.setText((int)ratingBar.getRating()+"颗星星");
    }

    @Override
    public void onRightClick() {
        commit();
    }

    public static void invoke(Context context,String order_id){
        Intent intent = new Intent(context, HospitalEvaluateActivity.class);
        intent.putExtra("order_id",order_id);
        context.startActivity(intent);
    }


    private void commit(){
        String content=etContent.getText().toString();
//        if(AppUtil.isNull(content)){
//            return;
//        }
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        params.append("content",content);
        params.append("order_id",order_id);
        params.append("score",ratingBar.getRating());
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_EVALUATE_COMMIT), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){//请求连通性没问题
                    BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj.status==BaseObject.STATUS_OK){
                        AppUtil.showToast(getApplicationContext(),"评论成功");
                        finish();
                    }else {
                        AppUtil.showToast(getApplicationContext(),obj.msg);
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"请检查网络");
                }
            }
        });
    }

}
