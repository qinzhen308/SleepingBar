package com.core.framework.store.file;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.core.framework.app.MyApplication;
import com.core.framework.util.ApiUtil;

public class FileHelper {
	/**
	 * 删除文件，可以是单个文件或文件夹
	 *
	 * @param fileName
	 *            待删除的文件名
	 * @return 文件删除成功返回true,否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {
				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
        return file.exists() && file.isFile() && file.delete();
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 *
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			return false;
		}

		// 删除当前目录
        return dirFile.delete();
	}

    public static String getMIMEType(String filePath) {
        File f = new File(filePath);
        return getMIMEType(f);
    }

    /**
     * 获取文件的MIME type
     * @param f
     * @return
     */
    public static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}



    public static String getAppFilesPath() {
        return MyApplication.getInstance().getFilesDir().getAbsolutePath();
    }


    public static File getDiskCacheDir(Context context) {
        File externalFile = getExternalCacheDir(context);
        if (!externalFile.exists()) {
            externalFile = context.getCacheDir();
        }

        return externalFile;
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        File externalFile = getExternalCacheDir(context);
        if (!externalFile.exists()) {
            externalFile = context.getCacheDir();
        }

        return new File(externalFile, uniqueName);
    }

    public static File getExternalCacheDir(Context context) {
        File cacheDir = null;
        if (ApiUtil.hasFroyo()&&context!=null) {
            cacheDir = context.getExternalCacheDir();
        }

        if (cacheDir == null) {
            String cacheDirPath = "/Android/data/" + MyApplication.getInstance().getTruePackageName() + "/cache/";
            cacheDir = new File(Environment.getExternalStorageDirectory(), cacheDirPath);
        }

        return cacheDir;
    }
}
