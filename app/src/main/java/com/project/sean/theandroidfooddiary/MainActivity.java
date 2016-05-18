package com.project.sean.theandroidfooddiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sean.theandroidfooddiary.Database.FoodDiary;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FoodDiaryDBHelper dbHelper;
    //
    private ListView mDrawerList;
    //
    private ArrayAdapter<String> mAdapter;

    //
    private ActionBarDrawerToggle mDrawerToggle;
    //Layout for the menu drawer
    private DrawerLayout mDrawerLayout;
    //Title of the menu drawer
    private String mActivityTitle;

    //TextView to display the current date
    private TextView date_text_view;

    //ListView to display the Food Diary
    private ListView lv_food_diary_list;

    //Data for the Food Diary ListView
    private ArrayList<FoodDiary> diaryResult;

    //Instance of the adapter class for the Food Diary ListView
    private FoodDiaryAdapter foodAdapter;

    //Current day, month and year of the selected date
    private Calendar currentSelectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        //Create the items for the menu
        addDrawerItems();
        //Set the draw menu up
        setUpDrawer();

        //Retrieve a reference to this activity's ActionBar.
        //Set home to be displayed as an "up" affordance.
        //Set to true if selecting "home" returns up by a single level instead of top
        //level or front page.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Enable or disable the "home" button in the corner of the aciton bar.
        getSupportActionBar().setHomeButtonEnabled(true);



        //ListView of diary items
        lv_food_diary_list = (ListView) findViewById(R.id.lv_food_diary_list);

        //Get todays date and set the MainActivity on launch to it
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Gets the day of the week, EEEE is for the long name
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        weekDay = dayFormat.format(c.getTime());

        //Gets the month of the year, MM is displays "05" for May
        String monthNumber;
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        monthNumber = monthFormat.format(c.getTime());

        String currentDate = weekDay + " " + day + "/" + monthNumber + "/" + year;

        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        currentSelectDate = c;

        date_text_view = (TextView) findViewById(R.id.date_text_view);
        date_text_view.setText(currentDate);

        diaryResult = new ArrayList<>();
//        diaryResult.addAll(dbHelper.getAllFoodDiaryEntries(c.getTimeInMillis()));
//        foodAdapter = new FoodDiaryAdapter(getApplicationContext(), R.layout.listview_food_diary, diaryResult);
//        lv_food_diary_list.setAdapter(foodAdapter);

        //Store time in the DB as milliseconds
        if(dbHelper.exsists(currentSelectDate.getTimeInMillis())) {
            diaryResult.clear();
            diaryResult.addAll(dbHelper.getAllFoodDiaryEntries(currentSelectDate.getTimeInMillis()));
            foodAdapter = new FoodDiaryAdapter(getApplicationContext(), R.layout.listview_food_diary, diaryResult);
            lv_food_diary_list.setAdapter(foodAdapter);
        } else {
            diaryResult.clear();
            foodAdapter = new FoodDiaryAdapter(getApplicationContext(), R.layout.listview_food_diary, diaryResult);
            lv_food_diary_list.setAdapter(foodAdapter);
        }

        registerForContextMenu(lv_food_diary_list);

        //FloatingActionButton to add a new entry into the diary.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDiaryEntryActivity.class);
                intent.putExtra("calendar", currentSelectDate);
                startActivity(intent);
            }
        });
    }

    /**
     * Sync the indicator to match the current state of the navigation drawer
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Sync when the configuration of the Activity changes, e.g. from portait ro landscape
     * or showing the soft keyboard on screen.
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Inflates the menu, adds items to the action bar if it is present.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will automatically
     * handle clicks on the Home/Up button, so long as you specify a parent
     * activity in AndroidManifest.xml/
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_date_pick) {
            showDatePickerDialog();
            return true;
        }

        //Activate the navigation drawer toggler
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * To add items and configure the list for the drawer navigation
     */
    private void addDrawerItems() {
        Resources res = getResources();
        String[] navArray = res.getStringArray(R.array.nav_menu_array);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, FoodLibraryActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(MainActivity.this, FoodSearchActivity.class);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Sets up the navigation drawer.
     */
    private void setUpDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Sets title when drawer is open
                getSupportActionBar().setTitle("Main Menu");
                //Invalidate the options menu in case it needs to be recreated with different
                //options for when the navigation drawer is open
                invalidateOptionsMenu();//Creates call to onPrepareOtionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //Sets title when drawer is closed
                getSupportActionBar().setTitle(mActivityTitle);
                //Invalidate the options menu in case it needs to be recreated with different
                //options for when the navigation drawer is open
                invalidateOptionsMenu(); //Creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Displays the date picker dialog for the user to select a date.
     */
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Populates the list view with the diary entries from the given date.
     * @param cal
     */
    public void diaryList(Calendar cal) {

        currentSelectDate = cal;
        //Gets day of the week
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dayFormat.format(currentSelectDate.getTime());

        //Gets the month of the year, MM is displays "05" for May
        String monthNumber;
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        monthNumber = monthFormat.format(currentSelectDate.getTime());

        String chosenDate = dayOfWeek + " "+ currentSelectDate.get(Calendar.DAY_OF_MONTH) + "/"+ monthNumber + "/" + currentSelectDate.get(Calendar.YEAR);
        date_text_view.setText(chosenDate);

        Log.d("TIME MILLI: ", String.valueOf(cal.getTimeInMillis()));

        //Store time in the DB as milliseconds
        if(dbHelper.exsists(currentSelectDate.getTimeInMillis())) {
            diaryResult.clear();
            diaryResult.addAll(dbHelper.getAllFoodDiaryEntries(currentSelectDate.getTimeInMillis()));
            foodAdapter.notifyData(dbHelper.getAllFoodDiaryEntries(currentSelectDate.getTimeInMillis()));
            foodAdapter.notifyDataSetChanged();
        } else {
            diaryResult.clear();
            foodAdapter.notifyData(diaryResult);
            foodAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Custom Adapter class to handle the ListView for the diary.
     */
    public class FoodDiaryAdapter extends ArrayAdapter<FoodDiary> {

        private ArrayList<FoodDiary> foodDiaryList;

        public FoodDiaryAdapter(Context context, int resource, ArrayList<FoodDiary> diaryList) {
            super(context, resource, diaryList);
            this.foodDiaryList = new ArrayList<FoodDiary>();
            this.foodDiaryList.addAll(diaryList);
        }

        private class ViewHolder {
            TextView tv_food_diary_time;
            TextView tv_food_diary_name;
            TextView tv_food_diary_note;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listview_food_diary, null);

                holder = new ViewHolder();

                holder.tv_food_diary_time = (TextView) convertView.findViewById(R.id.tv_food_diary_time);
                holder.tv_food_diary_name = (TextView) convertView.findViewById(R.id.tv_food_diary_name);
                holder.tv_food_diary_note = (TextView) convertView.findViewById(R.id.tv_food_diary_note);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Calendar diaryTime = Calendar.getInstance();
            diaryTime.clear();
            diaryTime.set(Calendar.HOUR, foodDiaryList.get(position).getHour());
            diaryTime.set(Calendar.MINUTE, foodDiaryList.get(position).getMinute());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = timeFormat.format(diaryTime.getTime());

            //String time = foodDiaryList.get(position).getHour() + ":" + foodDiaryList.get(position).getMinute();

            holder.tv_food_diary_time.setText(time);
            holder.tv_food_diary_name.setText(foodDiaryList.get(position).getFoodItem());
            holder.tv_food_diary_note.setText(foodDiaryList.get(position).getFoodNote());

            return convertView;
        }

        public void notifyData(ArrayList<FoodDiary> foodDiary) {
            foodDiaryList.clear();
            foodDiaryList.addAll(foodDiary);
            notifyDataSetChanged();
        }

    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent date) {
        super.onActivityResult(requestCode, resultCode, date);
        if(requestCode == 1) {
            currentSelectDate = (Calendar) getIntent().getSerializableExtra("calendar");
            diaryList(currentSelectDate);
        } else if(requestCode == 2) {
            currentSelectDate =(Calendar) getIntent().getSerializableExtra("calendar");
            diaryList(currentSelectDate);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lv_food_diary_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(diaryResult.get(info.position).getFoodItem().toString());
            String[] menuItems = getResources().getStringArray(R.array.diary_context);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.diary_context);
        String menuItemName = menuItems[menuItemIndex];
        //String listItemName = shoppingCart.getCartItems().get(info.position - 1).getCartItem().getStockName();

        switch(menuItemName) {
            case "Update": {
                Toast.makeText(this, "Update pressed.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EditDiaryEntryActivity.class);
                intent.putExtra("calendar", currentSelectDate);
                intent.putExtra("diaryEntry", diaryResult.get(info.position));
                startActivity(intent);
                break;
            }
            case "Delete": {
                Toast.makeText(this, "Delete pressed.", Toast.LENGTH_SHORT).show();
                confirmDeleteDialog(info.position);
                break;
            }
        }
        return true;
    }

    /**
     * AlertDialog to check if the user wants to delete the stock item selected.
     */
    public void confirmDeleteDialog(final int position) {
        if(dbHelper.exsists(currentSelectDate.getTimeInMillis())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setMessage("Are you sure you want to delete this food entry?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            deleteSelectedEntry(position);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "Diary entry does not exist.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Delete selected entry after confirmation dialog.
     * @param position
     */
    public void deleteSelectedEntry(int position) {
        int currentPosition = position;

        diaryResult.remove(currentPosition);
        foodAdapter.notifyData(diaryResult);
        foodAdapter.notifyDataSetChanged();
    }
}