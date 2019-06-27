package pmt.kyawkyaw.myapp.mediacom.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pmt.kyawkyaw.myapp.mediacom.MessagingActivity;

public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // super.onMessageReceived(remoteMessage);
        Log.i("MSG",remoteMessage.toString());
        //get current user from share preference
        if (remoteMessage==null){
            Log.i("MSG","NULL");
            return;
        }else {
            Log.i("MSG",remoteMessage.getNotification().getBody());
            SharedPreferences sp = getSharedPreferences("noti_auth_user", MODE_PRIVATE);
            String savedCurrentUser = sp.getString("Current_USERID", "None");

            String sent = remoteMessage.getData().get("sent");
            String user = remoteMessage.getData().get("user");
            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
            if (fuser != null && sent.equals(fuser.getUid())) {
                if (!savedCurrentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOAndAboveNotification(remoteMessage);
                    } else {
                        sendNormalNotification(remoteMessage);
                    }
                }
            }
        }
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MessagingActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("id",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1=new OreoAndAboveNotification(this);
        Notification.Builder builder=notification1.getNotifications(title,body,pIntent,defSoundUri,icon);
        int j=0;
        if(i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());
    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {

    }
}
