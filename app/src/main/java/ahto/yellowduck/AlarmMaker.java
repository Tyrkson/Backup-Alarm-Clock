package ahto.yellowduck;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;




public  class AlarmMaker extends AppCompatActivity{
    //Igasugused muutujad, mida on vaja selle äratuse aja tegemiseks
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    //TextView update_text;
    Context context;
    Calendar c;
    Calendar cal;
    public ArrayList<AlarmList> alarmid;
    PendingIntent pendindIntent;
    int olemas;
    int end;
    long snoze;
    int alarming; //Kui see on 0 siis ei ärata hetkel aga kui 1 siis äratab hetkel
    public static int vib;
    public static int vol;
    public static String music = null;
    public static String musicN;
    public static int snoozemin;
    public static ArrayList<Integer> days = new ArrayList<>();
    public static int repeat;
    public static int id; //-1 = uue äratusega 0 v suurem olemasoleva muutmisega
    public static int sendSMS = 0;
    public static String SMSNumber;
    public static String SMSName;
    public static String SMS;
    public static int holdHour;
    public static int holdMinute;

    public void saveAlarms(){
        SharedPreferences sp = AlarmMaker.this.getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmid);
        editor.remove(getString(R.string.alarms));
        editor.putString(getString(R.string.alarms), json);
        editor.apply();
    }
    public void getAlarms(){
        SharedPreferences sp = AlarmMaker.this.getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        Gson gson = new Gson();
        String response = sp.getString(getString(R.string.alarms), "");
        if (response.equals("")){
            alarmid = new ArrayList<>();
        }else {
            alarmid = gson.fromJson(response, new TypeToken<ArrayList<AlarmList>>() {
            }.getType());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAlarms();
        if(alarmid == null){
            alarmid = new ArrayList<>();
        }
        //Nendele muutujatele väärtuste andmine
        this.context = this;
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);

        alarm_timepicker.setIs24HourView(getTimeFormat());


        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //music, volume, vibrate, snooze jm väärtused
        if(getIntent().hasExtra("id")){
            id = getIntent().getExtras().getInt("id");
            if(id != -1) {
                vib = alarmid.get(id).getHasVib();
                vol = alarmid.get(id).getSoundVolume();
                music = alarmid.get(id).getMusic();
                musicN = alarmid.get(id).getMusicN();
                snoozemin = alarmid.get(id).getSnoozeMinute();
                repeat = alarmid.get(id).getRepeat();
                days = alarmid.get(id).getAlarmDay();
                sendSMS = alarmid.get(id).getSendSMS();
                SMSName = alarmid.get(id).getSMSn();
                SMSNumber = alarmid.get(id).getNumber();
                SMS = alarmid.get(id).getSMS();
            }else{
                sendSMS = 0;
                vol = 6;
            }
        }
        if(getIntent().hasExtra("vib")) {
            vib = getIntent().getExtras().getInt("vib");
            vol = getIntent().getExtras().getInt("vol");
            music = getIntent().getExtras().getString("path");
            musicN = getIntent().getExtras().getString("nimi");
        }
        if(getIntent().hasExtra("fsnooze")){
            snoozemin = getIntent().getExtras().getInt("fsnooze");
            repeat = getIntent().getExtras().getInt("rep");
        }
        if(getIntent().hasExtra("day")){
            days = getIntent().getExtras().getIntegerArrayList("day");
            if(id != -1){
                alarmid.get(id).setAlarmDay(days);
            }
        }
        if(getIntent().hasExtra("num")){
            SMSNumber = getIntent().getExtras().getString("num");
            SMSName = getIntent().getExtras().getString("name");
            SMS = getIntent().getExtras().getString("SMS");
            if(SMSName == null){
                sendSMS = 0;
            }else{
                sendSMS = 1;
            }
        }

        if(id != -1){
            alarm_timepicker.setHour(alarmid.get(id).getHour());
            alarm_timepicker.setMinute(alarmid.get(id).getMinute());
        }
        if(holdHour != 0){
            alarm_timepicker.setHour(holdHour);
            alarm_timepicker.setMinute(holdMinute);
        }

        //Nupp, mis salvestab äratuse aja, kui vajutada peale
        Button alarm = (Button) findViewById(R.id.jalg);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uuele/olemasolevale äratusele aja andmine
                c = Calendar.getInstance();
                cal = (Calendar) c.clone();
                cal.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                cal.set(Calendar.MINUTE, alarm_timepicker.getMinute());
                cal.set(Calendar.SECOND, 0);
                //1 on pühapäev, 2 on esmaspäev jne
                if(days.size() == 0){//Päevi ei ole
                        if (cal.before(c)) {
                            cal.add(Calendar.DATE, 1);
                        }
                }else {//Päevad on antud
                    getAlarmDay(c.get(Calendar.DAY_OF_WEEK));
                }
                //--------------------------------------------------------------
                if(id == -1) {
                    if (alarmid == null) {
                        end = 0;
                    }else {
                        end = alarmid.size();
                    }
                }else {
                    end = id;
                }
                if(repeat == 0){//Kui snooze ei panda, siis default on 3 korda
                    repeat = 3;
                }
                    AlarmList a = new AlarmList(cal.getTimeInMillis(), olemas = 1, end, snoze, alarming,
                            vib, vol, music, snoozemin, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.DAY_OF_WEEK), repeat,
                            SMSNumber, SMS, sendSMS, SMSName, musicN);//number = telefoni number; SMS = sõnumi sisu; 1 = boolean send or no
                    a.setRepeatsleft(a.getRepeat());
                    if (days.size() != 0) {
                        for (Integer i : days) {
                            a.getAlarmDay().add(i);
                        }
                    }
                if(id == -1) {
                    alarmid.add(a);
                }else{
                    Intent mIntent = new Intent(AlarmMaker.this, AlarmReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(AlarmMaker.this, id, mIntent, 0);
                    alarm_manager.cancel(pi);
                    alarmid.set(id, a);
                }
                //Saadab Receiverisse äratuse
                for (AlarmList i : alarmid) {
                    if (i.getHasAlarm() == 1) {
                        //Intent - kavatsus; Saadab alarmi Receiverisse
                        Intent myIntent = new Intent(AlarmMaker.this, AlarmReceiver.class);
                        //Saadab lisaks veel kaasa ka "järjekorranumbri"

                        myIntent.putExtra("rc", i.getRequestCode());
                        pendindIntent = PendingIntent.getBroadcast(AlarmMaker.this, i.getRequestCode(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, i.getAlarmtime(), pendindIntent);
                    }
                }
                //Kontrollib, et sama kellaajaga äratusi ei oleks mitu
                alarmControl(a);

                saveAlarms();
                if(a.getMinute() < 10){
                    Toast.makeText(context, "Alarm is set " + String.valueOf(a.getHour()) + ":0" + String.valueOf(a.getMinute()), Toast.LENGTH_SHORT).show();

                }else Toast.makeText(context, "Alarm is set " + String.valueOf(a.getHour()) + ":" + String.valueOf(a.getMinute()), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }

            //muudab textviewi teksti
            /*private void set_alarm_text(String output) {


                update_text.setText(output);

            } */


        });
        //Nupp, mis viib tagasi menusse
        ImageButton nool = (ImageButton) findViewById(R.id.tagasi);
        nool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog("Are you sure you want to leave?\nAlarm won't be saved!", "Unsaved alarm", -1);
            }
        });
        //Nupp mis viib muusika ja heli settingutesse
        Button sound = (Button) findViewById(R.id.musicbutton);
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentTimepickerTime();
                Intent toSound = new Intent(AlarmMaker.this, CreateSound.class);
                toSound.putExtra("vol", vol);
                toSound.putExtra("path", music);
                toSound.putExtra("vib", vib);
                toSound.putExtra("nimi", musicN);
                startActivity(toSound);
            }
        });
        //Nupp, mis viib SMS makeri settingutesse
        Button sms = (Button) findViewById(R.id.messagecontent);
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentTimepickerTime();
                if(ContextCompat.checkSelfPermission(AlarmMaker.this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AlarmMaker.this,
                            new String[]{Manifest.permission.SEND_SMS}, 1);
                }else {
                    Intent tosms = new Intent(AlarmMaker.this, SetMessage.class)
                            .putExtra("num", SMSNumber)
                            .putExtra("SMS", SMS)
                            .putExtra("name", SMSName);
                    startActivity(tosms);
                }
            }
        });
        //Nupp, mis snoozei settingutesse
        Button setSnooze = (Button) findViewById(R.id.setSnooze);
        setSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentTimepickerTime();
                startActivity(new Intent(AlarmMaker.this, Snooze.class)
                        .putExtra("smin", snoozemin)
                        .putExtra("rep", repeat)
                );
            }
        });
        Button setDay = (Button) findViewById(R.id.Alarmtime);
        setDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentTimepickerTime();
                startActivity(new Intent(AlarmMaker.this, AlarmDays.class).putExtra("day", days));
            }
        });
        ImageButton del = (ImageButton) findViewById(R.id.kustuta);
        if(id != -1){
            del.setVisibility(View.VISIBLE);
        }else del.setVisibility(View.GONE);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog("Are you sure you want to delete it?", "Delete alarm", id);
            }
        });

        final Switch toggleSMS = (Switch) findViewById(R.id.togglemessage);
        if (sendSMS == 1) {
            toggleSMS.setChecked(true);
        }else{
            toggleSMS.setChecked(false);
        }
        toggleSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    sendSMS = 1;
                    getCurrentTimepickerTime();
                    if(ContextCompat.checkSelfPermission(AlarmMaker.this,
                            Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AlarmMaker.this,
                                new String[]{Manifest.permission.SEND_SMS}, 1);
                        toggleSMS.setChecked(false);
                    }else {
                        startActivity(new Intent(AlarmMaker.this, SetMessage.class).putExtra("num", SMSNumber)
                                .putExtra("name", SMSName).putExtra("SMS", SMS));
                    }
                }else {
                    sendSMS = 0;
                }
            }
        });

    }


    private Boolean getTimeFormat() {
        SharedPreferences sp = AlarmMaker.this.getSharedPreferences(getString(R.string.format_file), MODE_PRIVATE);
        return sp.getBoolean("time", true);
    }

    private void alarmControl(AlarmList a) {
        int remove = -1;
        for(AlarmList i : alarmid){
            if(i.getRequestCode() != a.getRequestCode()) {
                if (i.getHour() == a.getHour() && i.getMinute() == a.getMinute()) {
                    remove = i.getRequestCode();
                }
            }
        }
        if(remove != -1) {
            deleteAlarm(remove);
        }
    }

    private void deleteAlarm(int id) {
        cancelAlarm(id);
        alarmid.remove(id);
        for(AlarmList i : alarmid){
            if(i.getRequestCode() > id) {
                Intent mIntent = new Intent(AlarmMaker.this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(AlarmMaker.this, i.getRequestCode(), mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.cancel(pi);
                i.setRequestCode(i.getRequestCode() - 1);
                pi = PendingIntent.getBroadcast(AlarmMaker.this, i.getRequestCode(), mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, i.getAlarmtime(), pi);
            }
        }
        if(alarmid.size() == 0){
            alarmid = new ArrayList<>();
        }
        saveAlarms();
        startActivity(new Intent(AlarmMaker.this, MainActivity.class));

    }

    private void cancelAlarm(int id) {
        Intent mIntent = new Intent(AlarmMaker.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(AlarmMaker.this, id, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm_manager.cancel(pi);
    }

    private void getAlarmDay(int today) {
        int index;
        int add = 0;
        int newDay = 0;
        int date = cal.get(Calendar.DATE);
        if(days.contains(today)){
            if(cal.before(c)){
                index = days.indexOf(today);
                index += 1;
                if(index != days.size()){
                    cal.set(Calendar.DAY_OF_WEEK, days.get(index));
                }else{
                    cal.set(Calendar.DAY_OF_WEEK, days.get(0));
                    add = days.get(0) + (7 - today);
                }
            }else{
                cal.set(Calendar.DAY_OF_WEEK, today);
            }
        }else if(today != 1){
            for(Integer i : days) {
                if(i > today){
                    newDay = i;
                    break;
                }
            }
            if(newDay == 0){//Käivitub siis, kui tänane päev pole listis ja tänane päev on suurem kui listi viimane päev
                cal.set(Calendar.DAY_OF_WEEK, days.get(0));
                add = days.get(0) + (7 - today);
            }else{
                cal.set(Calendar.DAY_OF_WEEK, newDay);
            }
        }else{
            cal.set(Calendar.DAY_OF_WEEK, days.get(0));
            add = days.get(0) - today;
            if(add == 0){
                add = 7;
            }
        }
        if(add != 0) {
            date = date + add;
            cal.set(Calendar.DATE, date);
        }

    }

    public void onBackPressed() {
        alertDialog("Are you sure you want to leave?\nAlarm won't be saved!", "Unsaved alarm", -1);

    }

    private void alertDialog(String content, String title, final int delID) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(delID != -1){
                            deleteAlarm(delID);
                        }else {
                            holdHour = 0;
                            holdMinute = 0;
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
    public void getCurrentTimepickerTime(){
        holdHour = alarm_timepicker.getHour();
        holdMinute = alarm_timepicker.getMinute();
    }
}
