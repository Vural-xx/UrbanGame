package nl.hs_hague.urbangame.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.model.Checkpoint;
import nl.hs_hague.urbangame.model.Room;

/**
 * Created by vural on 06.10.16.
 */

public class ExpandableRoomAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Room>> listDataChild;
    private List<Checkpoint> listCheck;
    private float[] res;

    public ExpandableRoomAdapter(Context context, List<String> listDataHeader) {
        this._context = context;
        this.listDataHeader = listDataHeader;
        listDataChild = new HashMap<String, List<Room>>();
        for(int i = 0; i < listDataHeader.size(); i++){
            listDataChild.put(listDataHeader.get(i), new ArrayList<Room>());
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
        try{
            CurrentLocationAdapter objCurrentLocationAdapter = new CurrentLocationAdapter(_context);
            Location locaux = objCurrentLocationAdapter.getCurrentLocation();
            System.out.println(locaux);
            if(!room.getCheckpoints().isEmpty())
            {
                    listCheck = room.getCheckpoints();
                   for(int i =0; i<listCheck.size(); i++){
                       locaux.distanceBetween(listCheck.get(i).getLatitude(),listCheck.get(i).getLongitude(),locaux.getLatitude(),locaux.getLongitude(),res);
                       childText.concat(" Distance: "+res[0]);
                   }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.room_name);

        txtListChild.setText(childText);
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
        this.notifyDataSetChanged();
    }

}
