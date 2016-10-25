package nl.hs_hague.urbangame.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * Created by vural on 26.10.16.
 */

public class YourReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        final Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Toast.makeText(context, "entering", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "exiting", Toast.LENGTH_SHORT).show();
        }
    }
}
