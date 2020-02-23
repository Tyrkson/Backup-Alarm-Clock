package ahto.yellowduck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    int rc;
    @Override
    public void onReceive(Context context, Intent intent) {
        rc = intent.getExtras().getInt("rc");
        //Log.e("We are in the receiver.", "Yay");
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
        serviceIntent.putExtra("rc", rc);
        context.startService(serviceIntent);

    }
}
