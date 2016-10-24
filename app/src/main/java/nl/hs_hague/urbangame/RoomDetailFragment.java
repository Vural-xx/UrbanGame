package nl.hs_hague.urbangame;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.hs_hague.urbangame.model.Room;
import nl.hs_hague.urbangame.model.User;

public class RoomDetailFragment extends Fragment {

    public static final String ARG_ITEM = "item_id";
    private Room currentRoom;
   // public static final String distance="";
    private Button bntJoinRoom;

    public RoomDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            currentRoom = (Room) getArguments().getSerializable(ARG_ITEM);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(currentRoom.getName());
            }



        }
        /*if (getArguments().containsKey(distance)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            currentRoom = (Room) getArguments().getSerializable(distance);

            Activity activity = this.getActivity();
            TextView descrip = (TextView) activity.findViewById(R.id.room_description);
            if (descrip != null) {
                descrip.setText(descrip.getText()+"Distance: "+distance);
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.room_detail, container, false);

        TabHost tabHost = (TabHost) rootView.findViewById(R.id.mytabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("General");
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Members");
        tabHost.addTab(spec2);

        TabHost.TabSpec spec3 = tabHost.newTabSpec("tab3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Progress");
        tabHost.addTab(spec3);

        if (currentRoom != null) {


            ((TextView) rootView.findViewById(R.id.room_detail)).setText(currentRoom.getName());
            ((TextView) rootView.findViewById(R.id.room_description)).setText(currentRoom.getDescription());
            bntJoinRoom = (Button) rootView.findViewById(R.id.btn_join_room);
            if(currentRoom.getOwnerId().equals(RoomListActivity.firebaseAuth.getCurrentUser().getUid()) || RoomListActivity.playerMemberofRoom(currentRoom)){
                bntJoinRoom.setVisibility(View.INVISIBLE);
            }else{
                bntJoinRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentRoom.getMembers() == null){
                            List<User> userList = new ArrayList<User>();
                            currentRoom.setMembers(userList);
                        }
                        currentRoom.getMembers().add(new User(RoomListActivity.firebaseAuth.getCurrentUser().getUid()));
                        RoomListActivity.databaseHandler.createRoom(currentRoom);
                    }
                });
            }
        }

        return rootView;
    }

}
