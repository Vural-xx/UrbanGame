package nl.hs_hague.urbangame;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, MarkersFragment.MakersFragmentListener {

    private GoogleMap mMap; //The map that we will show
    private GoogleApiClient mGoogleApiClient; //The client object needed to get access to the location of the device
    private Location mLastLocation;
    public LatLng sydney, cLocation;
    public List<Address> resAddress; // Arraylist where is saved the current location
    public List<Address> markersAddress; //Arraylist where are saved the addresses of the result from the search, in this demo it is searching for Jumbo stores
    private List<LatLng> markers; //Arraylist where are save the latitude and the longitude of the elements of the markersAddress array
    public Geocoder geo; //Object to get access to the geolocation
    private List<Marker> idMarkers;
    MarkersFragment dialog;
    private  Context context;
    private final int maxMarkers = 10;
    private int counterMarkers;
    String mess;

    /*Initializing the map*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geo = new Geocoder(this);
        markers = new ArrayList<LatLng>();
        idMarkers = new ArrayList<Marker>();
       counterMarkers=0;
        context = this;
        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*You can get the markers with the getMarkers method
                 It returns a List of addresses, in order to get the name of the marker just use the method getTitle()
                 In order to get the coordenates use the method getPosition and it returns a LatLng object and you can get the coordenates with that object
                 with the methods getLatitude() and getLongitude

                 */
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    /**
     * Once the map is ready we can do some stuff, here I am adding a listener in order to add a new marker
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    /*Mandatory methods*/
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        dialog = new MarkersFragment();
        dialog.show(getSupportFragmentManager(), "Add a name to your marker");
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient); //Getting the current location


        cLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()); //Getting the coordenates
       // mMap.addMarker(new MarkerOptions().position(cLocation).title("Marker in Den Haag")); //Adding a new marker in our current location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cLocation)); //Moving the camera to our current location
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14)); //Having a better view of our location
        if (mLastLocation != null) {
            try {
                resAddress = geo.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),5);
                mess = "Your address is:";
                Toast.makeText(this, mess+resAddress.get(0).getAddressLine(0)+" "+resAddress.get(0).getPostalCode(),
                        Toast.LENGTH_LONG).show();
                //addMarkers("Jumbo","The Hague");

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        int index = 0;
                        String cLocality, mLocality;
                        markers.add(latLng);
                        index = markers.size();
                         try{
                             //Getting the locality of the potential marker
                            markersAddress = geo.getFromLocation(latLng.latitude,latLng.longitude,2);
                             mLocality = markersAddress.get(0).getLocality();
                             //Getting our current locality
                             markersAddress = geo.getFromLocation(cLocation.latitude,cLocation.longitude,2);
                             cLocality = markersAddress.get(0).getLocality();

                             if(cLocality.equals(mLocality)) {
                                 if (counterMarkers < maxMarkers) {
                                     mMap.addMarker(new MarkerOptions().position(latLng).title("Marker " + index));
                                     counterMarkers++;
                                     mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                         @Override
                                         public boolean onMarkerClick(Marker arg0) {
                                             showNoticeDialog();
                                             int i = 0;
                                             try {

                                                 if (idMarkers.isEmpty() == true) {
                                                     idMarkers.add(arg0);

                                                 }
                                                 do {
                                                     if (arg0.equals(idMarkers.get(i))) {
                                                         if (!mess.equals(""))
                                                             idMarkers.get(i).setTitle(mess);

                                                         Toast.makeText(context, "Your marker: " + idMarkers.get(i).getTitle() + " " + idMarkers.get(i).getId(), Toast.LENGTH_SHORT).show();
                                                         mess = "";
                                                         break;
                                                     }
                                                     i++;
                                                 } while (i < idMarkers.size());
                                                 if (i == idMarkers.size()) {
                                                     idMarkers.add(arg0);
                                                     if (!mess.equals(""))
                                                         idMarkers.get(i + 1).setTitle(mess);

                                                     Toast.makeText(context, "Your marker: " + idMarkers.get(i + 1).getTitle() + " " + idMarkers.get(i + 1).getId(), Toast.LENGTH_SHORT).show();
                                                     mess = "";
                                                 }
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }
                                             return true;
                                         }
                                     });
                                     for (int i = 0; i < markers.size(); i++)
                                         System.out.println("Marker " + i + " " + markers.get(i).latitude + " " + markers.get(i).longitude);
                                 }
                                 else{
                                     Toast.makeText(context,"You can not add more markers", Toast.LENGTH_SHORT).show();
                                 }
                             }
                             else{
                                 Toast.makeText(context,"You can not add marker out of your current city", Toast.LENGTH_SHORT).show();
                             }


                         }
                         catch(Exception e){
                             e.printStackTrace();
                         }


                    }
                });
                /*Showing the current location**/
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void addMarkers(String place, String locality){
        double latitude, longitude;
        try {

            markersAddress = geo.getFromLocationName(place+locality, 10); //Ypu have to define how many results you want, in this case they are only 10
            mess = "Your coordenates are:";
            //  sydney = new LatLng(52.067197,4.324365);
            // markers.add(sydney);

            try {

                // Adding the markers with the results
                for (int i = 0; i < markersAddress.size(); i++) {
                    latitude = markersAddress.get(i).getLatitude();
                    longitude = markersAddress.get(i).getLongitude();
                    Toast.makeText(this, String.valueOf(latitude) + " " + String.valueOf(longitude) + " " + markersAddress.get(i).getLocality(),
                            Toast.LENGTH_LONG).show();
                    cLocation = new LatLng(latitude, longitude);
                    markers.add(cLocation);
                }
                for (int i = 0; i < markers.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(markers.get(i)).title("Jumbo no: " + i));
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                        @Override
                        public boolean onMarkerClick(Marker arg0) {
                            showNoticeDialog();
                            try {

                                arg0.setTitle(mess);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*I have not tried this method yet but it is supposed to compare your current location with all the coordenates that are saverd as a marker*/
    @Override
    public void onLocationChanged(Location location) {
        Location mCurrentLocation = location;
        String success = "You have reached a checkpoint";

        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Toast.makeText(this, success+" time: ",
                Toast.LENGTH_LONG).show();
        for(int i=0; i<idMarkers.size();i++){
            if((mCurrentLocation.getLatitude() == idMarkers.get(i).getPosition().latitude) && (mCurrentLocation.getLongitude() == idMarkers.get(i).getPosition().longitude))
                Toast.makeText(this, success+" time: "+mLastUpdateTime,
                        Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name) {
        mess = name;
    }
    public List<Address> getMarkers(){
        return markersAddress;
    }

}
