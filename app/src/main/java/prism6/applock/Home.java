package prism6.applock;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import prism6.applock.service.LockAppService;
import prism6.applock.util.Utility;

public class Home extends AppCompatActivity {
	@BindView(R.id.cond_text) TextView cond_text;

	@OnClick(R.id.stop_btn)
	public void stopService(){
		Log.e("Service", "Stop Service");
		stopService(new Intent(this, LockAppService.class));
	}

	@OnClick(R.id.start_btn)
	public void startService(){
		Log.e("Service", "Start Service");
		if(!Utility.isMyServiceRunning(this, LockAppService.class))
			startService(new Intent(this, LockAppService.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);

		if(!Utility.isMyServiceRunning(this, LockAppService.class))
			startService(new Intent(this, LockAppService.class));

		new getAppList().execute();

	}

	protected void onResume(){
		super.onResume();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if(Utility.isMyServiceRunning(getBaseContext(), LockAppService.class))
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							cond_text.setText("Locker Running");
						}
					});
				else
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							cond_text.setText("Locker Stop");
						}
					});
			}
		}, 100, 1000);
	}

	private class getAppList extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			Log.e("getAppList", "Geting");

			PackageManager pm = getPackageManager();
			List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

			List<ApplicationInfo> installedApps = new ArrayList<>();

			for(ApplicationInfo app : apps) {
				Log.e("className", app.packageName);
				//checks for flags; if flagged, check if updated system app
				if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
					installedApps.add(app);
					//it's a system app, not interested
				} else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
					//Discard this one
					//in this case, it should be a user-installed app
				} else {
					installedApps.add(app);
				}
			}

			return null;
		}
	}
}
