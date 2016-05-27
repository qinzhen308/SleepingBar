package com.bolaa.medical.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.model.Order;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.FileUtils;
import com.bolaa.medical.utils.HttpDownloader;
import com.core.framework.util.DialogUtil;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import java.io.File;

/**
 * Created by paulz on 2016/4/1.
 */
public class ReportDetailActivity extends BaseActivity implements OnPageChangeListener{

    PDFView mPdfView;
    String reportUrl;
    String order_id;
    private TextView tvPage;

    private String pdfPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
        downLoadAndEnterPDF(reportUrl);
    }

    private void setExtra(){
        Intent intent=getIntent();
        reportUrl=intent.getStringExtra("pdf_url");
        order_id=intent.getStringExtra("order_id");
    }

    private void initView(){
        setActiviyContextView(R.layout.activity_report_detail, true, true);
//        setTitleText("", "体检报告", 0, true);
        setTitleTextRightText("","体检报告","评价",true);
        mPdfView=(PDFView)findViewById(R.id.pdfview);
        tvPage=(TextView)findViewById(R.id.tv_page);
    }

    @Override
    public void onRightClick() {
        HospitalEvaluateActivity.invoke(this,order_id);
    }

    private void display(String fileName, boolean jumpToFirstPage) {
        File file=new File(fileName);
        mPdfView.fromFile(file)
                .defaultPage(1)
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        tvPage.setText(page+"/"+pageCount);
    }

    Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what==100){//成功
                showSuccess();
                display(pdfPath, true);
            }else if (msg.what==11){//失败
                showFailture();
            }else {//其他
                showFailture();
            }
        };
    };

    FileUtils futils;
    public void downLoadAndEnterPDF(final String fileUrl){
        if(AppUtil.isNull(fileUrl)){
            showNodata();
            return;
        }
        showLoading();
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(futils==null){
                    futils = new FileUtils();
                }
                String fileName = getFileNameFromUrl(fileUrl);
                String sdPath = futils.getSDPATH() + "/medical/" + fileName;
                HttpDownloader httpDownLoader = new HttpDownloader();
                int result = httpDownLoader.downfile(fileUrl, "/medical/", fileName);
                if(!AppUtil.isNull(sdPath)&&result==0){//下载成功
                    pdfPath=sdPath;
                }else if(result==1){//已存在
                    pdfPath=sdPath;
                }else {//失败
                    handler.sendEmptyMessage(11);
                    return;
                }
                handler.sendEmptyMessage(100);
            }

        });
        t.start();
    }

    public String getFileNameFromUrl(String fileUrl) {
        String fileName = "";
        int index;
        if (fileUrl != null || fileUrl.trim() != "") {
            index = fileUrl.lastIndexOf("/");
            fileName = fileUrl.substring(index + 1, fileUrl.length());
        }
        return fileName;
    }

    public static void invoke(Context context,String path){
        Intent intent = new Intent(context, ReportDetailActivity.class);
        intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, path);
        context.startActivity(intent);
    }
    public static void invoke(Context context,Order order){
        Intent intent = new Intent(context, ReportDetailActivity.class);
        if(order!=null){
            if(order.report_url.contains("?")){
                intent.putExtra("pdf_url", order.report_url+"&is_app=1");
            }else {
                intent.putExtra("pdf_url", order.report_url+"?is_app=1");
            }
            intent.putExtra("order_id", order.order_id);
        }
        context.startActivity(intent);
    }

}
