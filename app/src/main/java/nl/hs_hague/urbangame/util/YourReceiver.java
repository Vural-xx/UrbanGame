package nl.hs_hague.urbangame.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

import nl.hs_hague.urbangame.model.Checkpoint;

/**
 * Created by vural on 26.10.16.
 */

public class YourReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Checkpoint checkpoint = (Checkpoint) intent.getSerializableExtra("checkpoint");
        Toast.makeText(context, "Congratulations you found the checkpoint", Toast.LENGTH_SHORT).show();
    }
}
