package nl.hs_hague.urbangame;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.hs_hague.urbangame.adapter.RoomAdapter;
import nl.hs_hague.urbangame.database.DatabaseHandler;
import nl.hs_hague.urbangame.fcm.RegistrationIntentService;
import nl.hs_hague.urbangame.model.Room;
public class RoomListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private ListView lvRooms;
    private ArrayList<Room> rooms = new ArrayList<Room>();
    RoomAdapter roomAdapter;
    private Context context = null;
    public static DatabaseHandler databaseHandler = new DatabaseHandler();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        if (findViewById(R.id.room_detail_container) != null) {
            mTwoPane = true;
        }

        // Firebase Registration
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        lvRooms = (ListView) findViewById(R.id.lvRooms);
        roomAdapter = new RoomAdapter(this, R.layout.room_list_content, rooms);
        lvRooms.setAdapter(roomAdapter);
        context = this;

        lvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Room restaurant = (Room) parent.getItemAtPosition(position);
                if (mTwoPane) {
                    RoomDetailFragment fragment = new RoomDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RoomDetailFragment.ARG_ITEM,restaurant);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.room_detail_container, fragment)
                            .commit();

                } else {
                    // Beim klciken auf das Listenelement wird die neue Activity geöffnet
                    Intent intent = new Intent(getApplicationContext(), RoomDetailActivity.class);
                    intent.putExtra(RoomDetailActivity.ARG_ITEM, restaurant);
                    startActivity(intent);
                }
            }
        });

        searchQuery = "";
        Intent searchIntent = getIntent();
        if(Intent.ACTION_SEARCH.equals(searchIntent.getAction())){
            searchQuery = searchIntent.getStringExtra(SearchManager.QUERY);
        }

        databaseHandler = new DatabaseHandler();


        databaseHandler.getRoot().child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<Room> set = new HashSet<Room>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    String keyItem = ((DataSnapshot)i.next()).getKey();
                    if(searchQuery != null &&  !searchQuery.equals("") && keyItem.equals(searchQuery)){
                        set.add(new Room(keyItem));
                    }else if(searchQuery == null || searchQuery.equals("")){
                        set.add(new Room(keyItem));
                    }
                }
                rooms.clear();
                rooms.addAll(set);
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void goLogin() {
        Intent intent= new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    private List<Room> generateRooms(){
        List<Room> rooms = new ArrayList<Room>();
        rooms.add(new Room("Room1"));
        rooms.add(new Room("Room2"));
        return rooms;
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
        }else if (id ==  R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }else if (id == R.id.menu_create){
            DialogFragment createFragment = new RoomCreateFragment();
            createFragment.show(getSupportFragmentManager(),"RoomListActivity");

        }else if(id == R.id.action_logout){
            LoginManager.getInstance().logOut();//log out facebook
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("Login",false).apply();
            goLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        if(view.getId() == R.id.btnSetCheckpoints){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
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


}

