package nl.hs_hague.urbangame;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.hs_hague.urbangame.adapter.FoundCheckpointAdapter;
import nl.hs_hague.urbangame.adapter.UserAdapter;
import nl.hs_hague.urbangame.model.Room;
import nl.hs_hague.urbangame.model.User;

public class RoomDetailFragment extends Fragment {

    public static final String ARG_ITEM = "item_id";
    private Room currentRoom;
   // public static final String distance="";
    private Button bntJoinRoom;
    private View view;
    private ListView lvMembers;
    private UserAdapter userAdapter;
    private ListView lvCheckpoints;
    private FoundCheckpointAdapter checkpointAdapter;

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
            final LinearLayout roomDetail = (LinearLayout) rootView.findViewById(R.id.room_detail_holder);
            ((TextView) rootView.findViewById(R.id.room_detail)).setText(currentRoom.getName());
            ((TextView) rootView.findViewById(R.id.room_description)).setText(currentRoom.getDescription());
            bntJoinRoom = (Button) rootView.findViewById(R.id.btn_join_room);
            view = (View) rootView.findViewById(R.id.map_fragment);

            if(currentRoom.getOwnerId().equals(RoomListActivity.firebaseAuth.getCurrentUser().getUid()) || RoomListActivity.playerMemberofRoom(currentRoom)){
                bntJoinRoom.setVisibility(View.INVISIBLE);
                ((TextView) rootView.findViewById(R.id.hints_text)).setText(currentRoom.gethints());
                ((TextView) rootView.findViewById(R.id.hints_text)).setText(currentRoom.getCurrentCheckpoint(RoomListActivity.firebaseAuth.getCurrentUser().getUid()).getHint());
                ((TextView) rootView.findViewById(R.id.hints_label)).setText("Current Hint:");
            }else{
                view.setVisibility(View.INVISIBLE);//hide map
                bntJoinRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentRoom.getMembers() == null){
                            List<User> userList = new ArrayList<User>();
                            currentRoom.setMembers(userList);
                        }
                        currentRoom.getMembers().add(new User(RoomListActivity.firebaseAuth.getCurrentUser().getUid()));
                        RoomListActivity.databaseHandler.createRoom(currentRoom);
                        roomDetail.setVisibility(View.INVISIBLE);

                    }
                });
            }
            // get the listview
            lvMembers = (ListView) rootView.findViewById(R.id.lvMembers);
            userAdapter = new UserAdapter(getContext(), R.layout.room_member_list_content, getMembersWithoutCurrentUser());
            lvMembers.setAdapter(userAdapter);

            lvCheckpoints = (ListView) rootView.findViewById(R.id.lvFoundCheckpoints);
            checkpointAdapter = new FoundCheckpointAdapter(getContext(), R.layout.room_checkpoint_list_content, currentRoom.foundCheckPoints(RoomListActivity.firebaseAuth.getCurrentUser().getUid()));
            lvCheckpoints.setAdapter(checkpointAdapter);
        }

        return rootView;
    }

    public List<User> getMembersWithoutCurrentUser(){
        List<User> users = new ArrayList<>();
        if(currentRoom.getMembers() != null){
            for(User u: currentRoom.getMembers()){
                if(!u.getUuid().equals(RoomListActivity.firebaseAuth.getCurrentUser().getUid())){
                    users.add(u);
                }
            }
        }

        return users;
    }
}
