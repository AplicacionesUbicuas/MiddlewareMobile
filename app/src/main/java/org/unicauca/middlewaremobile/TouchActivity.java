package org.unicauca.middlewaremobile;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.unicauca.middlewaremobile.actions.UserActionListener;
import org.unicauca.middlewaremobile.fragments.AlertDialogFragment;
import org.unicauca.middlewaremobile.fragments.ProgressDialogFragment;
import org.unicauca.middlewaremobile.middleware.client.MantissClient;
import org.unicauca.middlewaremobile.middleware.client.MantissClientFactory;
import org.unicauca.middlewaremobile.middleware.handler.MantissConnectionHandler;
import org.unicauca.middlewaremobile.middleware.handler.MantissControlHandler;
import org.unicauca.middlewaremobile.middleware.message.MantissMessage;
import org.unicauca.middlewaremobile.middleware.message.MantissMessageFactory;
import org.unicauca.middlewaremobile.middleware.message.MantissMotionEvent;

import java.util.UUID;

import de.tavendo.autobahn.WebSocket;

public class TouchActivity extends Activity implements
		MantissConnectionHandler, MantissControlHandler, UserActionListener,
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	private static final String TAG = TouchActivity.class.getSimpleName();
	private String ipT = "";
	private static final String CANNOT_CONNECT_ALERT_TAG = "CannotConnectAlertTag";
	private static final String CONNECTION_LOST_ALERT_TAG = "ConnectionLostAlertTag";
	private static final String CONNECT_PROGRESSDIALOG_TAG = "ConnectProgressDialog";
	private final String FLURRY_APIKEY = "QB8QQZ3P9C43YJXMB8WQ"; // DEVELOP_MANTISS
	private MantissClient mClient;
	private Handler handler;
	private GestureDetectorCompat mDetector;
	private SetupAsyncTask setupAsyncTask;
	private String uuid = "";
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_touch);
		sp = getSharedPreferences(
				getResources().getString(R.string.shared_preferences),
				MODE_PRIVATE);
		mClient = MantissClientFactory.getInstance(
				"ws://" + sp.getString("ip", "192.168.1.1") + ":8080", this,
				null, this);
		// Instantiate the gesture detector with the
		// application context and an implementation of
		// GestureDetector.OnGestureListener
		mDetector = new GestureDetectorCompat(this, this);
		// Set the gesture detector as the double tap
		// listener.
		mDetector.setOnDoubleTapListener(this);
		Log.d(TAG, "TouchActivity initialized");
		handler = new Handler();

	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, FLURRY_APIKEY);

		if (!mClient.isConnected()) {
			showReconnectButton();
			showProgressDialog();
			hideTouchView();
			mClient.connect();
		}

		hideReconnectButton();
		checkConnectionStatus();
	}

	private void checkConnectionStatus() {
		handler.postDelayed(checkConnectionStatusRunnable, 1000);
	}

	private Runnable checkConnectionStatusRunnable = new Runnable() {
		public void run() {
			if (mClient.isConnected()) {
				hideReconnectButton();
			} else {
				showReconnectButton();
			}
			checkConnectionStatus();
		}
	};

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");

		MantissMessage byeMessage = MantissMessageFactory.getInstanceBye(
				"REQUEST", uuid);
		mClient.publishControl(byeMessage);

		mClient.disconnect();

		super.onDestroy();

	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();

		handler.removeCallbacks(checkConnectionStatusRunnable);
	}

	private void showAlertDialog(String title, String message, String tag) {
		FragmentManager fragmentManager = getFragmentManager();
		AlertDialogFragment fragment = new AlertDialogFragment();

		Bundle args = new Bundle();
		args.putString(AlertDialogFragment.MESSAGE_TITLE, title);
		args.putString(AlertDialogFragment.MESSAGE_KEY, message);
		args.putString(AlertDialogFragment.POSITIVE_LABEL_KEY,
				getString(R.string.positive_button));
		fragment.setArguments(args);

		fragmentManager.beginTransaction().add(fragment, tag).show(fragment)
				.commitAllowingStateLoss();

	}

	private void showProgressDialog() {

		FragmentManager fragmentManager = getFragmentManager();
		ProgressDialogFragment fragment = new ProgressDialogFragment();

		fragment.show(fragmentManager, CONNECT_PROGRESSDIALOG_TAG);
	}

	private void dismissFragmentDialog(String tag) {
		FragmentManager fragmentManager = getFragmentManager();
		DialogFragment fragment = (DialogFragment) fragmentManager
				.findFragmentByTag(tag);
		if (fragment != null && fragment.isAdded()) {
			fragmentManager.beginTransaction().remove(fragment)
					.commitAllowingStateLoss();
		}
	}

	public void reconnect(View v) {
		showProgressDialog();
		mClient.connect();
	}

	private void showReconnectButton() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ImageButton imb = (ImageButton) findViewById(R.id.imageButton1);
				imb.setVisibility(View.VISIBLE);
			}
		});

	}

	private void hideReconnectButton() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ImageButton imb = (ImageButton) findViewById(R.id.imageButton1);
				imb.setVisibility(View.GONE);
			}
		});
	}

	private void hideTouchView() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				View view = findViewById(R.id.imageView2);
				TextView title = (TextView) findViewById(R.id.title_touch);
				Button settings = (Button) findViewById(R.id.settingsButton);
				title.setVisibility(View.INVISIBLE);
				settings.setVisibility(View.VISIBLE);
				view.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void showTouchView() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				View view = findViewById(R.id.imageView2);
				TextView title = (TextView) findViewById(R.id.title_touch);
				Button settings = (Button) findViewById(R.id.settingsButton);
				settings.setVisibility(View.INVISIBLE);
				title.setVisibility(View.VISIBLE);
				view.setVisibility(View.VISIBLE);

			}
		});
	}

	/**
	 * Handle WAMP onOpen event
	 */
	@Override
	public void onOpen() {
		Log.i(TAG, "YA estamos conectados!!");

		uuid = UUID.randomUUID().toString();
		mClient.subscribeControl();

		setupAsyncTask = new SetupAsyncTask();
		setupAsyncTask.execute(mClient);
	}

	/**
	 * Handle WAMP onClose event
	 */
	@Override
	public void onClose(int code, String reason) {

		switch (code) {
		case WebSocket.ConnectionHandler.CLOSE_NORMAL:
			break;

		case WebSocket.ConnectionHandler.CLOSE_CANNOT_CONNECT:
		case WebSocket.ConnectionHandler.CLOSE_SERVER_ERROR:
		case WebSocket.ConnectionHandler.CLOSE_INTERNAL_ERROR:

			dismissFragmentDialog(CONNECT_PROGRESSDIALOG_TAG);
			dismissFragmentDialog(CANNOT_CONNECT_ALERT_TAG);
			showAlertDialog(getString(R.string.title_cannot_connect), reason,
					CANNOT_CONNECT_ALERT_TAG);
			showReconnectButton();
			break;

		case WebSocket.ConnectionHandler.CLOSE_CONNECTION_LOST:
			dismissFragmentDialog(CONNECTION_LOST_ALERT_TAG);
			showAlertDialog(getString(R.string.title_connection_lost), reason
					+ "/r" + getString(R.string.message_connection_lost),
					CONNECTION_LOST_ALERT_TAG);
			break;

		case WebSocket.ConnectionHandler.CLOSE_PROTOCOL_ERROR:
			break;

		case WebSocket.ConnectionHandler.CLOSE_RECONNECT:
			break;

		}
	}

	/**
	 * HANDLES USER INTERACTIONS
	 */

	@Override
	public void onPositive(String tag) {

	}

	@Override
	public void onNegative(String tag) {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent event) {
		// Log.d(TAG,"onDown: " + event.toString());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2,
			float velocityX, float velocityY) {
		Log.d(TAG, "onFling: " + event1.toString() + event2.toString());

		MantissMessage message = MantissMessageFactory.getInstanceSwipe(
				new MantissMotionEvent(event1), new MantissMotionEvent(event2));
		mClient.publish(message);

		return true;
	}

	@Override
	public void onLongPress(MotionEvent event) {
		// Log.d(TAG, "onLongPress: " + event.toString());
		MantissMessage message = MantissMessageFactory
				.getInstanceLongPress(new MantissMotionEvent(event));
		mClient.publish(message);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// Log.d(TAG, "onScroll: " + e1.toString()+e2.toString());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent event) {
		Log.d(TAG, "onShowPress: " + event.toString());
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		Log.d(TAG, "onSingleTapUp: " + event.toString());
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		Log.d(TAG, "onDoubleTap: " + event.toString());

		MantissMessage message = MantissMessageFactory
				.getInstanceDoubleTap(new MantissMotionEvent(event));
		mClient.publish(message);

		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		Log.d(TAG, "onDoubleTapEvent: " + event.toString());

		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		Log.d(TAG, "onSingleTapConfirmed: " + event.toString());

		MantissMessage message = MantissMessageFactory
				.getInstanceTap(new MantissMotionEvent(event));
		mClient.publish(message);
		return true;
	}

	private class SetupAsyncTask extends
			AsyncTask<MantissClient, Integer, Long> {

		@Override
		protected Long doInBackground(MantissClient... params) {
			MantissClient mantissClient = params[0];

			while (!isCancelled()) {

				MantissMessage message = MantissMessageFactory
						.getInstanceSetup("REQUEST", uuid);
				Log.i(TAG, "publicando mensaje de inicio " + message.toString());
				mantissClient.publishControl(message);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	@Override
	public void onSetup(String topicUri, MantissMessage message) {
		Log.i(TAG, "incomming SetupMessage " + message.toString());

		if ("RESPONSE".equals(message.getStartContent())) {
			if (uuid.equals(message.getEndContent())) {
				dismissFragmentDialog(CONNECT_PROGRESSDIALOG_TAG);
				hideReconnectButton();
				showTouchView();
				setupAsyncTask.cancel(true);
			} else {
				Log.i(TAG, "No es para mi");
			}
		} else {
			Log.i(TAG, "No es una respuesta");
		}
	}

	@Override
	public void onPing(String topicUri, MantissMessage message) {
		Log.i(TAG, "incomming PingMessage " + message);

		if ("REQUEST".equals(message.getStartContent())) {
			if (uuid.equals(message.getEndContent())) {
				MantissMessage pingMessage = MantissMessageFactory
						.getInstancePing("RESPONSE", uuid);
				mClient.publishControl(pingMessage);
			} else {
				Log.i(TAG, "No es para mi");
			}
		} else {
			Log.i(TAG, "No es una petición de ping");
		}
	}

	@Override
	public void onBye(String topicUri, MantissMessage message) {
		Log.i(TAG, "incomming ByeMessage");

		if ("REQUEST".equals(message.getStartContent())) {
			if (uuid.equals(message.getEndContent())) {
				mClient.disconnect();
				Log.i(TAG, "Disconnected fron Server");
			}
		}
	}

	public void settingsDialog(View v) {
		final Dialog dialog = new Dialog(this,
				R.style.NoBackgroundTitleDialogTheme);
		dialog.setContentView(R.layout.dialog_ip_config);
		Button acceptBtn = (Button) dialog.findViewById(R.id.btnAccept);
		Button cancelBtn = (Button) dialog.findViewById(R.id.btnCancel);
		final EditText ip = (EditText) dialog.findViewById(R.id.ipEditText);
		acceptBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (!ip.getText().toString().isEmpty()) {
					ipT = ip.getText().toString();
					// Writing data to SharedPreferences
					Editor editor = sp.edit();
					editor.putString("ip", ipT);
					editor.commit();
					Toast.makeText(getApplicationContext(),
							"Ip Agregada correctamente", Toast.LENGTH_SHORT)
							.show();
					dialog.dismiss();
					finish();
					startActivity(new Intent(getApplicationContext(),
							TouchActivity.class));
				} else {
					Toast.makeText(getApplicationContext(),
							"Por favor ingresa una dirección",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

}
