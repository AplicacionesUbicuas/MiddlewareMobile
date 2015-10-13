package org.unicauca.middlewaremobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

public class SplashActivity extends Activity {

	private static final String TAG = SplashActivity.class.getSimpleName();
	// private final String FLURRY_APIKEY = "QB8QQZ3P9C43YJXMB8WQ"; //
	// DEVELOP_MANTISS
	private final long SPLASH_TIME = 1500;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		handler = new Handler();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(getApplicationContext(),
						TouchActivity.class);
				startActivity(intent);
				finish();
				Log.d(TAG, "SplashActivity finished");
			}
		}, SPLASH_TIME);

		Log.d(TAG, "SplashActivity initialized");

	}
}
