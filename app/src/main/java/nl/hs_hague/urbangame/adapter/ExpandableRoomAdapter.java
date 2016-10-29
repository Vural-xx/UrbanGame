package nl.hs_hague.urbangame.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.RoomListActivity;
import nl.hs_hague.urbangame.comparator.RoomComparator;
import nl.hs_hague.urbangame.model.Room;

/**
 * Created by vural on 06.10.16.
 */

public class ExpandableRoomAdapter extends BaseExpandableListAdapter {

    private Context _context;
    public static Location locaux;
    private List<String> listDataHeader; // header titles
    private List<Address> markersAddress;
    // child data in format of header title, child title
    private HashMap<String, List<Room>> listDataChild;
    private float[] res;

    public ExpandableRoomAdapter(Context context, List<String> listDataHeader) {
        this._context = context;
        this.listDataHeader = listDataHeader;
        res = new float[20];
        listDataChild = new HashMap<String, List<Room>>();
        markersAddress = new ArrayList<Address>();
        for(int i = 0; i < listDataHeader.size(); i++){
            listDataChild.put(listDataHeader.get(i), new ArrayList<Room>());
        }

    }


    public void calculateDistance(){
        List<Room> publicRooms = listDataChild.get(RoomListActivity.HEADER_PUBLIC_ROOMS);
        CurrentLocationAdapter objCurrentLocationAdapter = new CurrentLocationAdapter(_context);
        locaux = objCurrentLocationAdapter.getCurrentLocation();


            for (int i = 0; i < publicRooms.size(); i++) {
                if (!publicRooms.get(i).getCheckpoints().isEmpty()) {

                        locaux.distanceBetween(publicRooms.get(i).getCheckpoints().get(0).getLatitude(), publicRooms.get(i).getCheckpoints().get(0).getLongitude(), locaux.getLatitude(), locaux.getLongitude(), res);
                        publicRooms.get(i).setDistance((int) res[0]);

                }
            }
        Collections.sort(publicRooms, new RoomComparator());
        for (int j = 0; j < publicRooms.size(); j++) {
            listDataChild.get(RoomListActivity.HEADER_PUBLIC_ROOMS).set(j, publicRooms.get(j));
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Room room = (Room)getChild(groupPosition,childPosition);

         String childText = (String) room.getName();


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.room_list_content, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.room_name);
        TextView childDistance = (TextView) convertView
                .findViewById(R.id.room_distance);
        childDistance.setText("");
        if(groupPosition == 0){
            childText = childText + " " + room.foundCheckPoints(RoomListActivity.firebaseAuth.getCurrentUser().getUid()).size() + "/" + room.getCheckpoints().size();
            if(room.roomCompleted(RoomListActivity.firebaseAuth.getCurrentUser().getUid())){
                txtListChild.setTextColor(Color.GREEN);
            }
        }
        txtListChild.setText(childText);
        if(groupPosition == 1) {
            System.out.println("GroupPosition: "+groupPosition + " "+ childText);

            childDistance.setText("Distance: " + room.getDistance()+" meters");

        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(listDataChild != null && listDataHeader != null && listDataChild.size() != 0 && listDataChild.get(listDataHeader.get(groupPosition)) != null){
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    public void updateRooms(List<Room> newlist, int group) {
        listDataChild.get(listDataHeader.get(group)).clear();
        listDataChild.get(listDataHeader.get(group)).addAll(newlist);
        if(group == 1)
        calculateDistance();
        this.notifyDataSetChanged();
    }

}
