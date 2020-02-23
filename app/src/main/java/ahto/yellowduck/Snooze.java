package ahto.yellowduck;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Snooze extends AppCompatActivity{
    public int finalSnooze = 0;
    public int repeat = 3;
    public RadioGroup rg;
    public RadioGroup rg2;
    int smin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snoozesettings);
        if(getIntent().hasExtra("smin")) {
            smin = getIntent().getExtras().getInt("smin");
            repeat = getIntent().getExtras().getInt("rep");
            Toast.makeText(this, String.valueOf(smin), Toast.LENGTH_SHORT).show();
        }
        rg = (RadioGroup) findViewById(R.id.radioSex);
        if(smin == 0){
            RadioButton r = (RadioButton) findViewById(R.id.R0);
            r.setChecked(true);
        }else if(smin == 1){
            RadioButton r = (RadioButton) findViewById(R.id.R1);
            r.setChecked(true);
        }else if(smin == 5) {
            RadioButton r = (RadioButton) findViewById(R.id.R2);
            r.setChecked(true);
        }else if(smin == 10) {
            RadioButton r = (RadioButton) findViewById(R.id.R3);
            r.setChecked(true);
        }
        rg2 = (RadioGroup) findViewById(R.id.radiofex);
        if(repeat == 1){
            RadioButton r = (RadioButton) findViewById(R.id.x4);
            r.setChecked(true);
        }else if(repeat == 3){
            RadioButton r = (RadioButton) findViewById(R.id.x3);
            r.setChecked(true);
        }else if(repeat == 10) {
            RadioButton r = (RadioButton) findViewById(R.id.x2);
            r.setChecked(true);
        }

        Button snoozeDone = (Button) findViewById(R.id.snoozeDone);
        snoozeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snoozeFinished();
            }
        });

    }
    public void snoozeFinished(){
        int snozeID = rg.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(snozeID);
        String snoozeControl = String.valueOf(rb.getText());
        switch (snoozeControl) {
            case "Disabled":
                finalSnooze = 0;
                break;
            case "1 minute":
                finalSnooze = 1;
                break;
            case "5 minutes":
                finalSnooze = 5;
                break;
            case "10 minutes":
                finalSnooze = 10;
                break;
        }
        snozeID = rg2.getCheckedRadioButtonId();
        RadioButton rb2 = (RadioButton) findViewById(snozeID);
        snoozeControl = String.valueOf(rb2.getText());
        switch (snoozeControl) {
            case "1 time":
                repeat = 1;
                break;
            case "3 times":
                repeat = 3;
                break;
            case "10 times":
                repeat = 10;
                break;
        }
        startActivity(new Intent(getApplicationContext(), AlarmMaker.class).putExtra("fsnooze", finalSnooze).putExtra("rep", repeat));
    }
    public void onBackPressed(){
        snoozeFinished();
    }
}
