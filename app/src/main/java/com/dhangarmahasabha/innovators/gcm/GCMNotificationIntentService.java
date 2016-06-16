package com.dhangarmahasabha.innovators.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.dhangarmahasabha.innovators.MainActivity;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.ui.news.NotificationNewsActivity;
import com.dhangarmahasabha.innovators.util.ConstCore;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	private NotificationUtils notificationUtils;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		Log.d(TAG, "Message: " + extras.get(Config.MESSAGE_KEY));
		Log.d(TAG, "Url: " + extras.get(Config.PATH_KEY));
		Log.d(TAG, "Id: " + extras.get(Config.ID_KEY));
		Log.d(TAG, "Status: " + extras.get(Config.STATUS_KEY));
		String data = extras.getString(Config.MESSAGE_KEY);
		String imageUrl = extras.getString(Config.PATH_KEY);
		String id = extras.getString(Config.ID_KEY);
		String status = extras.getString(Config.STATUS_KEY);
		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				if (TextUtils.isEmpty(imageUrl)){
					//sendNotification(""+extras.get(Config.MESSAGE_KEY));
					if (TextUtils.isEmpty(id) && TextUtils.isEmpty(status)) {
						Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
						showNotificationMessage(getApplicationContext(), getString(R.string.app_name), data, resultIntent);
					}else {

						Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
						resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						resultIntent.putExtra(ConstCore.TAG_NID,id);
						resultIntent.putExtra(ConstCore.TAG_STATUS,status);
						showNotificationMessage(getApplicationContext(), getString(R.string.app_name), data, resultIntent);
					}
				}else {
					Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
					showNotificationMessageWithBigImage(getApplicationContext(),getString(R.string.app_name), data, resultIntent, com.dhangarmahasabha.innovators.util.Config.IMAGE_URL+imageUrl);
				}


			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.logo)
				.setContentTitle(getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	private void showNotificationMessage(Context context, String title, String message, Intent intent) {
		notificationUtils = new NotificationUtils(context);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationUtils.showNotificationMessage(title, message, intent);
	}

	private void showNotificationMessageWithBigImage(Context context, String title, String message, Intent intent, String imageUrl) {
		Log.d(TAG, "In BigInage");
		notificationUtils = new NotificationUtils(context);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
	}
}
