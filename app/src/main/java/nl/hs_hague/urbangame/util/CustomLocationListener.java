package nl.hs_hague.urbangame.util;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by vural on 24.10.16.
 * Class to react on lacationchanges of the user
 */
public class CustomLocationListener implements LocationListener {
    // latitude of location
    private static double lat =0.0;
    // longitude of location
    private static double lon = 0.0;
    // Height of location
    private static double alt = 0.0;
    // Speed of user
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
    }


    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}