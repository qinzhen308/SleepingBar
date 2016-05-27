package com.bolaa.medical.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * 图形类
 * 
 * @author jjj
 * 
 * @date 2015-8-5
 */
public class ImageUtil {
	public static String filePath = Environment.getExternalStorageDirectory()
			.getPath() + "/Haiyuehui/images/";// 头像存储路径;

	/**
	 * 获取相对路径
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	public static String bitmap2File(String path, String name) {

		Bitmap bitmap = compressImage(path);
		// Bitmap bitmap = getimage_(path);

		if (null == bitmap) {
			return null;
		}
		File file = null;
		try {
			File pathFile = new File(filePath);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			file = new File(filePath, name);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileOutputStream fileOut = new FileOutputStream(file);
			// int size = 100;
			// if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
			// size = 80;
			// }
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);

			fileOut.flush();
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}

	public static File saveImag(Bitmap bitmap, String name) {

		// Bitmap bitmap = compressImage(path);
		// // Bitmap bitmap = getimage_(path);

		if (null == bitmap) {
			return null;
		}
		File file = null;
		try {
			File pathFile = new File(filePath);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			file = new File(filePath, name);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileOutputStream fileOut = new FileOutputStream(file);
			int size = 100;
			if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
				size = 80;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);

			fileOut.flush();
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	// 按比例压缩
	// private static Bitmap getimage_(String srcPath) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
	//
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 800f;// 这里设置高度为800f
	// float ww = 480f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage_(bitmap);// 压缩好比例大小后再进行质量压缩
	// }

	// 按质量压缩
	// private static Bitmap compressImage_(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
	// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// int options = 100;
	// while (baos.toByteArray().length / 1024 > 100) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// options -= 10;// 每次都减少10
	// }
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
	// 把ByteArrayInputStream数据生成图片
	// return bitmap;
	// }

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
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
	 * 旋转图片为正方向
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 获得压缩过后的图片
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static Bitmap compressImage(String path) {

		if (null == path || path.contains("null")) {
			return null;
		}
		Bitmap bitmap = null;

		BitmapFactory.Options Boptions = new BitmapFactory.Options();
		Boptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, Boptions);
		Boptions.inSampleSize = calculateInSampleSize(Boptions, 480, 800);
		Boptions.inPurgeable = true;
		Boptions.inInputShareable = true;
		Boptions.inJustDecodeBounds = false;

		bitmap = BitmapFactory.decodeFile(path, Boptions);
		int degree = readPictureDegree(path);
		if (degree != 0) {
			bitmap = rotaingImageView(degree, bitmap);// 传入压缩后的图片进行旋转
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

		int size1 = 100;// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩

			baos.reset();// 重置baos即清空baos
			size1 -= 10;// 每次都减少10
			if (size1 == 0) {
				size1 = 1;
			}

			bitmap.compress(Bitmap.CompressFormat.JPEG, size1, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		bitmap = BitmapFactory.decodeStream(isBm);
		return bitmap;
	}

	//
	// /**
	// * 获得压缩过后的图片
	// *
	// * @param path
	// * 文件路径
	// * @return
	// */
	// public static Bitmap compressImage(String path) {
	//
	// if (null == path || path.contains("null")) {
	// return null;
	// }
	// Bitmap bitmap = null;
	//
	// BitmapFactory.Options Boptions = new BitmapFactory.Options();
	// Boptions.inJustDecodeBounds = true;
	// BitmapFactory.decodeFile(path, Boptions);
	// Boptions.inSampleSize = calculateInSampleSize(Boptions, 480, 800);
	// Boptions.inPurgeable = true;
	// Boptions.inInputShareable = true;
	// Boptions.inJustDecodeBounds = false;
	//
	// bitmap = BitmapFactory.decodeFile(path, Boptions);
	// int degree = readPictureDegree(path);
	// if (degree != 0) {
	// bitmap = rotaingImageView(degree, bitmap);// 传入压缩后的图片进行旋转
	// }
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//
	// bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
	//
	// int size1 = 100;// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	//
	// while (baos.toByteArray().length / 1024 > 100) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	//
	// baos.reset();// 重置baos即清空baos
	// size1 -= 10;// 每次都减少10
	// if (size1 == 0) {
	// size1 = 1;
	// }
	//
	// bitmap.compress(Bitmap.CompressFormat.JPEG, size1, baos);
	// }
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// bitmap = BitmapFactory.decodeStream(isBm);
	// return bitmap;
	// }

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	// public static Bitmap toRoundBitmap(Bitmap bitmap) {
	// int width = bitmap.getWidth();
	// int height = bitmap.getHeight();
	// float roundPx;
	// float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
	// if (width <= height) {
	// roundPx = width / 2;
	// left = 0;
	// top = 0;
	// right = width;
	// bottom = width;
	// height = width;
	// dst_left = 0;
	// dst_top = 0;
	// dst_right = width;
	// dst_bottom = width;
	// } else {
	// roundPx = height / 2;
	// float clip = (width - height) / 2;
	// left = clip;
	// right = width - clip;
	// top = 0;
	// bottom = height;
	// width = height;
	// dst_left = 0;
	// dst_top = 0;
	// dst_right = height;
	// dst_bottom = height;
	// }
	//
	// Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xff424242;
	// final Paint paint = new Paint();
	// final Rect src = new Rect((int) left, (int) top, (int) right,
	// (int) bottom);
	// final Rect dst = new Rect((int) dst_left, (int) dst_top,
	// (int) dst_right, (int) dst_bottom);
	//
	// paint.setAntiAlias(true);// 设置画笔无锯齿
	//
	// canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
	// paint.setColor(color);
	//
	// // 以下有两种方法画圆,drawRounRect和drawCircle
	// // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
	// // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
	// canvas.drawCircle(roundPx, roundPx, roundPx, paint);
	//
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));//
	// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
	// canvas.drawBitmap(bitmap, src, dst, paint); //
	// 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
	//
	// return output;
	// }

	// public static void compressBmpToFile(Bitmap bmp, File file) {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// int options = 80;// 个人喜欢从80开始,
	// bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
	// while (baos.toByteArray().length / 1024 > 100) {
	// baos.reset();
	// options -= 10;
	// bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
	// }
	// try {
	// FileOutputStream fos = new FileOutputStream(file);
	// fos.write(baos.toByteArray());
	// fos.flush();
	// fos.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// ****************this all modify by lilifeng
	// ***************************************************************************************************************************
	// public static String bitmap_too_File(String path, String name) {
	// Bitmap bit = getimage(path);
	//
	// if (null == bit) {
	// return null;
	// }
	// File file = null;
	// try {
	// File pathFile = new File(filePath);
	// if (!pathFile.exists()) {
	// pathFile.mkdirs();
	// }
	// file = new File(filePath, name);
	// if (file.exists()) {
	// file.delete();
	// }
	//
	// file.createNewFile();
	//
	// FileOutputStream fileOut = new FileOutputStream(file);
	// int size = 100;
	// if (bit.getHeight() > 1000 || bit.getWidth() > 1000) {
	// size = 80;
	// }
	// bit.compress(Bitmap.CompressFormat.JPEG, size, fileOut);
	//
	// fileOut.flush();
	// fileOut.close();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return file.getAbsolutePath();
	//
	// }

	// public static Bitmap compressImage(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
	// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// int options = 100;
	// while (baos.toByteArray().length / 1024 > 100) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// options -= 10;// 每次都减少10
	// }
	// System.out.println("图片压缩之后的大小" + baos.toByteArray().length / 1024);
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
	// 把ByteArrayInputStream数据生成图片
	// return bitmap;
	// }
	//
	// public static Bitmap getimage(String srcPath) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
	//
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 160f;// 这里设置高度为800f
	// float ww = 240f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	// }
	//
	// public static Bitmap comp(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
	// while (baos.toByteArray().length / 1024 > 100) {//
	// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//
	// 这里压缩50%，把压缩后的数据存放到baos中
	//
	// }
	// ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 160f;// 这里设置高度为800f
	// float ww = 240f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// isBm = new ByteArrayInputStream(baos.toByteArray());
	// bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
	// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	// }

	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * 
	 * @param activity
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	// 根据uri获得图片真实地址
	// public static String getPath(Uri uri, Context context) {
	// String[] proj = { MediaStore.Images.Media.DATA };
	// ContentResolver cr = context.getContentResolver();
	//
	// Cursor cursor = cr.query(uri, proj, null, null, null);
	//
	//
	// int actual_image_column_index = cursor
	// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// cursor.moveToFirst();
	// return cursor.getString(actual_image_column_index);
	//
	// }
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}
}
