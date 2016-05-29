package com.project.sean.theandroidfooddiary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sean on 15/05/2016.
 */
public class ReportActivity extends AppCompatActivity implements OnClickListener {

    private EditText editStartDate;
    private EditText editEndDate;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private Button buttonGenerateReport;

    private Calendar startDate;
    private Calendar endDate;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setTitle("Reports");

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        startDate = Calendar.getInstance();
        startDate.clear();
        endDate = Calendar.getInstance();
        endDate.clear();

        editStartDate = (EditText) findViewById(R.id.editStartDate);
        editStartDate.setInputType(InputType.TYPE_NULL);
        editStartDate.requestFocus();

        editEndDate = (EditText) findViewById(R.id.editEndDate);
        editEndDate.setInputType(InputType.TYPE_NULL);

        editStartDate.setOnClickListener(this);
        editEndDate.setOnClickListener(this);

        buttonGenerateReport = (Button) findViewById(R.id.buttonGenerateReport);
        buttonGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editStartDate != null && editEndDate != null) {
                    Intent intent = new Intent(ReportActivity.this, ViewReportActivity.class);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    startActivity(intent);
                } else {
                    Toast.makeText(ReportActivity.this, "Start and end date required.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startDate.set(year, monthOfYear, dayOfMonth);
                editStartDate.setText(dateFormatter.format(startDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endDate.set(year, monthOfYear, dayOfMonth);
                editEndDate.setText(dateFormatter.format(endDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {
        if(view == editStartDate) {
            startDatePickerDialog.show();
        } else if(view == editEndDate) {
            endDatePickerDialog.show();
        }
    }
}
