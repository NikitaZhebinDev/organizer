package first.project.nikzhebindev.organizer.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import first.project.nikzhebindev.organizer.MainMenu;
import first.project.nikzhebindev.organizer.NewTask;
import first.project.nikzhebindev.organizer.R;
import first.project.nikzhebindev.organizer.db.DataBaseLists;
import first.project.nikzhebindev.organizer.db.DataBaseTasks;

import java.util.Random;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MyPreferenceFragment extends PreferenceFragment {

  private static String MARKET_URI = "market://details?id=first.project.nikzhebindev.organizerplus";

  Preference versionOrganizer;
  Preference removeAds;

  CheckBoxPreference permanentNotification;
  CheckBoxPreference confirmFTasks;
  CheckBoxPreference Images;
  CheckBoxPreference vibration;

  ListPreference taskNotification;
  ListPreference listShowStartup;
  ListPreference time_format;

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);

    versionOrganizer = findPreference("versionOrganizer");
    removeAds = findPreference("removeAds");

    permanentNotification = (CheckBoxPreference) findPreference("permanentNotification");
    confirmFTasks = (CheckBoxPreference) findPreference("confirmFTasks");
    Images = (CheckBoxPreference) findPreference("Images");
    vibration = (CheckBoxPreference) findPreference("vibration");

    taskNotification = (ListPreference) findPreference("taskNotification");
    listShowStartup = (ListPreference) findPreference("listShowStartup");
    time_format = (ListPreference) findPreference("time_format");

    permanentNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {
        if (permanentNotification.isChecked()) {
          permanentNotification.setSummary(getActivity().getString(R.string.enabled));
          sendDefaultNotification();
        } else {
          permanentNotification.setSummary(getActivity().getString(R.string.disabled));
          cancelDefaultNotification();
        }
        return false;
      }
    });

    if (permanentNotification.isChecked()) {
      permanentNotification.setSummary(getActivity().getString(R.string.enabled));
    } else {
      permanentNotification.setSummary(getActivity().getString(R.string.disabled));
    }

    confirmFTasks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {
        if (confirmFTasks.isChecked()) {
          confirmFTasks.setSummary(getActivity().getString(R.string.enabled));
        } else {
          confirmFTasks.setSummary(getActivity().getString(R.string.disabled));
        }
        return false;
      }
    });

    if (confirmFTasks.isChecked()) {
      confirmFTasks.setSummary(getActivity().getString(R.string.enabled));
    } else {
      confirmFTasks.setSummary(getActivity().getString(R.string.disabled));
    }

    vibration.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        if (vibration.isChecked()) {
          vibration.setSummary(getActivity().getString(R.string.enabled));
        } else {
          vibration.setSummary(getActivity().getString(R.string.disabled));
        }
        return false;
      }
    });
    if (vibration.isChecked()) {
      vibration.setSummary(getActivity().getString(R.string.enabled));
    } else {
      vibration.setSummary(getActivity().getString(R.string.disabled));
    }

    Images.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {
        getActivity().recreate();
        return false;
      }
    });

    versionOrganizer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {
        Random rand = new Random();
        int randomNum = rand.nextInt(5) + 1;
        String smile = "";
        switch (randomNum) {
          case 2:
            smile = "◔◡‿◡◔";
            break;
          case 3:
            smile = "*^_^*";
            break;
          case 4:
            smile = "~(˘▾˘~)";
            break;
          default:
            smile = "(>^.^)>";
        }
        // TODO: implement our own Toast class with override method
        Toast.makeText(getActivity(), smile, Toast.LENGTH_SHORT).show();
        return false;
      }
    });

    removeAds.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(MARKET_URI));
        startActivity(intent);
        return false;
      }
    });

    taskNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        taskNotification.setValue(newValue.toString());
        taskNotification.setSummary(newValue.toString());
        return false;
      }
    });

    if (taskNotification.getValue().compareTo("0") != 0) {
      taskNotification.setSummary(taskNotification.getValue());
    } else if (taskNotification.getValue().compareTo("0") == 0) {
      taskNotification.setSummary(getActivity().getResources().getStringArray(R.array.task_notification)[0]);
      taskNotification.setValue(taskNotification.getSummary().toString());
    }

    time_format.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        time_format.setValue(newValue.toString());
        time_format.setSummary(newValue.toString());
        return false;
      }
    });

    if (time_format.getValue().compareTo("0") != 0) {
      time_format.setSummary(time_format.getValue());
    } else if (time_format.getValue().compareTo("0") == 0) {
      time_format.setSummary(getActivity().getResources().getStringArray(R.array.array_time_formats)[1]);
      time_format.setValue(time_format.getSummary().toString());
    }

    setListPreferenceData(listShowStartup);
    listShowStartup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        setListPreferenceData(listShowStartup);
        listShowStartup.setValue(newValue.toString());
        listShowStartup.setSummary(newValue.toString());
        return false;
      }
    });

    DataBaseLists dataBaseLists = new DataBaseLists(getActivity());
    SQLiteDatabase sqLiteDatabase = dataBaseLists.getWritableDatabase();

    if (DataBaseLists.checkListPresence(sqLiteDatabase, listShowStartup.getValue())) {
      listShowStartup.setSummary(listShowStartup.getValue());
    } else {
      listShowStartup.setValue(MainMenu.KEY_ALL_TASKS);
      listShowStartup.setSummary(MainMenu.KEY_ALL_TASKS);
    }
  }

  public void sendDefaultNotification() {
    NotificationCompat.Builder builder;
    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

    RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.permanent_notification);
    remoteViews.setImageViewResource(R.id.imageViewAddTask, R.drawable.ic_add_task_notification);
    remoteViews.setImageViewResource(R.id.imageViewToSettings, R.drawable.ic_settings_notification);
    remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.ic_launcher);

    DataBaseTasks dbTasks = new DataBaseTasks(getActivity());
    SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();
    int amount = DataBaseTasks.getAmountTasksToday(databaseTasks);

    if (amount < 1) {
      remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.no_tasks_today));
      remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
    } else {
      remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.amount_of_tasks) + " " + amount);
      remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
    }
    //////////// FOR imageViewAddTask ////////////
    Intent addTaskIntent = new Intent(getActivity(), NewTask.class);
    addTaskIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentAddTaskIntent = PendingIntent.getActivity(getActivity(),
        111, addTaskIntent, FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.imageViewAddTask, contentAddTaskIntent);
    //////////// FOR imageViewAddTask ////////////

    //////////// FOR imageViewToSettings ////////////
    Intent toSettingsIntent = new Intent(getActivity(), MainMenu.class);
    toSettingsIntent.putExtra("requestCode", 112);
    toSettingsIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(getActivity(),
        112, toSettingsIntent, FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.imageViewToSettings, contentToSettingsIntentIntent);
    //////////// FOR imageViewToSettings ////////////

    Intent notifIntent = new Intent(getActivity(), MainMenu.class);
    notifIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 68571,
        notifIntent, FLAG_CANCEL_CURRENT);

    builder = new NotificationCompat.Builder(getActivity());
    builder.setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(contentIntent)
        .setOngoing(true)
        .setCustomContentView(remoteViews);
    try {
      notificationManager.notify(MainMenu.def_notification_id, builder.build());
    } catch (Exception e) {
      // TODO
    }
  }

  public void cancelDefaultNotification() {
    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
    try {
      notificationManager.cancel(MainMenu.def_notification_id);
    } catch (Exception e) {
      // TODO
    }
  }

  public void setListPreferenceData(ListPreference lp) {
    DataBaseLists dataBaseLists = new DataBaseLists(getActivity());
    SQLiteDatabase sqLiteDatabase = dataBaseLists.getWritableDatabase();
    String[] listsFromDB = DataBaseLists.readDBLists(sqLiteDatabase);

    String[] lists = new String[listsFromDB.length + 1];

    lists[0] = MainMenu.KEY_ALL_TASKS;
    for (int i = 1; i < lists.length; i++) {
      lists[i] = listsFromDB[i - 1];
    }

    CharSequence[] entries = new CharSequence[lists.length];
    CharSequence[] entryValues = new CharSequence[lists.length];

    for (int i = 0; i < lists.length; i++) {
      entries[i] = lists[i];
      entryValues[i] = lists[i];
    }
    lp.setEntries(entries);
    lp.setEntryValues(entryValues);
  }
}
