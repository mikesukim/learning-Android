package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;

public class ToDoAddEditActivity extends AppCompatActivity {
    
    ToDoItem item;
    TextView title;
    TextView content;
    EditText dueDate;
    CheckBox completion;

    Calendar myCalendar;
    String dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_to_do_item);

        // get ToDoItem
        Bundle extra = getIntent().getExtras();
        item = (ToDoItem) extra.get("ToDoItem");

        // Initialize Title & content
        title = (TextView) this.findViewById(R.id.etItemTitle);
        content = (TextView) this.findViewById(R.id.etItemContent);
        dueDate = (EditText) this.findViewById(R.id.etItemDate);
        completion = (CheckBox) this.findViewById(R.id.etItemCheckBox);

        // Set item infos using passed Item
        title.setText(item.getTitle());
        content.setText(item.getContent());
        completion.setChecked(item.getCompleted());

        dateFormat = "MM/dd/yy hh/mm a";
        if(item.getDueDate() == 0){
            dueDate.setText("Set your DueDate");
        }else{
            dueDate.setText(getDate(item.getDueDate(), dateFormat));
        }
        myCalendar = Calendar.getInstance();
//        myCalendar.setTimeInMillis(item.getDueDate());


        // Initialize Date & Time Picker
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);

                updateLabel();
            }
        };

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                new TimePickerDialog(ToDoAddEditActivity.this,time,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),false).show();
            }

        };

        // Add Button Listener
        this.findViewById(R.id.btnSaveToDoItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItem();
                sendBackData();
                setAlarmManager(view);
            }
        });
        this.findViewById(R.id.btnDeleteToDoItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItem();
                sendBackDataForDeletion();
            }
        });
        this.findViewById(R.id.etItemDate).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                new DatePickerDialog(ToDoAddEditActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }
    @Override
    public void onBackPressed() {
//        setItem();

        sendBackData();
    }

    private void setItem() {

        item.setTitle(title.getText().toString());
        item.setContent(content.getText().toString());
        item.setCompleted(completion.isChecked());
        item.setDueDate(myCalendar.getTimeInMillis());

    }

    private void sendBackData(){
        Intent data = new Intent();
        data.putExtra("ToDoItem", item);
        setResult(RESULT_OK, data);
        finish();
    }

    private void sendBackDataForDeletion(){
        Intent data = new Intent();
        data.putExtra("ToDoItem", item);
        data.putExtra("Deletion", true);
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dueDate.setText(sdf.format(myCalendar.getTime()));
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void setAlarmManager(View view){

        AlarmManager alarmManager;
        if(Build.VERSION.SDK_INT >= 23){
            alarmManager = view.getContext().getSystemService(AlarmManager.class);
        }else{
            alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
        }
        Intent alarmNotificationIntent = new Intent(view.getContext(),AlarmNotification.class);
        alarmNotificationIntent.putExtra("ToDoTitle", item.getTitle());
        alarmNotificationIntent.putExtra("ToDoContent", item.getContent());
        alarmNotificationIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(view.getContext(),0,alarmNotificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,myCalendar.getTimeInMillis(),alarmIntent);

    }
}