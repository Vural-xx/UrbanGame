package nl.hs_hague.urbangame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.hs_hague.urbangame.model.Checkpoint;
import nl.hs_hague.urbangame.model.CheckpointHolder;

import static java.lang.Thread.sleep;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, MarkersFragment.MakersFragmentListener, Serializable {

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
    private int checkCounter=0;
    private Context context;
    private final int maxMarkers = 10;
    private int counterMarkers;
    String mess;
    private Button doneButton;
    private List<String> hints = new ArrayList<String>(10);
    private List<String> names = new ArrayList<String>(10);
    LocationRequest locationRequest;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String SAVED_GAME = "savedGame";
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    /*Initializing the map*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            MapsActivity game = (MapsActivity) savedInstanceState.get(SAVED_GAME);
            idMarkers = game.getidMarkers();
            context = game.getContext();
            checkCounter = game.getCheckCounter();
            cLocation = game.getcLocation();
            resAddress = game.getResAddress();
            markersAddress = game.getMarkersAddress();
            markers = game.getMarkersList();
            geo = game.getGeo();
            names = game.getNames();
            hints = game.getHints();
            counterMarkers = game.getCounterMarkers();
            doneButton = (Button) findViewById(R.id.doneButton);

            mGoogleApiClient = game.getmGoogleApiClient();
            mess = game.getMess();

        }
        else {
                    geo = new Geocoder(this);
                    markers = new ArrayList<LatLng>();
                    idMarkers = new ArrayList<Marker>();
                    counterMarkers = 0;
                    context = this;
                    doneButton = (Button) findViewById(R.id.doneButton);
                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            retrieveMarkers();
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
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable(SAVED_GAME, MapsActivity.this);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    /**
     * Once the map is ready we can do some stuff, here I am adding a listener in order to add a new marker
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        System.out.println("Im in onMapReady");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        try {
            if (counterMarkers > 0) {
                for (int i = 0; i < idMarkers.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(idMarkers.get(i).getPosition()).title(idMarkers.get(i).getTitle()));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveMarkers();
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                System.out.println("I added the click listener");
                mapLongListener(latLng);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                return markerClick(arg0);
            }
        });
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

    public void showNoticeDialog(String markerid) {
        // Create an instance of the dialog fragment and show it
        dialog = new MarkersFragment();
        Bundle arguments = new Bundle();
        arguments.putString(MarkersFragment.ARG_ITEM,markerid);
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "Add a name to your marker");
    }
    /******/
    void mapLongListener(LatLng latLng){
            int index = 0;
            String cLocality, mLocality;
            markers.add(latLng);

            index = markers.size();
            try {
                //Getting the locality of the potential marker
                markersAddress = geo.getFromLocation(latLng.latitude, latLng.longitude, 2);
                mLocality = markersAddress.get(0).getLocality();
                //Getting our current locality
                markersAddress = geo.getFromLocation(cLocation.latitude, cLocation.longitude, 2);
                cLocality = markersAddress.get(0).getLocality();

                if (cLocality.equals(mLocality)) {
                    if (counterMarkers < maxMarkers) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker " + index));
                        counterMarkers++;
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker arg0) {
                                return markerClick(arg0);
                            }
                        });

                    } else {
                        Toast.makeText(context, "You can not add more markers", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "You can not add marker out of your current city", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /********/
    public boolean markerClick(Marker arg0){
            showNoticeDialog(arg0.getId());
            int i = 0;
            try {

                if (idMarkers.isEmpty() == true) {
                    idMarkers.add(arg0);

                }
                do {
                    if (arg0.equals(idMarkers.get(i))) {
                        if (!mess.equals(""))
                            idMarkers.get(i).setTitle(mess);

                        mess = "";
                        break;
                    }
                    i++;
                } while (i < idMarkers.size());
                if (i == idMarkers.size()) {
                    idMarkers.add(arg0);
                    if (!mess.equals(""))
                        idMarkers.get(i).setTitle(mess);
                    mess = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient); //Getting the current location


        if (mLastLocation != null) {
            cLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()); //Getting the coordenates
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cLocation)); //Moving the camera to our current location
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14)); //Having a better view of our location
            try {
                resAddress = geo.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 5);
                mess = "Your address is:";
                Toast.makeText(this, mess + resAddress.get(0).getAddressLine(0) + " " + resAddress.get(0).getPostalCode(),
                        Toast.LENGTH_LONG).show();

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        System.out.println("I added the click listener");
                        mapLongListener(latLng);
                    }
                });
                /*Showing the current location**/
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /*I have not tried this method yet but it is supposed to compare your current location with all the coordenates that are saverd as a marker*/
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name, String hint, String id) {
        mess = name;
        System.out.println("You clicked on: "+id);
        String getId[] = id.split("m");
        int idReceived = Integer.parseInt(getId[1]);
        if((names.isEmpty() == true) || (names.size() == idReceived)){
            names.add(name);
            hints.add(hint);
        }
        else if(names.size() > idReceived){
            System.out.println("I entered to the else part"+ name+hint);
            names.set(idReceived,name);
            hints.set(idReceived,hint);
        }
    }


    private void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                }
                return;
            }
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }

        // Create the location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, this);
    }

    private void retrieveMarkers(){
        Intent intent = this.getIntent();
        CheckpointHolder checkpointHolder = new CheckpointHolder();
        checkpointHolder.setCheckpoints(new ArrayList<Checkpoint>());
        for (int i = 0; i < idMarkers.size(); i++){

            idMarkers.get(i).setTitle(names.get(i));

            Checkpoint checkpoint = new Checkpoint(idMarkers.get(i).getTitle(), idMarkers.get(i).getPosition().latitude, idMarkers.get(i).getPosition().longitude);
            checkpoint.setHint(hints.get(i));
            checkpointHolder.getCheckpoints().add(checkpoint);
        };
        intent.putExtra(RoomCreateFragment.MARKER, checkpointHolder);
        this.setResult(RESULT_OK, intent);
        finish();
    }




    /*******/
    public List<Address> getMarkersAddress(){
        return markersAddress;
    }
    public List<Marker> getidMarkers(){
        return idMarkers;
    }
    public Context getContext(){
        return context;
    }
    public LatLng getcLocation(){
        return cLocation;
    }
    public List<Address> getResAddress(){
        return resAddress;
    }
    public List<LatLng> getMarkersList(){
        return markers;
    }
    public Geocoder getGeo(){
        return geo;
    }
    public int getCounterMarkers(){
        return counterMarkers;
    }
    public GoogleMap getmMap(){
        return mMap;
    }
    public GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
    }
    public String getMess(){
        return mess;
    }
    public Button getDoneButton(){return doneButton;}
    public int getCheckCounter(){
        return checkCounter;
    }
    public List<String> getNames(){return names;}
    public List<String> getHints(){return hints;}


}
