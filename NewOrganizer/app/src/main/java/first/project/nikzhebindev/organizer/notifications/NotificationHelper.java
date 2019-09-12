package first.project.nikzhebindev.organizer.notifications;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;

import java.util.Date;

import first.project.nikzhebindev.organizer.MainMenu;
import first.project.nikzhebindev.organizer.R;

public class NotificationHelper {



    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL";






    public NotificationHelper(Context context) {
        mContext = context;
    }








    /**
     * Create and push the notification
     */

    public void createNotification(String message, int id)
    {
        NotificationManager mNotificationManager;
        NotificationCompat.Builder builder;
        int NOTIFICATION_ID;



        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(mContext);



        //////////////////////////// Sound ////////////////////////////
        SharedPreferences getAlarms = PreferenceManager.
                getDefaultSharedPreferences(mContext);
        String alarms = getAlarms.getString("my_ringtone_preference", "true");
        Uri uriSound = Uri.parse(alarms);
        //////////////////////////// Sound ////////////////////////////









        // THAT IS BETTER (WE GET CURRENT TIME)
        String title = mContext.getString(R.string.task_at) + " " + DateUtils
                .formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);




        /**               Create random ID  */
        NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);




        /**Creates an explicit intent for an Activity in your app**/
        Intent notificationIntent = new Intent(mContext, MainMenu.class);
        //notificationIntent.putExtra("notificationMessage", message);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);



        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                id+850, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);



        long[] vibrate = {500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000};




        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = null;


            try {
                notificationChannel =
                        mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            } catch(Exception e){}


            if(notificationChannel == null) {
                notificationChannel =
                        new NotificationChannel
                                (NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME_ORGANIZER", importance);
                notificationChannel.setDescription("NOTIFICATION_CHANNEL_NAME_ORGANIZER");
                if(myPreference.getBoolean("vibration", true)) {
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(vibrate);
                }
                else{
                    notificationChannel.enableVibration(false);
                    notificationChannel.setVibrationPattern(new long[]{0, 0, 0, 0});
                }
            }
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);

            if(myPreference.getBoolean("vibration", true)) {
                notificationChannel.setVibrationPattern(vibrate);
            }
            else {
                notificationChannel.setVibrationPattern(new long[]{0, 0, 0, 0});
            }

            mNotificationManager.createNotificationChannel(notificationChannel);
        }


        builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification_black)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(uriSound)
                .setVibrate(vibrate)
                .setLights(Color.BLUE, 3000, 3000)
                .setPriority(Notification.PRIORITY_MAX);







        if(alarms.compareTo("true") == 0 || alarms.compareTo("false") == 0 ||
                alarms.compareTo("") == 0){
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND); // Set Default
        }







        if(myPreference.getBoolean("vibration", true))
            builder.setVibrate(vibrate);
        else
            builder.setVibrate(new long[]{0,0,0,0});










        /////////////////////   Alarm Mode   /////////////////////

        if(myPreference.getBoolean("alarm_mode", true)) {
            builder.build().flags = builder.build().flags | Notification.FLAG_INSISTENT;
        }
        else{
            builder.build().flags = builder.build().flags | Notification.FLAG_ONLY_ALERT_ONCE;
        }

        /////////////////////   Alarm Mode   /////////////////////



        assert mNotificationManager != null;

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

