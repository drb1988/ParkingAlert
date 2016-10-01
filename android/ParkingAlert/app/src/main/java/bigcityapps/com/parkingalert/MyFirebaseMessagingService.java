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


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "meniuu";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_FILTER_Notificari = "INTENT_FILTER_NOTIFICARI";
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String nr_car = null,notification_id = null;
        int type_of_notification=0;
        JSONObject question= new JSONObject(remoteMessage.getData());
        try {
            nr_car=question.getString("car_id");
            notification_id=question.getString("notification_id");
        } catch (JSONException e) {
            Log.w("meniuu","catch la firebase");
            e.printStackTrace();
        }
        Log.w(TAG,"notificari:"+Notificari.active+" mainactivity:"+MainActivity.active);
        if(Notificari.active) {
            Intent intent = new Intent(INTENT_FILTER_Notificari);
            intent.putExtra("meniuu", remoteMessage);
            sendBroadcast(intent);
        }else
          if(MainActivity.active){
              Intent intent = new Intent(INTENT_FILTER);
              intent.putExtra("meniuu", remoteMessage);
              sendBroadcast(intent);
          }else {
              sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(),notification_id,nr_car);
          Log.w("meniuu","se trimite notificare");
          }
    }

    private void sendNotification(String messageBody, String title, String notification_id, String nr_car) {
        Intent intent = new Intent(this, ViewNotification.class);
        intent.putExtra("notification_id",notification_id);
        intent.putExtra("nr_car",nr_car);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Log.w(TAG,"intent"+intent);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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