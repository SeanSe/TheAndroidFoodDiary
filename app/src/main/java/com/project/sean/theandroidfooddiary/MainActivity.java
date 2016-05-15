package com.project.sean.theandroidfooddiary;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //
    private ListView mDrawerList;
    //
    private ArrayAdapter<String> mAdapter;

    //
    private ActionBarDrawerToggle mDrawerToggle;
    //
    private DrawerLayout mDrawerLayout;
    //
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        //Create the items for the menu
        addDrawerItems();
        //
        setUpDrawer();

        //Retrieve a reference to this activity's ActionBar.
        //Set home to be displayed as an "up" affordance.
        //Set to true if selecting "home" returns up by a single level instead of top
        //level or front page.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Enable or disable the "home" button in the corner of the aciton bar.
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Get todays date and set the MainActivity on launch to it
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Gets the day of the week, EEEE is for the long name
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        weekDay = dayFormat.format(c.getTime());

        String currentDate = weekDay + " " + day + "/" + month + "/" + year;

        TextView date_text_view = (TextView) findViewById(R.id.date_text_view);
        date_text_view.setText(currentDate);
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
                if(position == 0) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(position == 1) {
                    Intent intent = new Intent(MainActivity.this, FoodLibraryActivity.class);
                    startActivity(intent);
                } else if(position == 2) {
                    Intent intent = new Intent(MainActivity.this, FoodSearchActivity.class);
                    startActivity(intent);
                } else if(position == 3) {
                    Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                    startActivity(intent);
                } else if(position == 4) {
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
}