package nl.hs_hague.urbangame;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.hs_hague.urbangame.adapter.ExpandableRoomAdapter;
import nl.hs_hague.urbangame.database.DatabaseHandler;
import nl.hs_hague.urbangame.fcm.RegistrationIntentService;
import nl.hs_hague.urbangame.model.Room;
import nl.hs_hague.urbangame.model.User;
import nl.hs_hague.urbangame.util.CustomLocationListener;
import nl.hs_hague.urbangame.util.GeofenceReceiver;
import nl.hs_hague.urbangame.util.YourReceiver;

public class RoomListActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private boolean mTwoPane;
    ExpandableRoomAdapter roomAdapter;
    ExpandableListView lvRooms;
    List<String> roomsHeader;
    public static  HashMap<String, List<Room>> rooms;
    private Context context = null;
    public static DatabaseHandler databaseHandler = new DatabaseHandler();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private String searchQuery;
    public static final String HEADER_STARTED_ROOMS = "Started Rooms";
    public static final String HEADER_PUBLIC_ROOMS = "Public Rooms";
    public static final String HEADER_OWN_ROOMS = "Own Rooms";
    public static FirebaseAuth firebaseAuth;
    private  GoogleApiClient mGoogleApiClient;
    private GeofencingRequest geofencingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        if (findViewById(R.id.room_detail_container) != null) {
            mTwoPane = true;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        CustomLocationListener locationListener = new CustomLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        IntentFilter intentFilter = new IntentFilter("ACTION_PROXIMITY_ALERT");
        registerReceiver(new YourReceiver(), intentFilter);
        // 100 meter radius
        float radius = 100f;
        // Expiration is 10 Minutes
        long expiration = 600000;

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Intent geoIntent = new Intent("ACTION_PROXIMITY_ALERT");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, geoIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        locManager.addProximityAlert(-84, 33, radius, expiration, pendingIntent);



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean name = preferences.getBoolean(SettingsActivity.authomatic_login_key, true);


        // Firebase Registration
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // get the listview
        lvRooms = (ExpandableListView) findViewById(R.id.lvRooms);

        // preparing list data
        prepareListData();

        roomAdapter = new ExpandableRoomAdapter(this, roomsHeader);
        // setting list adapter
        lvRooms.setAdapter(roomAdapter);


        context = this;


        lvRooms.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        lvRooms.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Room currentRoom = rooms.get(roomsHeader.get(groupPosition)).get(childPosition);

                if (mTwoPane) {
                    RoomDetailFragment fragment = new RoomDetailFragment();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RoomDetailFragment.ARG_ITEM, currentRoom);

                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.room_detail_container, fragment)
                            .commit();

                } else {
                    // By clicking on the listElement the new Activity is getting called
                    Intent intent = new Intent(getApplicationContext(), RoomDetailActivity.class);
                    intent.putExtra(RoomDetailActivity.ARG_ITEM, currentRoom);
                    startActivity(intent);
                }

                return false;
            }
        });
        startGeofence();

    }

    private void goLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, RoomListActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.menu_create) {
            DialogFragment createFragment = new RoomCreateFragment();
            createFragment.show(getSupportFragmentManager(), "RoomListActivity");

        } else if (id == R.id.action_logout) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("Login", false).apply();

            Boolean login_face = preferences.getBoolean("Login_face", false);
            Boolean login_fire = preferences.getBoolean("Login_fire", false);
            if (login_face) {
                LoginManager.getInstance().logOut();//log out facebook
                editor.putBoolean("Login_face", false).apply();
            }

            if (login_fire) {
                FirebaseAuth.getInstance().signOut();//log out from firebase
                editor.putBoolean("Login_fire", false).apply();
            }
            goLogin();
        } else if (id == R.id.menu_profile) {
            Intent intent = new Intent(this, UserSettingsActivity.class);
            startActivity(intent);
            //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static boolean playerMemberofRoom(Room room) {
        if (room.getMembers() != null) {
            for (User u : room.getMembers()) {
                if (u.getUuid().equals(firebaseAuth.getCurrentUser().getUid())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void prepareListData() {
        roomsHeader = new ArrayList<String>();
        rooms = new HashMap<String, List<Room>>();
        roomsHeader.add(HEADER_STARTED_ROOMS);
        roomsHeader.add(HEADER_PUBLIC_ROOMS);
        roomsHeader.add(HEADER_OWN_ROOMS);

        databaseHandler = new DatabaseHandler();

        searchQuery = "";
        Intent searchIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
            searchQuery = searchIntent.getStringExtra(SearchManager.QUERY);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    databaseHandler.getRoot().child("rooms").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Set<Room> startedSet = new HashSet<Room>();
                            Set<Room> publicSet = new HashSet<Room>();
                            Set<Room> ownSet = new HashSet<Room>();
                            Iterator i = dataSnapshot.getChildren().iterator();
                            while (i.hasNext()) {
                                Room room = (Room) (((DataSnapshot) i.next()).getValue(Room.class));

                                if (searchQuery != null && !searchQuery.equals("") && room.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                                    publicSet.add(room);
                                } else if (searchQuery == null || searchQuery.equals("")) {
                                    if (room.getOwnerId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                        ownSet.add(room);
                                    } else if (!room.getOwnerId().equals(firebaseAuth.getCurrentUser().getUid())
                                            && !playerMemberofRoom(room)) {
                                        publicSet.add(room);
                                    } else {
                                        startedSet.add(room);
                                    }

                                }
                            }
                            rooms.put(roomsHeader.get(0), new ArrayList<Room>(startedSet));
                            roomAdapter.updateRooms(new ArrayList<Room>(startedSet), 0);
                            rooms.put(roomsHeader.get(1), new ArrayList<Room>(publicSet));
                            roomAdapter.updateRooms(new ArrayList<Room>(publicSet), 1);
                            rooms.put(roomsHeader.get(2), new ArrayList<Room>(ownSet));
                            roomAdapter.updateRooms(new ArrayList<Room>(ownSet), 2);
                            lvRooms.expandGroup(0);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }

    private void getPermission() {
        Activity activity = (Activity) context;
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                }
                return;
            }
        }
    }

    public void startGeofence(){
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId("TEST")
                .setCircularRegion(33, -84, 1000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

       // Intent intent = new Intent(this, GeofenceTrasitionService.class);
       // PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }



        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofencingRequest,getGeofenceTransitionPendingIntent()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()){
                    Log.d("RoomListActivity", "Successfully added geofence");
                }else if(!status.isSuccess()){
                    Log.d("RoomListActivity", "Couldnt add geofence");
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    /**
     * Create a PendingIntent that triggers GeofenceTransitionIntentService when a geofence
     * transition occurs.
     */
    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

