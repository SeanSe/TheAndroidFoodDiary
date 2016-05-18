package com.project.sean.theandroidfooddiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.project.sean.theandroidfooddiary.models.FoodItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows a user to search for a specific food item using JSON objects retrieved from a web service.
 * Connects using URLConnection to the webservice, using a URL to request specific information.
 * The webservice returns a JSON object which is then parsed, this parsed data is then put into
 * a view for the user to see. Each food item is selectable and will create a new activity,
 * NutrientActivity, to show the nutrient of that food item.
 *
 * @author Sean Sexton
 */
public class FoodSearchActivity extends AppCompatActivity {

    //private TextView tvData;
    private ListView lvItems;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);

        lvItems = (ListView) findViewById(R.id.lvItems);

        search = (SearchView) findViewById(R.id.svFoodItem);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String search = query;
                String apiKey = "OJSuRrEnYD1JHKpoW8x1g2lHieEnktyMnHN82fLR";
                String searchURL = "http://api.nal.usda.gov/ndb/search/?format=json&q=" + search + "&sort=n&max=25&offset=0&api_key=" + apiKey;
                new FoodSearchTask().execute(searchURL);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        setTitle("Food Search");

//        final EditText tfFoodSearch = (EditText)findViewById(R.id.tfFoodSearch);
//
//        Button btnSearch = (Button)findViewById(R.id.btnSearch);
//
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String search = tfFoodSearch.getText().toString();
//                String apiKey = "OJSuRrEnYD1JHKpoW8x1g2lHieEnktyMnHN82fLR";
//                String searchURL = "http://api.nal.usda.gov/ndb/search/?format=json&q=" + search + "&sort=n&max=25&offset=0&api_key=" + apiKey;
//                new FoodSearchTask().execute(searchURL);
//            }
//        });

//        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String foodName = ;
//                Intent myIntent = new Intent(getApplicationContext(), NutrientActivity.class);
//                myIntent.putExtra("foodName", foodName);
//                startActivity(myIntent);
//            }
//        });
    }

    public class FoodSearchTask extends AsyncTask<String, String, List<FoodItemModel>> {

        AlertDialog.Builder alertDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(FoodSearchActivity.this);
        }

        @Override
        protected List<FoodItemModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                //TODO need to deal with JSON errors that are return
                //TODO maybe create new activity/view for errors

                JSONObject listObject = parentObject.getJSONObject("list");
                JSONArray itemsArray = listObject.getJSONArray("item");

                List<FoodItemModel> itemList = new ArrayList<>();
                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject finalObject = itemsArray.getJSONObject(i);

                    FoodItemModel foodItemModel = new FoodItemModel();
                    foodItemModel.setOffset(finalObject.getInt("offset"));
                    foodItemModel.setGroup(finalObject.getString("group"));
                    foodItemModel.setName(finalObject.getString("name"));
                    foodItemModel.setNdbno(finalObject.getString("ndbno"));
                    //Adding the final object in the list
                    itemList.add(foodItemModel);
                }

                return itemList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<FoodItemModel> result) {
            super.onPostExecute(result);
            if (result == null) {
                alertDialog.setTitle("Error - Status 400");
                alertDialog.setMessage("Your search resulted in zero items. Please change your search and try again.");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            } else {
                FoodItemAdapter adapter = new FoodItemAdapter(getApplicationContext(),
                        R.layout.item_row, result);
                lvItems.setAdapter(adapter);

                final List<FoodItemModel> currentList = result;

                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FoodItemModel foodList = currentList.get(position);
                        //String foodName = foodList.getName();
                        String foodId = foodList.getNdbno();
                        Intent myIntent = new Intent(getApplicationContext(), NutrientActivity.class);
                        //myIntent.putExtra("foodName", foodName);
                        myIntent.putExtra("foodId", foodId);
                        startActivity(myIntent);
                    }
                });
                //TODO add data to the list
            }
        }
    }

    public class FoodItemAdapter extends ArrayAdapter {

        private List<FoodItemModel> foodItemModelList;
        private int resource;
        private LayoutInflater inflater;

        public FoodItemAdapter(Context context, int resource, List<FoodItemModel> objects) {
            super(context, resource, objects);
            foodItemModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            Log.d("Food Item Size: ", "" + foodItemModelList.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);

                holder.tvOffset = (TextView) convertView.findViewById(R.id.tvOffset);
                holder.tvGroup = (TextView) convertView.findViewById(R.id.tvGroup);
                holder.tvNdbno = (TextView) convertView.findViewById(R.id.tvNdbno);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.d("Position: ", "" + position);

            holder.tvOffset.setText("Offset: " + foodItemModelList.get(position).getOffset());
            holder.tvGroup.setText("Group: " + foodItemModelList.get(position).getGroup());
            holder.tvNdbno.setText("Food ID: " + foodItemModelList.get(position).getNdbno());
            holder.tvName.setText("Name: " + foodItemModelList.get(position).getName());

            //Alternates row colours, BLUE if the remainder of position divided by 2 is 0
            //otherwise it is CYAN
            //.setBackGroundColor(Color.parseColor(#fffff));
            if ((position % 2) == 0) {
                convertView.setBackgroundColor(Color.WHITE);
            } else {
                convertView.setBackgroundColor(Color.LTGRAY);
            }

            return convertView;
        }

        class ViewHolder {

            private TextView tvOffset;
            private TextView tvGroup;
            private TextView tvNdbno;
            private TextView tvName;
        }
    }

}
