package ahto.yellowduck;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;

public class AlarmDays extends AppCompatActivity{

    CheckBox Mon, Tue, Wed, Thu, Fri, Sat, Sun;
    ArrayList<Integer> day;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmextendedsettings);
        if(getIntent().hasExtra("day")){
            day = getIntent().getExtras().getIntegerArrayList("day");
        }
        Mon = (CheckBox) findViewById(R.id.checkBox);
        if(day != null) {
            if (day.contains(2)) {
                Mon.setChecked(true);
            }
        }
        Tue = (CheckBox) findViewById(R.id.checkBox2);
        if(day != null) {
            if (day.contains(3)) {
                Tue.setChecked(true);
            }
        }
        Wed = (CheckBox) findViewById(R.id.checkBox3);
        if(day != null) {
            if (day.contains(4)) {
                Wed.setChecked(true);
            }
        }
        Thu = (CheckBox) findViewById(R.id.checkBox4);
        if(day != null) {
            if (day.contains(5)) {
                Thu.setChecked(true);
            }
        }
        Fri = (CheckBox) findViewById(R.id.checkBox5);
        if(day != null) {
            if (day.contains(6)) {
                Fri.setChecked(true);
            }
        }
        Sat = (CheckBox) findViewById(R.id.checkBox6);
        if(day != null) {
            if (day.contains(7)) {
                Sat.setChecked(true);
            }
        }
        Sun = (CheckBox) findViewById(R.id.checkBox7);
        if(day != null) {
            if (day.contains(1)) {
                Sun.setChecked(true);
            }
        }

        Button back = (Button) findViewById(R.id.daysback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = new ArrayList<>();
                if(Sun.isChecked()){
                    day.add(1);
                }
                if(Mon.isChecked()){
                    day.add(2);
                }
                if(Tue.isChecked()){
                    day.add(3);
                }
                if(Wed.isChecked()){
                    day.add(4);
                }
                if(Thu.isChecked()){
                    day.add(5);
                }
                if(Fri.isChecked()){
                    day.add(6);
                }
                if(Sat.isChecked()){
                    day.add(7);
                }

                startActivity(new Intent(getApplicationContext(), AlarmMaker.class).putExtra("day", day));
            }
        });

    }
    public void onBackPressed(){
        day = new ArrayList<>();
        if(Sun.isChecked()){
            day.add(1);
        }
        if(Mon.isChecked()){
            day.add(2);
        }
        if(Tue.isChecked()){
            day.add(3);
        }
        if(Wed.isChecked()){
            day.add(4);
        }
        if(Thu.isChecked()){
            day.add(5);
        }
        if(Fri.isChecked()){
            day.add(6);
        }
        if(Sat.isChecked()){
            day.add(7);
        }

        startActivity(new Intent(getApplicationContext(), AlarmMaker.class).putExtra("day", day));
    }
}
