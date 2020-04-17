package first.project.nikzhebindev.organizer.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MyReceiver extends BroadcastReceiver {

  public MyReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    boolean BOOT;
    try {
      BOOT = intent.getAction().compareTo(Intent.ACTION_BOOT_COMPLETED) == 0;
    } catch (Exception e) {
      BOOT = false;
    }
    if (BOOT) {
      try {
        Intent intentREBOOT = new Intent(context, MyIntentService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intentREBOOT.putExtra("Reboot", "Yes"));
        } else {
          context.startService(intentREBOOT.putExtra("Reboot", "Yes"));
        }
      } catch (Exception e) {
        // TODO: implement!
      }
    } else {
      try {
        String message = intent.getStringExtra("NotificationMessage");
        String id = intent.getStringExtra("id_Notification");
        Intent intentService = new Intent(context, MyIntentService.class);
        intentService.putExtra("NotificationMessage", message);
        intentService.putExtra("id_Notification", id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intentService.putExtra("Reboot", "No"));
        } else {
          context.startService(intentService.putExtra("Reboot", "No"));
        }
      } catch (Exception e) {
        // TODO: implement!
      }
    }
  }
}
