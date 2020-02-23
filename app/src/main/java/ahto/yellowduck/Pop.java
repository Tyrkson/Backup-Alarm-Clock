package ahto.yellowduck;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Pop extends Activity {
    TextView start;
    static Calendar cal;
    AlarmManager aM;
    static MediaPlayer mediaSong;
    AudioManager audio;
    static Vibrator vib;
    int requestCode;
    static ArrayList<AlarmList> alarms;
    Uri myUri;
    boolean isPlaying;
    public int count;
    static Timer timer;
    static AlarmList a;
    static boolean active = false;
    static SharedPreferences sp;
    private void unlockScreen(){
        Window pop = this.getWindow();
        pop.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        pop.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        pop.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
    public AlarmList snooze(AlarmList i, Timer timer){
        timer.cancel();
        if(isPlaying) {
            mediaSong.stop();
        }
        vib.cancel();
        i.setHasAlarm(1);
        i.setIsAlarming(0);

        cal = Calendar.getInstance();
        int min = cal.get(Calendar.MINUTE);
        cal.set(Calendar.MINUTE, min + i.getSnoozeMinute());
        i.setSnoozeTime(cal.getTimeInMillis());
        Intent s = new Intent(Pop.this, AlarmReceiver.class);
        s.putExtra("rc", i.getRequestCode());
        s.putExtra("obj", i);
        s.putExtra("list", alarms);

        PendingIntent sn = PendingIntent.getBroadcast(Pop.this, i.getRequestCode(), s, PendingIntent.FLAG_UPDATE_CURRENT);

        aM.setExact(AlarmManager.RTC_WAKEUP, i.getSnoozeTime(), sn);
        return i;
    }
    public AlarmList stopAlarm(AlarmList i, Timer timer){
        timer.cancel();
        if(isPlaying) {
            mediaSong.stop();
        }
        vib.cancel();
        i.setHasAlarm(0);//sellel objektil ei ole äratust
        i.setIsAlarming(0);//äratus ei tööta
        i.setRepeatsleft(i.getRepeat());
        cal = Calendar.getInstance();

        if(!i.getAlarmDay().isEmpty() && i.getAlarmDay().size() > 1) {
            //Võtab järgmise päeva
            int today = i.getDay();
            int add;
            int index = i.getAlarmDay().indexOf(i.getDay());
            index += 1;
            if(index != i.getAlarmDay().size()){
                i.setDay(i.getAlarmDay().get(index));
            }else {
                i.setDay(i.getAlarmDay().get(0));
                if(today == 1){
                    add = i.getDay() - today;
                }else add = i.getDay() + (7 - today);
                cal.add(Calendar.DATE, add);
            }
            //Paneb uue äratuse jaoks ajad sisse
            cal.set(Calendar.HOUR, i.getHour());
            cal.set(Calendar.MINUTE, i.getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_WEEK, i.getDay());
            i.setAlarmtime(cal.getTimeInMillis());
            i.setHasAlarm(1);

        }else if(i.getAlarmDay().size() == 1){
            int date = cal.get(Calendar.DATE);
            date = date + 7;
            cal.set(Calendar.DATE, date);
            i.setHasAlarm(1);
        }
        if(i.getHasAlarm() == 1) {
            Intent s = new Intent(Pop.this, AlarmReceiver.class);
            s.putExtra("rc", i.getRequestCode());
            s.putExtra("obj", i);
            s.putExtra("list", alarms);
            PendingIntent sn = PendingIntent.getBroadcast(Pop.this, i.getRequestCode(), s, PendingIntent.FLAG_UPDATE_CURRENT);
            aM.setExact(AlarmManager.RTC_WAKEUP, i.getAlarmtime(), sn);
        }
        return i;
    }

    public void saveAlarms(Context c){
        sp = c.getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarms);
        editor.remove(getString(R.string.alarms));
        editor.putString(getString(R.string.alarms), json);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup1);
        sp = Pop.this.getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        Gson gson = new Gson();
        String response = sp.getString(getString(R.string.alarms), "");
        if (response.equals("")) {
            alarms = new ArrayList<>();
        } else {
            alarms = gson.fromJson(response, new TypeToken<ArrayList<AlarmList>>() {
            }.getType());
        }
        try{
            requestCode = getIntent().getExtras().getInt("rc");
        }catch(NullPointerException e){
            finish();
        }

        a = alarms.get(requestCode);
        if(a.getHasAlarm() == 0){
            active = false;
            finish();
        }
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, a.getSoundVolume(), 0);
        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        //Nimi ütleb juba ära küll, aga igatahes avab ekraani
        unlockScreen();
        //Peidab ülemise ääre ära (notificationid, aku, kell)
        hideStatusBar();

        if(a.getMusic() != null) {
            myUri = Uri.parse(a.getMusic());
            mediaSong = new MediaPlayer();
            mediaSong.setAudioStreamType(AudioManager.STREAM_ALARM);
           // mediaSong.setVolume(a.getSoundVolume(), a.getSoundVolume());
            try {
                mediaSong.setDataSource(getApplicationContext(), myUri);
                mediaSong.prepare();
                mediaSong.start();
                isPlaying = true;
            } catch (IOException e) {
                Toast.makeText(this, "Mediafile doesn't exist anymore?!", Toast.LENGTH_SHORT).show();
            }
        }else{
            isPlaying = false;
        }
        if (a.getHasVib() == 1){
            long[] pattern = {0, 400, 1000};
            vib.vibrate(pattern, 0);
        }

        aM = (AlarmManager) getSystemService(ALARM_SERVICE);

        start = (TextView) findViewById(R.id.start);
        //start.setText(String.valueOf(a.getHour()) + String.valueOf(a.getMinute()));
        start.setText(" ");
        final Button peata = (Button) findViewById(R.id.stop);
        final Button snooze = (Button) findViewById(R.id.snooze);


        timer = new Timer();
        count = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count == 60){
                    Pop.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(a.getSnoozeMinute() > 0 && a.getRepeatsleft() > 0){
                                a.setRepeatsleft(a.getRepeatsleft() - 1);
                                a = snooze(alarms.get(requestCode), timer);
                                alarms.set(requestCode, a);
                            }else{
                                a = stopAlarm(alarms.get(requestCode), timer);
                                alarms.set(requestCode, a);
                                if(a.getSendSMS() == 1){
                                    SmsManager sManager = SmsManager.getDefault();
                                    sManager.sendTextMessage(a.getNumber(), null, a.getSMS(), null, null);
                                }
                                makeNotification(getApplicationContext());

                            }
                            saveAlarms(getApplicationContext());
                            ClosePop();
                        }
                    });
                }
            }


        }, 1000, 1000);
        peata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = stopAlarm(alarms.get(requestCode), timer);
                alarms.set(requestCode, a);
                saveAlarms(getApplicationContext());
                ClosePop();
            }
        });
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(a.getSnoozeMinute() != 0) {
                    a = snooze(alarms.get(requestCode), timer);
                }else {
                    a = stopAlarm(alarms.get(requestCode), timer);
                }
                alarms.set(requestCode, a);
                saveAlarms(getApplicationContext());
                ClosePop();
            }
        });

    }

    public void ClosePop() {
        active = false;
        boolean close = true;
        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("Close", close));
        finish();
    }
    //Sest siis on vaja ainult see layout kinni panna
    //Ja kuna see kõik käib nii ruttu, siis kasutaja ei saa arugi, et see koraaks kinni läks
    public void ClosePopFromService() {
        finish();
    }
    public void makeNotification(Context con) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(con)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Missed alarm")
                        .setContentText("You missed an alarm: " + String.valueOf(a.getHour()) + ":" + String.valueOf(a.getMinute()));
        int notiId = 1;
        NotificationManager notiM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiM.notify(notiId, mBuilder.build());

    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onBackPressed() {
    }
}
