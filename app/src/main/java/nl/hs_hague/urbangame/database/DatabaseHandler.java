package nl.hs_hague.urbangame.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import nl.hs_hague.urbangame.model.Room;
import nl.hs_hague.urbangame.model.User;

/**
 * Created by vural on 03.10.16.
 * Databasehandler for Firebae
 */

public class DatabaseHandler {
    // Rootreference of the database
    private DatabaseReference root;

    public DatabaseHandler(){
        root = FirebaseDatabase.getInstance().getReferenceFromUrl("https://urbangame-4f82c.firebaseio.com/").getRoot();
    }

    /**
     * Method to create a room
     * @param room: Room that should be created
     */
    public void createRoom(Room room){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(room.getName(),room);
        root.child("rooms").updateChildren(map);
    }

    /**
     * Method to create a user
     * @param user: User that should be created
     */
    public void createUser(User user){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(user.getUsername(),user);
        root.child("users").updateChildren(map);
    }

    public DatabaseReference getRoot(){
        return root;
    }
}
