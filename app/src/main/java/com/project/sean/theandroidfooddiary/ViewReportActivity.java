package com.project.sean.theandroidfooddiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.sean.theandroidfooddiary.Database.FoodDiary;
import com.project.sean.theandroidfooddiary.Database.FoodDiaryDBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private Button button_save_storage;

    private String reportDateRange;

    private ArrayList<FoodDiary> diaryResult;

    private FoodReportAdapter foodReportAdapter;

    private SimpleDateFormat dayFormat;
    private SimpleDateFormat timeFormat;

    boolean permResult;

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
        button_save_storage = (Button) findViewById(R.id.button_save_storage);

        dateRange();

        //Create array of diary results
        diaryResult = new ArrayList<>();
        diaryResult.addAll(dbHelper.getFoodDiaryResults(startDate.getTimeInMillis(), endDate.getTimeInMillis()));

        //Create and set adapter to display results
        foodReportAdapter = new FoodReportAdapter(this, R.layout.listview_food_report, diaryResult);
        lv_report_result.setAdapter(foodReportAdapter);

        button_email_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReport();
                Intent intent = new Intent(ViewReportActivity.this, EmailReportActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_save_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReport();
            }
        });

        permResult = isStoragePermissionGranted();
        if(permResult) {
            Toast.makeText(this, "External Storage access granted!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "External Storage access denied!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets a String of the date range for the report.
     */
    public void dateRange() {
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String rangeStart = dayFormat.format(startDate.getTime());
        String rangeEnd = dayFormat.format(endDate.getTime());

        reportDateRange = rangeStart + " - " + rangeEnd;
        tvDateRange.setText(reportDateRange);
    }

    public void saveReport() {

        if(!permResult) {
            Toast.makeText(this, "No file access currently.", Toast.LENGTH_LONG).show();
        } else {
            FileOutputStream fOut = null;
            OutputStreamWriter osw = null;

            String state;
            state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state)) {
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/TheFoodDiary");
                if(!dir.exists()) {
                    dir.mkdir();
                }

                File file = new File(dir + "/food_diary.pdf");

                try {
                    fOut = new FileOutputStream(file);

                    osw = new OutputStreamWriter(fOut);

                    Document document = new Document();

                    PdfWriter.getInstance(document, fOut);

                    document.open();

                    Paragraph paragraphOne = new Paragraph();
                    paragraphOne.add("The Android Food Diary Report");
                    paragraphOne.setAlignment(Element.ALIGN_CENTER);
                    paragraphOne.setSpacingAfter(5f);
                    document.add(paragraphOne);

                    Paragraph paragraphTwo = new Paragraph();
                    paragraphTwo.add("Date Range: " + reportDateRange);
                    paragraphTwo.setAlignment(Element.ALIGN_CENTER);
                    paragraphTwo.setSpacingAfter(10f);
                    document.add(paragraphTwo);
                    document.addCreationDate();

                    Calendar tempReport = Calendar.getInstance();
                    tempReport.clear();
                    for (FoodDiary foodDiary : diaryResult) {
                        //Date header
                        tempReport.setTimeInMillis(foodDiary.getDate());
                        Paragraph paragraph = new Paragraph();
                        paragraph.setAlignment(Element.ALIGN_CENTER);
                        paragraph.add(dayFormat.format(tempReport.getTime()));
                        paragraph.setSpacingBefore(35f);
                        paragraph.setSpacingAfter(10f);
                        document.add(paragraph);
                        //Food diary entry
                        PdfPTable table = new PdfPTable(2);

                        tempReport.clear();
                        tempReport.set(Calendar.HOUR, foodDiary.getHour());
                        tempReport.set(Calendar.MINUTE, foodDiary.getMinute());
                        table.addCell("Food Time: ");
                        table.addCell(timeFormat.format(tempReport.getTime()));

                        table.addCell("Food Name: ");
                        table.addCell(foodDiary.getFoodItem());

                        table.addCell("Food Note: ");
                        table.addCell(foodDiary.getFoodNote());

                        document.add(table);

                        document.add(new Paragraph());
                    }

                    document.close();
                    //---display file saved message---
                    Toast.makeText(getBaseContext(),
                            "File saved successfully!",
                            Toast.LENGTH_SHORT).show();


                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } finally {
                    if (fOut != null) {
                        try {
                            fOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (osw != null) {
                        try {
                            osw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "SD not found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    /**
     * Custom Adapter class to handle the ListView for the diary.
     */
    public class FoodReportAdapter extends ArrayAdapter<FoodDiary> {

        private ArrayList<FoodDiary> foodDiaryList;

        public FoodReportAdapter(Context context, int resource, ArrayList<FoodDiary> diaryList) {
            super(context, resource, diaryList);
            this.foodDiaryList = new ArrayList<FoodDiary>();
            this.foodDiaryList.addAll(diaryList);
        }

        private class ViewHolder {
            TextView tvReportItemDate;
            TextView tv_food_report_time;
            TextView tv_food_report_name;
            TextView tv_food_report_note;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listview_food_report, null);

                holder = new ViewHolder();

                holder.tvReportItemDate = (TextView) convertView.findViewById(R.id.tvReportItemDate);
                holder.tv_food_report_time = (TextView) convertView.findViewById(R.id.tv_food_report_time);
                holder.tv_food_report_name = (TextView) convertView.findViewById(R.id.tv_food_report_name);
                holder.tv_food_report_note = (TextView) convertView.findViewById(R.id.tv_food_report_note);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Calendar diaryTime = Calendar.getInstance();
            diaryTime.clear();
            diaryTime.set(Calendar.HOUR, foodDiaryList.get(position).getHour());
            diaryTime.set(Calendar.MINUTE, foodDiaryList.get(position).getMinute());

            //SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = timeFormat.format(diaryTime.getTime());


            diaryTime.setTimeInMillis(foodDiaryList.get(position).getDate());
            String date = dayFormat.format(diaryTime.getTime());

            //String time = foodDiaryList.get(position).getHour() + ":" + foodDiaryList.get(position).getMinute();

            holder.tvReportItemDate.setText(date);
            holder.tv_food_report_time.setText(time);
            holder.tv_food_report_name.setText(foodDiaryList.get(position).getFoodItem());
            holder.tv_food_report_note.setText(foodDiaryList.get(position).getFoodNote());

            return convertView;
        }

        public void notifyData(ArrayList<FoodDiary> foodDiary) {
            foodDiaryList.clear();
            foodDiaryList.addAll(foodDiary);
            notifyDataSetChanged();
        }
    }
}
