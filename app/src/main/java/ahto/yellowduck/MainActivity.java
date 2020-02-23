package ahto.yellowduck;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {
    TabHost tabhost;
    ArrayList<AlarmList> alarmid;
    ArrayList<ImageButton> ibs;
    private static final int numCols = 2;
    private static boolean eBoolean = false; //EULA Dialog boolean aka first time opening

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getEulaBoolean();
        SharedPreferences sp = MainActivity.this.getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        Gson gson = new Gson();
        String response = sp.getString(getString(R.string.alarms), "");
        if (response.equals("")){
            alarmid = new ArrayList<>();
        }else {
            alarmid = gson.fromJson(response, new TypeToken<ArrayList<AlarmList>>() {
            }.getType());
        }
        if(alarmid != null) {
            makebuttons();
        }
        if(getIntent().hasExtra("Close")){
            boolean b = getIntent().getBooleanExtra("Close", true);
            startActivity(new Intent(this, MainActivity.class).putExtra("Exit", b).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if(getIntent().hasExtra("Exit")){
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        }

        tabhost = (TabHost) findViewById(R.id.tabHost);
        tabhost.setup();
        //Tab 1
        TabHost.TabSpec spec = tabhost.newTabSpec("Menu");
        spec.setContent(R.id.Menu);
        spec.setIndicator("Menu");
        tabhost.addTab(spec);
        //Tab 2
        spec = tabhost.newTabSpec("Settings");
        spec.setContent(R.id.Settings);
        spec.setIndicator("Settings");
        tabhost.addTab(spec);

        Button toAbout = (Button) findViewById(R.id.wot);
        toAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Documentation.class));
            }
        });

        ToggleButton timeFormat = (ToggleButton) findViewById(R.id.timeFormat);
        sp = MainActivity.this.getSharedPreferences(getString(R.string.format_file), MODE_PRIVATE);
        timeFormat.setChecked(sp.getBoolean("time", false));
        timeFormat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveTimeFormat(b);
            }
        });

        ImageButton addAlarm = (ImageButton) findViewById(R.id.lisa);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AlarmMaker.class).putExtra("id", -1));
            }
        });
    }

    private void saveTimeFormat(boolean b) {
        SharedPreferences sp = MainActivity.this.getSharedPreferences(getString(R.string.format_file), MODE_PRIVATE);
        sp.edit().putBoolean("time", b).apply();
    }

    private void getEulaBoolean() {
        SharedPreferences sp = MainActivity.this.getSharedPreferences(getString(R.string.EULA_file), MODE_PRIVATE);
        eBoolean = sp.getBoolean("Accepted", false);
        if(!eBoolean) showEulaDialog();
    }

    private void showEulaDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Backup Alarm Clock")
                .setMessage(getString(R.string.Eula))
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sp = MainActivity.this.getSharedPreferences(getString(R.string.EULA_file), MODE_PRIVATE);
                        eBoolean = true;
                        sp.edit().putBoolean("Accepted", true).apply();
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void makebuttons() {
        ibs = new ArrayList<>();
        TableLayout table = (TableLayout)findViewById(R.id.displayLinear);
        TableRow.MarginLayoutParams lp = new TableRow.MarginLayoutParams(TableLayout.MarginLayoutParams.MATCH_PARENT,
                TableLayout.MarginLayoutParams.WRAP_CONTENT);
        lp.topMargin = 35;
        for(AlarmList i: alarmid){
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(lp);
            tr.setPadding(0, 30, 0, 0);
            table.addView(tr, lp);
            for(int y = 0; y < numCols; y++) {
                if(y == 1) {
                    Button b = new Button(this);
                    if(i.getMinute() < 10){
                        b.setText(String.valueOf(i.getHour()) + ":" + "0" + String.valueOf(i.getMinute()));
                    }else {
                        b.setText(String.valueOf(i.getHour()) + ":" + String.valueOf(i.getMinute()));
                    }
                    b.setId(i.getRequestCode());
                    b.setBackground(ContextCompat.getDrawable(this, R.drawable.test2layouttekstinupp));
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), AlarmMaker.class).putExtra("id", view.getId()));
                        }
                    });
                    tr.addView(b, WRAP_CONTENT, MATCH_PARENT);
                }else{
                    final ImageButton ib = new ImageButton(this);
                    ib.setId(i.getRequestCode());
                    ib.setMaxWidth(MATCH_PARENT);
                    ib.setMaxHeight(120);
                    ib.setBackground(ContextCompat.getDrawable(this, R.drawable.test2layoutimagenupp));
                    Drawable d = getResources().getDrawable(android.R.drawable.ic_lock_idle_alarm);
                    Bitmap bit = ((BitmapDrawable) d).getBitmap();
                    d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bit, 50, 50, true));
                    ib.setImageDrawable(d);
                    if(i.getHasAlarm() == 1) {
                        ib.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    }else {
                        ib.setColorFilter(Color.parseColor("#4f5359"));
                    }
                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageButton b = ibs.get(view.getId());
                            toggleAlarm(b);
                        }
                    });
                    ibs.add(ib);
                    tr.addView(ib, 100, 100);
                }
            }
        }
        //Ma ei oskanud hetkel parema lahendusega välja tulla,
        //Aga probleem oli selles, et kui vajutada ikoonile, siis käivitus ka viimasena tehtud ikooninupp
        //Käivitus selles mõttes, et vahetas ka värvi, aga äratuse settinguid ei muutnud
        //Ning lahenduseks sain hetkel teha juurde veel üks nupp ja teha see nähtamatuks
        //Kuid ma kindlasti tahan mingi parema lahendusega välja tulla ja kindlasti ka tegelen sellega
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(lp);
        tr.setPadding(0, 15, 0, 0);
        table.addView(tr, lp);
        ImageButton ib = new ImageButton(this);
        ib.setId(alarmid.size());
        ib.setVisibility(View.INVISIBLE);
        ib.setMaxWidth(MATCH_PARENT);
        ib.setMaxHeight(120);
        ib.setBackground(ContextCompat.getDrawable(this, R.drawable.transparent));
        Drawable d = getResources().getDrawable(android.R.drawable.ic_lock_idle_alarm);
        Bitmap bit = ((BitmapDrawable) d).getBitmap();
        d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bit, 50, 50, true));
        ib.setImageDrawable(d);
        tr.addView(ib, 100, WRAP_CONTENT);

    }
    public void toggleAlarm(final ImageButton b){
        int xcode = 0;
        for(AlarmList i : alarmid){
            if(b.getId() == i.getRequestCode()){
                xcode = i.getRequestCode();
            }
        }
        if(alarmid.get(xcode).getHasAlarm() == 1){
            alarmid.get(xcode).setHasAlarm(0);
            b.setColorFilter(Color.parseColor("#4f5359"));
            cancelAlarm(alarmid.get(xcode).getRequestCode());
        }else{
            alarmid.get(xcode).setHasAlarm(1);
            b.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            setAlarm(alarmid.get(xcode).getRequestCode());
        }
        saveAlarms();
    }

    public void setAlarm(int x) {
        Calendar calendar = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        AlarmList a = alarmid.get(x);
        c.setTimeInMillis(a.getAlarmtime());
        int date = c.get(Calendar.DATE);
        if(a.getAlarmDay().size() == 0){//Päevi ei ole
            if(c.before(calendar)){
                date = date + 1;
                c.set(Calendar.DATE, date);
            }
        }else {//Päevad on antud
            if(!c.after(calendar)) {
                getAlarmDay(calendar.get(Calendar.DAY_OF_WEEK), a, c, calendar);
            }
        }

        a.setAlarmtime(c.getTimeInMillis());
        AlarmManager alarmM = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        mIntent.putExtra("rc", a.getRequestCode());
        a.setHasAlarm(1);
        if(a.getMinute() < 10){
            Toast.makeText(getApplicationContext(), "Alarm is set " + String.valueOf(a.getHour()) + ":0" + String.valueOf(a.getMinute()), Toast.LENGTH_SHORT).show();

        }else Toast.makeText(getApplicationContext(), "Alarm is set " + String.valueOf(a.getHour()) + ":" + String.valueOf(a.getMinute()), Toast.LENGTH_SHORT).show();
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, x, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, a.getAlarmtime(), pi);
    }

    public void  getAlarmDay(int today, AlarmList a, Calendar c, Calendar calendar) {//See method tagastab järgmise sobiva päeva
        int index;
        int add = 0;
        int newDay = 0;
        int date = c.get(Calendar.DATE);
        if(a.getAlarmDay().contains(today)){
                if (c.before(calendar)) {
                    index = a.getAlarmDay().indexOf(today);
                    index += 1;
                    if (index != a.getAlarmDay().size()) {
                        c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(index));
                    } else {
                        c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(0));
                        add = a.getAlarmDay().get(0) + (7 - today);
                    }
                } else {
                    c.set(Calendar.DAY_OF_WEEK, today);
                }
        }else if(today != 1){
            for(Integer i : a.getAlarmDay()) {
                if(i > today){
                    newDay = i;
                    break;
                }
            }
            if(newDay == 0){
                c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(0));
                add = a.getAlarmDay().get(0) + (7 - today);
            }else{
                c.set(Calendar.DAY_OF_WEEK, newDay);
            }
        }else{
            c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(0));
            add = a.getAlarmDay().get(0) - today;
            if(add == 0){
                add = 7;
            }
        }
        if(add != 0) {
            date = date + add;
            c.set(Calendar.DATE, date);
        }

    }

    public void cancelAlarm(int x) {
        AlarmManager alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, x, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm_manager.cancel(pi);
    }

    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }

    private void saveAlarms() {
        SharedPreferences sp = MainActivity.this.getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmid);
        editor.remove(getString(R.string.alarms));
        editor.putString(getString(R.string.alarms), json);
        editor.apply();
    }
}
