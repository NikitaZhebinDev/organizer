package first.project.nikzhebindev.organizer.notification;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import first.project.nikzhebindev.organizer.MainMenu;
import first.project.nikzhebindev.organizer.NewTask;
import first.project.nikzhebindev.organizer.R;
import first.project.nikzhebindev.organizer.db.DataBaseLists;
import first.project.nikzhebindev.organizer.db.DataBaseTasks;

import java.util.Date;

public class MyIntentService extends IntentService {


    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {


        if(intent.getStringExtra("Reboot").compareTo("Yes") == 0){
            /**                         RESTORE                              */
            /**   Now we have to READD ALL of our Tasks  */
            ////////////////////////////////////////////////////////////////////////////////////////////
            DataBaseTasks dbTasks = new DataBaseTasks(this);
            SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();

            DataBaseTasks.DataArrayList dataArrayList = DataBaseTasks.getSortedAllDataWithArrayList(databaseTasks);

            for(int i = 0; i < dataArrayList.arrTimeInMillis.size(); i++) {

                if (dataArrayList.arrTimeInMillis.get(i) != 0L) {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTimeInMillis(dataArrayList.arrTimeInMillis.get(i));

                    String id = DataBaseTasks.getCertainTaskID(databaseTasks, dataArrayList.arrTasks.get(i));

                    sendNotification(calendar, id, dataArrayList.arrRepeat.get(i), dataArrayList.arrTasks.get(i), this);
                }

            }
            ////////////////////////////////////////////////////////////////////////////////////////////
            SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
            if (myPreference.getBoolean("permanentNotification", true))
                sendDefaultNotification();
            else
                cancelDefaultNotification();
            ////////////////////////////////////////////////////////////////////////////////////////////
        }
        else if (intent.getStringExtra("Reboot").compareTo("Yes") != 0) {


            int NOTIFICATION_ID;
            /**               Create random ID  */
            NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);


            //String title = intent.getStringExtra("NotificationTitle");
            String message = intent.getStringExtra("NotificationMessage");
            int id = Integer.decode(intent.getStringExtra("id_Notification"));
            // THAT IS BETTER (WE GET CURRENT TIME)
            String title = getString(R.string.task_at) + " " +
                    DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);

            Intent notificationIntent = new Intent(this, MainMenu.class);
            //notificationIntent.putExtra("notificationMessage", message);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);


            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    id + 850, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);






            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                //NotificationHelper notificationOreo = new NotificationHelper(this);
                //notificationOreo.createNotification(message, id);


                /**  Create Notification Channel */


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                String idChannel = "first.project.nikzhebindev.organizerplus.channel";
                // The user-visible name of the channel.
                CharSequence name = "Organizer";
                // The user-visible description of the channel.
                String description = "Organizer Notifications";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(idChannel, name, importance);
                // Configure the notification channel.
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                // Sets the notification light color for notification posted to this
                // channel, if the device supports this feature.
                mChannel.setLightColor(Color.BLUE);
                notificationManager.createNotificationChannel(mChannel);


                /**  Create Notification Channel */




                /**  Create Notification */


                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder
                        (getApplicationContext(),idChannel)
                        .setSmallIcon(R.drawable.ic_notification_icon) //your app icon
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                        .setChannelId(idChannel)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setContentText(message)
                        .setWhen(System.currentTimeMillis());
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

                /**  Create Notification */

            } else {



                //////////////////////////// Sound ////////////////////////////
                SharedPreferences getAlarms = PreferenceManager.
                        getDefaultSharedPreferences(this);
                String alarms = getAlarms.getString("my_ringtone_preference", "true");
                Uri uriSound = Uri.parse(alarms);
                //////////////////////////// Sound ////////////////////////////


                long[] vibrate = new long[]{500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000};



                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                Notification.Builder notificationBuilder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_black)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(contentIntent)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)

                        .setSound(uriSound)

                        .setVibrate(vibrate)
                        .setLights(Color.BLUE, 3000, 3000)

                        .setPriority(Notification.PRIORITY_MAX);


                if (alarms.compareTo("true") == 0 || alarms.compareTo("false") == 0 ||
                        alarms.compareTo("") == 0) {
                    notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND); // Set Default
                }


                SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
                if (myPreference.getBoolean("vibration", true))
                    notificationBuilder.setVibrate(vibrate);
                else
                    notificationBuilder.setVibrate(new long[]{0, 0, 0, 0});


                Notification notification = notificationBuilder.build();


                /////////////////////   Alarm Mode   /////////////////////

                if (myPreference.getBoolean("alarm_mode", true)) {
                    notification.flags = notification.flags | Notification.FLAG_INSISTENT;
                } else {
                    notification.flags = notification.flags | Notification.FLAG_ONLY_ALERT_ONCE;
                }

                /////////////////////   Alarm Mode   /////////////////////




        /*Intent notifyIntent = new Intent(this, MainMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);*/


                try {
                    mNotificationManager.notify(NOTIFICATION_ID, notification);

                    //WakeLock screenOn = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
                    //screenOn.acquire();

                } catch (Exception e) {
                }
            }
        }
    }



    private void sendNotification(java.util.Calendar calendar, String id, String repeat, String message, Context context) {
        // Calendar calendar, String repeating
        // and then in your activity set the alarm manger to start the broadcast receiver
        // at a specific time and use AlarmManager setRepeating method to repeat it this
        // example bellow will repeat it every day.


        //String title = getString(R.string.task_at) + " " + editTime.getEditableText().toString();
        //String message = editTask.getEditableText().toString();


        Intent notifyIntent = new Intent(context, MyReceiver.class);
        //notifyIntent.putExtra("NotificationTitle", title);
        notifyIntent.putExtra("NotificationMessage", message);
        notifyIntent.putExtra("id_Notification", id);


        int newID = 0;
        try{
            newID = Integer.decode(id);
        }
        catch(Exception e){
            newID = Integer.getInteger(id);
        }
        // ONE REAQUEST CODE FOR ALL.... That's bad I think !!!!!!!!!!!!!!!!!!
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, (newID+87658), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);




        int timeBefore = 0;
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(context);

        if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[0]) == 0){      // 0 mins
            timeBefore = 0;
        }
        else if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[1]) == 0){ // 5 mins
            timeBefore = 60000 * 5;
        }
        else if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[2]) == 0){ // 15 mins
            timeBefore = 60000 * 15;
        }
        else if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[3]) == 0){ // 30 mins
            timeBefore = 60000 * 30;
        }
        else if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[4]) == 0){ // 1 hour
            timeBefore = 60000 * 60;
        }
        else if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[5]) == 0){ // 2 hours
            timeBefore = 60000 * 60 * 2;
        }
        else if(myPreference.getString("taskNotification", "0")
                .compareTo(context.getResources().getStringArray(R.array.task_notification)[6]) == 0){ // 3 hours
            timeBefore = 60000 * 60 * 3;
        }



        try {
            if (repeat.compareTo(DataBaseLists.listRepeatNames[0]) == 0) { // Не повторять
                alarmManager.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis()- timeBefore, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[1]) == 0) { // Каждый час
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                        AlarmManager.INTERVAL_HOUR, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[2]) == 0) { // Ежедневно
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[3]) == 0) { // Еженедельно
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                        AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            } else if (repeat.compareTo(DataBaseLists.listRepeatNames[4]) == 0) { // Ежегодно
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - timeBefore,
                        AlarmManager.INTERVAL_DAY * 365, pendingIntent);
            }
        } catch(Exception e) {
            //
        }




    }












    public void sendDefaultNotification() {
        //////////////////////// NOTIFICATION ////////////////////////

        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.permanent_notification);
        remoteViews.setImageViewResource(R.id.imageViewAddTask, R.drawable.ic_add_task_notification);
        remoteViews.setImageViewResource(R.id.imageViewToSettings, R.drawable.ic_settings_notification);
        remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.ic_launcher);

        //Icon icon = Icon.createWithResource(this, R.mipmap.ic_launcher);
        //remoteViews.setImageViewIcon(R.id.imageViewAppIcon, icon);



        DataBaseTasks dbTasks = new DataBaseTasks(this);
        SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();
        int amount = DataBaseTasks.getAmountTasksToday(databaseTasks);

        if(amount < 1){
            remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.no_tasks_today));
            remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
        }
        else {
            remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.amount_of_tasks) + " " + amount);
            remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
        }



        //////////// FOR imageViewAddTask ////////////
        Intent addTaskIntent = new Intent(this, NewTask.class);
        addTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentAddTaskIntent = PendingIntent.getActivity(this,
                111, addTaskIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewAddTask, contentAddTaskIntent);
        //////////// FOR imageViewAddTask ////////////


        //////////// FOR imageViewToSettings ////////////
        Intent toSettingsIntent = new Intent(this, MainMenu.class);
        toSettingsIntent.putExtra("requestCode", 112);
        toSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(this,
                112, toSettingsIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageViewToSettings, contentToSettingsIntentIntent);
        //////////// FOR imageViewToSettings ////////////



        //Intent btnAddTask_intent = new Intent("btnAddTask_clicked");
        //btnAddTask_intent.putExtra("btnAddTask_id", notification_id);


        Intent notifIntent = new Intent(this, MainMenu.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                68571, notifIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification_black)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setCustomContentView(remoteViews)
        ;


        try{
            notificationManager.notify(MainMenu.def_notification_id, builder.build());
        }
        catch (NullPointerException e) {
            //
        }

        //////////////////////// NOTIFICATION ////////////////////////
    }

    public void cancelDefaultNotification(){

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try { notificationManager.cancel(MainMenu.def_notification_id); } catch (Exception e) {
            //
        }

    }






}

