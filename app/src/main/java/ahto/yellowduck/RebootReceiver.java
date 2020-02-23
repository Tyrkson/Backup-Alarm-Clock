package ahto.yellowduck;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Reboot!!!!", Toast.LENGTH_SHORT).show();
        Intent startServiceIntent = new Intent(context, RebootService.class);
        context.startService(startServiceIntent);
    }


}
