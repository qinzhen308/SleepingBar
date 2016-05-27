package com.core.framework.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.core.framework.develop.LogUtil;

/**
 * Created by IntelliJ IDEA.
 * User: qikai
 * Date: 12-9-16
 * Time: 下午11:17
 * To change this template use File | Settings | File Templates.
 */
public class ImgUtil {

    /**
     * 将bitmap转化为byte[]
     */
    public static byte[] generateByteArray(Bitmap bm, int quality) {
        byte[] imgByteArray;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        imgByteArray = bos.toByteArray();
        closeIO(null, bos);

        return imgByteArray;
    }

    /**
     * 将input流转为byte数组，自动关闭
     *
     * @param in
     * @return
     */
    public static byte[] toByteArray(InputStream in) {
        if (in == null) return null;

        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 100];
            int n;
            while (-1 != (n = in.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
        } catch (Exception e){
            LogUtil.w(e);
        } finally {
            closeIO(in, output);
        }

        return result;
    }

    /**
     * 关闭IO流
     *
     * @param in
     * @param out
     */
    public static void closeIO(InputStream in, OutputStream out) {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File saveBitmapToFile(Bitmap bitmap, File file) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(bos.toByteArray());
            fos.flush();
            return file ;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeIO(null, fos);
        }
    }

    public static File saveBitmapInputToFile(InputStream in, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            byte[] buffer = new byte[1024];
            int n;
            while (-1 != (n = in.read(buffer))) {
                fos.write(buffer, 0, n);
                fos.flush();
            }
            return file;
        } catch (Exception e){
                LogUtil.w(e);
            return  null;
        } finally {
            closeIO(in, fos);
        }
    }

    /**
     * 压缩图片并返回图片字节数据
     *
     * @param b ：被压缩的图片
     * @param len ：指定被压缩后的最大宽度或高度
     * @param maxSize :指定被压缩后的最大容量
     * @return
     */
    public static byte[] compressPhotoByte(Bitmap b, int len, int maxSize) {
        int w = b.getWidth();
        int h = b.getHeight();
        float s;
        if (w < len && h < len) {
            s = 1;
        }
        if (w > h) {
            s = (float) len / w;
        } else {
            s = (float) len / h;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(s, s);
        // 压缩图片
        Bitmap newB = Bitmap.createBitmap(b, 0, 0, w, h, matrix, false);
        // 将压缩后的图片转换为字节数组，如果字节数组大小超过200K，继续压缩
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int qt = 75;
        newB.compress(Bitmap.CompressFormat.JPEG, qt, bos);

        int size = bos.size();
        while (qt != 0 && size > maxSize) {
            if (qt < 0) qt = 0;
            bos.reset();
            newB.compress(Bitmap.CompressFormat.JPEG, qt, bos);
            size = bos.size();
            qt -= 5;
        }
        newB.recycle();
        b.recycle();
        return bos.toByteArray();
    }

    /**
     * 缓存图片到本地存储卡
     *
     * @param photo ：输入图片
     * @param file ：图片文件
     */
    public static File makeTempFile(Bitmap photo, File file, int defaultWidth) {
        // 等比例压缩图片，将较长的一边压缩到defaultWidth，最大容量不超过200K
        byte[] tempData = compressPhotoByte(photo, defaultWidth, 200 * 1024);
        // 将压缩后的图片缓存到存储卡根目录下
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(tempData);
            fos.flush();
            if (file.exists() && file.length() > 0)
                return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(null, fos);
        }
        return null;
    }

    public static Bitmap getSourceBitmap(ContentResolver resolver, Uri uri,
                                         int requiredSize) throws FileNotFoundException {
        Bitmap result = null;
        if (uri != null && uri.getPath().length() != 0) {
            InputStream is = resolver.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            closeIO(is, null);

            /**
             * 对于待上传的图片： 默认使用530*350；保持原图的宽高比
             */

            if (options.outWidth < requiredSize)
                return result;

            int widthRatio = (int) Math.ceil(options.outWidth / requiredSize);
            int heightRatio = (int) Math.ceil(options.outHeight / requiredSize);
            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio > heightRatio) {
                    options.inSampleSize = widthRatio;
                } else {
                    options.inSampleSize = heightRatio;
                }
            }
            options.inJustDecodeBounds = false;

            try {
                is = resolver.openInputStream(uri);
                result = BitmapFactory.decodeStream(is, null, options);
                closeIO(is, null);
            } catch (Exception e) {
                LogUtil.w(e);
            }
        }

        return result;
    }

    public static Bitmap getSourceBitmap(File file, int requiredSize) {
        Bitmap result = null;
        if (null == file) return result;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            result = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            int heightRatio = (int) Math.ceil(options.outHeight / requiredSize);
            int widthRatio = (int) Math.ceil(options.outWidth / requiredSize);

            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    options.inSampleSize = heightRatio;
                } else {
                    options.inSampleSize = widthRatio;
                }
            }

            options.inJustDecodeBounds = false;
            result = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        } catch (Exception e) {
            result = null;
            LogUtil.w(e);
        }

        return result;
    }

    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isSDCardFull() {
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        if (availableBlocks * blockSize == 0) {
            return true;
        }
        return false;
    }

    public static Bitmap getImageCompress_ByWH_BySize(String srcPath,int needW,int needH,int maxByte) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为

        float hh = needW;//这里设置高度为800f
        float ww = needH;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap,maxByte);//压缩好比例大小后再进行质量压缩
    }

    private static Bitmap compressImage(Bitmap image,int maxByte) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxByte) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    public static File copyFile(File midFile, File file) {
        midFile.renameTo(file);
        return file;
    }
}
