package com.project.sean.theandroidfooddiary;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.project.sean.theandroidfooddiary.Database.FoodDiary;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Sean on 29/05/2016.
 */
public class ViewReportActivity extends AppCompatActivity {

    private FoodDiaryDBHelper dbHelper;

    private Calendar startDate;
    private Calendar endDate;

    private TextView tvDateRange;

    private ListView lv_report_result;

    private Button button_email_report;

    private String reportDateRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        setTitle("View Report");

        dbHelper = FoodDiaryDBHelper.getInstance(this);

        startDate = (Calendar) getIntent().getSerializableExtra("startDate");
        endDate = (Calendar) getIntent().getSerializableExtra("endDate");

        tvDateRange = (TextView) findViewById(R.id.tvDateRange);

        lv_report_result = (ListView) findViewById(R.id.lv_report_result);

        button_email_report = (Button) findViewById(R.id.button_email_report);

        dateRange();
    }

    /**
     * Gets a String of the date range for the report.
     */
    public void dateRange() {

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String rangeStart = dayFormat.format(startDate.getTime());
        String rangeEnd = dayFormat.format(endDate.getTime());

        reportDateRange = rangeStart + " - " + rangeEnd;
        tvDateRange.setText(reportDateRange);
    }

    public void reportList() {

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
}
