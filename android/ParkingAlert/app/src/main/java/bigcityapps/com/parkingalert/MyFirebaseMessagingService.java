package bigcityapps.com.parkingalert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "meniuu";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(MainActivity.active) {
            Intent intent = new Intent(INTENT_FILTER);
            intent.putExtra("meniuu", remoteMessage);
            sendBroadcast(intent);
        }else
          if(Notificari.active){
              Intent intent = new Intent(INTENT_FILTER);
              intent.putExtra("meniuu", remoteMessage);
              sendBroadcast(intent);
          }else
              sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle() );
    }

    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, ViewNotification.class);
//        intent.putExtra("question_id",id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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

//    private void sendNotification(String messageBody, String id,String title, int type_of_notification) {
//        Intent intent = null;
//        if(type_of_notification==1) {
//            intent = new Intent(this, Vote_new.class);
//            Log.w("meniuu","a intrat in Vote vew:"+type_of_notification);
//        }
//        else
//        if(type_of_notification==2)
//        {   intent = new Intent(this, Resume.class);
//            Log.w("meniuu","a intrat in Resume:"+type_of_notification);
//        }
//        else
//        if(type_of_notification==3)
//        {   intent = new Intent(this, QuestionActivity.class);
//            Log.w("meniuu","a intrat in QuestionActivity:"+type_of_notification);
//        }
//        intent.putExtra("question_id",id);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.logo_app)
//                .setContentTitle(title)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
}