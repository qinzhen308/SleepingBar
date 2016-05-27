package com.core.framework.image.image13;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.core.framework.R;
import com.core.framework.app.MyApplication;
import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.dataLoadView.ThreadManager;
import com.core.framework.develop.DevRunningTime;
import com.core.framework.develop.FaceTestforDlp;
import com.core.framework.image.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.core.framework.image.universalimageloader.core.DealUrl;
import com.core.framework.image.universalimageloader.core.DisplayImageOptions;
import com.core.framework.image.universalimageloader.core.ImageLoader;
import com.core.framework.image.universalimageloader.core.ImageLoaderConfiguration;
import com.core.framework.image.universalimageloader.core.assist.ImageLoadingListener;
import com.core.framework.image.universalimageloader.core.assist.QueueProcessingType;
import com.core.framework.image.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.core.framework.image.universalimageloader.core.display.FadeInBitmapDisplayer;


public class Image13lLoader {

    private static com.core.framework.image.image13.Image13lLoader inst ;
    public static com.core.framework.image.image13.Image13lLoader getInstance() {
        if(inst==null) {
            inst = new com.core.framework.image.image13.Image13lLoader();
            inst.initImageLoader(MyApplication.getInstance(), R.drawable.img_list_default, R.drawable.img_list_default,
                    R.drawable.img_list_default, R.drawable.img_list_default);
        }
        return inst;
    }
    ImageLoader imageLoader;
    DisplayImageOptions options;

    private static int FADE_IN_DURATION = 50;// 淡入效果的持续时间
    /**
     * 初始化图片加载类配置信息*
     */
    public void initImageLoader(Context context,int mg_default_list,int img_default_listUri,
                                int testimg_default_list,int nomal_mg_default_list) {
        ImageLoaderConfiguration.Builder bin= new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.MIN_PRIORITY)//加载图片的线程数
                .denyCacheImageMultipleSizesInMemory() //解码图像的大尺寸将在内存中缓存先前解码图像的小尺寸。
                //.discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//设置磁盘缓存文件名称
                .tasksProcessingOrder(QueueProcessingType.LIFO)//设置加载显示图片队列进程  后进先出
                .threadPoolSize(ThreadManager.getImageThread())
                //.memoryCache(new LruMemoryCache(10))
                .discCacheFileCount(1000);
        if (!AppConfig.LOG_CLOSED&& DevRunningTime.isShowImageErr) {
            bin.enableLogging();
        }else{
        }
        ImageLoaderConfiguration config =bin.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        DisplayImageOptions.Builder disBin=new DisplayImageOptions.Builder()
                .showStubImage(mg_default_list)
                .showImageForEmptyUri(img_default_listUri)
                //.memoryCache(new UsingFreqLimitedCache(2000000))
                .cacheInMemory(DevRunningTime.isCacheFull)// .cacheInMemory()
                .cacheOnDisc(true)
//                 .displayer(new RoundedBitmapDisplayer(20))0
//                .displayer(new FadeInBitmapDisplayer(FADE_IN_DURATION))// 添加淡入效果
                .bitmapConfig(Bitmap.Config.ARGB_8888);

        if(!AppConfig.LOG_CLOSED) {
            disBin.showImageOnFail(testimg_default_list);
        }else{
            disBin.showImageOnFail(nomal_mg_default_list);
        }
        options =disBin .build();
    }

    public void flushCache() {
        imageLoader.clearMemoryCache();
    }

    public void clearCache() {
    }


    @Deprecated
    public boolean hasImageCache(String msgImgUrl) {//这个没有实现
        return false;
    }


    @FaceTestforDlp
    public void loadImageForTest(String imageUrl) {
        ImageView  mImageView= new ImageView(MyApplication.getInstance());
        loadImage(imageUrl,mImageView);
    }

    public void loadImage(String msgImgUrl, ImageLoadingListener mImageLoadingListener) {
        if(msgImgUrl==null)return;
       imageLoader.loadImage(new DealUrl(msgImgUrl),options,mImageLoadingListener);
    }

    public void loadImage(String msgImgUrl, ImageView image) {
        if(msgImgUrl==null)return;
       if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image,options,mAnimateFirstDisplayListener);
    }

    /**
     * 4.0.0修改：之前加载图片有错乱，因为加载完之后才给imageview设置tag。
     * --qz
     * @param msgImgUrl
     * @param image
     */
    public void loadImageFade(String msgImgUrl, ImageView image) {
        image.setTag(msgImgUrl);
        if(msgImgUrl==null)return;
//        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image, getFadeImageOptions(),mAnimateFirstDisplayListener);
    }

    public void loadImage(String msgImgUrl, ImageView image, int imgDefaultId ) {
        if(msgImgUrl==null)return;
        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image, getImageOptions(imgDefaultId),mAnimateFirstDisplayListener);
    }
    public void loadImagePromote(String msgImgUrl, ImageView image, int imgDefaultId ) {
        if(msgImgUrl==null)return;
        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image, getImageOptions(imgDefaultId),mAnimateFirstDisplayListenerPromote ,0);
    }

    public void  loadImagePromoteGrid(String msgImgUrl, ImageView image, ImageLoadingListener listener) {
        if(msgImgUrl==null)return;
        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image,options, listener,0);

    }

    public void loadImage(String msgImgUrl, ImageView image, ImageLoadingListener listener,int imgDefaultId ) {
        if(msgImgUrl==null)return;
    /*    if(msgImgUrl==image.getTag())  return;*/
        imageLoader.displayImage(new DealUrl(msgImgUrl), image, getImageOptions(imgDefaultId),listener);
    }

    public void loadImage(String msgImgUrl, ImageView image, int imgDefaultId ,int from) {
        if(msgImgUrl==null)return;
        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image, getImageOptions(imgDefaultId),mAnimateFirstDisplayListener,from);
    }


    public void  loadImage(String msgImgUrl, ImageView image, ImageLoadingListener listener) {
        if(msgImgUrl==null)return;
        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image,options, listener);

    }

    public void loadImage(String imageUrl, ImageView view, DisplayImageOptions options) {
        if(imageUrl==view.getTag())  return;
        imageLoader.displayImage(new DealUrl(imageUrl), view, options,mAnimateFirstDisplayListener);
    }

    public void loadImageDisplayFadeInAndOut(String imageUrl, ImageView view) {
        if(imageUrl==view.getTag())  return;
        imageLoader.displayImage(new DealUrl(imageUrl), view, getFadeImageOptions(),mAnimateFirstDisplayListener);
    }
    
    public void loadNoStubImageFade(String msgImgUrl, ImageView image) {
        image.setTag(msgImgUrl);
        if(msgImgUrl==null)return;
//        if(msgImgUrl==image.getTag())  return;
        imageLoader.displayImage(new DealUrl(msgImgUrl), image, getNoStubImageOptions(),mAnimateFirstDisplayListener);
    }

    private DisplayImageOptions getImageOptions (int ImgDefaultId) {
        DisplayImageOptions.Builder disBin = new DisplayImageOptions.Builder()
                .showStubImage(ImgDefaultId)
                .showImageForEmptyUri(ImgDefaultId)
                .cacheInMemory(DevRunningTime.isCacheFull)// .cacheInMemory()
                .cacheOnDisc(true)// .cacheOnDisc()
//                 .displayer(new RoundedBitmapDisplayer(20))
//                .displayer(new FadeInBitmapDisplayer(FADE_IN_DURATION))
                .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnFail(ImgDefaultId);

        return disBin.build();
    }


    private DisplayImageOptions getNoStubImageOptions() {
        DisplayImageOptions.Builder builder =new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.img_list_default)
                .showImageOnFail(R.drawable.img_list_default)
                .showImageForEmptyUri(R.drawable.img_list_default)
                .cacheInMemory(DevRunningTime.isCacheFull)
                .cacheOnDisc(true)
               // .displayer(new FadeInBitmapDisplayer(FADE_IN_DURATION))
                .bitmapConfig(Bitmap.Config.ARGB_8888);

        return builder.build();
    }
    
    private DisplayImageOptions getFadeImageOptions() {
        DisplayImageOptions.Builder builder =new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.img_list_default)
                .showImageOnFail(R.drawable.img_list_default)
                .showImageForEmptyUri(R.drawable.img_list_default)
                .cacheInMemory(DevRunningTime.isCacheFull)
                .cacheOnDisc(true)
               // .displayer(new FadeInBitmapDisplayer(FADE_IN_DURATION))
                .bitmapConfig(Bitmap.Config.ARGB_8888);

        return builder.build();
    }

    AnimateFirstDisplayListener mAnimateFirstDisplayListener=new AnimateFirstDisplayListener();
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                if(imageUri!=imageView.getTag()){
                    return;
                }
                FadeInBitmapDisplayer.animate(imageView, 0);
            }
        }
    }

    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage,int from) {
        if (loadedImage != null) {
            ImageView imageView = (ImageView) view;
            if(imageUri==imageView.getTag()){
                return;
            }
            imageView.setTag(imageUri);
            if (from == 0){//直接截断，不再走动画效果
                return;
            }
            FadeInBitmapDisplayer.animate(imageView, 500);
        }
    }

    AnimateFirstDisplayListenerPromote mAnimateFirstDisplayListenerPromote=new AnimateFirstDisplayListenerPromote();

    private static class AnimateFirstDisplayListenerPromote extends SimpleImageLoadingListener {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                FadeInBitmapDisplayer.animate(imageView, 500);
           }
        }
    }
    public void cancel(ImageView imageView) {
        imageLoader.cancelDisplayTask(imageView);
    }
    @Deprecated
    public void setExitTasksEarly(boolean b) {
        if (b) {
            imageLoader.stop();//清理所有
        } else {
            imageLoader.resume();//开始
        }
    }

    @Deprecated
    public void setPauseWork(boolean b) {
        if (b) {
            imageLoader.pause();//暂停
        } else {
            imageLoader.resume();//开始
        }
    }


    public void  stop(){
//        Su.log("image 13 stop");
        imageLoader.stop();//清理所有
    }

    public void   pause(){
//        Su.log("image 13 pause");
        imageLoader.pause();//暂停
    }

    public void  resume(){
//        Su.log("image 13 resume");
        imageLoader.resume();//开始
    }

    public void clearMemoryCache(){
        imageLoader.clearMemoryCache();
    }

    public void handleSlowNetwork(boolean handleSlowNetwork) {
        imageLoader.handleSlowNetwork(handleSlowNetwork);
    }
}
