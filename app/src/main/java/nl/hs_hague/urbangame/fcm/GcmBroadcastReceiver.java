package nl.hs_hague.urbangame.fcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.RoomListActivity;

/**
 * Created by vural on 05.10.16.
 * Receiver for push notifications
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MainActivity", "Something has arrived");
        // Notification is build
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Urban Game")
                        .setContentText(intent.getStringExtra("message"));

        // Intent to start MainActivity
        Intent resultIntent = new Intent(context, RoomListActivity.class);

        // Back Button to home screen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(999, mBuilder.build());

        setResultCode(Activity.RESULT_OK);
    }

}
