package bigcityapps.com.parkingalert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "meniuu";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_FILTER_Notificari = "INTENT_FILTER_NOTIFICARI";
    SharedPreferences prefs;
    RequestQueue queue;
    boolean senderRead, receiverRead;
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String nr_car = null, notification_id = null, notification_type = null, estimated_time = null, answered_at = null, date_neformated, latitude = null, longitude = null;
        JSONObject question = new JSONObject(remoteMessage.getData());
        Log.w("meniuu","amprimit un push notification in firebase: "+question);
        try {
            notification_id = question.getString("notification_id");
            notification_type = question.getString("notification_type");
            date_neformated = question.getString("answered_at");
            nr_car = question.getString("car_id");
            try {
                latitude = question.getString("latitude");
                longitude = question.getString("longitude");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try{
                estimated_time = question.getString("estimated_time");
            }catch (Exception e){
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
            if (MainActivity.active) {
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("notification_id", notification_id);
                    intent.putExtra("notification_type", notification_type);
                    intent.putExtra("mPlates", nr_car);
                    intent.putExtra("mHour", date_neformated);
                    intent.putExtra("estimated_time", estimated_time);
                    sendBroadcast(intent);
                } else {
                    checkSenderRead(notification_id,notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), nr_car, estimated_time, answered_at, latitude, longitude);
//                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
                }



//            if (notification_type.equals("sender")) {
//                notification_id = question.getString("notification_id");
//                if (Notifications.active) {
//                    Intent intent = new Intent(INTENT_FILTER_Notificari);
//                    sendBroadcast(intent);
//                } else if (MainActivity.active) {
//                    Log.w("meniuu","este sender");
//                    Intent intent = new Intent(INTENT_FILTER);
//                    intent.putExtra("notification_id", notification_id);
//                    intent.putExtra("mPlates", nr_car);
//                    intent.putExtra("notification_type", notification_type);
//                    intent.putExtra("notification_id", notification_id);
//                    intent.putExtra("lat", latitude);
//                    intent.putExtra("lng", longitude);
//                    sendBroadcast(intent);
//                } else {
//                    checkSenderRead(notification_id);
//                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
//                    Log.w("meniuu", "se trimite notificare");
//                }
//            }
//
//            /////receiverul
//            else if (notification_type.equals("receiver")) {
//                estimated_time = question.getString("estimated_time");
//                notification_id = question.getString("notification_id");
//
//                if (Notifications.active) {
//                    Intent intent = new Intent(INTENT_FILTER_Notificari);
//                    sendBroadcast(intent);
//                } else if (MainActivity.active) {
//                    Log.w("meniuu","este receiver");
//                    Intent intent = new Intent(INTENT_FILTER);
//                    intent.putExtra("estimated_time", estimated_time);
//                    intent.putExtra("mPlates", nr_car);
//                    intent.putExtra("notification_type", notification_type);
//                    intent.putExtra("answered_at", answered_at);
//                    intent.putExtra("notification_id", notification_id);
//                    intent.putExtra("lat", latitude);
//                    intent.putExtra("lng", longitude);
//                    sendBroadcast(intent);
//                } else {
//                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
//                    Log.w("meniuu", "se trimite notificare");
//                }
//            } else if (notification_type.equals("extended")) {
//                estimated_time = question.getString("estimated_time");
//                notification_id = question.getString("notification_id");
//
//                if (Notifications.active) {
//                    Intent intent = new Intent(INTENT_FILTER_Notificari);
//                    sendBroadcast(intent);
//                } else if (MainActivity.active) {
//                    Log.w("meniuu","este extendet");
//                    Intent intent = new Intent(INTENT_FILTER);
//                    intent.putExtra("estimated_time", estimated_time);
//                    intent.putExtra("mPlates", nr_car);
//                    intent.putExtra("notification_type", notification_type);
//                    intent.putExtra("answered_at", answered_at);
//                    intent.putExtra("notification_id", notification_id);
//                    intent.putExtra("lat", latitude);
//                    intent.putExtra("lng", longitude);
//                    sendBroadcast(intent);
//                } else {
//                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
//                    Log.w("meniuu", "se trimite notificare");
//                }
//            } else if (notification_type.equals("review")) {
//                notification_id = question.getString("notification_id");
//
//                if (Notifications.active) {
//                    Intent intent = new Intent(INTENT_FILTER_Notificari);
//                    sendBroadcast(intent);
//                } else if (MainActivity.active) {
//                    Log.w("meniuu","este review");
//                    Intent intent = new Intent(INTENT_FILTER);
//                    intent.putExtra("mPlates", nr_car);
//                    intent.putExtra("notification_id", notification_id);
//                    intent.putExtra("notification_type", notification_type);
//                    intent.putExtra("answered_at", answered_at);
//                    intent.putExtra("notification_id", notification_id);
//                    intent.putExtra("lat", latitude);
//                    intent.putExtra("lng", longitude);
//                    sendBroadcast(intent);
//                } else {
//                    sendNotification(notification_type, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
//                    Log.w("meniuu", "se trimite notificare");
//                }
//            }

        } catch (JSONException e) {
            Log.w("meniuu", "catch la firebase");
            e.printStackTrace();
        }
        Log.w(TAG, "notificari:" + Notifications.active + " mainactivity:" + MainActivity.active);
    }

    private void sendNotification(String notification_type, String messageBody, String title, String notification_id, String nr_car, String estimated_time, String answered_at, String latitude, String longitude) {
        Log.w("meniuu", " ora in constructia notificarii:" + answered_at);
        Intent intent = null;
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("time", estimated_time);
            intent.putExtra("mHour", answered_at);
            intent.putExtra("mPlates", nr_car);
            intent.putExtra("notification_id", notification_id);
            intent.putExtra("lat", latitude);
            intent.putExtra("lng", longitude);
            intent.putExtra("notification_type", notification_type);
//        if (notification_type.equals("sender")) {
//            intent = new Intent(this, ViewNotification.class);
//            intent.putExtra("notification_id", notification_id);
//            intent.putExtra("mPlates", nr_car);
//        } else if (notification_type.equals("receiver")) {
//            intent = new Intent(this, Timer.class);
//            intent.putExtra("time", estimated_time);
//            intent.putExtra("mHour", answered_at);
//            intent.putExtra("mPlates", nr_car);
//            intent.putExtra("notification_id", notification_id);
//            intent.putExtra("lat", latitude);
//            intent.putExtra("lng", longitude);
//            Log.w("meniuu"," in timer_sender: time:"+estimated_time+" mhour:"+answered_at+" mplates:"+nr_car+" notif_id:"+notification_id+" lat:"+latitude+" lng:"+longitude);
//        } else if (notification_type.equals("extended")) {
//            intent = new Intent(this, Timer.class);
//            intent.putExtra("time", estimated_time);
//            intent.putExtra("mHour", answered_at);
//            intent.putExtra("mPlates", nr_car);
//            intent.putExtra("notification_id", notification_id);
//            intent.putExtra("lat", latitude);
//            intent.putExtra("lng", longitude);
//        } else if (notification_type.equals("review")) {
//            intent = new Intent(this, Review.class);
//            intent.putExtra("notification_id", notification_id);
//            intent.putExtra("mPlates", nr_car);
//        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
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

    public void checkSenderRead(final String notification_id, final String notification_type, final String messageBody, final String title, final String nr_car, final String estimated_time, final String answered_at, final String latitude, final String longitude){
        prefs = new SecurePreferences(this);
        queue = Volley.newRequestQueue(this);
        Log.w("meniuu","notification id:"+notification_id);
        String url = Constants.URL+"notifications/getNotification/"+notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        try {
                            JSONObject notif= new JSONObject(json);
                             senderRead=notif.getBoolean("receiver_read");
//                            if(senderRead==false){
                                Log.w("meniuu", "se trimite notificare");
                                sendNotification(notification_type, messageBody, title, notification_id, nr_car, estimated_time, answered_at, latitude, longitude);
//                            }
//                            else
//                                Log.w("meniuu","numai trimit notifcicare pt ca deja e citita id notif:"+notification_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, ErrorListener) {
            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
//    public void checkReceiverRead(String notification_id){
//        prefs = new SecurePreferences(this);
//        queue = Volley.newRequestQueue(this);
//        Log.w("meniuu","notification id:"+notification_id);
//        String url = Constants.URL+"notifications/getNotification/"+notification_id;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    public void onResponse(String response) {
//                        String json = response;
//                        try {
//                            Log.w("meniuu", "response: receiveranswer" + response);
//                            JSONObject notif= new JSONObject(json);
//                            receiverRead=notif.getBoolean("receiver_read");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, ErrorListener) {
//            public java.util.Map getHeaders() throws AuthFailureError {
//                String auth_token_string = prefs.getString("token", "");
//                java.util.Map params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer "+auth_token_string);
//                return params;
//            }
//        };
//        queue.add(stringRequest);
//    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
}