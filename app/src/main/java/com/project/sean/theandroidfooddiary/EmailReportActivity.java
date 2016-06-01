package com.project.sean.theandroidfooddiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Sean on 29/05/2016.
 */
public class EmailReportActivity extends AppCompatActivity {


    private EditText editEmailAdd;
    private EditText editSubject;
    private EditText editMessage;

    private Button buttonSendReportEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_report);
        setTitle("Email Report");

        editEmailAdd = (EditText) findViewById(R.id.editEmailAdd);
        editSubject = (EditText) findViewById(R.id.editSubject);
        editMessage = (EditText) findViewById(R.id.editMessage);

        buttonSendReportEmail = (Button) findViewById(R.id.buttonSendReportEmail);
        buttonSendReportEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(editEmailAdd) &&
                        !isEmpty(editSubject)) {
                    sendMail();
                } else {
                    Toast.makeText(EmailReportActivity.this, "Email address and subject required.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendMail() {
        File fileLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/TheFoodDiary/food_diary.pdf");
        if(fileLocation != null) {
            Uri path = Uri.fromFile(fileLocation);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {editEmailAdd.getText().toString()});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, editSubject.getText().toString());
            emailIntent.putExtra(Intent.EXTRA_TEXT, editMessage.getText().toString());
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(emailIntent, "Email"));
        }
    }

    /**
     * Checks if an EditText field is empty.
     * @param etText
     * @return true if empty, false if not
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
