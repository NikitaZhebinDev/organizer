package first.project.nikzhebindev.organizer.notifications;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MyReceiver extends BroadcastReceiver{

    public MyReceiver() {}





    @Override
    public void onReceive(Context context, Intent intent) {


        boolean BOOT = false;
        try{
            BOOT = intent.getAction().compareTo(Intent.ACTION_BOOT_COMPLETED) == 0;
        }
        catch(Exception e){
            BOOT = false;
        }


        if (BOOT) {
            //context.startService(new Intent(context, MyIntentService.class));
            try {

                //String title = intent.getStringExtra("NotificationTitle");
                /*String message = "AAAAAAAAAAAAgjrfkbgcbc";//intent.getStringExtra("NotificationMessage");
                String id = "77881";//intent.getStringExtra("id_Notification");


                Intent myIntent = new Intent(context, MyIntentService.class);


                // myIntent.putExtra("NotificationTitle", title);
                myIntent.putExtra("NotificationMessage", message);
                myIntent.putExtra("id_Notification", id);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myIntent);
                } else {
                    context.startService(myIntent);
                }*/

                Intent intentREBOOT = new Intent(context, MyIntentService.class);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intentREBOOT.putExtra("Reboot", "Yes"));
                } else {
                    context.startService(intentREBOOT.putExtra("Reboot", "Yes"));
                }











            } catch (Exception e) {
            }
        }
        else {
            try {

                //String title = intent.getStringExtra("NotificationTitle");
                String message = intent.getStringExtra("NotificationMessage");
                String id = intent.getStringExtra("id_Notification");


                Intent myIntent = new Intent(context, MyIntentService.class);


                // myIntent.putExtra("NotificationTitle", title);
                myIntent.putExtra("NotificationMessage", message);
                myIntent.putExtra("id_Notification", id);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(myIntent.putExtra("Reboot", "No"));
                } else {
                    context.startService(myIntent.putExtra("Reboot", "No"));
                }
            } catch (Exception e) {
            }
        }






    }



}
