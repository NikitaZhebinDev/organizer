package first.project.nikzhebindev.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import first.project.nikzhebindev.organizer.fragment.AboutAppFragment;
import first.project.nikzhebindev.organizer.fragment.MyPreferenceFragment;
import first.project.nikzhebindev.organizer.fragment.ThemesFragment;
import first.project.nikzhebindev.organizer.db.DataBaseFinishedTasks;
import first.project.nikzhebindev.organizer.db.DataBaseLists;
import first.project.nikzhebindev.organizer.db.DataBaseTasks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  /**
   * Advertisement
   */
  private AdView mAdView;
  private InterstitialAd mInterstitialAd;
  public static boolean WE_CAME_FROM_NEWTASK = false;

  public static int def_notification_id = 311344;
  public static boolean FloatBtnPressed = false;
  public static boolean NeedUpdateTaskAdapterFromToolbarTitle = false;
  public static String TitleToolbar;
  public static String KEY_ALL_TASKS;
  public static boolean openedMain, openedLists, openedFTasks, openedSettings, openedAboutApp;
  public static String selectedSpinnerList;
  public static int lastSpinnerSelection = 0;
  private static final String MARKET_URI = "market://details?id=first.project.nikzhebindev.organizerplus";

  DataBaseLists dbLists;
  Spinner spinner;
  SpinnerAdapter adapterSpinner;
  android.widget.SearchView searchView;
  FloatingActionButton floatBtnToNewTask;
  Toolbar toolbar;
  RelativeLayout RL_Spinner_Search;
  NavigationView navigationView;
  DrawerLayout drawer;

  ArrayList<String> lists;
  ArrayList<Integer> listsSize;
  int sizeAllTasks;

  boolean finishedMainAnimation = false;
  boolean finishedListsAnimation = false;
  boolean finishedFTasksAnimation = false;
  boolean finishedSettingsAnimation = false;
  boolean finishedAboutAppAnimation = false;
  boolean finishedThemesAnimation = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    /** THEME */
    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
    String savedTHEME = sPref.getString("THEME", "");

    if (savedTHEME.compareTo("RandomTheme") == 0) {
      Random rand = new Random();
      int n = rand.nextInt(10) + 1;

      switch (n) {
        case 1:
          savedTHEME = "Default";
          break;
        case 2:
          savedTHEME = "DefaultThemeN";
          break;
        case 3:
          savedTHEME = "TTheme";
          break;
        case 4:
          savedTHEME = "NightTheme";
          break;
        case 5:
          savedTHEME = "IndigoTheme";
          break;
        case 6:
          savedTHEME = "IndigoThemeN";
          break;
        case 7:
          savedTHEME = "GreenTheme";
          break;
        case 8:
          savedTHEME = "GreenThemeN";
          break;
        case 9:
          savedTHEME = "PurpleTheme";
          break;
        case 10:
          savedTHEME = "PurpleThemeN";
          break;
      }
    }

    switch (savedTHEME) {
      case "Default":
        break;
      case "DefaultThemeN":
        setTheme(R.style.DefaultThemeN);
        break;
      case "TTheme":
        setTheme(R.style.TTheme);
        break;
      case "NightTheme":
        setTheme(R.style.NightTheme);
        break;
      case "IndigoTheme":
        setTheme(R.style.IndigoTheme);
        break;
      case "IndigoThemeN":
        setTheme(R.style.IndigoThemeN);
        break;
      case "GreenTheme":
        setTheme(R.style.GreenTheme);
        break;
      case "GreenThemeN":
        setTheme(R.style.GreenThemeN);
        break;
      case "PurpleTheme":
        setTheme(R.style.PurpleTheme);
        break;
      case "PurpleThemeN":
        setTheme(R.style.PurpleThemeN);
        break;
      case "LeoTheme":
        setTheme(R.style.LeoTheme);
        break;
      default:
        break;
    }
    SharedPreferences.Editor ed = sPref.edit();
    ed.putString("ThemeForNewTask", savedTHEME);
    ed.apply();
    /** THEME */

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_menu);

    /** Advertisement */
    // Sample AdMob app ID: ca-app-pub-5033052294993457~5980065515
    MobileAds.initialize(this, "ca-app-pub-5033052294993457~5980065515");
    /** Advertisement */

    DataBaseLists.listRepeatNames[0] = this.getString(R.string.no_repeat);
    DataBaseLists.listRepeatNames[1] = this.getString(R.string.once_an_hour);
    DataBaseLists.listRepeatNames[2] = this.getString(R.string.once_a_day);
    DataBaseLists.listRepeatNames[3] = this.getString(R.string.once_a_week);
    DataBaseLists.listRepeatNames[4] = this.getString(R.string.once_a_year);

    DataBaseLists.defaultListsNames[0] = this.getString(R.string.defaultlist);
    DataBaseLists.defaultListsNames[1] = this.getString(R.string.personal);

    KEY_ALL_TASKS = this.getString(R.string.all_lists);
    selectedSpinnerList = KEY_ALL_TASKS;

    floatBtnToNewTask = findViewById(R.id.floatBtnToNewTask);
    floatBtnToNewTask.setSize(75);
    floatBtnToNewTask.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FloatBtnPressed = true;

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(floatBtnToNewTask.getContext(), R.anim.press_float_btn);
        floatBtnToNewTask.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {
            // TODO: implement!
          }

          @Override
          public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(MainMenu.this, NewTask.class);
            startActivity(intent);
          }

          @Override
          public void onAnimationRepeat(Animation animation) {
            // TODO: implement!
          }
        });
      }
    });

    /** Advertisement */
    mAdView = findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);
    mAdView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        mAdView.setClickable(true);
        mAdView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAdFailedToLoad(int errorCode) {
        mAdView.setClickable(false);
        mAdView.setVisibility(View.GONE);
      }
    });
    /** Advertisement */

    RL_Spinner_Search = findViewById(R.id.RL_Spinner_Search);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.hideOverflowMenu();

    drawer = findViewById(R.id.drawer_layout);

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
      public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        drawerOpenedOrClosed();
      }

      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        drawerOpenedOrClosed();
      }
    };
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    setDrawerLeftEdgeSize(this, drawer, 0.6f);

    navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    /** THEME */
    switch (savedTHEME) {
      case "Default":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.nav_view_state_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.nav_view_state_color_text));
        break;
      case "DefaultThemeN":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.nav_view_state_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.nav_view_state_color_text));
        break;
      case "TTheme":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.t_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.t_theme_color_text));
        break;
      case "NightTheme":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.vk_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.vk_theme_color_text));
        break;
      case "IndigoTheme":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.indigo_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.indigo_theme_color_text));
        break;
      case "IndigoThemeN":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.indigo_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.indigo_theme_color_text));
        break;
      case "GreenTheme":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.green_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.green_theme_color_text));
        break;
      case "GreenThemeN":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.green_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.green_theme_color_text));
        break;
      case "PurpleTheme":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.purple_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.purple_theme_color_text));
        break;
      case "PurpleThemeN":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.purple_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.purple_theme_color_text));
        break;
      case "LeoTheme":
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.leo_theme_color));
        navigationView.setItemTextColor(getResources().getColorStateList(R.color.leo_theme_color_text));
        break;
      default:
        break;
    }

    dbLists = new DataBaseLists(this);

    final SQLiteDatabase db = dbLists.getWritableDatabase();
    // filling with default list names
    if (!DataBaseLists.checkListPresence(db, DataBaseLists.defaultListsNames[0])) {
      for (int i = 0; i < DataBaseLists.defaultListsNames.length; i++) {
        DataBaseLists.addToDBLists(db, DataBaseLists.defaultListsNames[i]);
      }
    }

    /** MainMenu Toolbar elements */
    spinner = findViewById(R.id.spinnerInsideToolbar);
    searchView = findViewById(R.id.searchMainMenu);

    setSpinnerLists();
    makeVisibleSpinnerAndSearchView();
    OpenedMain();

    /** List to show at Startup */
    SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
    String listStartup = myPreference.getString("listShowStartup", "0");

    if (listStartup.compareTo(KEY_ALL_TASKS) != 0) {
      DataBaseLists dataBaseLists = new DataBaseLists(this);
      SQLiteDatabase sqLiteDatabase = dataBaseLists.getWritableDatabase();
      String[] listsFromDB = DataBaseLists.readDBLists(sqLiteDatabase);
      String[] lists = new String[listsFromDB.length + 1];
      lists[0] = MainMenu.KEY_ALL_TASKS;
      for (int i = 1; i < lists.length; i++) {
        lists[i] = listsFromDB[i - 1];
      }
      for (int i = 0; i < lists.length; i++) {
        if (lists[i].compareTo(listStartup) == 0) {
          spinner.setSelection(i, true);
          break;
        }
      }
    }
    /** List to show at Startup */

    /** Show Images(Cats etc) or not */
    if (myPreference.getBoolean("Images", true)) {
      View headerLayout = navigationView.getHeaderView(0);
      Drawable drawable = getResources().getDrawable(R.drawable.cats);
      headerLayout.setBackground(drawable);
      TextView textHeader = headerLayout.findViewById(R.id.text_of_header);
      textHeader.setVisibility(View.GONE);
    } else {
      View headerLayout = navigationView.getHeaderView(0);
      Drawable drawable = getResources().getDrawable(R.color.transparentColor);
      headerLayout.setBackground(drawable);
    }
    /** Show Images(Cats etc) or not */
  }

  public void setSpinnerLists() {

    class DefineSpinnerLists implements Callable<ArrayList<String>> {

      private SQLiteDatabase databaseLists;

      private DefineSpinnerLists(SQLiteDatabase db) {
        this.databaseLists = db;
      }

      @Override
      public ArrayList<String> call() throws Exception {
        String[] listBufDemo = DataBaseLists.readDBLists(databaseLists);
        ArrayList<String> listBuf = new ArrayList<>();
        listBuf.add(KEY_ALL_TASKS);
        for (int i = 0; i < listBufDemo.length; i++) {
          listBuf.add(listBufDemo[i]);
        }
        return listBuf;
      }
    }

    final SQLiteDatabase databaseLists = dbLists.getWritableDatabase();

    ExecutorService exec = Executors.newCachedThreadPool();
    Future<ArrayList<String>> futureString = exec.submit(new DefineSpinnerLists(databaseLists));

    ArrayList<String> listsNames = new ArrayList<>();
    try {
      listsNames = futureString.get();
    } catch (Exception e) {
      // TODO: implement!
    } finally {
      exec.shutdown();
    }

    /** set sizes for EACH list */
    DataBaseTasks dataBaseTasks = new DataBaseTasks(this);
    SQLiteDatabase dbTasks = dataBaseTasks.getWritableDatabase();

    int sumListsSizes = 0;
    ArrayList<Integer> listsSizes = new ArrayList<>();
    for (int i = 0; i < listsNames.size(); i++) {
      listsSizes.add(i, DataBaseTasks.getAmountOfTasksByList(dbTasks, listsNames.get(i)));
      sumListsSizes += listsSizes.get(i);
    }
    /** set sizes for EACH list */

    // tune OUR OWN Spinner Adapter
    adapterSpinner = new SpinnerAdapter(this, R.layout.spinner_outside, R.id.nameOfList,
        listsNames, listsSizes, sumListsSizes);
    adapterSpinner.setDropDownViewResource(R.layout.spinner_item);

    spinner.setAdapter(adapterSpinner);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
          selectedSpinnerList = KEY_ALL_TASKS;
        } else {
          // '- 1' due to "All Tasks"
          selectedSpinnerList = DataBaseLists.readDBLists(databaseLists)[position - 1];
        }
        lastSpinnerSelection = position;
        loadTaskListFragment();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // TODO: implement!
      }
    });
  }

  public class SpinnerAdapter extends ArrayAdapter<String> {

    SpinnerAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects, ArrayList<Integer> objectsSize, int sizeAllObjects) {
      super(context, resource, textViewResourceId, objects);
      lists = objects;
      listsSize = objectsSize;
      sizeAllTasks = sizeAllObjects;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
      LayoutInflater inflater = getLayoutInflater();
      View row = inflater.inflate(R.layout.spinner_item, parent, false);

      TextView label = row.findViewById(R.id.nameOfListDropDown);
      label.setText(lists.get(position));

      TextView numTasks = row.findViewById(R.id.textView_numOfTasks);
      if (listsSize.get(position) > 0) {
        numTasks.setText(String.valueOf(listsSize.get(position)));
      } else {
        numTasks.setText("");
      }
      ImageView icon = row.findViewById(R.id.imageViewDef_spinner_item);

      if (lists.get(position).compareTo(KEY_ALL_TASKS) == 0) {
        icon.setImageResource(R.drawable.ic_home);
        numTasks.setText(String.valueOf(sizeAllTasks));
      }
      return row;
    }
  }

  public void loadTaskListFragment() {
    selectedSpinnerList = spinner.getSelectedItem().toString();
    TaskListFragment fragment = new TaskListFragment();
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commitAllowingStateLoss();
  }

  public void loadSettingsFragment() {
    MyPreferenceFragment fragment = new MyPreferenceFragment();
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commitAllowingStateLoss();
  }

  public void loadAboutAppFragment() {
    AboutAppFragment fragment = new AboutAppFragment();
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.frameLayoutForListFragment, fragment).commitAllowingStateLoss();
  }

  public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
    if (activity == null || drawerLayout == null) {
      return;
    }
    try {
      // find ViewDragHelper and set it accessible
      Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
      leftDraggerField.setAccessible(true);
      ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);

      // find edgesize and set is accessible
      Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
      edgeSizeField.setAccessible(true);
      int edgeSize = edgeSizeField.getInt(leftDragger);

      // set new edgesize
      Point displaySize = new Point();
      activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
      edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x * displayWidthPercentage)));

    } catch (NoSuchFieldException e) {
      // TODO
    } catch (IllegalArgumentException e) {
      // TODO
    } catch (IllegalAccessException e) {
      // TODO
    }
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
    if (myPreference.getBoolean("permanentNotification", true)) {
      sendDefaultNotification();
    } else {
      cancelDefaultNotification();
    }

    /** THEME */
    if (myPreference.getString("ThemeWasChanged", "").compareTo("YES") == 0) {
      SharedPreferences.Editor ed = myPreference.edit();
      ed.putString("ThemeWasChanged", "NO");
      ed.apply();
      recreate();
    }
  }

  public void sendDefaultNotification() {
    NotificationCompat.Builder builder;
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.permanent_notification);
    remoteViews.setImageViewResource(R.id.imageViewAddTask, R.drawable.ic_add_task_notification);
    remoteViews.setImageViewResource(R.id.imageViewToSettings, R.drawable.ic_settings_notification);
    remoteViews.setImageViewResource(R.id.imageViewAppIcon, R.mipmap.ic_launcher);

    DataBaseTasks dbTasks = new DataBaseTasks(this);
    SQLiteDatabase databaseTasks = dbTasks.getWritableDatabase();
    int amount = DataBaseTasks.getAmountTasksToday(databaseTasks);

    if (amount < 1) {
      remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.no_tasks_today));
      remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
    } else {
      remoteViews.setTextViewText(R.id.textViewTaskAmount, getString(R.string.amount_of_tasks) + " " + amount);
      remoteViews.setTextViewText(R.id.textViewGoodDay, getString(R.string.have_a_nice_day));
    }

    // FOR imageViewAddTask
    Intent addTaskIntent = new Intent(this, NewTask.class);
    addTaskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentAddTaskIntent = PendingIntent.getActivity(this,
        111, addTaskIntent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.imageViewAddTask, contentAddTaskIntent);
    // FOR imageViewAddTask

    // FOR imageViewToSettings
    Intent toSettingsIntent = new Intent(this, MainMenu.class);
    toSettingsIntent.putExtra("requestCode", 112);
    toSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentToSettingsIntentIntent = PendingIntent.getActivity(this,
        112, toSettingsIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.imageViewToSettings, contentToSettingsIntentIntent);
    // FOR imageViewToSettings

    Intent notifIntent = new Intent(this, MainMenu.class);
    notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentIntent = PendingIntent.getActivity(this,
        68571, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    builder = new NotificationCompat.Builder(this);
    builder.setSmallIcon(R.drawable.ic_notification_black)
        .setContentIntent(contentIntent)
        .setOngoing(true)
        .setCustomContentView(remoteViews);
    try {
      notificationManager.notify(def_notification_id, builder.build());
    } catch (NullPointerException e) {
      // TODO
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    try {
      if (intent.getExtras().getInt("requestCode") == 112) {
        hideSpinnerAndSearchView();
        toolbar.getMenu().clear();
        floatBtnToNewTask.setVisibility(View.GONE);
        toolbar.setTitle("Settings"); // TODO: MUST BE STRING LINK!!!!!
        finishedSettingsAnimation = true;
        OpenedSettings();
        loadSettingsFragment();
        finishedSettingsAnimation = false;
        toolbar.setTitleTextColor(getResources().getColor(R.color.JustWhite));
        Toast.makeText(this, getString(R.string.u_can_enable_disable), Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      // TODO
    }
  }

  public void cancelDefaultNotification() {
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    try {
      notificationManager.cancel(MainMenu.def_notification_id);
    } catch (Exception e) {
      // TODO
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
    if (myPreference.getBoolean("permanentNotification", true)) {
      sendDefaultNotification();
    } else {
      cancelDefaultNotification();
    }
  }

  public boolean isOnline() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = null;
    try {
      netInfo = cm.getActiveNetworkInfo();
    } catch (Exception e) {
      // TODO
    }
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();

    if (isOnline()) {
      mAdView.setClickable(true);
      mAdView.setVisibility(View.VISIBLE);
    } else {
      mAdView.setClickable(false);
      mAdView.setVisibility(View.GONE);
    }
    if (WE_CAME_FROM_NEWTASK) {
      /** Advertisement */
      if (isOnline()) {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.on_start_ad));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
          @Override
          public void onAdLoaded() {
            if (mInterstitialAd.isLoaded()) {
              mInterstitialAd.show();
            }
          }

          @Override
          public void onAdClosed() {
            super.onAdClosed();
          }
        });
      }
      /** Advertisement */
      WE_CAME_FROM_NEWTASK = false;
    } else {
      WE_CAME_FROM_NEWTASK = false;
    }

    SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
    if (myPreference.getBoolean("permanentNotification", true)) {
      sendDefaultNotification();
    } else {
      cancelDefaultNotification();
    }
  }

  @Override
  public void onBackPressed() {

    if (TaskListFragment.ToolbarButtonBackIsPressed) {
      toolbar.inflateMenu(R.menu.menu_lists);
      TaskListFragment.ToolbarButtonBackIsPressed = false;
    }
    if (TaskListFragment.openedTasksByListClick) {
      drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
      toolbar.hideOverflowMenu();
      DrawerLayout drawer = findViewById(R.id.drawer_layout);
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
          R.string.navigation_drawer_open, R.string.navigation_drawer_close);
      toggle.syncState();

      TaskListFragment.openedTasksByListClick = false;

      OpenedLists();

      loadTaskListFragment();
      finishedListsAnimation = false;

      hideSpinnerAndSearchView();

      toolbar.setTitle(getString(R.string.lists_of_tasks));
      toolbar.setTitleTextColor(getResources().getColor(R.color.JustWhite));

      floatBtnToNewTask.setVisibility(View.GONE);
    } else {
      DrawerLayout drawer = findViewById(R.id.drawer_layout);
      if (drawer.isDrawerOpen(GravityCompat.START)) {
        drawer.closeDrawer(GravityCompat.START);
      } else {
        /** DIALOG EXIT */

        /** Advertisement */
        if (isOnline()) {
          final AdView mAdViewDialogExit;

          // Get view from dialog_new_list.xml, which will be applied to Dialog Window:
          LayoutInflater li = LayoutInflater.from(this);
          View viewDialogAdMob = li.inflate(R.layout.dialog_exit_admob, null);

          mAdViewDialogExit = viewDialogAdMob.findViewById(R.id.mAdViewDialogExit);
          mAdViewDialogExit.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
              mAdViewDialogExit.setClickable(true);
              mAdViewDialogExit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
              mAdViewDialogExit.setClickable(false);
              mAdViewDialogExit.setVisibility(View.GONE);
            }
          });
          AdRequest adRequest = new AdRequest.Builder()
              .build();
          mAdViewDialogExit.loadAd(adRequest);
          /** Advertisement */

          new AlertDialog.Builder(this)
              .setTitle(getString(R.string.do_u_want_to_exit))
              /** Advertisement */
              .setView(viewDialogAdMob)
              /** Advertisement */
              .setNegativeButton(R.string.no, null)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                  MainMenu.super.onBackPressed();
                }
              }).create().show();
        } else {
          new AlertDialog.Builder(this)
              .setTitle(getString(R.string.do_u_want_to_exit))
              .setNegativeButton(R.string.no, null)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                  MainMenu.super.onBackPressed();
                }
              }).create().show();
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it's present.
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private void hideSpinnerAndSearchView() {
    RL_Spinner_Search.setVisibility(View.GONE);
  }

  private void makeVisibleSpinnerAndSearchView() {
    RL_Spinner_Search.setVisibility(View.VISIBLE);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();

    TaskListFragment.openedTasksByListClick = false;

    if (id == R.id.nav_main) {
      if (!openedMain) {
        finishedMainAnimation = true;
        // Signal for TaskListFragment
        OpenedMain();
      }
    } else if (id == R.id.nav_lists) {
      if (!openedLists) {
        hideSpinnerAndSearchView();
        toolbar.getMenu().clear();
        floatBtnToNewTask.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.lists_of_tasks));
        finishedListsAnimation = true;
        // Signal for TaskListFragment
        OpenedLists();
      }
    } else if (id == R.id.nav_finished_tasks) {
      if (!openedFTasks) {
        hideSpinnerAndSearchView();
        toolbar.getMenu().clear();
        floatBtnToNewTask.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.finished_tasks));
        finishedFTasksAnimation = true;
        // Signal for TaskListFragment
        OpenedFTasks();
      }
    } else if (id == R.id.nav_settings) {
      if (!openedSettings) {
        hideSpinnerAndSearchView();
        toolbar.getMenu().clear();
        floatBtnToNewTask.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.title_activity_settings));
        finishedSettingsAnimation = true;
        // Signal for MyReferenceFragment
        OpenedSettings();
      }
    } else if (id == R.id.nav_about) {
      if (!openedAboutApp) {
        hideSpinnerAndSearchView();
        toolbar.getMenu().clear();
        floatBtnToNewTask.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.title_activity_about_app));
        finishedAboutAppAnimation = true;
        // Signal for AboutAppFragment Fragment
        OpenedAboutApp();
      }
    } else if (id == R.id.nav_themes) {
      finishedThemesAnimation = true;
    } else if (id == R.id.remove_ads) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(MARKET_URI));
      startActivity(intent);
    }

    item.setChecked(true);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void makeToast(Context context, CharSequence text, int duration, boolean vibration) {
    Toast toast = Toast.makeText(context, text, duration);
    toast.show();
    if (vibration) {
      makeVibration();
    }
  }

  private void showDialogAddNewList() {

    /** DIALOG ADD NEW LIST */
    showKeyboard();
    dbLists = new DataBaseLists(this);
    final SQLiteDatabase databaseLists = dbLists.getWritableDatabase();

    // Get view from dialog_new_list.xml, which will be applied to Dialog Window:
    LayoutInflater li = LayoutInflater.from(this);
    View viewDialogNewList = li.inflate(R.layout.dialog_new_list, null);

    final EditText editListName = viewDialogNewList.findViewById(R.id.editListName);

    final AlertDialog dialogAddList = new AlertDialog.Builder(this)
        .setView(viewDialogNewList)
        .setTitle(getString(R.string.new_list))
        .setPositiveButton(getString(R.string.add), null) //Set to null => we override onclick
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface arg0, int arg1) {
            showKeyboard();
          }
        })
        .create();

    dialogAddList.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String nameList = editListName.getEditableText().toString();

            String bufDivider = "";
            bufDivider += DataBaseLists.DIVIDER;

            if (DataBaseLists.checkListPresence(databaseLists, nameList)) {
              makeToast(MainMenu.this, getString(R.string.this_list_already_exist), Toast.LENGTH_SHORT, true);
            } else if (nameList.compareTo("") == 0) {
              makeToast(MainMenu.this, getString(R.string.enter_list_name), Toast.LENGTH_SHORT, true);
            } else if (nameList.contains(bufDivider)) {
              makeToast(MainMenu.this, "Character '" + DataBaseLists.DIVIDER + "' is invalid!", Toast.LENGTH_SHORT, true);
            } else {
              DataBaseLists.addToDBLists(databaseLists, nameList);

              dialogAddList.dismiss();

              showKeyboard();
              makeToast(MainMenu.this, getString(R.string.list_is_added), Toast.LENGTH_SHORT, false);
              loadTaskListFragment();
            }
          }
        });
      }
    });
    dialogAddList.show();
  }

  private void showDialogClearFinishedTasks() {
    if (isOnline()) {
      /** DIALOG Clear Finished Tasks */

      /** Advertisement */
      final AdView mAdViewDialogDelFTasks;

      // Get view from dialog_new_list.xml, which will be applied to Dialog Window:
      LayoutInflater li = LayoutInflater.from(this);
      View viewDialogAdMob = li.inflate(R.layout.dialog_delete_ftasks_admob, null);

      mAdViewDialogDelFTasks = viewDialogAdMob.findViewById(R.id.mAdViewDialogDelFTasks);
      mAdViewDialogDelFTasks.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
          mAdViewDialogDelFTasks.setClickable(true);
          mAdViewDialogDelFTasks.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
          mAdViewDialogDelFTasks.setClickable(false);
          mAdViewDialogDelFTasks.setVisibility(View.GONE);
        }

      });
      AdRequest adRequest = new AdRequest.Builder().build();
      mAdViewDialogDelFTasks.loadAd(adRequest);
      /** Advertisement */

      new AlertDialog.Builder(this)
          .setTitle(getString(R.string.delete_ftasks))
          /** Advertisement */
          .setView(viewDialogAdMob)
          /** Advertisement */
          .setNegativeButton(R.string.no, null)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
              DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
              SQLiteDatabase dbFinishedTasks = dbFTasks.getWritableDatabase();
              DataBaseFinishedTasks.clearDataBase(dbFinishedTasks);

              makeToast(MainMenu.this, getString(R.string.tasks_deleted), Toast.LENGTH_SHORT, false);
              loadTaskListFragment();
            }
          }).create().show();
    } else {
      new AlertDialog.Builder(this)
          .setTitle(getString(R.string.delete_ftasks))
          .setNegativeButton(R.string.no, null)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
              DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
              SQLiteDatabase dbFinishedTasks = dbFTasks.getWritableDatabase();
              DataBaseFinishedTasks.clearDataBase(dbFinishedTasks);

              makeToast(MainMenu.this, getString(R.string.tasks_deleted), Toast.LENGTH_SHORT, false);
              loadTaskListFragment();
            }
          }).create().show();
    }
  }

  public static void OpenedMain() {
    openedMain = true;
    openedLists = false;
    openedFTasks = false;
    openedSettings = false;
    openedAboutApp = false;
  }

  public static void OpenedLists() {
    openedMain = false;
    openedLists = true;
    openedFTasks = false;
    openedSettings = false;
    openedAboutApp = false;
  }

  public static void OpenedFTasks() {
    openedMain = false;
    openedLists = false;
    openedFTasks = true;
    openedSettings = false;
    openedAboutApp = false;
  }

  public static void OpenedSettings() {
    openedMain = false;
    openedLists = false;
    openedFTasks = false;
    openedSettings = true;
    openedAboutApp = false;
  }

  public static void OpenedAboutApp() {
    openedMain = false;
    openedLists = false;
    openedFTasks = false;
    openedSettings = false;
    openedAboutApp = true;
  }

  public void makeVibration() {
    long mills = 100L;
    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    try {
      vibrator.vibrate(mills);
    } catch (Exception e) {
      // TODO
    }
  }

  public void showKeyboard() {
    try {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    } catch (Exception e) {
      // TODO
    }
  }

  public void drawerOpenedOrClosed() {

    if (finishedMainAnimation) {
      setSpinnerLists();
      loadTaskListFragment();
      finishedMainAnimation = false;

      floatBtnToNewTask.setVisibility(View.VISIBLE);
      makeVisibleSpinnerAndSearchView();

      setSupportActionBar(toolbar);
      toolbar.hideOverflowMenu();
      DrawerLayout drawer = findViewById(R.id.drawer_layout);
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainMenu.this, drawer,
          toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        public void onDrawerClosed(View view) {
          super.onDrawerClosed(view);
          drawerOpenedOrClosed();
        }

        public void onDrawerOpened(View drawerView) {
          super.onDrawerOpened(drawerView);
          drawerOpenedOrClosed();
        }
      };
      drawer.addDrawerListener(toggle);
      toggle.syncState();

    } else if (finishedListsAnimation) {
      loadTaskListFragment();
      finishedListsAnimation = false;

      toolbar.inflateMenu(R.menu.menu_lists);
      toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
          if (item.getItemId() == R.id.item_add_new_list) {
            showDialogAddNewList();
          }
          return true;
        }
      });
    } else if (finishedFTasksAnimation) {
      loadTaskListFragment();
      finishedFTasksAnimation = false;

      toolbar.hideOverflowMenu();
      DrawerLayout drawer = findViewById(R.id.drawer_layout);
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainMenu.this, drawer,
          toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        public void onDrawerClosed(View view) {
          super.onDrawerClosed(view);
          drawerOpenedOrClosed();
        }

        public void onDrawerOpened(View drawerView) {
          super.onDrawerOpened(drawerView);
          drawerOpenedOrClosed();
        }
      };
      drawer.addDrawerListener(toggle);
      toggle.syncState();

      toolbar.inflateMenu(R.menu.menu_finished_tasks);
      toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

          if (item.getItemId() == R.id.item_clear_finished_tasks) {
            DataBaseFinishedTasks dbFTasks = new DataBaseFinishedTasks(MainMenu.this);
            SQLiteDatabase databaseFTasks = dbFTasks.getWritableDatabase();

            if (DataBaseFinishedTasks.getOnlyTasksWithArrayList(databaseFTasks).size() > 0) {
              showDialogClearFinishedTasks();
            } else {
              Toast.makeText(MainMenu.this, getString(R.string.tasks_deleted), Toast.LENGTH_SHORT).show();
              makeVibration();
            }
          }
          return true;
        }
      });
    } else if (finishedSettingsAnimation) {
      loadSettingsFragment();
      finishedSettingsAnimation = false;
    } else if (finishedAboutAppAnimation) {
      loadAboutAppFragment();
      finishedAboutAppAnimation = false;
    } else if (finishedThemesAnimation) {
      finishedThemesAnimation = false;
      Intent intent = new Intent(MainMenu.this, ThemesFragment.class);
      startActivity(intent);
    }
  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();

    if (!openedAboutApp) {
      if (!NewTask.wasPressedBack) {
        if (NewTask.wasSomeChangeWithSomeList) {
          setSpinnerLists();
          spinner.setSelection(lastSpinnerSelection, true);
          NewTask.wasSomeChangeWithSomeList = false;
        }
        if (!openedSettings) {
          if (!TaskListFragment.openedTasksByListClick) {
            loadTaskListFragment();
          } else if (TaskListFragment.openedTasksByListClick) {
            NeedUpdateTaskAdapterFromToolbarTitle = true;
            TitleToolbar = toolbar.getTitle().toString();
            loadTaskListFragment();
          }
        }
      } else {
        NewTask.wasPressedBack = false;
      }
    }
  }
}
