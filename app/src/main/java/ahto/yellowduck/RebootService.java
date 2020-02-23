package ahto.yellowduck;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

//SEE VAJAB KINDLASTI KÕVASTI TESTIMIST, aga senimaani on töötanud :P//
public class RebootService extends Service {
    ArrayList<AlarmList> alarmid;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        super.onCreate();
        alarmid = getAlarms(getApplicationContext());
        if(alarmid.isEmpty()){
            return;
        }
        for(AlarmList i : alarmid){
            if(i.getHasAlarm() == 1) {
                setAlarm(i.getRequestCode());
            }
        }
        saveAlarms();
    }
    private ArrayList<AlarmList> getAlarms(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.alarms_file),
                MODE_PRIVATE);
        Gson gson = new Gson();
        String response = sp.getString(context.getString(R.string.alarms), "");
        if (response.equals("")){
            alarmid = new ArrayList<>();
        }else {
            alarmid = gson.fromJson(response, new TypeToken<ArrayList<AlarmList>>() {
            }.getType());
        }
        saveAlarms();
        return alarmid;
    }
    public void saveAlarms(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.alarms_file),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmid);
        editor.remove(getString(R.string.alarms));
        editor.putString(getString(R.string.alarms), json);
        editor.apply();
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
            if(!c.after(calendar)) {//Võib juhtuda, et äratusel on kõik õige juba, siis skipitakse see osa
                getAlarmDay(calendar.get(Calendar.DAY_OF_WEEK), a, c, calendar);
            }
        }

        a.setAlarmtime(c.getTimeInMillis());
        AlarmManager alarmM = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        mIntent.putExtra("rc", a.getRequestCode());
        a.setHasAlarm(1);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), x, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(a.getAlarmDay().size() == 1){
            alarmM.setInexactRepeating(AlarmManager.RTC_WAKEUP, a.getAlarmtime(), 7 * 24 * 60* 60 * 1000, pi);
        }else{
            alarmM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, a.getAlarmtime(), pi);
        }
    }

    public void  getAlarmDay(int today, AlarmList a, Calendar c, Calendar calendar) {//See method tagastab järgmise sobiva päeva
        int index;//nädalapäeva asukoht listis
        int add = 0;//See näitab kui palju kuupäevi on vaja juurde lisada, et ikka saaks järgmine nädalapäev
        int newDay = 0;//Siia tuleb see päev
        int date = c.get(Calendar.DATE);//Praegune kuupäev
            if (a.getAlarmDay().contains(today)) {//Kas äratuse settimise päev on nädalapäevade listis
                if (c.before(calendar)) {//Kas setitav äratus tund ja minut on enne või pärast
                    index = a.getAlarmDay().indexOf(today);//kui on ennem ja nädalapäev on listis, siis võetakse
                    index += 1;//Järgmine nädalapäev
                    if (index != a.getAlarmDay().size()) {//Kui ei võrdu, siis sobib
                        c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(index));
                    } else {//Kui võrdub, siis võetakse indeks 0 ehk esimene sobiv nädalapäev listis
                        c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(0));
                        add = a.getAlarmDay().get(0) + (7 - today);
                    }
                } else {//Kui äratus äratab peale hetkelist kellaaega, siis pannakse tänane päev
                    c.set(Calendar.DAY_OF_WEEK, today);
                }
            } else if (today != 1) {//Kui täna ei ole pühapäev
                for (Integer i : a.getAlarmDay()) {//Võtab esimese sobiva järgmise päeva
                    if (i > today) {
                        newDay = i;
                        break;
                    }
                }
                if (newDay == 0) {//Kui jääb 0, siis tuleb võtta esimene päev
                    c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(0));
                    add = a.getAlarmDay().get(0) + (7 - today);
                } else {//Kui ei tule, siis võetaske see, mis tuleb
                    c.set(Calendar.DAY_OF_WEEK, newDay);
                }
            } else {//Kui tänane päev on pühapäev ja tänane päev enam kellajaliselt ei sobi, siis võetakse kohe listi esimene päev
                c.set(Calendar.DAY_OF_WEEK, a.getAlarmDay().get(0));
                add = a.getAlarmDay().get(0) - today;
                if (add == 0) {//Tuleb ainult siis kui päevaks sai pühapäev
                    add = 7;//Ja siis tuleb nädal juurde liita, sest muidu Android ei saa aru kumba pühapäeva mõeldakse
                }

            }
        if(add != 0) {
            date = date + add;//Hetke kuupäevale liidetakse vastav arv päevi juurde
            c.set(Calendar.DATE, date);//Ja see määratakse äratamise kuupäevaks
        }

    }
}
