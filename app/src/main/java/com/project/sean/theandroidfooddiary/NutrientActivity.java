package com.project.sean.theandroidfooddiary;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.project.sean.theandroidfooddiary.models.NutrientModel;

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
 * Created by Sean on 21/02/2016.
 */
public class NutrientActivity extends AppCompatActivity {

    TextView tvFoodName;
    TextView tvFoodNo;
    TextView tvWeight;
    TextView tvMeasure;
    ListView lvNutrients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrients);
        //Retrieve data from FoodSearchActivity on listview item click
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//
//        if(Intent.ACTION_SEND.equals(action) && type != null) {
//            if
//        }

        tvFoodName = (TextView)findViewById(R.id.tvFoodName);
        tvFoodNo = (TextView)findViewById(R.id.tvFoodNo);
        tvWeight = (TextView)findViewById(R.id.tvWeight);
        tvMeasure = (TextView)findViewById(R.id.tvMeasure);


        lvNutrients = (ListView)findViewById(R.id.lvNutrients);


        Bundle recdData = getIntent().getExtras();
        String foodNoVal = recdData.getString("foodId");
        Log.d("Food No: ", foodNoVal);


        //http://api.nal.usda.gov/ndb/nutrients/?format=json&api_key=DEMO_KEY&nutrients=208&nutrients=204&nutrients=606&nutrients=205&nutrients=269&nutrients=203&nutrients=307&nutrients=291&ndbno=01009
        String apiKey = "OJSuRrEnYD1JHKpoW8x1g2lHieEnktyMnHN82fLR";
        String searchURL = "http://api.nal.usda.gov/ndb/nutrients/?format=json&api_key=" + apiKey + "&nutrients=208&nutrients=204&nutrients=606&nutrients=205&nutrients=269&nutrients=203&nutrients=307&nutrients=291&ndbno=" + foodNoVal;
        Log.d("URL Sent: ", searchURL);
        new NutrientSearchTask().execute(searchURL);
    }

    public class NutrientSearchTask extends AsyncTask<String, String, List<NutrientModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<NutrientModel> doInBackground(String... params) {
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

                Log.d("JSON Nutrients: ", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);

                JSONObject reportObject = parentObject.getJSONObject("report");
                JSONArray foodsArray = reportObject.getJSONArray("foods");

                List<NutrientModel> nutrientModelList = new ArrayList<>();

                for(int i = 0; i < foodsArray.length(); i++) {
                    JSONObject finalObject = foodsArray.getJSONObject(i);

                    NutrientModel nutrientModel = new NutrientModel();

                    nutrientModel.setNdbno(finalObject.getString("ndbno"));
                    nutrientModel.setName(finalObject.getString("name"));
                    nutrientModel.setWeight(String.valueOf(finalObject.get("weight")));
                    nutrientModel.setMeasure(finalObject.getString("measure"));

                    List<NutrientModel.Nutrients> nutrientsList = new ArrayList<>();
                    for(int j = 0; j < finalObject.getJSONArray("nutrients").length(); j++) {
                        NutrientModel.Nutrients nutrients = new NutrientModel.Nutrients();
                        nutrients.setNutrient_id(finalObject.getJSONArray("nutrients").getJSONObject(j).getString("nutrient_id"));
                        nutrients.setNutrient(finalObject.getJSONArray("nutrients").getJSONObject(j).getString("nutrient"));
                        nutrients.setUnit(finalObject.getJSONArray("nutrients").getJSONObject(j).getString("unit"));
                        nutrients.setValue(finalObject.getJSONArray("nutrients").getJSONObject(j).getString("value"));
                        nutrients.setGm(String.valueOf(finalObject.getJSONArray("nutrients").getJSONObject(j).get("gm")));

                        nutrientsList.add(nutrients);
                    }
                    nutrientModel.setNutrients(nutrientsList);

                    nutrientModelList.add(nutrientModel);
                }
                return nutrientModelList;

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
        protected void onPostExecute(List<NutrientModel> result) {
            super.onPostExecute(result);
            if(result == null){

            } else {
                List<NutrientModel> nutrientModelList = result;
                NutrientModel nutrientModel = nutrientModelList.get(0);
                List<NutrientModel.Nutrients> nutrientList = nutrientModel.getNutrients();

                NutrientsAdapter adapter = new NutrientsAdapter(NutrientActivity.this, R.layout.nutrient_row, nutrientList);
                lvNutrients.setAdapter(adapter);

                final List<NutrientModel> currentList = result;
                String foodNameVal = currentList.get(0).getName();
                String foodNoVal = currentList.get(0).getNdbno();
                String foodWeightVal = currentList.get(0).getWeight();
                String foodMeasureVal = currentList.get(0).getMeasure();

                tvFoodName.setText("Food Name: " + foodNameVal);
                tvFoodNo.setText("Food No: " + foodNoVal);
                tvWeight.setText("Weight: " + foodWeightVal);
                tvMeasure.setText("Measurement: " + foodMeasureVal);
            }
        }
    }

    public class NutrientsAdapter extends ArrayAdapter {

        //private List<NutrientModel> nutrientModelList;
        private int resource;
        private LayoutInflater inflater;
        //private NutrientModel nutrientModel;
        private List<NutrientModel.Nutrients> nutrientList;

        //TODO change the List<NutrientModel> to List<Nutrients>
        public NutrientsAdapter(Context context, int resource, List<NutrientModel.Nutrients> objects) {
            super(context, resource, objects);
            //nutrientModelList = objects;

            //nutrientModel = nutrientModelList.get(0);
            nutrientList = objects;

            this.resource = resource;
            inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

            //Log.d("ModelList Size: ", "" + nutrientModelList.size());

            Log.d("NutrientList Size", "" + nutrientList.size());

        }

        //position is used to allow for multiple items to be called
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);

                holder.tvNutrientID = (TextView)convertView.findViewById(R.id.tvNutrientID);
                holder.tvNutrient = (TextView)convertView.findViewById(R.id.tvNutrient);
                holder.tvUnit = (TextView)convertView.findViewById(R.id.tvUnit);
                holder.tvValue = (TextView)convertView.findViewById(R.id.tvValue);
                holder.tvGm = (TextView)convertView.findViewById(R.id.tvGm);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.d("Position: ", "" + position);
//
//            //How many elements inside the nutrient list
//            Log.d("Nutrient Size: ", "" + nutrient.size());
//
            holder.tvNutrientID.setText("Nutrient ID: " + nutrientList.get(position).getNutrient_id());
            holder.tvNutrient.setText("Nutrient Name: " + nutrientList.get(position).getNutrient());
            holder.tvUnit.setText("Unit: " + nutrientList.get(position).getUnit());
            holder.tvValue.setText("Value: " + nutrientList.get(position).getValue());
            holder.tvGm.setText("gm: " + nutrientList.get(position).getGm());

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

            private TextView tvNutrientID;
            private TextView tvNutrient;
            private TextView tvUnit;
            private TextView tvValue;
            private TextView tvGm;
        }
    }
}
