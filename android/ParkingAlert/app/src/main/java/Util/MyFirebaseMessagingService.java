package Util;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "meniuu";
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String id="";
        int type_of_notification=0;
        JSONObject question= new JSONObject(remoteMessage.getData());
        try {
            id=question.getString("question_id");
            type_of_notification=question.getInt("type_of_notification");
            Log.w("meniuu","id:"+id+" type_of_notification:"+type_of_notification);
        } catch (JSONException e) {
            Log.w("meniuu","catch la firebase");
            e.printStackTrace();
        }

        //Calling method to generate notification
//        sendNotification(remoteMessage.getNotification().getBody(),id,remoteMessage.getNotification().getTitle(), type_of_notification );
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts

//    private void sendNotification(String messageBody, String id, String title) {
//        Intent intent = new Intent(this, Vote_new.class);
//        intent.putExtra("question_id",id);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.logo_app)
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

    @Override
    protected Intent zzaa(Intent intent) {
        return null;
    }
}