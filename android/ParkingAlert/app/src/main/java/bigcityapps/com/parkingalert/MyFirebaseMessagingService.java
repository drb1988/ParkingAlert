package bigcityapps.com.parkingalert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "meniuu";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_FILTER_Notificari = "INTENT_FILTER_NOTIFICARI";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        String nr_car = null, notification_id = null, notification_type = null, estimated_time = null, answered_at = null, date_neformated, latitude = null, longitude = null;
        JSONObject question = new JSONObject(remoteMessage.getData());
        try {
            Log.w("meniuu", "data:" + question);
            notification_type = question.getString("notification_type");
            date_neformated = question.getString("answered_at");
            nr_car = question.getString("car_id");
            try {
                latitude = question.getString("latitude");
                longitude = question.getString("longitude");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                Date myDate = simpleDateFormat.parse(date_neformated);
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                String data = format1.format(myDate);
                answered_at = data;
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("meniuu", "catch la formatarea datei");
            }

            if (notification_type.equals("sender")) {
                notification_id = question.getString("notification_id");
                if (Notifications.active) {
                    Intent intent = new Intent(INTENT_FILTER_Notificari);
                    sendBroadcast(intent);
                } else if (MainActivity.active) {
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("mPlates", nr_car);
                    intent.putExtra("notification_type", notification_type);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    sendBroadcast(intent);
                } else {

                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
                    Log.w("meniuu", "se trimite notificare");
                }
            }

            /////receiverul
            else if (notification_type.equals("receiver")) {
                estimated_time = question.getString("estimated_time");
                notification_id = question.getString("notification_id");

                if (Notifications.active) {
                    Intent intent = new Intent(INTENT_FILTER_Notificari);
                    sendBroadcast(intent);
                } else if (MainActivity.active) {
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("estimated_time", estimated_time);
                    intent.putExtra("mPlates", nr_car);
                    intent.putExtra("notification_type", notification_type);
                    intent.putExtra("answered_at", answered_at);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    sendBroadcast(intent);
                } else {
                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
                    Log.w("meniuu", "se trimite notificare");
                }
            } else if (notification_type.equals("extended")) {
                estimated_time = question.getString("estimated_time");
                notification_id = question.getString("notification_id");

                if (Notifications.active) {
                    Intent intent = new Intent(INTENT_FILTER_Notificari);
                    sendBroadcast(intent);
                } else if (MainActivity.active) {
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("estimated_time", estimated_time);
                    intent.putExtra("mPlates", nr_car);
                    intent.putExtra("notification_type", notification_type);
                    intent.putExtra("answered_at", answered_at);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    sendBroadcast(intent);
                } else {
                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
                    Log.w("meniuu", "se trimite notificare");
                }
            } else if (notification_type.equals("review")) {
                notification_id = question.getString("notification_id");

                if (Notifications.active) {
                    Intent intent = new Intent(INTENT_FILTER_Notificari);
                    sendBroadcast(intent);
                } else if (MainActivity.active) {
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("mPlates", nr_car);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("notification_type", notification_type);
                    intent.putExtra("answered_at", answered_at);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    sendBroadcast(intent);
                } else {
                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
                    Log.w("meniuu", "se trimite notificare");
                }
            }

        } catch (JSONException e) {
            Log.w("meniuu", "catch la firebase");
            e.printStackTrace();
        }
        Log.w(TAG, "notificari:" + Notifications.active + " mainactivity:" + MainActivity.active);
    }

    private void sendNotification(String notification_type, String messageBody, String title, String notification_id, String nr_car, String estimated_time, String answered_at, String latitude, String longitude) {
        Log.w("meniuu", " ora in constructia notificarii:" + answered_at);
        Intent intent = null;
        if (notification_type.equals("sender")) {
            intent = new Intent(this, ViewNotification.class);
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("mPlates", nr_car);
        } else if (notification_type.equals("receiver")) {
            intent = new Intent(this, Timer.class);
            intent.putExtra("time", estimated_time);
            intent.putExtra("mHour", answered_at);
            intent.putExtra("mPlates", nr_car);
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("lat", latitude);
            intent.putExtra("lng", longitude);
            Log.w("meniuu"," in timer: time:"+estimated_time+" mhour:"+answered_at+" mplates:"+nr_car+" notif_id:"+notification_id+" lat:"+latitude+" lng:"+longitude);
        } else if (notification_type.equals("extended")) {
            intent = new Intent(this, Timer.class);
            intent.putExtra("time", estimated_time);
            intent.putExtra("mHour", answered_at);
            intent.putExtra("mPlates", nr_car);
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("lat", latitude);
            intent.putExtra("lng", longitude);
        } else if (notification_type.equals("review")) {
            intent = new Intent(this, Review.class);
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("mPlates", nr_car);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Log.w(TAG, "intent" + intent);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}