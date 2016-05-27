package com.core.framework.image.universalimageloader.core;


/**
 * Created by suwg on 2014/11/25.
 */
public class DealUrl<Parm> {

//   public ItemAnalysisable deal;
   public  String url;
   public  int position;

    public DealUrl(String url) {
        this.url = url;
        this.position=-1;
    }

    public DealUrl( String url,int position) {
//        this.deal = deal;
        this.url = url;
        this.position=position;
    }

    public String getHeadInfo(Parm... p) {
//        String info= deal.getHeadInfo_B(position,p);
//        return info;

        return null;
    }

    @Override
    public String toString() {
        return url;
    }
}
