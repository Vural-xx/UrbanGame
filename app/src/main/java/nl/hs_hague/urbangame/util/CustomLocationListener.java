package nl.hs_hague.urbangame.util;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.List;

import nl.hs_hague.urbangame.RoomListActivity;
import nl.hs_hague.urbangame.model.Checkpoint;
import nl.hs_hague.urbangame.model.Room;

/**
 * Created by vural on 24.10.16.
 */

public class CustomLocationListener implements LocationListener {
    private static double lat =0.0;
    private static double lon = 0.0;
    private static double alt = 0.0;
    private static double speed = 0.0;

    public static double getLat()
    {
        return lat;
    }

    public static double getLon()
    {
        return lon;
    }

    public static double getAlt()
    {
        return alt;
    }

    public static double getSpeed()
    {
        return speed;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        lat = location.getLatitude();
        lon = location.getLongitude();
        alt = location.getAltitude();
        speed = location.getSpeed();
        System.out.println("Location changed");
        checkRoomMatch();
    }

    public void checkRoomMatch(){
        List<Room> rooms =RoomListActivity.rooms.get(RoomListActivity.HEADER_STARTED_ROOMS);


        for(Room r : rooms){
            for(Checkpoint c: r.getCheckpoints()){
                String latCheckpoint = new DecimalFormat("##.##").format(c.getLatitude());
                String lonCheckpoint = new DecimalFormat("##.##").format(c.getLongitude());
                String lati = new DecimalFormat("##.##").format(lat);
                String longi = new DecimalFormat("##.##").format(lon);


                Log.d("LocationListener_Check", c.getLatitude() +", " +c.getLongitude());
                Log.d("CustomLocationListener", lat +", " + lon);
                if(latCheckpoint.equals(lati) && lonCheckpoint.equals(longi)){
                    c.getFoundBy().add(RoomListActivity.firebaseAuth.getCurrentUser().getUid());
                    Log.d("CustomLocationListener", "Congratulations you found the checkpoint");
                    RoomListActivity.databaseHandler.createRoom(r);
                }
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}