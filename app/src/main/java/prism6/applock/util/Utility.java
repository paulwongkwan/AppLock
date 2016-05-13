package prism6.applock.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by MEMEME-iClass on 5/5/2016.
 */
public class Utility {

	public static boolean isMyServiceRunning(Context c, Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean stringSearch(String[] arr, String targetValue) {
		for(String s: arr){
			if(s.contains(targetValue) || targetValue.contains(s))
				return true;
		}
		return false;
	}

}
