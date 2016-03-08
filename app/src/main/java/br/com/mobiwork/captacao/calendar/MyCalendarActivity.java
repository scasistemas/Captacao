package br.com.mobiwork.captacao.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Toast;
import android.view.Menu;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.mobiwork.captacao.R;

public class MyCalendarActivity extends Activity {
    CalendarView calend;
    private int dayOfMonthatu,monthatu,yearatu=0;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_calendar_view);
        calend = (CalendarView) findViewById(R.id.calendarView1);
        Calendar cal = Calendar.getInstance();
        dayOfMonthatu = cal.get(Calendar.DAY_OF_MONTH);
        monthatu= cal.get(Calendar.MONTH);
        yearatu= cal.get(Calendar.YEAR);

        calend.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                dayOfMonthatu = dayOfMonth;
                monthatu = month;
                yearatu = year;
            }
        });
    }

    public void escData(View v){
        Intent result = new Intent(MyCalendarActivity.this, MyCalendarActivity.class);
        result.putExtra("data1", yearatu+"-"+String.format("%02d",monthatu+1)+"-"+String.format("%02d",dayOfMonthatu));
        result.putExtra("data2",String.format("%02d",dayOfMonthatu)+"/"+String.format("%02d",monthatu+1)+"/"+yearatu);
        setResult(RESULT_OK, result);
        MyCalendarActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }



}