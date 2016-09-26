package nl.hs_hague.urbangame.adapter;

/**
 * Created by vural on 26.09.16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.model.Room;


public class RoomAdapter extends ArrayAdapter<Room> {

    private LayoutInflater inflater;
    private int resource;
    private List<Room> rooms;

    public RoomAdapter(Context context, int resource, List<Room> rooms) {
        super(context, resource, rooms);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.rooms = rooms;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Hier wird die einzelne Reihe inflated
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }
        TextView tvRoomName = (TextView) convertView.findViewById(R.id.room_name);
        final Room currentRoom = (Room) rooms.get(position);
        tvRoomName.setText(currentRoom.getName());
        notifyDataSetChanged();
        return convertView;
    }

}