package ahto.yellowduck;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;



public class RingtonePlayingService extends Service {
    int rcr;
    ArrayList<AlarmList> as;
    AlarmList a;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        loadAlarms();
        //Kui teine alarm käib, siis lõpetab selle(ei lähe snooze)
        if(Pop.active){
            Toast.makeText(this, String.valueOf(Pop.active), Toast.LENGTH_SHORT).show();
            Pop pop = new Pop();
            //Pop pop = ((Pop)getApplicationContext());
            a = pop.stopAlarm(pop.alarms.get(pop.requestCode), pop.timer);
            pop.timer.cancel();;
            pop.alarms.set(pop.requestCode, a);
            if(a.getSendSMS() == 1){
                SmsManager sManager = SmsManager.getDefault();
                sManager.sendTextMessage(a.getNumber(), null, a.getSMS(), null, null);
            }
            makeNotification(getApplicationContext());
            //pop.saveAlarms(getApplicationContext());
            Pop.active = false;
            pop.ClosePopFromService();
        }
        if(intent.hasExtra("list")) {
            rcr = intent.getExtras().getInt("rc");
        }
        if(as.size() != 0) {
            if (as.get(rcr).getHasAlarm() == 1) {
                Intent popup = new Intent(this, Pop.class);
                popup.putExtra("rc", rcr);
                popup.putExtra("list", as);
                popup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                popup.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                startActivity(popup);
                return START_NOT_STICKY;
            }
        }


        return START_NOT_STICKY;

    }

    private void loadAlarms() {
        if(as == null) {
            SharedPreferences sp = RingtonePlayingService.this.getSharedPreferences(getString(R.string.alarms_file),
                    MODE_PRIVATE);
            Gson gson = new Gson();
            String response = sp.getString(getString(R.string.alarms), "");
            if (response.equals("")) {
                as = new ArrayList<>();
            } else {
                as = gson.fromJson(response, new TypeToken<ArrayList<AlarmList>>() {
                }.getType());
            }
        }
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
}
