package first.project.nikzhebindev.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import first.project.nikzhebindev.organizer.databases.DataBaseFinishedTasks;
import first.project.nikzhebindev.organizer.databases.DataBaseLists;
import first.project.nikzhebindev.organizer.databases.DataBaseTasks;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;

/***
 * Main Fragment, which adapt for OnNavigationItemClickListener
 */
public class TaskListFragment extends ListFragment {

    public static boolean ToolbarButtonBackIsPressed = false;
    public static boolean openedTasksByListClick = false;

    MyTaskAdapter myTaskAdapter;
    MyListAdapter myListAdapter;
    MyFTasksAdapter myFTasksAdapter;

    public static boolean setPrimaryNewTask = false;
    public static String idF = "", taskF = "", dateF = "", timeF = "", repeatF = "", listF = "";

    DataBaseTasks dbTasks;
    SQLiteDatabase databaseTasks;
    DataBaseLists dbLists;
    SQLiteDatabase databaseLists;
    DataBaseFinishedTasks dbFTasks;
    SQLiteDatabase databaseFTasks;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (MainMenu.openedMain) {
            setTaskAdapter();
        } else if (MainMenu.openedLists) {
            setListAdapter();
        } else if (MainMenu.openedFTasks) {
            setFTasksAdapter();
        }
    }

    private void setTaskAdapter() {
        dbTasks = new DataBaseTasks(getActivity());
        databaseTasks = dbTasks.getWritableDatabase();

        if (MainMenu.NeedUpdateTaskAdapterFromToolbarTitle) {
            MainMenu.NeedUpdateTaskAdapterFromToolbarTitle = false;
            MainMenu.selectedSpinnerList = MainMenu.TitleToolbar;
        }
        if (MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0) {
            DataBaseTasks.DataArrayList dataArrayList = DataBaseTasks
                    .getSortedAllDataWithArrayList(databaseTasks);

            ArrayList<String> arrayTasks = dataArrayList.arrTasks;
            ArrayList<Long> longTasks = dataArrayList.arrTimeInMillis;
            ArrayList<String> arrayDate = dataArrayList.arrDate;
            ArrayList<String> arrayTime = dataArrayList.arrTime;
            ArrayList<String> arrayRepeat = dataArrayList.arrRepeat;
            ArrayList<String> arrayList = dataArrayList.arrList;

            myTaskAdapter = new MyTaskAdapter(getActivity(), R.layout.fragment_item_task,
                    arrayTasks, longTasks, arrayDate, arrayTime, arrayRepeat, arrayList);
            setListAdapter(myTaskAdapter);
        } else {
            DataBaseTasks.DataArrayList dataArrayList = DataBaseTasks
                    .getSortedAllDataByListWithArrayList(databaseTasks, MainMenu.selectedSpinnerList);

            ArrayList<String> arrayTasks = dataArrayList.arrTasks;
            ArrayList<Long> longTasks = dataArrayList.arrTimeInMillis;
            ArrayList<String> arrayDate = dataArrayList.arrDate;
            ArrayList<String> arrayTime = dataArrayList.arrTime;
            ArrayList<String> arrayRepeat = dataArrayList.arrRepeat;
            ArrayList<String> arrayList = dataArrayList.arrList;

            myTaskAdapter = new MyTaskAdapter(getActivity(), R.layout.fragment_item_task,
                    arrayTasks, longTasks, arrayDate, arrayTime, arrayRepeat, arrayList);
            setListAdapter(myTaskAdapter);
        }
    }

    private void setListAdapter() {
        dbLists = new DataBaseLists(getActivity());
        databaseLists = dbLists.getWritableDatabase();
        String[] strLists = DataBaseLists.readDBLists(databaseLists);

        ArrayList<String> arrayLists = new ArrayList<>();
        for (String list : strLists) {
            arrayLists.add(list);
        }
        myListAdapter = new MyListAdapter(getActivity(), R.layout.fragment_item_list, arrayLists);
        setListAdapter(myListAdapter);
    }

    private void setFTasksAdapter() {
        dbFTasks = new DataBaseFinishedTasks(getActivity());
        databaseFTasks = dbFTasks.getWritableDatabase();

        ArrayList<String> arrayFTasks = DataBaseFinishedTasks.getOnlyTasksWithArrayList(databaseFTasks);
        ArrayList<String> arrayFDate = DataBaseFinishedTasks.getOnlyDateWithArrayList(databaseFTasks);
        ArrayList<String> arrayFTime = DataBaseFinishedTasks.getOnlyTimeWithArrayList(databaseFTasks);

        myFTasksAdapter = new MyFTasksAdapter(getActivity(), R.layout.fragment_item_finished_task,
                arrayFTasks, arrayFDate, arrayFTime);
        setListAdapter(myFTasksAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, null);
    }

    public class MyTaskAdapter extends ArrayAdapter<String> {

        private ArrayList<String> arrayTasks;
        private ArrayList<Long> arrayTimeInMillis;
        private ArrayList<String> arrayDate;
        private ArrayList<String> arrayTime;
        private ArrayList<String> arrayRepeat;
        private ArrayList<String> arrayList;
        private LayoutInflater inflater;
        private int layout;

        private MyTaskAdapter(Context context, int textViewResourceId, ArrayList<String> objects,
                              ArrayList<Long> objTimeInMillis, ArrayList<String> objDate,
                              ArrayList<String> objTime, ArrayList<String> objRepeat,
                              ArrayList<String> objList) {
            super(context, textViewResourceId, objects);
            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
            this.arrayTasks = objects;
            this.arrayTimeInMillis = objTimeInMillis;
            this.arrayDate = objDate;
            this.arrayTime = objTime;
            this.arrayRepeat = objRepeat;
            this.arrayList = objList;
        }

        @Override
        @NonNull
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.btnLabel.setText(MyTaskAdapter.this.arrayTasks.get(position));
            viewHolder.btnLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] data = DataBaseTasks.getCertainTask(databaseTasks, ((Button) v).getText().toString());

                    taskF = data[0];
                    dateF = data[1];
                    timeF = data[2];
                    repeatF = data[3];
                    listF = data[4];
                    idF = data[5];

                    setPrimaryNewTask = true;
                    startActivity(new Intent(getContext(), NewTask.class));
                }
            });
            viewHolder.dateOfItemTask.setText(arrayDate.get(position));
            viewHolder.timeOfItemTask.setText(arrayTime.get(position));

            if (viewHolder.dateOfItemTask.getText().toString().compareTo(getString(R.string.no_date)) == 0) {
                viewHolder.dateOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
            } else if ((System.currentTimeMillis()) < arrayTimeInMillis.get(position)) {
                viewHolder.dateOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));
                viewHolder.timeOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));
            } else {
                viewHolder.dateOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));
                viewHolder.timeOfItemTask.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));
            }

            Calendar currentTime = Calendar.getInstance();
            Calendar establishedTime = Calendar.getInstance();
            establishedTime.setTimeInMillis(arrayTimeInMillis.get(position));

            // Set Type of Tasks
            if (position == 0) {

                if (arrayDate.get(position).compareTo(getString(R.string.no_date)) == 0) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.no_date));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));

                } else if (currentTime.get(Calendar.DAY_OF_YEAR) == establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.today));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentTime.get(Calendar.DAY_OF_YEAR) + 1) == establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentTime.get(Calendar.DAY_OF_YEAR) + 2) == establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.day_after_tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentTime.get(Calendar.DAY_OF_YEAR) + 2) < establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.MONTH) == establishedTime.get(Calendar.MONTH)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (((((currentTime.get(Calendar.MONTH) + 1) == (establishedTime.get(Calendar.MONTH)))
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        || (((currentTime.get(Calendar.MONTH) + 1) == 12 && (establishedTime.get(Calendar.MONTH)) == 0)
                        && currentTime.get(Calendar.YEAR) == (establishedTime.get(Calendar.YEAR) + 1)))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.next_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR)
                        && ((currentTime.get(Calendar.MONTH) + 1) < (establishedTime.get(Calendar.MONTH)))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_year));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentTime.get(Calendar.YEAR) != establishedTime.get(Calendar.YEAR)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.later));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentTime.getTimeInMillis() > arrayTimeInMillis.get(position)) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.overdue));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));

                } else {
                    viewHolder.typeTasks.setVisibility(View.GONE);
                }
            } else {
                Calendar establishedCalendarPrevious = Calendar.getInstance();
                establishedCalendarPrevious.setTimeInMillis(arrayTimeInMillis.get(position - 1));

                if (arrayDate.get(position).compareTo(getString(R.string.no_date)) == 0 && arrayDate.get(position - 1).compareTo(getString(R.string.no_date)) != 0) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.no_date));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));

                } else if ((currentTime.get(Calendar.DAY_OF_YEAR) == establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        && !(currentTime.get(Calendar.DAY_OF_YEAR) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.today));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (((currentTime.get(Calendar.DAY_OF_YEAR) + 1) == establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        && !((currentTime.get(Calendar.DAY_OF_YEAR) + 1) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (((currentTime.get(Calendar.DAY_OF_YEAR) + 2) == establishedTime.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        && !((currentTime.get(Calendar.DAY_OF_YEAR) + 2) == establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.day_after_tomorrow));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (((currentTime.get(Calendar.DAY_OF_YEAR) + 2) < establishedTime.get(Calendar.DAY_OF_YEAR)
                        && ((currentTime.get(Calendar.MONTH)) == (establishedTime.get(Calendar.MONTH)))
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        && !((currentTime.get(Calendar.MONTH) + 1) == (establishedTime.get(Calendar.MONTH)))
                        && !((currentTime.get(Calendar.DAY_OF_YEAR) + 2) < establishedCalendarPrevious.get(Calendar.DAY_OF_YEAR)
                        && currentTime.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (((((currentTime.get(Calendar.MONTH) + 1) == (establishedTime.get(Calendar.MONTH)))
                        && currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        || (((currentTime.get(Calendar.MONTH)) == Calendar.DECEMBER && (establishedTime.get(Calendar.MONTH)) == Calendar.JANUARY)
                        && currentTime.get(Calendar.YEAR) == (establishedTime.get(Calendar.YEAR) + 1)))
                        && !((((currentTime.get(Calendar.MONTH) + 1) == (establishedCalendarPrevious.get(Calendar.MONTH)))
                        && currentTime.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))
                        || (((currentTime.get(Calendar.MONTH)) == Calendar.DECEMBER && (establishedCalendarPrevious.get(Calendar.MONTH)) == Calendar.JANUARY)
                        && currentTime.get(Calendar.YEAR) == (establishedCalendarPrevious.get(Calendar.YEAR) + 1)))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.next_month));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentTime.get(Calendar.YEAR) == establishedTime.get(Calendar.YEAR))
                        && (currentTime.get(Calendar.MONTH) + 1) < establishedTime.get(Calendar.MONTH)
                        && !((currentTime.get(Calendar.MONTH) + 1) < establishedCalendarPrevious.get(Calendar.MONTH))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.this_year));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if ((currentTime.get(Calendar.YEAR) != establishedTime.get(Calendar.YEAR))
                        && (currentTime.get(Calendar.YEAR) == establishedCalendarPrevious.get(Calendar.YEAR))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.later));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));

                } else if (currentTime.getTimeInMillis() > arrayTimeInMillis.get(position)
                        && !(currentTime.getTimeInMillis() > arrayTimeInMillis.get(position - 1))) {

                    viewHolder.typeTasks.setVisibility(View.VISIBLE);
                    viewHolder.typeTasks.setText(getString(R.string.overdue));
                    viewHolder.typeTasks.setTextColor(getActivity().getResources().getColor(R.color.OverdueColor));

                } else {
                    viewHolder.typeTasks.setVisibility(View.GONE);
                }
            }

            if (MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) == 0 && !openedTasksByListClick) {
                final CharSequence text = DataBaseTasks.getCertainTask(databaseTasks, viewHolder.btnLabel.getText().toString())[4];
                viewHolder.listOfItemTask.setText(text);
            }
            viewHolder.check.setTag(R.id.textView_list_of_itemTask, viewHolder.listOfItemTask.getText().toString());
            viewHolder.check.setTag(viewHolder.btnLabel.getText().toString());
            viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                    if (b) {
                        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(getContext());

                        if (myPreference.getBoolean("confirmFTasks", true)) {

                            // <!-- DIALOG "Finish this task?" -->
                            new AlertDialog.Builder(getContext())
                                    .setTitle(getString(R.string.finish_task))
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            compoundButton.setChecked(false);
                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            compoundButton.setChecked(false);
                                        }
                                    })
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();
                                            DataBaseTasks.deleteCertainTask(databaseTasks, databaseFinishedTasks,
                                                    compoundButton.getTag().toString(), getContext());

                                            Toast.makeText(getContext(), getString(R.string.task_finished), Toast.LENGTH_SHORT).show();

                                            SharedPreferences myPreference2 = PreferenceManager.getDefaultSharedPreferences(getContext());
                                            if (myPreference2.getBoolean("permanentNotification", true)) {
                                                ((MainMenu) getContext()).sendDefaultNotification();
                                            } else {
                                                ((MainMenu) getContext()).cancelDefaultNotification();
                                            }

                                            ((MainMenu) getContext()).sizeAllTasks--;
                                            ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();

                                            if (openedTasksByListClick) {
                                                String list = ((MainMenu) getContext()).toolbar.getTitle().toString();

                                                DataBaseLists dbListsssss = new DataBaseLists(getContext());
                                                SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();

                                                String[] lists = DataBaseLists.readDBLists(databaseli);
                                                int index = 0;
                                                for (int i = 0; i < lists.length; i++) {
                                                    if (lists[i].compareTo(list) == 0) {
                                                        index = i + 1;
                                                        break;
                                                    }
                                                }
                                                ((MainMenu) getContext()).listsSize.set(index, (((MainMenu) getContext()).listsSize.get(index) - 1));
                                                ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();

                                            } else {
                                                if (MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) != 0) {
                                                    ((MainMenu) getContext()).listsSize.set(MainMenu.lastSpinnerSelection, (((MainMenu) getContext()).listsSize.get(MainMenu.lastSpinnerSelection) - 1));
                                                    ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();
                                                } else {
                                                    String list = compoundButton.getTag(R.id.textView_list_of_itemTask).toString();

                                                    DataBaseLists dbListsssss = new DataBaseLists(getContext());
                                                    SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();

                                                    String[] lists = DataBaseLists.readDBLists(databaseli);
                                                    int index = 0;
                                                    for (int i = 0; i < lists.length; i++) {
                                                        if (lists[i].compareTo(list) == 0) {
                                                            index = i + 1;
                                                            break;
                                                        }
                                                    }
                                                    ((MainMenu) getContext()).listsSize.set(index, (((MainMenu) getContext()).listsSize.get(index) - 1));
                                                    ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();
                                                }
                                            }

                                            /** ANIMATION */
                                            arrayTasks.remove(position);
                                            arrayDate.remove(position);
                                            arrayTime.remove(position);
                                            arrayRepeat.remove(position);
                                            arrayTimeInMillis.remove(position);
                                            myTaskAdapter.notifyDataSetChanged();
                                        }
                                    }).create().show();
                            // <!-- DIALOG "Finish this task?" -->
                        } else {
                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                            DataBaseTasks.deleteCertainTask(databaseTasks, databaseFinishedTasks,
                                    compoundButton.getTag().toString(), getContext());
                            Toast.makeText(getContext(), getString(R.string.task_finished), Toast.LENGTH_SHORT).show();

                            SharedPreferences myPreference2 = PreferenceManager.getDefaultSharedPreferences(getContext());
                            if (myPreference2.getBoolean("permanentNotification", true)) {
                                ((MainMenu) getContext()).sendDefaultNotification();
                            } else {
                                ((MainMenu) getContext()).cancelDefaultNotification();
                            }
                            ((MainMenu) getContext()).sizeAllTasks--;
                            ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();

                            if (openedTasksByListClick) {
                                String list = ((MainMenu) getContext()).toolbar.getTitle().toString();

                                DataBaseLists dbListsssss = new DataBaseLists(getContext());
                                SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();

                                String[] lists = DataBaseLists.readDBLists(databaseli);
                                int index = 0;
                                for (int i = 0; i < lists.length; i++) {
                                    if (lists[i].compareTo(list) == 0) {
                                        index = i + 1;
                                        break;
                                    }
                                }
                                ((MainMenu) getContext()).listsSize.set(index, (((MainMenu) getContext()).listsSize.get(index) - 1));
                                ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();

                            } else {
                                if (MainMenu.selectedSpinnerList.compareTo(MainMenu.KEY_ALL_TASKS) != 0) {
                                    ((MainMenu) getContext()).listsSize.set(MainMenu.lastSpinnerSelection, (((MainMenu) getContext()).listsSize.get(MainMenu.lastSpinnerSelection) - 1));
                                    ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();
                                } else {
                                    String list = compoundButton.getTag(R.id.textView_list_of_itemTask).toString();

                                    DataBaseLists dbListsssss = new DataBaseLists(getContext());
                                    SQLiteDatabase databaseli = dbListsssss.getWritableDatabase();

                                    String[] lists = DataBaseLists.readDBLists(databaseli);
                                    int index = 0;
                                    for (int i = 0; i < lists.length; i++) {
                                        if (lists[i].compareTo(list) == 0) {
                                            index = i + 1;
                                            break;
                                        }
                                    }
                                    ((MainMenu) getContext()).listsSize.set(index, (((MainMenu) getContext()).listsSize.get(index) - 1));
                                    ((MainMenu) getContext()).adapterSpinner.notifyDataSetChanged();
                                }
                            }
                            arrayTasks.remove(position);
                            arrayDate.remove(position);
                            arrayTime.remove(position);
                            arrayRepeat.remove(position);
                            arrayTimeInMillis.remove(position);
                            myTaskAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

            viewHolder.check.setChecked(false);

            if (arrayRepeat.get(position).compareTo(DataBaseLists.listRepeatNames[0]) == 0) {
                viewHolder.imageViewRepeat.setVisibility(View.GONE);
            } else {
                viewHolder.imageViewRepeat.setVisibility(View.VISIBLE);
            }

            /** BEST ANIMATION OF APPEARANCE OF LIST ITEMS EVER !!!!!!!!!!!!!!!!!!! */
            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            alpha.setDuration(400); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            convertView.startAnimation(alpha);
            return convertView;
        }

        private class ViewHolder {

            public boolean needInflate;
            final Button btnLabel;
            final CheckBox check;
            final TextView dateOfItemTask;
            final TextView timeOfItemTask;
            final TextView typeTasks;
            final ImageView imageViewRepeat;
            final TextView listOfItemTask;

            public ViewHolder(View v) {
                btnLabel = v.findViewById(R.id.label);
                check = v.findViewById(R.id.check);
                dateOfItemTask = v.findViewById(R.id.textView_date_of_itemTask);
                timeOfItemTask = v.findViewById(R.id.textView_time_of_itemTask);
                typeTasks = v.findViewById(R.id.textView_type_tasks);
                imageViewRepeat = v.findViewById(R.id.imageView_item_repeat);
                listOfItemTask = v.findViewById(R.id.textView_list_of_itemTask);
            }
        }
    }

    public class MyListAdapter extends ArrayAdapter<String> {

        private ArrayList<String> arrayLists;
        private LayoutInflater inflater;
        private int layout;

        private MyListAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.arrayLists = objects;
            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder); // save to Tag
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            dbTasks = new DataBaseTasks(getContext());
            databaseTasks = dbTasks.getWritableDatabase();

            String num = Integer.toString(DataBaseTasks.getAmountOfTasksByList(databaseTasks, MyListAdapter.this.arrayLists.get(position)));
            if (num.compareTo("0") == 0 || num.compareTo("null") == 0) {
                viewHolder.amountOfTasks.setText(getString(R.string.no_tasks));
                viewHolder.amountOfTasks.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
            } else {
                String newNum = getString(R.string.amount_list_tasks) + " " + num;
                viewHolder.amountOfTasks.setText(newNum);
                viewHolder.amountOfTasks.setTextColor(getActivity().getResources().getColor(R.color.PrimaryColor));
            }

            viewHolder.btnLabelList.setText(MyListAdapter.this.arrayLists.get(position));
            viewHolder.btnLabelList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbTasks = new DataBaseTasks(getContext());
                    databaseTasks = dbTasks.getWritableDatabase();

                    if (DataBaseTasks.getAmountOfTasksByList(databaseTasks, ((Button) v).getText().toString()) == 0) {
                        Toast.makeText(getContext(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    } else {
                        final Activity mainMenu = ((MainMenu) getContext());

                        ((MainMenu) getContext()).toolbar.getMenu().clear();

                        /** Toolbar Button BACK */
                        ((MainMenu) getContext()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        ((MainMenu) getContext()).toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
                        ((MainMenu) getContext()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ((MainMenu) getContext()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                ToolbarButtonBackIsPressed = true;
                                mainMenu.onBackPressed();

                            }
                        });

                        ((MainMenu) getContext()).toolbar.setTitle(((Button) v).getText().toString());
                        ((MainMenu) getContext()).toolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.JustWhite));

                        openedTasksByListClick = true;
                        MainMenu.OpenedMain();
                        // way to open tasks by click on list
                        MainMenu.selectedSpinnerList = ((Button) v).getText().toString();
                        setTaskAdapter();
                    }
                }
            });
            viewHolder.imageBtnDeleteList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbLists = new DataBaseLists(getContext());
                    databaseLists = dbLists.getWritableDatabase();
                    dbTasks = new DataBaseTasks(getContext());
                    databaseTasks = dbTasks.getWritableDatabase();

                    boolean DefaultList = false;

                    // IF PRESSED DEFAULT LIST BUTTON
                    for (int i = 0; i < DataBaseLists.defaultListsNames.length; i++) {
                        if (DataBaseLists.defaultListsNames[i].compareTo(arrayLists.get(position)) == 0) {
                            Toast.makeText(getContext(), getString(R.string.u_cant_delete_this_list), Toast.LENGTH_SHORT).show();
                            DefaultList = true;
                        }
                    }
                    if (!DefaultList) {

                        if (((MainMenu) getContext()).isOnline()) {
                            /** /////////////////////////// Advertisement /////////////////////////// */
                            final AdView mAdViewDialogDeleteList;

                            //Получаем вид с файла dialog_new_list.xml, который применим для диалогового окна:
                            LayoutInflater li = LayoutInflater.from(getContext());
                            View viewDialogAdMob = li.inflate(R.layout.dialog_del_list_admob, null);

                            mAdViewDialogDeleteList = viewDialogAdMob.findViewById(R.id.mAdViewDialogDeleteList);
                            mAdViewDialogDeleteList.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    mAdViewDialogDeleteList.setClickable(true);
                                    mAdViewDialogDeleteList.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    mAdViewDialogDeleteList.setClickable(false);
                                    mAdViewDialogDeleteList.setVisibility(View.GONE);
                                }
                            });
                            AdRequest adRequest = new AdRequest.Builder().build();
                            mAdViewDialogDeleteList.loadAd(adRequest);
                            /** /////////////////////////// Advertisement /////////////////////////// */

                            /** DIALOG ARE U SURE? */
                            new AlertDialog.Builder(getContext())
                                    .setTitle(getString(R.string.list) + " " + "\"" + arrayLists.get(position) + "\"" + " " + getString(R.string.will_be_deleted))
                                    /** /////////////////////////// Advertisement /////////////////////////// */
                                    .setView(viewDialogAdMob)
                                    /** /////////////////////////// Advertisement /////////////////////////// */
                                    .setNegativeButton(R.string.no, null)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            String list = arrayLists.get(position);
                                            Toast.makeText(getContext(), getString(R.string.list) + " " + "\"" + list + "\"" + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();

                                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                            DataBaseLists.deleteList(databaseLists, list);
                                            DataBaseTasks.deleteTasksByList(databaseTasks, databaseFinishedTasks, list, getContext());

                                            /**  W/O ANIMATION  */
                                            arrayLists.remove(position);
                                            myListAdapter.notifyDataSetChanged();

                                            arg0.cancel();
                                        }
                                    }).create().show();
                            /** DIALOG ARE U SURE? */
                        } else {
                            /** DIALOG ARE U SURE? */
                            new AlertDialog.Builder(getContext())
                                    .setTitle(getString(R.string.list) + " " + "\"" + arrayLists.get(position) + "\"" + " " + getString(R.string.will_be_deleted))
                                    //.setMessage("Список \"" + arrayLists.get(position) + "\" будет удален.") ТУТ БУДЕТ РЕКЛАМА
                                    .setNegativeButton(R.string.no, null)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            String list = arrayLists.get(position);
                                            Toast.makeText(getContext(), getString(R.string.list) + " " + "\"" + list + "\"" + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();

                                            DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                            SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                            DataBaseLists.deleteList(databaseLists, list);
                                            DataBaseTasks.deleteTasksByList(databaseTasks, databaseFinishedTasks, list, getContext());

                                            /**  W/O ANIMATION  */
                                            arrayLists.remove(position);
                                            myListAdapter.notifyDataSetChanged();

                                            arg0.cancel();
                                        }
                                    }).create().show();
                            /** DIALOG ARE U SURE? */
                        }
                    }
                }
            });

            viewHolder.imageBtnRenameList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbLists = new DataBaseLists(getContext());
                    databaseLists = dbLists.getWritableDatabase();
                    dbTasks = new DataBaseTasks(getContext());
                    databaseTasks = dbTasks.getWritableDatabase();

                    boolean DefaultList = false;

                    // IF PRESSED DEFAULT LIST BUTTON
                    for (int i = 0; i < DataBaseLists.defaultListsNames.length; i++) {
                        if (DataBaseLists.defaultListsNames[i].compareTo(arrayLists.get(position)) == 0) {
                            Toast.makeText(getContext(), getString(R.string.u_cant_rename_this_list), Toast.LENGTH_SHORT).show();
                            DefaultList = true;
                        }
                    }
                    if (!DefaultList) {
                        // Get view from dialog_new_list.xml, which will be applied to dialog window:
                        LayoutInflater li = LayoutInflater.from(getContext());
                        View viewDialogNewList = li.inflate(R.layout.dialog_new_list, null);

                        final EditText editListName = viewDialogNewList.findViewById(R.id.editListName);

                        editListName.setText(viewHolder.btnLabelList.getText().toString());
                        editListName.setSelection(editListName.getText().length());

                        /** DIALOG ADD NEW LIST */
                        final AlertDialog dialogAddList = new AlertDialog.Builder(getContext())
                                .setView(viewDialogNewList)
                                .setTitle(getString(R.string.rename_list_name))
                                .setPositiveButton(getString(R.string.rename), null) //Set to null => We override the onclick
                                .setNegativeButton(getString(R.string.cancel), null)
                                .create();
                        ((MainMenu) getContext()).showKeyboard();
                        dialogAddList.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Button button2 = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((MainMenu) getContext()).showKeyboard();
                                        dialogAddList.dismiss();
                                    }
                                });
                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final String nameList = editListName.getEditableText().toString();

                                        String bufDivider = "";
                                        bufDivider += DataBaseLists.DIVIDER;

                                        if (DataBaseLists.checkListPresence(databaseLists, nameList)) {
                                            Toast toast = Toast.makeText(getContext(), getString(R.string.this_list_already_exist), Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (nameList.compareTo("") == 0) {
                                            Toast toast = Toast.makeText(getContext(), getString(R.string.enter_list_name), Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else if (nameList.contains(bufDivider)) {
                                            Toast toast = Toast.makeText(getContext(), "Character '" + DataBaseLists.DIVIDER + "' is not valid!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else {
                                            ((MainMenu) getContext()).showKeyboard();
                                            DataBaseLists.renameList(databaseLists, arrayLists.get(position), nameList);
                                            DataBaseTasks.renameTasksByList(databaseTasks, arrayLists.get(position), nameList);

                                            dialogAddList.dismiss();

                                            Toast toast = Toast.makeText(getContext(), getString(R.string.renamed), Toast.LENGTH_SHORT);
                                            toast.show();

                                            arrayLists.set(position, nameList);
                                            myListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        });
                        dialogAddList.show();
                        /** DIALOG ADD NEW LIST */
                    }
                }
            });

            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            alpha.setDuration(450); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            convertView.startAnimation(alpha);
            return convertView;
        }

        private class ViewHolder {
            final Button btnLabelList;
            final ImageButton imageBtnDeleteList;
            final ImageButton imageBtnRenameList;
            final TextView amountOfTasks;

            ViewHolder(View v) {
                btnLabelList = v.findViewById(R.id.labelList);
                imageBtnDeleteList = v.findViewById(R.id.image_btn_delete_list);
                imageBtnRenameList = v.findViewById(R.id.image_btn_rename_list);
                amountOfTasks = v.findViewById(R.id.textView_AmountOfTasks_listItem);
            }
        }
    }

    public class MyFTasksAdapter extends ArrayAdapter<String> {

        private ArrayList<String> arrayFTasks;
        private ArrayList<String> arrayFDate;
        private ArrayList<String> arrayFTime;
        private LayoutInflater inflater;
        private int layout;

        private MyFTasksAdapter(Context context, int textViewResourceId, ArrayList<String> objects,
                                ArrayList<String> objFDate, ArrayList<String> objFTime) {
            super(context, textViewResourceId, objects);
            this.arrayFTasks = objects;
            this.arrayFDate = objFDate;
            this.arrayFTime = objFTime;
            this.layout = textViewResourceId;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                // FOR ANIMATION
            } else if (((MyFTasksAdapter.ViewHolder) convertView.getTag()).needInflate) {
                convertView = inflater.inflate(this.layout, parent, false);
                viewHolder = new MyFTasksAdapter.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.btnLabelFinished.setText(MyFTasksAdapter.this.arrayFTasks.get(position));
            viewHolder.btnLabelFinished.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    /** DIALOG "Return this task?" */
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.resume_task))
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    // TODO: implement!
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // TODO: implement!
                                }
                            })
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    DataBaseFinishedTasks dbFinishedTasks = new DataBaseFinishedTasks(getContext());
                                    SQLiteDatabase databaseFinishedTasks = dbFinishedTasks.getWritableDatabase();

                                    dbTasks = new DataBaseTasks(getContext());
                                    databaseTasks = dbTasks.getWritableDatabase();

                                    dbLists = new DataBaseLists(getContext());
                                    databaseLists = dbLists.getWritableDatabase();

                                    DataBaseFinishedTasks.deleteCertainFinishedTaskAndAddToDBTasks(
                                            databaseFinishedTasks, databaseTasks, databaseLists, ((Button) v).getText().toString(), getContext());

                                    Toast.makeText(getActivity(), getString(R.string.resumed), Toast.LENGTH_SHORT).show();

                                    dbTasks = new DataBaseTasks(getContext());
                                    databaseTasks = dbTasks.getWritableDatabase();

                                    /**  W/O ANIMATION  */
                                    arrayFTasks.remove(position);
                                    arrayFDate.remove(position);
                                    arrayFTime.remove(position);
                                    myFTasksAdapter.notifyDataSetChanged();
                                }
                            }).create().show();
                    /** DIALOG "Return this task?" */
                }
            });

            viewHolder.dateOfItemTask.setText(arrayFDate.get(position));
            viewHolder.timeOfItemTask.setText(arrayFTime.get(position));

            viewHolder.listOfItemTask.setText(DataBaseFinishedTasks.getCertainFinishedTask(databaseFTasks, viewHolder.btnLabelFinished.getText().toString())[4]);

            viewHolder.checkFinished.setChecked(true);
            viewHolder.checkFinished.setClickable(false);

            if (DataBaseFinishedTasks.getCertainFinishedTask(databaseFTasks,
                    MyFTasksAdapter.this.arrayFTasks.get(position))[3]
                    .compareTo(DataBaseLists.listRepeatNames[0]) == 0) {
                viewHolder.imageViewRepeat.setVisibility(View.GONE);
            } else {
                viewHolder.imageViewRepeat.setVisibility(View.VISIBLE);
            }

            AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
            alpha.setDuration(450); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            convertView.startAnimation(alpha);
            return convertView;
        }

        private class ViewHolder {

            public boolean needInflate;
            final Button btnLabelFinished;
            final CheckBox checkFinished;
            final TextView dateOfItemTask;
            final TextView timeOfItemTask;
            final TextView listOfItemTask;
            final ImageView imageViewRepeat;

            ViewHolder(View v) {
                btnLabelFinished = v.findViewById(R.id.labelFinished);
                checkFinished = v.findViewById(R.id.checkFinished);
                dateOfItemTask = v.findViewById(R.id.textView_date_of_itemFinishedTask);
                timeOfItemTask = v.findViewById(R.id.textView_time_of_itemFinishedTask);
                listOfItemTask = v.findViewById(R.id.textView_list_of_itemFinishedTask);
                imageViewRepeat = v.findViewById(R.id.imageView_item_finished_repeat);
            }
        }
    }
}
