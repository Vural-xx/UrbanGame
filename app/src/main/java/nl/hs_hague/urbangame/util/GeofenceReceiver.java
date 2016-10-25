package nl.hs_hague.urbangame.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vural on 26.10.16.
 */

public class GeofenceReceiver extends BroadcastReceiver {
    Context context;

    Intent broadcastIntent = new Intent();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("Hallo", "Entered Checkpoint");

    }
}