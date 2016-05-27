package com.core.framework.image.universalimageloader.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.core.framework.develop.LogUtil;
import com.core.framework.app.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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
        } catch (Exception e) {
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

    public static void saveBitmapToFile(Bitmap bitmap, File file) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(bos.toByteArray());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(null, fos);
        }
    }

    /**
     * 压缩图片并返回图片字节数据
     *
     * @param b       ：被压缩的图片
     * @param len     ：指定被压缩后的最大宽度或高度
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
     * @param file  ：图片文件
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

    /**
     * 缓存图片到本地存储卡
     *
     * @param photo ：输入图片
     * @param file  ：图片文件
     */
    public static byte[] makeTempFileNew(Bitmap photo, File file, int defaultWidth) {
        // 等比例压缩图片，将较长的一边压缩到defaultWidth，最大容量不超过200K
        byte[] tempData = compressPhotoByte(photo, defaultWidth, 200 * 1024);
        return tempData;
    }

    public static Bitmap getSourceBitmap(ContentResolver resolver, Uri uri,
                                         int requiredSize) throws FileNotFoundException {
        Bitmap result = null;
        if (uri != null && uri.getPath().length() != 0) {
            //获取图片的旋转角度
            int degree = readPictureDegreeByUri(uri);

            InputStream is = resolver.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            closeIO(is, null);

            /**
             * 对于待上传的图片： 默认使用530*350；保持原图的宽高比
             */

            if (options.outWidth < requiredSize && options.outHeight < requiredSize) {
                try {
                    is = resolver.openInputStream(uri);
                    result = BitmapFactory.decodeStream(is);
                    closeIO(is, null);
                    return result;
                } catch (Exception e) {
                    return result;
                }
            }

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
                result = rotaingImageView(degree, BitmapFactory.decodeStream(is, null, options));
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

        //获取图片的旋转角度
        int degree = readPictureDegree(file.getAbsolutePath());

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
            result = rotaingImageView(degree, BitmapFactory.decodeFile(file.getAbsolutePath(), options));
        } catch (Exception e) {
            result = null;
            LogUtil.w(e);
        }

        return result;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param mImageCaptureUri 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegreeByUri(Uri mImageCaptureUri) {
        int degree = 0;
        try{
            ContentResolver cr = MyApplication.getInstance().getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// 根据Uri从数据库中找
            if (cursor != null) {
                cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
                String orientation = cursor.getString(cursor.getColumnIndex("orientation"));// 获取旋转的角度
                cursor.close();
                if (orientation != null && !"".equals(orientation)) {
                    degree = Integer.parseInt(orientation);
                }
            }
        }catch (Exception e){
            return degree;
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
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
        } catch (Exception e) {
            LogUtil.w(e);
            return null;
        } finally {
            closeIO(in, fos);
        }
    }

    /*
    从byte[] 生成 webp bitmap
     */
//    public static Bitmap getWebpBitmap(byte[] data) {
//        Bitmap bitmap = null;
//        try {
//            int[] width = new int[]{0};
//            int[] height = new int[]{0};
//            byte[] decoded = WebPFactory.webPDecodeARGB(data, data.length, width, height);
//            if (decoded == null || decoded.length == 0) return null;
//            int[] pixels = new int[decoded.length / 4];
//            ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);
//            bitmap = Bitmap.createBitmap(pixels, width[0], height[0], Bitmap.Config.ARGB_8888);
//        } catch (OutOfMemoryError oom) {
//            bitmap = null;
//            LogUtil.w(oom);
//        }
//        return bitmap;
//    }

    //获得圆角的Top搜索框
    public static GradientDrawable generateShape(String colorStr,int radius) {
        GradientDrawable gd;
        try {
            int roundRadius = radius; // 8dp 圆角半径
            int fillColor = Color.parseColor("#" + colorStr);//内部填充颜色
            gd = new GradientDrawable();//创建drawable
            gd.setColor(fillColor);
            gd.setCornerRadius(roundRadius);
        }catch (NumberFormatException e){
            return null;
        }
        return gd;
    }

    /**
     */
    public static Bitmap cropImg(Bitmap bitmap,boolean isLeft) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX;
        int retY=0;
        if (isLeft){
            retX = 0;
        }else {
            retX = w/2;

        }
        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, w/2, h, null, false);
    }
}
