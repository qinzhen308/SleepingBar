package com.core.framework.develop;

import android.content.Context;
import android.os.Bundle;
import com.core.framework.develop.*;
import com.core.framework.develop.FaceTestforDlp;
import com.core.framework.develop.Su;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by suwg on 2015/1/30.
 */
public class DevAppRun {
	static TreeSet<Long> set;
	static HashMap<Long, String> map;
	static HashMap<String, String> nameMap;
	static {
		if (DevRunningTime.isShowAppusedTime) {
			set = new TreeSet<Long>();
			map = new HashMap<Long, String>();
			nameMap = new HashMap<String, String>();
			// nameMap.put("BoutiqueSwipeActivity", "首页");
			// nameMap.put("MainActivity", "首页框架");
			// nameMap.put("ClassificationNewActivity", "首页分类");
			// nameMap.put("BrandGroupMainActivity_3", "首页品牌团");
			// nameMap.put("SplashActivity", "启动页面");
			// nameMap.put("BoutiqueSwipeActivity", "首页");
			// nameMap.put("BoutiqueSwipeActivity", "首页");
			nameMap.put("MainActivity", "首页框架");
			nameMap.put("SplashActivity", "启动页面");
		}
	}
	static boolean isStartThread;

	// 耗时
	@FaceTestforDlp
	protected long onCreateTime, onResumeEndTime;

	@FaceTestforDlp
	protected int onCreateLe, onResumeLe;

	@FaceTestforDlp
	boolean isNotFirst;

	@FaceTestforDlp
	Context ac;

	public DevAppRun(Context ac) {
		super();
		this.ac = ac;
		onCreateTime = new Date().getTime();

		if (!isStartThread && DevRunningTime.isShowAppusedTime) {
			isStartThread = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000 * 60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						com.core.framework.develop.Su
								.logApp("==============================");
						Iterator<Long> it = set.iterator();
						while (it.hasNext()) {
							long in = it.next();
							String name = map.get(in);
							com.core.framework.develop.Su.logApp(in + ";"
									+ name + "=" + nameMap.get(name));
						}
						com.core.framework.develop.Su
								.logApp("==============================");
					}
				}
			}).start();
		}
	}

	@FaceTestforDlp
	private void showUsedTime(int le, String info) {
		if (isNotFirst || le != -1) {
			return;
		}
		onResumeEndTime = new Date().getTime();
		long uesd = onResumeEndTime - onCreateTime;

		uesd = getByused(uesd);
		map.put(uesd, ac.getClass().getSimpleName());
		set.add(uesd);

		String name = ac.getClass().getSimpleName();
		String real = nameMap.get(name);
		if (real != null)
			name = real + name;
		Su.logApp(name + " " + le + " " + info + " " + uesd);

	}

	private long getByused(long uesd) {
		if (map.containsKey(uesd)) {
			uesd++;
			return getByused(uesd);
		}
		return uesd;
	}

	@FaceTestforDlp
	public void onCreateEnd(Bundle savedInstanceState) {
		if (!DevRunningTime.isShowAppusedTime)
			return;
		showUsedTime(onCreateLe++, "onCreate End");
	}

	@FaceTestforDlp
	public void onResumeStart() {
		if (!DevRunningTime.isShowAppusedTime)
			return;
		showUsedTime(-1, "onResume start");
	}

	@FaceTestforDlp
	public void onResumeEnd() {
		if (!DevRunningTime.isShowAppusedTime)
			return;
		showUsedTime(onResumeLe++, "onResume End");
	}

	public void setNotFirst(boolean isNotFirst) {
		this.isNotFirst = true;
	}
}
