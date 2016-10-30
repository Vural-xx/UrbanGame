package nl.hs_hague.urbangame.comparator;

import java.util.Comparator;

import nl.hs_hague.urbangame.model.Room;

/**
 * Created by alex on 26/10/2016.
 * Class used to sort the PUBLIC ROOMS
 */

public class RoomComparator implements Comparator<Room> {
    @Override
    public int compare(Room room, Room t1) {
        return room.getDistance() - t1.getDistance();
    }
}
