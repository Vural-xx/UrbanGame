package nl.hs_hague.urbangame;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.hs_hague.urbangame.model.Room;
import nl.hs_hague.urbangame.model.User;

/**
 * A fragment representing a single Room detail screen.
 * This fragment is either contained in a {@link RoomListActivity}
 * in two-pane mode (on tablets) or a {@link RoomDetailActivity}
 * on handsets.
 */
public class RoomDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "item_id";
    private Room currentRoom;
    private Button bntJoinRoom;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoomDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
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

        // Show the dummy content as text in a TextView.
        if(currentRoom.getOwner().getEmail().equals(RoomListActivity.firebaseAuth.getCurrentUser().getEmail())){

        }
        if (currentRoom != null) {
            ((TextView) rootView.findViewById(R.id.room_detail)).setText(currentRoom.getName());
            ((TextView) rootView.findViewById(R.id.room_description)).setText(currentRoom.getDescription());
            bntJoinRoom = (Button) rootView.findViewById(R.id.btn_join_room);
            if(currentRoom.getOwner().getEmail().equals(RoomListActivity.firebaseAuth.getCurrentUser().getEmail())){
                bntJoinRoom.setVisibility(View.INVISIBLE);
            }else{
                bntJoinRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentRoom.getMembers() == null){
                            List<User> userList = new ArrayList<User>();
                            currentRoom.setMembers(userList);
                        }
                        currentRoom.getMembers().add(new User(RoomListActivity.firebaseAuth.getCurrentUser().getEmail()));
                        RoomListActivity.databaseHandler.createRoom(currentRoom);
                    }
                });
            }
        }

        return rootView;
    }
}
