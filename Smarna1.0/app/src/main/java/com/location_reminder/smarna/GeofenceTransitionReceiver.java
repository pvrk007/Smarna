package com.location_reminder.smarna;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by Chinmaya on 06-Nov-15.
 */
public class GeofenceTransitionReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = GeofenceTransitionReceiver.class.getSimpleName();

    private Context context;

    public GeofenceTransitionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive(context, intent)");
        Log.e(TAG,"onreceive called");
        this.context = context;
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event != null){
            if(event.hasError()){
                onError(event.getErrorCode());
            } else {
                int transition = event.getGeofenceTransition();
                if(transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL || transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                    String[] geofenceIds = new String[event.getTriggeringGeofences().size()];
                    for (int index = 0; index < event.getTriggeringGeofences().size(); index++) {
                        geofenceIds[index] = event.getTriggeringGeofences().get(index).getRequestId();
                    }
                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        onEnteredGeofences(geofenceIds);
                        Log.e(TAG,"onenteredGeofences called");
                    } else {
                        onExitedGeofences(geofenceIds);
                        Log.e(TAG,"On exitedGeofences called");
                    }
                    /*while(transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL)
                    {
                        try(
                                onEnteredGeofences(geofenceIds);

                    */

                }
            }
        }
    }

    protected void onEnteredGeofences(String[] geofenceIds) {
        Log.e(TAG,"inside on entered geo fences");
        for (String fenceId : geofenceIds) {
            //Toast.makeText(context, String.format("Entered this fence: %1$s", fenceId), Toast.LENGTH_SHORT).show();
            Log.i(TAG, String.format("Entered this fence: %1$s", fenceId));
            createNotification(fenceId, "Entered");
        }
    }

    protected void onExitedGeofences(String[] geofenceIds){
        Log.e(TAG,"on exited geo fence called");
        for (String fenceId : geofenceIds) {
            //Toast.makeText(context, String.format("Exited this fence: %1$s", fenceId), Toast.LENGTH_SHORT).show();
            Log.i(TAG, String.format("Exited this fence: %1$s", fenceId));
            createNotification(fenceId, "Exited");
        }
    }

    protected void onError(int errorCode){
        Log.e(TAG, "on error of receiver called");
        //Toast.makeText(context, String.format("onError(%1$d)", errorCode), Toast.LENGTH_SHORT).show();
        Log.e(TAG, String.format("onError(%1$d)", errorCode));
    }

    /**
     * Create our notification.
     *
     * @param fenceId the name of the Geofence
     * @param fenceState Entered, Exited or Dwell
     */
    private void createNotification(String fenceId, String fenceState) {
        Log.e(TAG,"notification getting created");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder
                .setContentText(fenceId)
                .setContentTitle("Task Reminder")
                //.setContentTitle(String.format("Fence %1$s", fenceState))
                .setSmallIcon(R.mipmap.homeicon2)
                .setColor(Color.GREEN);
                //.setTicker(String.format("%1$s Fence: %2$s", fenceState, fenceId));
        Intent notificationIntent = new Intent(context, WelcomeActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(WelcomeActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(R.id.notification, notificationBuilder.build());
        Log.e(TAG,"notification sent");
    }

}
