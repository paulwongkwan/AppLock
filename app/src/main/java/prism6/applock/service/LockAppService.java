package prism6.applock.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import prism6.applock.Home;
import prism6.applock.util.Utility;

/**
 * Created by MEMEME-iClass on 20/4/2016.
 */
public class LockAppService extends Service {

	static final String[] SYSTEM_APP = {"prism6.applock", "com.android.systemui.recentsactivity", "android.app.launcher"};

	String lastAppPN = "";
	boolean noDelay = false;
	public static LockAppService instance;
	private TimerTask task;
	private Timer timer;
	private ArrayList<String> watchList = new ArrayList<>();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method

		Log.e("LockAppService", "Service Start");

		scheduleMethod();
		Log.e("Current PN", "" + SYSTEM_APP[0]);

		instance = this;

		return START_STICKY;
	}

	private void scheduleMethod() {
		// TODO Auto-generated method stub

//		ScheduledExecutorService scheduler = Executors
//				.newSingleThreadScheduledExecutor();
//		scheduler.scheduleAtFixedRate(new Runnable() {
//
//			@Override
//			public void run() {
//					checkRunningApps();
//			}
//		}, 0, 100, TimeUnit.MILLISECONDS);

		task = new TimerTask() {
			@Override
			public void run() {
				if (Looper.myLooper() == null)
					Looper.prepare();

				checkRunningApps();
			}
		};
		timer = new Timer();
		timer.schedule(task, 0, 100);

	}

	public String checkRunningApps() {

		String activityOnTop = getTopAppName(LockAppService.this);

		if(!lastAppPN.equals(activityOnTop)) {
			lastAppPN = activityOnTop;

			//Action here
			if (!Utility.stringSearch(SYSTEM_APP, activityOnTop)) {
				Toast.makeText(getApplicationContext(), "Your opened " + activityOnTop, Toast.LENGTH_LONG).show();
				Log.e("PN: ", activityOnTop);

				Intent intent = new Intent();
				intent.setClass(LockAppService.this, Home.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {
				// DO nothing
			}
		}

		return activityOnTop;
	}

	public static void stop() {
		if (instance != null) {
			instance.stopSelf();
		}
	}

	public static String getTopAppName(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String strName = "";
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				strName = getLollipopFGAppPackageName(context);
				strName = mActivityManager.getRunningAppProcesses().get(0).processName;
			} else {
				strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Package Name: ", strName);
		}
		return strName;
	}

	public void onDestroy() {
		timer.cancel();
		stopSelf();
		super.onDestroy();
	}

}
