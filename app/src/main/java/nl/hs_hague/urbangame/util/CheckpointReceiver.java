package nl.hs_hague.urbangame.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.List;

import nl.hs_hague.urbangame.MapsActivity;
import nl.hs_hague.urbangame.RoomListActivity;
import nl.hs_hague.urbangame.model.Checkpoint;
import nl.hs_hague.urbangame.model.Room;

import static com.facebook.FacebookSdk.getApplicationContext;
import static nl.hs_hague.urbangame.RoomListActivity.firebaseAuth;

/**
 * Created by vural on 26.10.16.
 */

public class CheckpointReceiver extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Checkpoint checkpoint = (Checkpoint) intent.getSerializableExtra("checkpoint");
        List<Room> startedRooms = RoomListActivity.rooms.get(RoomListActivity.HEADER_STARTED_ROOMS);
        Room checkpointRoom = null;
        for(int i =0; i < startedRooms.size(); i++){
            checkpointRoom = startedRooms.get(i);
            if(checkpointRoom.containsChechkpoint(checkpoint)){
                for(int j =0; j < startedRooms.get(i).getCheckpoints().size(); j++){
                    if(startedRooms.get(i).getCheckpoints().get(j).getName().equals(checkpoint.getName()) && !alreadyFound(startedRooms.get(i).getCheckpoints().get(j))){
                        startedRooms.get(i).getCheckpoints().get(j).getFoundBy().add(RoomListActivity.firebaseAuth.getCurrentUser().getUid());
                        RoomListActivity.databaseHandler.createRoom(startedRooms.get(i));
                        if(!startedRooms.get(i).roomCompleted(firebaseAuth.getCurrentUser().getUid())){
                            addNewReceiver(startedRooms.get(i).getCheckpoints().get(j+1));
                        }
                        Toast.makeText(context, "Congratulations you found the checkpoint", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void addNewReceiver(Checkpoint checkpoint){
        // 100 meter radius
        float radius = 100f;
        // Expiration is 10 Minutes
        long expiration = 600000;
        Intent geoIntent = new Intent("ACTION_PROXIMITY_ALERT");
        geoIntent.putExtra("checkpoint", checkpoint);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, geoIntent, PendingIntent.FLAG_ONE_SHOT);
        RoomListActivity.locationManager.addProximityAlert(checkpoint.getLatitude(), checkpoint.getLongitude(), radius, expiration, pendingIntent);
        RoomListActivity.alertedCheckpoints.add(checkpoint);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }
    }
    private void getPermission() {
        Activity activity = (Activity) context;
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
    public boolean alreadyFound(Checkpoint checkpoint){
        for(int i = 0; i < checkpoint.getFoundBy().size(); i++){
            if(checkpoint.getFoundBy().get(i).equals(RoomListActivity.firebaseAuth.getCurrentUser().getUid())){
                return true;
            }
        }
        return false;
    }
}
