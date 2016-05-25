package com.project.sean.theandroidfooddiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.project.sean.theandroidfooddiary.Database.FoodLibrary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Handles the list of items on the users food, can add,update and delete the details
 * stored within the database. The items can either have a barcode or food ID associated
 * with them so they can be easily retrieved for later processing.
 * Created by Sean on 15/05/2016.
 */
public class FoodLibraryActivity extends AppCompatActivity {

    private FoodDiaryDBHelper dbHelper;

    private ListView lv_food_lib;

    private ArrayList<FoodLibrary> foodLibList;

    private FoodLibAdapter foodLibAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_library);
        setTitle("Food Library");

        //Get instance of the DB
        dbHelper = FoodDiaryDBHelper.getInstance(this);

        //List view for the food library
        lv_food_lib = (ListView) findViewById(R.id.lv_food_lib);

        foodLibList = new ArrayList<>();

        foodLibList.addAll(dbHelper.getAllFoodLibEntries());

        foodLibAdapter = new FoodLibAdapter(getApplicationContext(), R.layout.listview_food_library, foodLibList);

        lv_food_lib.setAdapter(foodLibAdapter);

        registerForContextMenu(lv_food_lib);

        //Potential use for showing when a list is empty
//        TextView emptyText1 = (TextView) findViewById(R.id.emptyText1);
//        lv_food_lib.setEmptyView(emptyText1);

        //FloatingActionButton to add a new entry into the diary.
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodLibraryActivity.this, AddFoodEntryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Custom Adapter class to handle the ListView for the diary.
     */
    public class FoodLibAdapter extends ArrayAdapter<FoodLibrary> {

        private ArrayList<FoodLibrary> foodList;

        public FoodLibAdapter(Context context, int resource, ArrayList<FoodLibrary> foodList) {
            super(context, resource, foodList);
            this.foodList = new ArrayList<FoodLibrary>();
            this.foodList.addAll(foodList);
        }

        private class ViewHolder {
            TextView tvFL_Food_ID;
            TextView tvFL_Food_Name;
            TextView tvFL_Note;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listview_food_library, null);

                holder = new ViewHolder();

                holder.tvFL_Food_ID = (TextView) convertView.findViewById(R.id.tvFL_Food_ID);
                holder.tvFL_Food_Name = (TextView) convertView.findViewById(R.id.tvFL_Food_Name);
                holder.tvFL_Note = (TextView) convertView.findViewById(R.id.tvFL_Note);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvFL_Food_ID.setText(foodList.get(position).getFoodId());
            holder.tvFL_Food_Name.setText(foodList.get(position).getFoodName());
            holder.tvFL_Note.setText(foodList.get(position).getNote());

            return convertView;
        }

        public void notifyData(ArrayList<FoodLibrary> foodList) {
            foodList.clear();
            foodList.addAll(foodList);
            notifyDataSetChanged();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lv_food_lib) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(foodLibList.get(info.position).getFoodName().toString());
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
                Intent intent = new Intent(FoodLibraryActivity.this, EditFoodEntryActivity.class);
                intent.putExtra("foodEntry", foodLibList.get(info.position));
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
        if(dbHelper.foodIdExsists(foodLibList.get(position).getFoodId())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setMessage("Are you sure you want to delete this food item?")
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
            Toast.makeText(this, "Food item does not exist.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Delete selected entry after confirmation dialog.
     * @param position
     */
    public void deleteSelectedEntry(int position) {
        int currentPosition = position;

        boolean deleted = dbHelper.deleteFoodLibEntry(foodLibList.get(currentPosition).getFoodId());
        if(deleted) {
            foodLibList.remove(currentPosition);
            foodLibAdapter.notifyData(foodLibList);
            foodLibAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Entry deleted successfully.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Entry not deleted.", Toast.LENGTH_LONG).show();
        }
    }

}
