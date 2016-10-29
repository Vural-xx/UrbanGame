package nl.hs_hague.urbangame;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.hs_hague.urbangame.adapter.ExpandableRoomAdapter;
import nl.hs_hague.urbangame.adapter.FoundCheckpointAdapter;
import nl.hs_hague.urbangame.adapter.UserAdapter;
import nl.hs_hague.urbangame.model.Checkpoint;
import nl.hs_hague.urbangame.model.CustomTimer;
import nl.hs_hague.urbangame.model.Room;
import nl.hs_hague.urbangame.model.User;

public class RoomDetailFragment extends Fragment {

    public static final String ARG_ITEM = "item_id";
    public static Room currentRoom;
    private Button bntJoinRoom;
    private View view;
    private ListView lvMembers;
    private UserAdapter userAdapter;
    private ListView lvCheckpoints;
    private FoundCheckpointAdapter checkpointAdapter;
    private TextView txtleftTime;
    private Date currentTime;
    private  TextView txtCurrentHint;
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

        if(!currentRoom.getOwnerId().equals(RoomListActivity.firebaseAuth.getCurrentUser().getUid())){
            TabHost.TabSpec spec3 = tabHost.newTabSpec("tab3");
            spec3.setContent(R.id.tab3);
            spec3.setIndicator("Progress");
            tabHost.addTab(spec3);
        }


        if (currentRoom != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            currentTime = new Date(ExpandableRoomAdapter.locaux.getTime());

                final LinearLayout roomDetail = (LinearLayout) rootView.findViewById(R.id.room_detail_holder);
                ((TextView) rootView.findViewById(R.id.room_detail)).setText(currentRoom.getName());
                ((TextView) rootView.findViewById(R.id.room_description)).setText(currentRoom.getDescription());
                bntJoinRoom = (Button) rootView.findViewById(R.id.btn_join_room);
                view = (View) rootView.findViewById(R.id.map_fragment);
                ((TextView) rootView.findViewById(R.id.time_label)).setText("Left Time:");
                txtleftTime = (TextView) rootView.findViewById(R.id.time_text);
                if (currentRoom.getOwnerId().equals(RoomListActivity.firebaseAuth.getCurrentUser().getUid()) || RoomListActivity.playerMemberofRoom(currentRoom)) {
                    bntJoinRoom.setVisibility(View.INVISIBLE);
                    if(currentRoom.timeLeft(currentTime)){
                        txtCurrentHint = (TextView) rootView.findViewById(R.id.hints_text);
                        setCurrentCheckpointText();
                        ((TextView) rootView.findViewById(R.id.hints_label)).setText("Current Hint:");
                    }
                } else {
                    view.setVisibility(View.INVISIBLE);//hide map
                    if(currentRoom.timeLeft(currentTime)){
                        bntJoinRoom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentRoom.getMembers() == null) {
                                    List<User> userList = new ArrayList<User>();
                                    currentRoom.setMembers(userList);
                                }
                                currentRoom.getMembers().add(new User(RoomListActivity.firebaseAuth.getCurrentUser().getUid()));
                                RoomListActivity.databaseHandler.createRoom(currentRoom);
                                roomDetail.setVisibility(View.INVISIBLE);

                            }
                        });
                    }else {
                        bntJoinRoom.setVisibility(View.INVISIBLE);
                    }

                }
                displayLeftTime();
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

    public void displayLeftTime(){
        if(currentRoom.timeLeft(currentTime)){
            CustomTimer customTimer =  currentRoom.getLeftTime(currentTime);
            String timeLeft ="";

            if(customTimer.getDaysLeft() > 0){
                timeLeft = timeLeft + customTimer.getDaysLeft() +" Days ";
                customTimer.setHoursLeft(customTimer.getHoursLeft() -(24 * customTimer.getDaysLeft()));
            }
            if(customTimer.getHoursLeft() > 0){
                timeLeft = timeLeft + customTimer.getHoursLeft() +" Hours ";
                customTimer.setMinutesLeft(customTimer.getMinutesLeft() -(24 * 60 * customTimer.getDaysLeft()));
                customTimer.setMinutesLeft(customTimer.getMinutesLeft() -(60 * customTimer.getHoursLeft()));
            }
            if(customTimer.getMinutesLeft() > 0){
                timeLeft = timeLeft + customTimer.getMinutesLeft() +" Minutes ";
            }

            txtleftTime.setText(timeLeft);
        }else {
            txtleftTime.setText("Game is over");
        }

    }


    public void setCurrentCheckpointText(){
        Checkpoint currentCheckpoint = currentRoom.getCurrentCheckpoint(RoomListActivity.firebaseAuth.getCurrentUser().getUid());
        String currentHint = "";
        if(currentCheckpoint != null){
            currentHint = currentCheckpoint.getHint();
        }else{
            currentHint ="Game completed";
        }
        txtCurrentHint.setText(currentHint);
    }


}
